package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentRequestDto;
import ru.practicum.ewm.comment.service.PrivateCommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
@Slf4j
public class PrivateCommentController {
    private final PrivateCommentService commentService;

    @PostMapping("/{userId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentDto> createComment(@PathVariable Long userId,
                                                    @RequestParam Long eventId,
                                                    @RequestBody @Validated CommentRequestDto commentRequestDto) {
        log.debug("POST /users/{}/comments?eventId={}", userId, eventId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.createComment(userId, eventId, commentRequestDto));
    }

    @GetMapping("/{userId}/comments")
    public ResponseEntity<List<CommentDto>> getAllCommentsByUserId(@PathVariable Long userId,
                                                                   @RequestParam(defaultValue = "0") @PositiveOrZero
                                                                   Integer from,
                                                                   @RequestParam(defaultValue = "10") @Positive
                                                                   Integer size) {
        log.debug("GET /users/{}/comments", userId);
        return ResponseEntity.ok(commentService.findAllCommentsByUserId(userId, from, size));
    }

    @PatchMapping("/{userId}/comments/{commId}")
    public ResponseEntity<CommentDto> patchComment(@PathVariable Long userId,
                                                   @PathVariable Long commId,
                                                   @RequestBody @Validated CommentRequestDto commentRequestDto) {
        log.debug("PATCH /users/{}/comments/{}", userId, commId);
        return ResponseEntity.ok(commentService.updateComment(userId, commId, commentRequestDto));
    }
}
