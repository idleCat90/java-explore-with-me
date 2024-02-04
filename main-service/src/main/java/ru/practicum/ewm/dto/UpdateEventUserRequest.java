package ru.practicum.ewm.dto;

import lombok.Getter;
import ru.practicum.ewm.model.state.EventUserState;

@Getter
public class UpdateEventUserRequest extends UpdateEventRequest {

    private EventUserState stateAction;
}
