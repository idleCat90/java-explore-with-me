package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.utility.Util;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotNull
    private Long category;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Util.DATE_TIME_FORMAT)
    @NotNull
    private LocalDateTime eventDate;
    @Valid
    @NotNull
    private LocationDto location;
    private boolean paid;
    @PositiveOrZero
    private Integer participantLimit = 0;
    @NotNull
    private boolean requestModeration = true;
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}
