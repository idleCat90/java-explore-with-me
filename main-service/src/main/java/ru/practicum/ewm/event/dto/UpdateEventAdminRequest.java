package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.event.model.state.AdminStateAction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateEventAdminRequest extends UpdateEventRequest {
    private AdminStateAction stateAction;
}
