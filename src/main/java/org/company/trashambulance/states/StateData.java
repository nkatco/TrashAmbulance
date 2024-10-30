package org.company.trashambulance.states;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Data
public class StateData {
    private HashMap<Long, String> currentStateMap = new HashMap<>();

    public void setCurrentStateMap(Long userId, String state) {
        currentStateMap.put(userId, state);
    }
}
