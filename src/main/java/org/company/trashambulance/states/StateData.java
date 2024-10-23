package org.company.trashambulance.states;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class StateData {
    private String currentState;
}
