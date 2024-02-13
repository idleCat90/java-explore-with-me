package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.model.state.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest update);

    List<EventFullDto> findEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);
}
