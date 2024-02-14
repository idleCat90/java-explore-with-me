package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentShortDto;

import java.util.List;

public interface PublicCommentService {
    CommentDto findCommentById(Long commentId);

    List<CommentShortDto> findCommentsByEventId(Long eventId, Integer from, Integer size);

}
