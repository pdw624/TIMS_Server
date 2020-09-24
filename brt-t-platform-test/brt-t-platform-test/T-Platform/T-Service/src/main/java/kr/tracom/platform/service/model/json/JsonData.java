package kr.tracom.platform.service.model.json;

import lombok.Data;

@Data
public class JsonData {
    private byte type;
    private String applyDateTime;
    private Object data;
}
