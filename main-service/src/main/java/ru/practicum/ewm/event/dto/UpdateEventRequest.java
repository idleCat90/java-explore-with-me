package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.utility.Util;
import ru.practicum.ewm.utility.Util.Marker.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateEventRequest {
    @Size(min = 20, max = 2000, groups = OnUpdate.class)
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000, groups = OnUpdate.class)
    private String description;
    @Future(groups = OnUpdate.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Util.DATE_TIME_FORMAT)
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    @PositiveOrZero(groups = OnUpdate.class)
    private Integer participantLimit;
    private Boolean requestModeration;
    @Size(min = 3, max = 120, groups = OnUpdate.class)
    private String title;
}
