package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentShortDto;
import ru.practicum.ewm.comment.service.PublicCommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
public class PublicCommentController {
    private final PublicCommentService commentService;

    @GetMapping("/comments/{commId}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long commId) {
        log.debug("GET /comments/{}", commId);
        return ResponseEntity.ok(commentService.findCommentById(commId));
    }

    @GetMapping("/events/{eventId}/comments")
    public ResponseEntity<List<CommentShortDto>> getCommentsByEventId(@PathVariable Long eventId,
                                                                      @RequestParam(defaultValue = "0")
                                                                      @PositiveOrZero
                                                                      Integer from,
                                                                      @RequestParam(defaultValue = "10")
                                                                      @Positive
                                                                      Integer size) {
        log.debug("GET /events/{}/comments", eventId);
        return ResponseEntity.ok(commentService.findCommentsByEventId(eventId, from, size));
    }
}
