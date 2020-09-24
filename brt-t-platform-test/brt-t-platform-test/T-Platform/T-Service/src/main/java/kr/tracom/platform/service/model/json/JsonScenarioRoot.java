package kr.tracom.platform.service.model.json;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JsonScenarioRoot {
    private String applyDate;
    private String deviceId;
    private byte count;
    private List<JsonScenarioForm> items;

    public JsonScenarioRoot() {
        items = new ArrayList<JsonScenarioForm>();
    }
}
