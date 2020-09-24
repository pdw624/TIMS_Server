package kr.tracom.platform.master.bis.v01;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class StationItem implements BaseModel {
    public static final int SIZE = 91;

    private String stationId;
    private String stationName1;
    private String stationName2;
    private String stationType;
    private double latitude;
    private double longitude;
    private String nodeId;

    public StationItem() {

    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.stationId, 10);
        byteHelper.setString(this.stationName1, 30);
        byteHelper.setString(this.stationName2, 30);
        byteHelper.setString(this.stationType, 3);
        byteHelper.setInt((int)(this.latitude * 1000000.0));
        byteHelper.setInt((int)(this.longitude * 1000000.0));
        byteHelper.setString(this.nodeId, 10);
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.stationId = byteHelper.getString(10);
        this.stationName1 = byteHelper.getString(30);
        this.stationName2 = byteHelper.getString(30);
        this.stationType = byteHelper.getString(3);
        this.latitude = ByteHelper.unsigned(byteHelper.getInt()) / 1000000.0;
        this.longitude = ByteHelper.unsigned(byteHelper.getInt()) / 1000000.0;
        this.nodeId = byteHelper.getString(10);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("STATION_ID", stationId);
        map.put("STATION_NAME1", stationName1);
        map.put("STATION_NAME2", stationName2);
        map.put("STATION_TYPE", stationType);
        map.put("LAT", latitude);
        map.put("LON", longitude);
        map.put("NODE_ID", nodeId);

        return map;
    }
}
