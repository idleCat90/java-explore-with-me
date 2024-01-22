package ru.practicum.ewm.service;

import ru.practicum.ewm.HitDto;
import ru.practicum.ewm.StatsRequestDto;
import ru.practicum.ewm.StatsResponseDto;

import java.util.List;

public interface StatsService {
    HitDto saveHit(HitDto hitDto);

    List<StatsResponseDto> readStats(StatsRequestDto statsRequestDto);
}
