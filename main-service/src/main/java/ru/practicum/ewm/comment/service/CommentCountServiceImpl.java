package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentCountDto;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class CommentCountServiceImpl implements CommentCountService {
    private final CommentRepository commentRepository;

    @Override
    public Map<Long, Long> getCommentCount(Collection<Event> events) {
        log.debug("Method call: getCommentCount(), events size={}", events.size());
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        List<CommentCountDto> commentCount = commentRepository.getCommentCountByEventIds(eventIds);
        return commentCount.stream()
                .collect(Collectors.toMap(CommentCountDto::getEventId, CommentCountDto::getCommentCount));
    }
}
