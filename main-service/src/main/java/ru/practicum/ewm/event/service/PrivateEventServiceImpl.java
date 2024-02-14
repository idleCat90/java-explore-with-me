package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.comment.service.CommentCountService;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.state.EventState;
import ru.practicum.ewm.event.model.state.PrivateStateAction;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.IncorrectParameterException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.location.dto.LocationMapper;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.repository.LocationRepository;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.stats.service.StatsService;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.utility.Util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final StatsService statsService;
    private final CommentCountService commentCountService;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, EventRequestDto eventRequestDto) {
        log.debug("Method call: createEvent(), userId={}, dto={}", userId, eventRequestDto);
        Event event = EventMapper.toEvent(eventRequestDto);
        event.setCategory(categoryRepository.findById(event.getCategory().getId()).orElseThrow(() -> {
            log.error("No category found");
            return new NotFoundException("Category not found");
        }));
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setLocation(locationRepository.save(event.getLocation()));
        event.setInitiator(userRepository.findById(userId).orElseThrow(() -> {
            log.error("User with id={} does not exist", userId);
            return new NotFoundException("No user found with id=" + userId);
        }));

        log.debug("Event created: {}", event);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> readEventsByUserId(Long userId, Integer from, Integer size) {
        log.debug("Method call: readEventsByUserId(), userId={}", userId);
        if (!userRepository.existsById(userId)) {
            log.error("User with id={} does not exist", userId);
            throw new NotFoundException("No user found with id=" + userId);
        }
        List<Event> events = eventRepository.findAllByInitiatorId(userId, Util.getPageRequestAsc("id", from, size));
        if (events.isEmpty()) {
            return List.of();
        }
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> commentCount = commentCountService.getCommentCount(events);

        for (Event event: events) {
            event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));
            event.setViews(views.getOrDefault(event.getId(), 0L));
            event.setCommentCount(commentCount.getOrDefault(event.getId(), 0L));
        }

        log.debug("Returned: events={}", events);
        return EventMapper.toEventFullDtoList(events);
    }

    @Override
    public EventFullDto readEventByUserIdAndEventId(Long userId, Long eventId) {
        log.debug("Method call: readEventByUserIdAndEventId(), userId={}, eventId={}", userId, eventId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> {
            log.error("Event with id={} and initiator id={} does not exist", eventId, userId);
            return new NotFoundException("Event not found");
        });
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(List.of(event));
        Map<Long, Long> views = statsService.getViews(List.of(event));
        Map<Long, Long> commentCount = commentCountService.getCommentCount(List.of(event));
        event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));
        event.setViews(views.getOrDefault(event.getId(), 0L));
        event.setCommentCount(commentCount.getOrDefault(event.getId(), 0L));

        log.debug("Event found: {}", event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        log.debug("Method call: updateEvent()");
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> {
            log.error("Event does not exist");
            return new NotFoundException("Event not found");
        });
        if (!event.getInitiator().getId().equals(userId)) {
            log.error("Unauthorised access");
            throw new ConflictException("Only the event owner may update");
        }
        if (event.getState().equals(EventState.PUBLISHED)) {
            log.error("PUBLISHED events can not be edited");
            throw new ConflictException("Event is already published");
        }
        LocalDateTime eventTime = updateRequest.getEventDate();
        if (eventTime != null) {
            if (eventTime.isBefore(LocalDateTime.now().plusHours(2L))) {
                log.error("Event time less than 2 hours from now");
                throw new IncorrectParameterException("Event time can not be less than 2 hours from now");
            }
            event.setEventDate(eventTime);
        }
        PrivateStateAction stateAction = updateRequest.getStateAction();
        if (stateAction != null) {
            if (stateAction.equals(PrivateStateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            }
            if (stateAction.equals(PrivateStateAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            }
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getLocation() != null) {
            event.setLocation(findLocation(updateRequest.getLocation())
                    .orElse(saveLocation(updateRequest.getLocation())));
        }
        if (updateRequest.getCategory() != null) {
            Long catId = updateRequest.getCategory();
            event.setCategory(categoryRepository.findById(catId).orElseThrow(() -> {
                log.error("Category with id={} does not exist", catId);
                return new NotFoundException("Category not found with id=" + catId);
            }));
        }

        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(List.of(event));
        Map<Long, Long> views = statsService.getViews(List.of(event));
        Map<Long, Long> commentCount = commentCountService.getCommentCount(List.of(event));
        event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));
        event.setViews(views.getOrDefault(event.getId(), 0L));
        event.setCommentCount(commentCount.getOrDefault(event.getId(), 0L));

        EventFullDto result = EventMapper.toEventFullDto(event);
        log.debug("Returned: saved event={}", result);
        return result;
    }

    @Override
    public List<RequestDto> readRequestByUserIdAndByEventId(Long userId, Long eventId) {
        log.debug("Method call: readRequestByUserIdAndByEventId(), userId={}, eventId={}", userId, eventId);
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            log.error("Unauthorised access");
            throw new ConflictException("User is not event initiator");
        }

        List<Request> requests = requestRepository.findAllByEventId(eventId);
        log.debug("Returned: requests={}", requests);
        return RequestMapper.toRequestDtoList(requests);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId,
                                                        EventRequestStatusUpdateRequest update) {
        log.debug("Method call: updateRequest(), update={}", update);
        if (!userRepository.existsById(userId)) {
            log.error("User with id={} does not exist", userId);
            throw new NotFoundException("User not found with id=" + userId);
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with id={} does not exist", eventId);
            return new NotFoundException("Event not found with id=" + eventId);
        });
        if (!event.getInitiator().getId().equals(userId)) {
            log.error("Unauthorised access");
            throw new ConflictException("User is not event initiator");
        }
        int confirmedRequests = statsService.getConfirmedRequests(List.of(event)).values().size();
        if (event.getParticipantLimit() != 0 && confirmedRequests >= event.getParticipantLimit()) {
            log.error("Participant limit exceeded");
            throw new ConflictException("Participant limit exceeded");
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        update.getRequestIds().forEach(id -> {
            Request request = requestRepository.findById(id).orElseThrow(() -> {
                log.error("Request with id={} does not exist", id);
                return new NotFoundException("Request not found with id=" + id);
            });
            if (update.getStatus().equals(RequestStatus.CONFIRMED)) {
                request.setStatus(RequestStatus.CONFIRMED);
                result.getConfirmedRequests().add(RequestMapper.toRequestDto(request));
            }
            if (update.getStatus().equals(RequestStatus.REJECTED)) {
                request.setStatus(RequestStatus.REJECTED);
                result.getRejectedRequests().add(RequestMapper.toRequestDto(request));
            }
        });

        log.debug("Returned: result={}", result);
        return result;
    }

    private Optional<Location> findLocation(LocationDto locationDto) {
        log.debug("Method call: findLocation(), dto={}", locationDto);
        return locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon());
    }

    @Transactional
    private Location saveLocation(LocationDto locationDto) {
        log.debug("Method call: saveLocation(), dto={}", locationDto);
        return locationRepository.save(LocationMapper.toLocation(locationDto));
    }
}
