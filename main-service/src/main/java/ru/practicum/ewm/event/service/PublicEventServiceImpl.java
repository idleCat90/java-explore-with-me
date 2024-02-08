package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.state.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.stats.service.StatsService;
import ru.practicum.ewm.utility.Util;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PublicEventServiceImpl implements PublicEventService{
    private final EventRepository eventRepository;
    private final StatsService statsService;

    @Override
    public List<EventShortDto> findEvents(String text,
                                          List<Long> categories,
                                          Boolean paid,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          Boolean onlyAvailable,
                                          String sort,
                                          Integer from,
                                          Integer size,
                                          HttpServletRequest request) {
        log.debug("Method call: findEvents()");
        sort = (sort != null && sort.equals("EVENT_DATE")) ? "eventDate" : "id";
        List<Event> events = eventRepository.findAllPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, Util.getPageRequestDesc(sort, from, size));

        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);
        Map<Long, Long> views = statsService.getViews(events);

        List<EventShortDto> result = new ArrayList<>();
        events.forEach(event -> {
            event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));
            event.setViews(views.getOrDefault(event.getId(), 0L));
            result.add(EventMapper.toEventShortDto(event));
        });

        statsService.postHit(request);
        log.debug("Returned: event list, size={}", result.size());
        return result;
    }

    @Override
    public EventFullDto getEvent(Long id, HttpServletRequest request) {
        log.debug("Method call: getEvent(), id={}", id);
        Event event = eventRepository.findById(id).orElseThrow(() -> {
            log.error("Event with id={} does not exist", id);
            return new NotFoundException("No event found with id=" + id);
        });
        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.error("Event with id={} has not been published", id);
            throw new NotFoundException("Event is not published");
        }
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(List.of(event));
        Map<Long, Long> views = statsService.getViews(List.of(event));
        statsService.postHit(request);
        event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));
        event.setViews(views.getOrDefault(event.getId(), 0L));

        EventFullDto result = EventMapper.toEventFullDto(event);
        log.debug("Returned: result={}", result);
        return result;
    }
}
