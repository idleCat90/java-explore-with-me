package ru.practicum.ewm;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@Builder
public class StatsRequestDto {
    private List<String> uris;
    private LocalDateTime start;
    private LocalDateTime end;
    private Boolean unique;
    private String app;
}
