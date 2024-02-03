package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto addNewEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);

    List<EventShortDto> getAllEventsPublic(SearchEventParams searchEventParams, HttpServletRequest request);

    List<EventFullDto> getAllEventsAdmin(SearchEventParamsAdmin searchEventParamsAdmin);

    List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    List<ParticipationRequestDto> getAllParticipationRequestsByEventIdAndOwnerId(Long userId, Long eventId);

    EventFullDto updateEventByUserIdAndEventId(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    EventFullDto updateEventAdmin(Long eventId, SearchEventParamsAdmin searchEventParamsAdmin);

    EventRequestStatusUpdateResponse updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest);


}
