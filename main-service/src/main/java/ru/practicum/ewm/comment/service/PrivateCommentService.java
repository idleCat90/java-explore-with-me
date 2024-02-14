package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentRequestDto;

import java.util.List;

public interface PrivateCommentService {
    CommentDto createComment(Long userId, Long eventId, CommentRequestDto commentRequestDto);

    List<CommentDto> findAllCommentsByUserId(Long userId, Integer from, Integer size);

    CommentDto updateComment(Long userId, Long commentId, CommentRequestDto commentRequestDto);

    void deleteComment(Long userId, Long commentId);

}
