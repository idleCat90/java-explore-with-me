package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto createRequest(Long userId, Long eventId);

    List<RequestDto> readAllRequests(Long userId);

    RequestDto cancelRequest(Long userId, Long requestId);
}
