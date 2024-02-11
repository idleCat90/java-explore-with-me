package ru.practicum.ewm;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HitDto {
    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(max = 50, groups = {Marker.OnCreate.class})
    private String app;
    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(max = 50, groups = {Marker.OnCreate.class})
    private String uri;
    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(max = 15, groups = {Marker.OnCreate.class})
    private String ip;
    @NotNull(groups = {Marker.OnCreate.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT)
    private LocalDateTime timestamp;
}
