package kr.tracom.platform.service.model.json;

import lombok.Data;

@Data
public class JsonControlNotify {
    private String deviceId;
    private int deviceType;
    private String code;
    private int value;
}
