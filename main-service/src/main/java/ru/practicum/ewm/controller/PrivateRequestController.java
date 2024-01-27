package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.RequestService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
public class PrivateRequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable @Min(1) Long userId,
                                              @RequestParam(name = "eventId") @Min(1) Long eventId) {
        log.info("POST \"/users/{}/requests?eventId={}\"", userId, eventId);
        return requestService.addNewRequest(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getRequests(@PathVariable @Min(1) Long userId) {
        log.info("GET \"/users/{}/requests\"", userId);
        return requestService.getRequestsByUserId(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @Min(1) Long userId,
                                                 @PathVariable @Min(1) Long requestId) {
        log.info("PATCH \"/users/{}/requests/{}/cancel\"", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}
