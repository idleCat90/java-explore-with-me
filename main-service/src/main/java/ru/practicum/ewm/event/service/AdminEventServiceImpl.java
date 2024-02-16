package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.comment.service.CommentCountService;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.state.AdminStateAction;
import ru.practicum.ewm.event.model.state.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.IncorrectParameterException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.location.dto.LocationMapper;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.repository.LocationRepository;
import ru.practicum.ewm.stats.service.StatsService;
import ru.practicum.ewm.utility.Util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final StatsService statsService;
    private final CommentCountService countService;

    @Override
    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest update) {
        log.debug("Method call: updateEvent(), update={}", update);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with id={} does not exist", eventId);
            return new NotFoundException("No event found with id=" + eventId);
        });
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1L))) {
            log.error("Event is already in progress or is over");
            throw new IncorrectParameterException("Event in progress or over");
        }
        if (update.getEventDate() != null) {
            if (update.getEventDate().isBefore(LocalDateTime.now().plusHours(1L))) {
                log.error("Event date is incorrect");
                throw new IncorrectParameterException("Incorrect event date");
            } else {
                event.setEventDate(update.getEventDate());
            }
        }
        if (update.getStateAction() != null) {
            if (!event.getState().equals(EventState.PENDING)) {
                log.error("Event with state={} can not be modified", event.getState());
                throw new ConflictException("Only pending events can be modified");
            }
            if (update.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
            if (update.getStateAction().equals(AdminStateAction.REJECT_EVENT)) {
                event.setState(EventState.CANCELED);
            }
        }
        if (update.getParticipantLimit() != null) {
            event.setParticipantLimit(update.getParticipantLimit());
        }
        if (update.getPaid() != null) {
            event.setPaid(update.getPaid());
        }
        if (update.getRequestModeration() != null) {
            event.setRequestModeration(update.getRequestModeration());
        }
        if (update.getTitle() != null) {
            event.setTitle(update.getTitle());
        }
        if (update.getAnnotation() != null) {
            event.setAnnotation(update.getAnnotation());
        }
        if (update.getDescription() != null) {
            event.setDescription(update.getDescription());
        }
        if (update.getLocation() != null) {
            event.setLocation(findLocation(update.getLocation())
                    .orElse(saveLocation(update.getLocation())));
        }
        if (update.getCategory() != null) {
            Long catId = update.getCategory();
            event.setCategory(categoryRepository.findById(catId).orElseThrow(() -> {
                log.error("Category with id={} does not exist", catId);
                return new NotFoundException("Category not found with id=" + catId);
            }));
        }

        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(List.of(event));
        Map<Long, Long> views = statsService.getViews(List.of(event));
        Map<Long, Long> commentCount = countService.getCommentCount(List.of(event));
        event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));
        event.setViews(views.getOrDefault(event.getId(), 0L));
        event.setCommentCount(commentCount.getOrDefault(event.getId(), 0L));

        EventFullDto result = EventMapper.toEventFullDto(event);
        log.debug("Returned: updated event={}", result);
        return result;
    }

    @Override
    public List<EventFullDto> findEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         Integer from, Integer size) {
        log.debug("Method call: findEvents()");
        List<Event> events = eventRepository.findAllAdmin(users, states, categories, rangeStart, rangeEnd,
                Util.getPageRequestAsc("id", from, size));
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> commentCount = countService.getCommentCount(events);
        for (Event event : events) {
            event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));
            event.setViews(views.getOrDefault(event.getId(), 0L));
            event.setCommentCount(commentCount.getOrDefault(event.getId(), 0L));
        }

        log.debug("Returned: events, size={}", events.size());
        return EventMapper.toEventFullDtoList(events);
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
