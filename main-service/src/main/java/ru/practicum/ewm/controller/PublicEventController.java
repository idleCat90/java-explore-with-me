package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.SearchEventParams;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@Slf4j
@Validated
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> searchEvents(@RequestBody @Valid SearchEventParams params,
                                         HttpServletRequest request) {
        log.info("GET \"/events\" params={}", params);
        return eventService.getAllEventsPublic(params, request);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable @Min(1) Long eventId,
                                     HttpServletRequest request) {
        log.info("GET \"/events/{}\"", eventId);
        return eventService.getEventById(eventId, request);
    }
}
