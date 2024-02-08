package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.utility.Util.Marker.onCreate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewUserRequest {
    @Email(groups = {onCreate.class})
    @Size(min = 6, max = 254, groups = {onCreate.class})
    @NotBlank(groups = {onCreate.class})
    private String email;
    @Size(min = 2, max = 250, groups = onCreate.class)
    @NotBlank(groups = {onCreate.class})
    private String name;
}
