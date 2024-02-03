package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.SearchEventParamsAdmin;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@Slf4j
@Validated
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> searchEvents(@RequestBody @Valid SearchEventParamsAdmin params) {
        log.info("GET \"/admin/events\" body={}", params);
        return eventService.getAllEventsAdmin(params);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable @Min(1) Long eventId,
                                    @RequestBody @Valid UpdateEventAdminRequest update) {
        log.info("PATCH \"/admin/events/{}\" body={}", eventId, update);
        return eventService.updateEventAdmin(eventId, update);
    }
}
