package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDto;

import java.util.List;

public interface AdminCommentService {
    List<CommentDto> search(String text, Integer from, Integer size);

    void deleteComment(Long commentId);
}
