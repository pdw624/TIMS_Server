package kr.tracom.platform.bis.domain;

import lombok.Data;

@Data
public class VwPredictionTime {
    private String routeId;
    private int nodeSeq;
    private String nodeId;
    private short stationSeq;
    private String stationId;
    private String bitId;
    private String busId;
    private String runType;
    private double latitude;
    private double longitude;
    private int linkLength;
    private short busSeq;
    private short relativeSeq;
    private int prdtTime;
    private String routeType;
    private String busType;
}
