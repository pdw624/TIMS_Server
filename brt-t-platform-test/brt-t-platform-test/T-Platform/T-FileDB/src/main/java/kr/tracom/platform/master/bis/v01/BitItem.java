package kr.tracom.platform.master.bis.v01;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class BitItem implements BaseModel {
    public static final int SIZE = 96;

    private String stationId;
    private String stationName1;
    private String stationName2;
    private String stationType;
    private String bitId;
    private String bitType;
    private String nodeId;

    public BitItem() {

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
        byteHelper.setString(this.bitId, 10);
        byteHelper.setString(this.bitType, 3);
        byteHelper.setString(this.nodeId, 10);
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.stationId = byteHelper.getString(10);
        this.stationName1 = byteHelper.getString(30);
        this.stationName2 = byteHelper.getString(30);
        this.stationType = byteHelper.getString(3);
        this.bitId = byteHelper.getString(10);
        this.bitType = byteHelper.getString(3);
        this.nodeId = byteHelper.getString(10);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("STATION_ID", stationId);
        map.put("STATION_NAME1", stationName1);
        map.put("STATION_NAME2", stationName2);
        map.put("STATION_TYPE", stationType);
        map.put("BIT_ID", bitId);
        map.put("BIT_TYPE", bitType);
        map.put("NODE_ID", nodeId);

        return map;
    }
}
