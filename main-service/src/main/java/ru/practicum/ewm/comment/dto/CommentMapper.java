package ru.practicum.ewm.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.user.dto.mapper.UserMapper;
import ru.practicum.ewm.utility.Util;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Util.DATE_TIME_FORMAT);

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .text(comment.getText())
                .event(EventMapper.toEventCommentDto(comment.getEvent()))
                .createdOn(comment.getCreatedOn().format(DATE_TIME_FORMATTER))
                .updatedOn((comment.getUpdatedOn() != null)
                        ? comment.getUpdatedOn().format(DATE_TIME_FORMATTER)
                        : null)
                .build();
    }

    public List<CommentDto> toCommentDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public CommentShortDto toCommentShortDto(Comment comment) {
        return CommentShortDto.builder()
                .id(comment.getId())
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .text(comment.getText())
                .createdOn(comment.getCreatedOn().format(DATE_TIME_FORMATTER))
                .build();
    }

    public List<CommentShortDto> toCommentShortDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentShortDto)
                .collect(Collectors.toList());
    }

    public Comment toComment(CommentRequestDto commentRequestDto) {
        return Comment.builder()
                .text(commentRequestDto.getText())
                .build();
    }
}
