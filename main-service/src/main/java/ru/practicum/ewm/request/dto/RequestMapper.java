package ru.practicum.ewm.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.request.model.Request;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {

    public RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }

    public List<RequestDto> toRequestDtoList(List<Request> requests) {
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }
}
