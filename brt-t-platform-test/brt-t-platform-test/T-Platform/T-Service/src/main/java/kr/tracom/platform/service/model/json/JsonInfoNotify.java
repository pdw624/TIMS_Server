package kr.tracom.platform.service.model.json;

import lombok.Data;

@Data
public class JsonInfoNotify {
    private String deviceId;
    private int deviceType;
    private int code;
    private int value;
}
