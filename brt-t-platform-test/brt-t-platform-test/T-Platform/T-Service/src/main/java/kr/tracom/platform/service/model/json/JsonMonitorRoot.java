package kr.tracom.platform.service.model.json;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JsonMonitorRoot {
    private String applyDate;
    private String deviceId;
    private byte count;
    private List<JsonMonitorItem> items;

    public JsonMonitorRoot() {
        items = new ArrayList<JsonMonitorItem>();
    }
}
