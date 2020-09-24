package kr.tracom.platform.bis.domain;

import kr.tracom.platform.db.base.BaseDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ItBusLocation implements BaseDTO {
    private String busId;
    private String impId;
    private String routeId;
    private int nodeSequence;
    private String nodeId;
    private int stationSequence;
    private String stationId;
    private String linkId;
    private String runType;
    private double latitude;
    private double longitude;
    private String gpsDateTime;
    private String alarmType;
    private String alarmDateTime;
    private String systemDateTime;

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("BUS_ID", this.busId);
        map.put("IMP_ID", this.impId);
        map.put("ROUTE_ID", this.routeId);
        map.put("NODE_SEQ", this.nodeSequence);
        map.put("NODE_ID", this.nodeId);
        map.put("STATION_SEQ", this.stationSequence);
        map.put("STATION_ID", this.stationId);
        map.put("LINK_ID", this.linkId);
        map.put("RUN_TYPE", this.runType);
        map.put("LAT", this.latitude);
        map.put("LON", this.longitude);
        map.put("GPS_DT", this.gpsDateTime);
        map.put("ALM_TYPE", this.alarmType);
        map.put("ALM_DT", this.alarmDateTime);
        map.put("SYS_DT", this.systemDateTime);

        return map;
    }
}
