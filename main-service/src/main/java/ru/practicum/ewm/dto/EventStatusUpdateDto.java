package ru.practicum.ewm.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventStatusUpdateDto {
    private List<Long> requestIds;
    private List<Long> processedIds;
}
