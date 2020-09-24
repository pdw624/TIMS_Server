package kr.tracom.platform.bis.domain;

import kr.tracom.platform.db.base.BaseDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PrBitService implements BaseDTO {
    private String systemDateTime;
    private String bitId;
    private String busId;
    private String routeId;
    private String stationId;
    private String routeType;
    private String busType;
    private String arrivalType;
    private String runType;
    private short busStnSeq;


    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("SYS_DT", systemDateTime);
        map.put("BIT_ID", bitId);
        map.put("BUS_ID", busId);
        map.put("ROUTE_ID", routeId);
        map.put("STATION_ID", stationId);
        map.put("ROUTE_TYPE", routeType);
        map.put("BUS_TYPE", busType);
        map.put("ARRIVAL_TYPE", arrivalType);
        map.put("RUN_TYPE", runType);
        map.put("BUS_STN_SEQ", busStnSeq);

        return map;
    }
}
