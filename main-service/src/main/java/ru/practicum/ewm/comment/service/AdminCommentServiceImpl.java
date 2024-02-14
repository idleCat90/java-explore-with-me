package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentMapper;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.utility.Util;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {
    private final CommentRepository commentRepository;

    @Override
    public List<CommentDto> search(String text, Integer from, Integer size) {
        log.debug("Method call: search(), text={}", text);
        Pageable pageable = Util.getPageRequestAsc("createdOn", from, size);
        List<CommentDto> result = CommentMapper.toCommentDtoList(commentRepository.findAllByText(text, pageable));
        log.debug("Returned: comment list size={}", result.size());
        return result;
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        log.debug("Method call: deleteComment(), id={}", commentId);
        if (!commentRepository.existsById(commentId)) {
            log.error("Comment with id={} does not exist", commentId);
            throw new NotFoundException("No comment found with id=" + commentId);
        }
        commentRepository.deleteById(commentId);
        log.debug("Comment deleted, id={}", commentId);
    }
}
