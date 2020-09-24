package kr.tracom.platform.master.bis.v01;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class RouteStationItem2 implements BaseModel {
    public static final int SIZE = 15;

    private short stationSeq;
    private String stationId;
    private String direction;

    public RouteStationItem2() {

    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setShort(this.stationSeq);
        byteHelper.setString(this.stationId, 10);
        byteHelper.setString(this.direction, 3);
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.stationSeq = byteHelper.getShort();
        this.stationId = byteHelper.getString(10);
        this.direction = byteHelper.getString(3);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("STATION_SEQ", stationSeq);
        map.put("STATION_ID", stationId);
        map.put("UPDOWN_DIR", direction);

        return map;
    }
}
