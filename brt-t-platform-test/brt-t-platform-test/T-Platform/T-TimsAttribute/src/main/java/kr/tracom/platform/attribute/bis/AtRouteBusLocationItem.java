package kr.tracom.platform.attribute.bis;

import kr.tracom.platform.net.protocol.TimsLogBuilder;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class AtRouteBusLocationItem extends AtData {
    public static final int SIZE = 24;

    private String stationId;
    private String routeType;
    private String busType;
    private String arrivalType;
    private String runType;
    private short busSeq;

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.stationId = byteHelper.getString(10);
        this.routeType = byteHelper.getString(3);
        this.busType = byteHelper.getString(3);
        this.arrivalType = byteHelper.getString(3);
        this.runType = byteHelper.getString(3);
        this.busSeq = byteHelper.getShort();
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.stationId, 10);
        byteHelper.setString(this.routeType, 3);
        byteHelper.setString(this.busType, 3);
        byteHelper.setString(this.arrivalType, 3);
        byteHelper.setString(this.runType, 3);
        byteHelper.setShort(this.busSeq);
    }

    @Override
    public String getLog() {
        return TimsLogBuilder.getLog(this);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("STN_ID", this.stationId);
        map.put("ROUTE_TYPE", this.routeType);
        map.put("BUS_TYPE", this.busType);
        map.put("ARRIVAL_TYPE", this.arrivalType);
        map.put("RUN_TYPE", this.runType);
        map.put("BUS_SEQ", ByteHelper.unsigned(this.busSeq));

        return map;
    }
}
