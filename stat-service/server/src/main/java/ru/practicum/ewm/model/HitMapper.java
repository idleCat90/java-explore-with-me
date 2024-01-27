package ru.practicum.ewm.model;

import ru.practicum.ewm.HitDto;

public class HitMapper {

    public static Hit toHit(HitDto hitDto) {
        return Hit.builder()
                .ip(hitDto.getIp())
                .uri(hitDto.getUri())
                .app(hitDto.getApp())
                .timestamp(hitDto.getTimestamp())
                .build();
    }

    public static HitDto toHitDto(Hit hit) {
        return HitDto.builder()
                .ip(hit.getIp())
                .uri(hit.getUri())
                .app(hit.getApp())
                .timestamp(hit.getTimestamp())
                .build();
    }
}
