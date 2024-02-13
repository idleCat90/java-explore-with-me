package ru.practicum.ewm.stats.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.StatsResponseDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.StatisticErrorException;
import ru.practicum.ewm.request.dto.ConfirmedRequestCountDto;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.utility.Util;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Util.DATE_TIME_FORMAT);
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    private String app;

    @Override
    public Map<Long, Long> getConfirmedRequests(Collection<Event> events) {
        log.debug("Method call: getConfirmedRequests(), events={}", events);
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        List<ConfirmedRequestCountDto> confirmedRequestCountDtos = requestRepository.countByEventId(eventIds);
        Map<Long, Long> confirmedRequests = confirmedRequestCountDtos.stream()
                .collect(Collectors.toMap(
                        ConfirmedRequestCountDto::getEventId,
                        ConfirmedRequestCountDto::getConfirmedRequestsCount));
        log.debug("Returned: map={}", confirmedRequests);
        return confirmedRequests;
    }

    @Override
    public Map<Long, Long> getViews(Collection<Event> events) {
        log.debug("Method call: getViews(), events={}", events);
        Map<Long, Long> views = new HashMap<>();
        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        if (start == null) {
            return Map.of();
        }

        log.debug("start={}", start);
        log.debug("start.formatted={}", start.format(DATE_TIME_FORMATTER));

        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        ResponseEntity<Object> response = statsClient.getStats(start.format(DATE_TIME_FORMATTER),
                LocalDateTime.now().format(DATE_TIME_FORMATTER),
                uris,
                true);

        try {
            StatsResponseDto[] stats = objectMapper.readValue(
                    objectMapper.writeValueAsString(response.getBody()), StatsResponseDto[].class);
            for (StatsResponseDto stat : stats) {
                views.put(
                        Long.parseLong(stat.getUri().replaceAll("\\D+", "")),
                        stat.getHits());
            }
        } catch (JsonProcessingException e) {
            log.error("Can't parse value");
            throw new StatisticErrorException("Can't process value");
        }
        return views;
    }

    @Override
    @Transactional
    public void postHit(HttpServletRequest request) {
        log.debug("Method call: postHit()");
        statsClient.postHit(request);
    }
}
