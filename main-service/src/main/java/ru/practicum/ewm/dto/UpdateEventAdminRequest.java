package ru.practicum.ewm.dto;

import lombok.Getter;
import ru.practicum.ewm.model.state.EventAdminState;

@Getter
public class UpdateEventAdminRequest extends UpdateEventRequest {

    private EventAdminState stateAction;
}
