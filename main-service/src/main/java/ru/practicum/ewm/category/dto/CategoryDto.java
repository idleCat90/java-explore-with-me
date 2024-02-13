package ru.practicum.ewm.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.utility.Util.Marker.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    @Size(min = 1, max = 50, groups = {OnCreate.class, OnUpdate.class})
    private String name;
}
