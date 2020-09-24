package kr.tracom.platform.service.model.json;

import lombok.Data;

@Data
public class JsonScenarioItem {
    private byte order;
    private String fileName;
    private byte displaySeconds;
}
