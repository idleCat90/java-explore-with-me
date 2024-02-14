package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentMapper;
import ru.practicum.ewm.comment.dto.CommentRequestDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.state.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.utility.Util;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class PrivateCommentServiceImpl implements PrivateCommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long eventId, CommentRequestDto commentRequestDto) {
        log.debug("Method call: createComment(), request={}", commentRequestDto);
        Comment comment = CommentMapper.toComment(commentRequestDto);
        User author = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User with id={} does not exist", userId);
            return new NotFoundException("No user found with id=" + userId);
        });
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with id={} does not exist", eventId);
            return new NotFoundException("No event found with id=" + eventId);
        });
        if ((!event.getState().equals(EventState.PUBLISHED))) {
            log.error("Event with id={} is not published yet", eventId);
            throw new ConflictException("Event is not PUBLISHED");
        }
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setCreatedOn(LocalDateTime.now());

        CommentDto result = CommentMapper.toCommentDto(commentRepository.save(comment));
        log.debug("Returned: created comment={}", result);
        return result;
    }

    @Override
    public List<CommentDto> findAllCommentsByUserId(Long userId, Integer from, Integer size) {
        log.debug("Method call: findAllCommentsByUserId(), id={}", userId);
        if (!userRepository.existsById(userId)) {
            log.error("User with id={} does not exist", userId);
            throw new NotFoundException("No user found with id=" + userId);
        }
        Pageable pageable = Util.getPageRequestAsc("createdOn", from, size);
        List<CommentDto> result = CommentMapper.toCommentDtoList(commentRepository.findAllByAuthorId(userId, pageable));
        log.debug("Returned: comment list, size={}", result.size());
        return result;
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, CommentRequestDto commentRequestDto) {
        log.debug("Method call: updateComment(), userId={}, commentId={}, dto={}", userId, commentId, commentRequestDto);
        if (!userRepository.existsById(userId)) {
            log.error("User with id={} does not exist", userId);
            throw new NotFoundException("No user found with id=" + userId);
        }
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.error("Comment with id={} does not exist", commentId);
            return new NotFoundException("No comment found with id=" + commentId);
        });
        if (!comment.getAuthor().getId().equals(userId)) {
            log.error("User with id={} is not author of comment with id={}", userId, commentId);
            throw new ConflictException("User is not comment author");
        }
        comment.setText(commentRequestDto.getText());
        comment.setUpdatedOn(LocalDateTime.now());
        CommentDto result = CommentMapper.toCommentDto(comment);
        log.debug("Returned: updated comment, dto={}", result);
        return result;
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        log.debug("Method call: deleteComment(), userId={}, commentId={}", userId, commentId);
        if (!userRepository.existsById(userId)) {
            log.error("User with id={} does not exist", userId);
            throw new NotFoundException("No user found with id=" + userId);
        }
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.error("Comment with id={} does not exist", commentId);
            return new NotFoundException("No comment found with id=" + commentId);
        });
        if (!comment.getAuthor().getId().equals(userId)) {
            log.error("User with id={} is not author of comment with id={}", userId, commentId);
            throw new ConflictException("User is not comment author");
        }
        commentRepository.deleteById(commentId);
        log.debug("Deleted comment, id={}", commentId);
    }
}
