package kr.tracom.platform.master.bis.v01;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class LinkItem implements BaseModel {
    public static final int SIZE = 101;

    private String linkId;
    private String linkName1;
    private String linkName2;
    private String linkType;
    private String fromNodeId;
    private String toNodeId;
    private short linkLength;
    private byte laneCount;
    private byte maxSpeed;
    private short inAngle;
    private short outAngle;

    public LinkItem() {

    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.linkId, 10);
        byteHelper.setString(this.linkName1, 30);
        byteHelper.setString(this.linkName2, 30);
        byteHelper.setString(this.linkType, 3);
        byteHelper.setString(this.fromNodeId, 10);
        byteHelper.setString(this.toNodeId, 10);
        byteHelper.setShort(this.linkLength);
        byteHelper.setByte(this.laneCount);
        byteHelper.setByte(this.maxSpeed);
        byteHelper.setShort(this.inAngle);
        byteHelper.setShort(this.outAngle);
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.linkId = byteHelper.getString(10);
        this.linkName1 = byteHelper.getString(30);
        this.linkName2 = byteHelper.getString(30);
        this.linkType = byteHelper.getString(3);
        this.fromNodeId = byteHelper.getString(10);
        this.toNodeId = byteHelper.getString(10);
        this.linkLength = byteHelper.getShort();
        this.laneCount = byteHelper.getByte();
        this.maxSpeed = byteHelper.getByte();
        this.inAngle = byteHelper.getShort();
        this.outAngle = byteHelper.getShort();
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("LINK_ID", linkId);
        map.put("LINK_NAME1", linkName1);
        map.put("LINK_NAME2", linkName2);
        map.put("LINK_TYPE", linkType);
        map.put("FROM_NODE_ID", fromNodeId);
        map.put("TO_NODE_ID", toNodeId);
        map.put("LINK_LENGTH", linkLength);
        map.put("LANE_COUNT", laneCount);
        map.put("MAX_SPEED", maxSpeed);
        map.put("IN_ANGLE", inAngle);
        map.put("OUT_ANGLE", outAngle);

        return map;
    }
}
