package kr.tracom.platform.bis.domain;

import lombok.Data;

@Data
public class HtBusEvent {
    private String gpsDateTime;
    private String busId;
    private String routeId;
    private String eventCode;
    private String eventData;
    private int eventSequence;
    private String runType;
    private double latitude;
    private double longitude;
    private int heading;
    private int speed;
    private int stopTime;
    private String systemDateTime;
}
