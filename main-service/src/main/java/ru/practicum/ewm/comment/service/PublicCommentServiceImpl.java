package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentMapper;
import ru.practicum.ewm.comment.dto.CommentShortDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.utility.Util;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class PublicCommentServiceImpl implements PublicCommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto findCommentById(Long commentId) {
        log.debug("Method call: findCommentById(), id={}", commentId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.error("Comment with id={} does not exist", commentId);
            return new NotFoundException("No comment found with id=" + commentId);
        });
        CommentDto result = CommentMapper.toCommentDto(comment);
        log.debug("Returned: dto={}", comment);
        return result;
    }

    @Override
    public List<CommentShortDto> findCommentsByEventId(Long eventId, Integer from, Integer size) {
        log.debug("Method call: findCommentsByEventId(), id={}", eventId);
        if (!eventRepository.existsById(eventId)) {
            log.error("Event with id={} does not exist", eventId);
            throw new NotFoundException("No event found with id=" + eventId);
        }
        Pageable pageable = Util.getPageRequestAsc("createdOn", from, size);
        List<CommentShortDto> result = CommentMapper
                .toCommentShortDtoList(commentRepository.findAllByEventId(eventId, pageable));
        log.debug("Returned: dtoList size={}", result.size());
        return result;
    }
}
