package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.service.PublicEventService;
import ru.practicum.ewm.utility.Util;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/events")
@Slf4j
public class PublicEventController {
    private final PublicEventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> findEvents(@RequestParam(required = false) String text,
                                                          @RequestParam(required = false) List<@Positive Long> categories,
                                                          @RequestParam(required = false) Boolean paid,
                                                          @RequestParam(required = false)
                                                          @DateTimeFormat(pattern = Util.DATE_TIME_FORMAT)
                                                              LocalDateTime rangeStart,
                                                          @RequestParam(required = false)
                                                          @DateTimeFormat(pattern = Util.DATE_TIME_FORMAT)
                                                              LocalDateTime rangeEnd,
                                                          @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                          @RequestParam(required = false) String sort,
                                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                          @RequestParam(defaultValue = "10") @Positive Integer size,
                                                          HttpServletRequest request) {
        log.debug("GET /events, rangeStart={}", rangeStart);
        return ResponseEntity.ok(eventService.findEvents(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable Long id, HttpServletRequest request) {
        log.debug("GET /events/{}", id);
        return ResponseEntity.ok(eventService.getEvent(id, request));
    }
}
