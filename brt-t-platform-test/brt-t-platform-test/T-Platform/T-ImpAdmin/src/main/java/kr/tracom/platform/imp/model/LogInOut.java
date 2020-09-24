package kr.tracom.platform.imp.model;

import lombok.Data;

@Data
public class LogInOut {
    private String sessionId;
    private String sessionName;
    private String firstDate;
    private String lastDate;
    private int dataCount;
}
