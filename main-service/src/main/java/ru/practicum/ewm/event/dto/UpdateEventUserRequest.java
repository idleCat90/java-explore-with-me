package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.event.model.state.PrivateStateAction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateEventUserRequest extends UpdateEventRequest {
    private PrivateStateAction stateAction;
}
