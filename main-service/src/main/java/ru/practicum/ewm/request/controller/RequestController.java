package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RequestDto> createRequest(@PathVariable Long userId,
                                                    @RequestParam Long eventId) {
        log.debug("POST /users/{}/requests?eventId={}", userId, eventId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(requestService.createRequest(userId, eventId));
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<RequestDto>> getRequests(@PathVariable Long userId) {
        log.debug("GET /users/{}/requests", userId);
        return ResponseEntity.ok(requestService.readAllRequests(userId));
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<RequestDto> cancelRequest(@PathVariable Long userId,
                                                    @PathVariable Long requestId) {
        log.debug("PATCH /users/{}/requests/{}/cancel", userId, requestId);
        return ResponseEntity.ok(requestService.cancelRequest(userId, requestId));
    }
}
