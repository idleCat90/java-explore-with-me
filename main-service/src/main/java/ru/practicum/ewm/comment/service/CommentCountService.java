package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.event.model.Event;

import java.util.Collection;
import java.util.Map;

public interface CommentCountService {
    Map<Long, Long> getCommentCount(Collection<Event> events);
}
