package ru.practicum.ewm.user.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {
    @NotNull
    @Positive
    private Long id;
    @NotBlank
    private String name;
}
