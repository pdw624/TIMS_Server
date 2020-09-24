package kr.tracom.platform.service.domain;

import lombok.Data;

@Data
public class MtLogFile {
    private String appId;
    private String logName;
    private String logPath;
    private String prcsType;
    private int deleteDays;
    private String enable;
}
