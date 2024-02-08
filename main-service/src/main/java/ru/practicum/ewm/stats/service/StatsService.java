package ru.practicum.ewm.stats.service;

import ru.practicum.ewm.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;

public interface StatsService {
    Map<Long, Long> getConfirmedRequests(Collection<Event> events);

    Map<Long, Long> getViews(Collection<Event> events);

    void postHit(HttpServletRequest request);
}
