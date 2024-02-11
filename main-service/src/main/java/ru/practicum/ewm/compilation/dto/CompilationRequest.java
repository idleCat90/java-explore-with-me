package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.utility.Util.Marker.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationRequest {
    @NotBlank(groups = {OnCreate.class})
    @Size(max = 50, groups = {OnCreate.class, OnUpdate.class})
    private String title;
    private Boolean pinned;
    private Set<Long> events = new HashSet<>();
}
