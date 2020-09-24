package kr.tracom.platform.service.model.json;

import lombok.Data;

@Data
public class JsonFtpNotify {
    private String applyDate;
    private String deviceId;
    private int deviceType;

    private int fileCode;
    private String ftpPath;
    private String ftpFile;
}
