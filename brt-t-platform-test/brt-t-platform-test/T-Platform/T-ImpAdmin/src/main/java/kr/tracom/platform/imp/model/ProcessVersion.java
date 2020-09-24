package kr.tracom.platform.imp.model;

import lombok.Data;

@Data
public class ProcessVersion {
    private String impId;
    private String processName;
    private byte processIndex;
    private byte versionHigh;
    private byte versionMiddle;
    private byte versionLow;
    private String startTime;
    private String lastRunTime;
    private String buildDate;
    private String updateDate;
}
