package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.model.state.EventState;
import ru.practicum.ewm.event.service.AdminEventService;
import ru.practicum.ewm.utility.Util;
import ru.practicum.ewm.utility.Util.Marker.OnUpdate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Validated
@RequiredArgsConstructor
@Slf4j
public class AdminEventController {
    private final AdminEventService eventService;

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> patchEvent(@PathVariable Long eventId,
                                                   @RequestBody @Validated({OnUpdate.class})
                                                   UpdateEventAdminRequest update) {
        log.debug("PATCH /admin/events/{} with body={}", eventId, update);
        return ResponseEntity.ok(eventService.updateEvent(eventId, update));
    }

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(@RequestParam(required = false) List<Long> users,
                                                        @RequestParam(required = false) List<EventState> states,
                                                        @RequestParam(required = false) List<Long> categories,
                                                        @RequestParam(required = false)
                                                        @DateTimeFormat(pattern = Util.DATE_TIME_FORMAT)
                                                        LocalDateTime rangeStart,
                                                        @RequestParam(required = false)
                                                        @DateTimeFormat(pattern = Util.DATE_TIME_FORMAT)
                                                        LocalDateTime rangeEnd,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.debug("GET /admin/events");
        return ResponseEntity.ok(eventService.findEvents(users, states, categories, rangeStart, rangeEnd, from, size));
    }
}
