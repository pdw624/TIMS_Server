package kr.tracom.platform.service.model.json;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JsonScenarioForm {
    private byte formType;
    private byte formCount;

    private List<JsonScenarioItem> items;

    public JsonScenarioForm() {
        items = new ArrayList<JsonScenarioItem>();
    }
}
