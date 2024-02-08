package ru.practicum.ewm.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmedRequestCountDto {
    private Long eventId;
    private Long confirmedRequestsCount;
}
