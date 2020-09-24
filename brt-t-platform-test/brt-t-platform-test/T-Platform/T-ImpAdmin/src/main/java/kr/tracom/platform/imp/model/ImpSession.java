package kr.tracom.platform.imp.model;

import lombok.Data;

@Data
public class ImpSession {
    private String sessionId;
    private String sessionName;
    private String localNumber;
    private String sessionIp;
    private String dbVersion;
    private String appVersion;
    private String compName;
}
