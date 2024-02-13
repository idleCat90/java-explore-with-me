package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.request.dto.RequestDto;

import java.util.List;

public interface PrivateEventService {
    EventFullDto createEvent(Long userId, EventRequestDto eventRequestDto);

    List<EventFullDto> readEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto readEventByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    List<RequestDto> readRequestByUserIdAndByEventId(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest update);
}
