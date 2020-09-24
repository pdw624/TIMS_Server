package kr.tracom.platform.master.bis.v01;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class RouteItem2 implements BaseModel {
    public static final int SIZE = 24;

    private String dayType;
    private byte serviceCount;
    private String startFirstTime;
    private String startLastTime;
    private String endFirstTime;
    private String endLastTime;
    private short minInterval;
    private short maxInterval;

    public RouteItem2() {

    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.dayType, 3);
        byteHelper.setByte(this.serviceCount);
        byteHelper.setString(this.startFirstTime, 4);
        byteHelper.setString(this.startLastTime, 4);
        byteHelper.setString(this.endFirstTime, 4);
        byteHelper.setString(this.endLastTime, 4);
        byteHelper.setShort(this.minInterval);
        byteHelper.setShort(this.maxInterval);
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.dayType = byteHelper.getString(3);
        this.serviceCount = byteHelper.getByte();
        this.startFirstTime = byteHelper.getString(4);
        this.startLastTime = byteHelper.getString(4);
        this.endFirstTime = byteHelper.getString(4);
        this.endLastTime = byteHelper.getString(4);
        this.minInterval = byteHelper.getShort();
        this.maxInterval = byteHelper.getShort();
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("DAY_TYPE", dayType);
        map.put("SERVICE_COUNT", serviceCount);
        map.put("ST_FIRST_TIME", startFirstTime);
        map.put("ST_LAST_TIME", startLastTime);
        map.put("ED_FIRST_TIME", endFirstTime);
        map.put("ED_LAST_TIME", endLastTime);
        map.put("MIN_INTERVAL", minInterval);
        map.put("MAX_INTERVAL", maxInterval);

        return map;
    }
}
