package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.service.AdminCommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminCommentController {
    private final AdminCommentService commentService;

    @GetMapping("/comments/search")
    public ResponseEntity<List<CommentDto>> searchComments(@RequestParam String text,
                                                           @RequestParam(defaultValue = "0") @PositiveOrZero
                                                           Integer from,
                                                           @RequestParam(defaultValue = "10") @Positive
                                                           Integer size) {
        log.debug("GET /admin/comments/search");
        return ResponseEntity.ok(commentService.search(text, from, size));
    }

    @DeleteMapping("/comments/{commId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteCommentById(@PathVariable Long commId) {
        log.debug("DELETE /admin/comments/{}", commId);
        commentService.deleteComment(commId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("Comment deleted, id=" + commId);
    }
}
