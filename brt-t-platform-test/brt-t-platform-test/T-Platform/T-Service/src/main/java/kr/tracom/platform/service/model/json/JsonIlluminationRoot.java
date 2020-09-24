package kr.tracom.platform.service.model.json;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonIlluminationRoot {
    private String applyDate;
    private String deviceId;
    private byte count;
    private List<JsonIlluminationItem> items;

    public JsonIlluminationRoot() {
        items = new ArrayList<JsonIlluminationItem>();
    }
}
