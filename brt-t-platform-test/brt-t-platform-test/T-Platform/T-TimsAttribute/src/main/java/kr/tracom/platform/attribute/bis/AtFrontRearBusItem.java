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
public class AtFrontRearBusItem extends AtData {
    public static final int SIZE = 58;

    private String locationType;
    private String busName;
    private String nodeName;
    private short relativeSeq;
    private byte gapTime;
    private short gapDistance;

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.locationType = byteHelper.getString(3);
        this.busName = byteHelper.getString(20);
        this.nodeName = byteHelper.getString(30);
        this.relativeSeq = byteHelper.getShort();
        this.gapTime = byteHelper.getByte();
        this.gapDistance = byteHelper.getShort();
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.locationType, 3);
        byteHelper.setString(this.busName, 20);
        byteHelper.setString(this.nodeName, 30);
        byteHelper.setShort(this.relativeSeq);
        byteHelper.setByte(this.gapTime);
        byteHelper.setShort(this.gapDistance);
    }

    @Override
    public String getLog() {
        return TimsLogBuilder.getLog(this);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("LOC_TYPE", this.locationType);
        map.put("BUS_NAME", this.busName);
        map.put("NODE_NAME", this.nodeName);
        map.put("REL_SEQ", this.relativeSeq);
        map.put("GAP_TIME", this.gapTime);
        map.put("GAP_DIST", this.gapDistance);

        return map;
    }
}
