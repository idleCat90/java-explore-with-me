package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@Slf4j
@Validated
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAllEventsByUserId(@PathVariable @Min(1) Long userId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET \"/users/{}/events\" from={} size={}", userId, from, size);
        return eventService.getEventsByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable @Min(1) Long userId,
                                 @RequestBody @Valid NewEventDto newEventDto) {
        log.info("POST \"/users/{}/events\" body={}", userId, newEventDto);
        return eventService.addNewEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByOwnerIdAndEventId(@PathVariable @Min(1) Long userId,
                                                    @PathVariable @Min(1) Long eventId) {
        log.info("GET \"/users/{}/events/{}", userId, eventId);
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable @Min(1) Long userId,
                                    @PathVariable @Min(1) Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest update) {
        log.info("PATCH \"/users/{}/events/{}\" body={}", userId, eventId, update);
        return eventService.updateEventByUserIdAndEventId(userId, eventId, update);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getAllRequests(@PathVariable @Min(1) Long userId,
                                                        @PathVariable @Min(1) Long eventId) {
        log.info("GET \"/users/{}/events/{}/requests\"", userId, eventId);
        return eventService.getAllParticipationRequestsByEventIdAndOwnerId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResponse updateRequestStatus(@PathVariable @Min(1) Long userId,
                                                                @PathVariable @Min(1) Long eventId,
                                                                @RequestBody EventRequestStatusUpdateRequest update) {
        log.info("PATCH \"/users/{}/events/{}/requests\" body={}", userId, eventId, update);
        return eventService.updateRequestStatus(userId, eventId, update);
    }
}
