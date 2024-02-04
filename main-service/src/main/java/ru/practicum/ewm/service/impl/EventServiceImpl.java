package ru.practicum.ewm.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatClient;
import ru.practicum.ewm.StatsResponseDto;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.IncorrectParameterException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.mapper.EventMapper;
import ru.practicum.ewm.model.mapper.LocationMapper;
import ru.practicum.ewm.model.mapper.RequestMapper;
import ru.practicum.ewm.model.state.EventAdminState;
import ru.practicum.ewm.model.state.EventState;
import ru.practicum.ewm.model.state.EventUserState;
import ru.practicum.ewm.model.state.RequestStatus;
import ru.practicum.ewm.repository.*;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final StatClient client;
    private final ObjectMapper objectMapper;

    @Value("${server.application.name:ewm-service}")
    private String applicationName;

    @Override
    public EventFullDto addNewEvent(Long userId, NewEventDto newEventDto) {
        LocalDateTime createdOn = LocalDateTime.now();
        User user = findUser(userId);
        Category category = findCategory(newEventDto.getCategory());
        validateDate(newEventDto.getEventDate());
        Event event = EventMapper.toEvent(newEventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setState(EventState.PENDING);
        event.setCreatedOn(createdOn);
        if (newEventDto.getLocation() != null) {
            event.setLocation(locationRepository.save(LocationMapper.toLocation(newEventDto.getLocation())));
        }

        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        eventFullDto.setViews(0L);
        eventFullDto.setConfirmedRequests(0);
        return eventFullDto;
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = findEvent(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + eventId + " is not published");
        }
        addStats(request);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        Map<Long, Long> statsMap = getAllEventViews(List.of(event));
        Long views = statsMap.getOrDefault(event.getId(), 0L);
        eventFullDto.setViews(views);
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getAllEventsPublic(SearchEventParams searchEventParams, HttpServletRequest request) {
        if (searchEventParams.getRangeEnd() != null && searchEventParams.getRangeStart() != null) {
            if (searchEventParams.getRangeEnd().isBefore(searchEventParams.getRangeStart())) {
                throw new IncorrectParameterException("End date must be after start date");
            }
        }

        addStats(request);

        Pageable pageable = PageRequest.of(searchEventParams.getFrom() / searchEventParams.getSize(), searchEventParams.getSize());
        Specification<Event> specification = Specification.where(null);

        if (searchEventParams.getText() != null) {
            String text = searchEventParams.getText().toLowerCase();
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text + "%")));
        }

        if (searchEventParams.getCategories() != null && !searchEventParams.getCategories().isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(searchEventParams.getCategories()));
        }

        LocalDateTime startDate = Objects.requireNonNullElse(searchEventParams.getRangeStart(), LocalDateTime.now());
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDate));

        if (searchEventParams.getRangeEnd() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), searchEventParams.getRangeEnd()));
        }

        if (searchEventParams.getOnlyAvailable() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), EventState.PUBLISHED));

        List<Event> events = eventRepository.findAll(specification, pageable).getContent();
        List<EventShortDto> result = events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        Map<Long, Long> statsMap = getAllEventViews(events);

        for (EventShortDto eventDto : result) {
            Long views = statsMap.getOrDefault(eventDto.getId(), 0L);
            eventDto.setViews(views);
        }

        return result;
    }

    @Override
    public List<EventFullDto> getAllEventsAdmin(SearchEventParamsAdmin searchEventParamsAdmin) {
        PageRequest pageRequest = PageRequest.of(searchEventParamsAdmin.getFrom() / searchEventParamsAdmin.getSize(),
                searchEventParamsAdmin.getSize());
        Specification<Event> specification = Specification.where(null);

        List<Long> users = searchEventParamsAdmin.getUsers();
        List<String> states = searchEventParamsAdmin.getStates();
        List<Long> categories = searchEventParamsAdmin.getCategories();
        LocalDateTime rangeEnd = searchEventParamsAdmin.getRangeEnd();
        LocalDateTime rangeStart = searchEventParamsAdmin.getRangeStart();

        if (users != null && !users.isEmpty()) {
            specification.and((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(users));
        }

        if (states != null && !states.isEmpty()) {
            specification.and((root, query, criteriaBuilder) ->
                    root.get("state").as(String.class).in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }

        if (rangeEnd != null) {
            specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        if (rangeStart != null) {
            specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        Page<Event> events = eventRepository.findAll(specification, pageRequest);
        List<EventFullDto> result = events.getContent().stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

        Map<Long, List<Request>> confirmedRequestsMap = getConfirmedRequests(events.toList());
        for (EventFullDto eventFullDto : result) {
            List<Request> requests = confirmedRequestsMap.getOrDefault(eventFullDto.getId(), List.of());
            eventFullDto.setConfirmedRequests(requests.size());
        }

        return result;
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        findUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        return eventRepository.findAll(pageRequest).getContent().stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        return EventMapper.toEventFullDto(findEventByInitiatorAndId(userId, eventId));
    }

    @Override
    public List<ParticipationRequestDto> getAllParticipationRequestsByEventIdAndOwnerId(Long userId, Long eventId) {
        findEventByInitiatorAndId(userId, eventId);
        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventByUserIdAndEventId(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        Event event = findEventByInitiatorAndId(userId, eventId);
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event is published, can not be updated");
        }
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("User with id=" + userId + " is not the event initiator");
        }

        Event update = updateCommon(event, updateRequest);
        boolean isUpdated = false;
        if (update == null) {
            update = event;
        } else {
            isUpdated = true;
        }

        LocalDateTime date = updateRequest.getEventDate();
        if (date != null) {
            validateDate(date);
            update.setEventDate(date);
            isUpdated = true;
        }

        EventUserState stateAction = updateRequest.getStateAction();
        if (stateAction != null) {
            switch (stateAction) {
                case SEND_TO_REVIEW:
                    update.setState(EventState.PENDING);
                    isUpdated = true;
                    break;
                case CANCEL_REVIEW:
                    update.setState(EventState.CANCELED);
                    isUpdated = true;
                    break;
            }
        }

        Event updatedEvent = null;
        if (isUpdated) {
            updatedEvent = eventRepository.save(update);
        }

        return updatedEvent != null ? EventMapper.toEventFullDto(updatedEvent) : null;
    }

    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        Event event = findEvent(eventId);
        if (event.getState().equals(EventState.PUBLISHED) || event.getState().equals(EventState.CANCELED)) {
            throw new ConflictException("Only pending events may be updated");
        }

        boolean isUpdated = false;
        Event update = updateCommon(event, updateRequest);
        if (update == null) {
            update = event;
        } else {
            isUpdated = true;
        }

        LocalDateTime date = updateRequest.getEventDate();
        if (date != null) {
            if (date.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new IncorrectParameterException("Updated event date must not be sooner than 1 hour from publication");
            }
            update.setEventDate(date);
            isUpdated = true;
        }

        EventAdminState stateAction = updateRequest.getStateAction();
        if (stateAction != null) {
            switch (stateAction) {
                case PUBLISH_EVENT:
                    update.setState(EventState.PUBLISHED);
                    isUpdated = true;
                    break;
                case REJECT_EVENT:
                    update.setState(EventState.CANCELED);
                    isUpdated = true;
                    break;
            }
        }

        Event updatedEvent = null;
        if (isUpdated) {
            updatedEvent = eventRepository.save(update);
        }

        return updatedEvent != null ? EventMapper.toEventFullDto(updatedEvent) : null;
    }

    @Override
    public EventRequestStatusUpdateResponse updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        Event event = findEventByInitiatorAndId(userId, eventId);

        if (event.isRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Event does not require requests to be moderated");
        }

        RequestStatus status = updateRequest.getStatus();
        int confirmedCount = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        switch (status) {
            case CONFIRMED:
                if (event.getParticipantLimit() == confirmedCount) {
                    throw new ConflictException("Participant limit is exceeded");
                }
                EventStatusUpdateDto updateDto = confirmStatus(event,
                        EventStatusUpdateDto.builder()
                                .requestIds(new ArrayList<>(updateRequest.getRequestIds()))
                                .build(),
                        RequestStatus.CONFIRMED,
                        confirmedCount);
                List<Request> confirmed = requestRepository.findAllById(updateDto.getProcessedIds());
                List<Request> rejected = new ArrayList<>();
                if (!updateDto.getRequestIds().isEmpty()) {
                    List<Long> ids = updateDto.getRequestIds();
                    rejected = rejectRequests(ids, eventId);
                }

                return EventRequestStatusUpdateResponse.builder()
                        .confirmedRequests(confirmed.stream()
                                .map(RequestMapper::toParticipationRequestDto)
                                .collect(Collectors.toList()))
                        .rejectedRequests(rejected.stream()
                                .map(RequestMapper::toParticipationRequestDto)
                                .collect(Collectors.toList()))
                        .build();
            case REJECTED:
                if (event.getParticipantLimit() == confirmedCount) {
                    throw new ConflictException("Participant limit is exceeded");
                }

                EventStatusUpdateDto rejectDto = confirmStatus(event,
                        EventStatusUpdateDto.builder()
                                .requestIds(new ArrayList<>(updateRequest.getRequestIds()))
                                .build(),
                        RequestStatus.REJECTED,
                        confirmedCount);

                List<Request> requests = requestRepository.findAllById(rejectDto.getProcessedIds());

                return EventRequestStatusUpdateResponse.builder()
                        .rejectedRequests(requests.stream()
                                .map(RequestMapper::toParticipationRequestDto)
                                .collect(Collectors.toList()))
                        .build();
            default:
                throw new IncorrectParameterException("Invalid status: " + status);
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("No user found with id=" + userId));
    }

    private Category findCategory(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("No category found with id=" + catId));
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("No event found with id=" + eventId));
    }

    private List<Request> findRequests(Long eventId, List<Long> requestIds) {
        findEvent(eventId);
        return requestRepository.findByEventIdAndIdIn(eventId, requestIds).orElseThrow(() ->
                new NotFoundException("No requests found with id=" + requestIds));
    }

    private Event findEventByInitiatorAndId(Long userId, Long eventId) {
        findUser(userId);
        return eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(() ->
                new NotFoundException("No event found with id=" + eventId + " and initiator id=" + userId));
    }

    private void validateDate(LocalDateTime dateTime) {
        if (dateTime.isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new IncorrectParameterException("Event must start later than 2 hours from now");
        }
    }

    private void addStats(HttpServletRequest request) {
        client.postHit(request);
    }

    private Map<Long, Long> getAllEventViews(List<Event> events) {
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());
        List<LocalDateTime> startDates = events.stream()
                .map(Event::getCreatedOn)
                .collect(Collectors.toList());
        LocalDateTime earliestDate = startDates.stream()
                .min(LocalDateTime::compareTo)
                .orElse(null);
        Map<Long, Long> statsMap = new HashMap<>();

        if (earliestDate != null) {
            ResponseEntity<Object> response = client.getStats(earliestDate, LocalDateTime.now(), uris, true);
            List<StatsResponseDto> statsList = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
            });

            statsMap = statsList.stream()
                    .filter(statsResponseDto -> statsResponseDto.getUri().startsWith("/events/"))
                    .collect(Collectors.toMap(
                            statsResponseDto -> Long.parseLong(statsResponseDto.getUri().substring("/events/".length())),
                            StatsResponseDto::getHits
                    ));
        }
        return statsMap;
    }

    private Map<Long, List<Request>> getConfirmedRequests(List<Event> events) {
        List<Request> requests = requestRepository.findAllByEventIdInAndStatus(events.stream()
                .map(Event::getId)
                .collect(Collectors.toList()),
                RequestStatus.CONFIRMED);
        return requests.stream()
                .collect(Collectors.groupingBy(r -> r.getEvent().getId()));
    }

    private Event updateCommon(Event event, UpdateEventRequest update) {
        boolean isUpdated = false;

        String annotation = update.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            event.setAnnotation(annotation);
            isUpdated = true;
        }

        Long categoryId = update.getCategory();
        if (categoryId != null) {
            event.setCategory(findCategory(categoryId));
            isUpdated = true;
        }

        String description = update.getDescription();
        if (description != null && !description.isBlank()) {
            event.setDescription(description);
            isUpdated = true;
        }

        LocationDto location = update.getLocation();
        if (location != null) {
            event.setLocation(LocationMapper.toLocation(location));
            isUpdated = true;
        }

        Integer participantLimit = update.getParticipantLimit();
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
            isUpdated = true;
        }

        Boolean paid = update.getPaid();
        if (paid != null) {
            event.setPaid(paid);
            isUpdated = true;
        }

        Boolean moderated = update.getRequestModeration();
        if (moderated != null) {
            event.setRequestModeration(moderated);
            isUpdated = true;
        }

        String title = update.getTitle();
        if (title != null && !title.isBlank()) {
            event.setTitle(title);
            isUpdated = true;
        }

        if (!isUpdated) {
            event = null;
        }

        return event;
    }

    private EventStatusUpdateDto confirmStatus(Event event,
                                               EventStatusUpdateDto updateDto,
                                               RequestStatus status,
                                               int confirmedCount) {
        int freeSlots = event.getParticipantLimit() - confirmedCount;
        List<Long> requestIds = updateDto.getRequestIds();
        List<Long> processedIds = new ArrayList<>();
        List<Request> requestList = findRequests(event.getId(), requestIds);
        List<Request> confirmedList = new ArrayList<>();

        for (Request request : requestList) {
            if (freeSlots == 0) {
                break;
            }
            request.setStatus(status);
            confirmedList.add(request);
            processedIds.add(request.getId());
            freeSlots--;
        }

        requestRepository.saveAll(confirmedList);
        updateDto.setProcessedIds(processedIds);
        return updateDto;
    }

    private List<Request> rejectRequests(List<Long> ids, Long eventId) {
        List<Request> rejected = new ArrayList<>();
        List<Request> requests = findRequests(eventId, ids);

        for (Request request : requests) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                break;
            }
            request.setStatus(RequestStatus.REJECTED);
            rejected.add(request);
        }
        return requestRepository.saveAll(rejected);
    }
}
