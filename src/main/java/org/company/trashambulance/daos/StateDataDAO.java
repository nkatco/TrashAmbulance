package org.company.trashambulance.daos;

import org.company.trashambulance.models.StateData;
import org.company.trashambulance.models.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StateDataDAO {

    public final Map<String, StateData> statesData = new HashMap<>();

    public StateData getStateDataByUserId(String id) {
        return statesData.get(id);
    }
    public void setStateData(User user, String state, Object data) {
        if(statesData.get(state + "_" + user.getId()) == null) {
            StateData stateData = new StateData();
            stateData.setUser_id(user.getId());
            stateData.setState(state);
            stateData.setData(data);
            statesData.put(state + "_" + user.getId(), stateData);
        } else {
            statesData.remove(state + "_" + user.getId());
            StateData stateData = new StateData();
            stateData.setUser_id(user.getId());
            stateData.setState(state);
            stateData.setData(data);
            statesData.put(state + "_" + user.getId(), stateData);
        }
    }

    public void removeStateDataByUserId(String id) {
        List<StateData> matchingData = new ArrayList<>();

        for (StateData data : statesData.values()) {
            if (data.getUser_id().equals(id)) {
                matchingData.add(data);
            }
        }
        for(StateData data : matchingData) {
            statesData.remove(data.getUser_id());
        }
    }
}
