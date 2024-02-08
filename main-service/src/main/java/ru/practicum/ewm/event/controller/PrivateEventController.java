package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.service.PrivateEventService;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.utility.Util;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
@Slf4j
public class PrivateEventController {
    private final PrivateEventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EventFullDto> createEvent(@PathVariable Long userId,
                                                    @RequestBody @Validated EventRequestDto eventRequestDto) {
        log.debug("POST /users/{}/events with body={}", userId, eventRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.createEvent(userId, eventRequestDto));
    }

    @GetMapping("/{userId}/events")
    public ResponseEntity<List<EventFullDto>> getEventsByUserId(@PathVariable Long userId,
                                                                @RequestParam(defaultValue = "0")
                                                                @PositiveOrZero Integer from,
                                                                @RequestParam(defaultValue = "10")
                                                                @Positive Integer size) {
        log.debug("GET /users/{}/events", userId);
        return ResponseEntity.ok(eventService.readEventsByUserId(userId, from, size));
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> patchEvent(@PathVariable Long userId,
                                                   @PathVariable Long eventId,
                                                   @RequestBody @Validated(Util.Marker.onUpdate.class)
                                                   UpdateEventUserRequest update) {
        log.debug("PATCH /users/{}/events/{} with body={}", userId, eventId, update);
        return ResponseEntity.ok(eventService.updateEvent(userId, eventId, update));
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> getEventByUserIdAndEventId(@PathVariable Long userId,
                                                                   @PathVariable Long eventId) {
        log.debug("GET /users/{}/events/{}", userId, eventId);
        return ResponseEntity.ok(eventService.readEventByUserIdAndEventId(userId, eventId));
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> patchRequest(@PathVariable Long userId,
                                                                       @PathVariable Long eventId,
                                                                       @RequestBody
                                                                       EventRequestStatusUpdateRequest request) {
        log.debug("PATCH /users/{}/events/{}/requests", userId, eventId);
        return ResponseEntity.ok(eventService.updateRequest(userId, eventId, request));
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<RequestDto>> getRequestsByUserIdAndEventId(@PathVariable Long userId,
                                                                          @PathVariable Long eventId) {
        log.debug("GET /users/{}/events/{}/requests", userId, eventId);
        return ResponseEntity.ok(eventService.readRequestByUserIdAndByEventId(userId, eventId));
    }
}
