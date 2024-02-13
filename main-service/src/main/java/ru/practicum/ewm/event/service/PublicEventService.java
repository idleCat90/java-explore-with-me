package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {

    List<EventShortDto> findEvents(String text,
                                   List<Long> categories,
                                   Boolean paid,
                                   LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd,
                                   Boolean onlyAvailable,
                                   String sort,
                                   Integer from,
                                   Integer size,
                                   HttpServletRequest request);

    EventFullDto getEvent(Long id, HttpServletRequest request);
}
