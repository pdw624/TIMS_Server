package kr.tracom.platform.master.bis.v01;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class NodeItem implements BaseModel {
    public static final int SIZE = 83;

    private String nodeId;
    private String nodeName1;
    private String nodeName2;
    private String nodeType;
    private double latitude;
    private double longitude;
    private byte inRadius;
    private byte outRadius;

    public NodeItem() {
        super();
    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.nodeId, 10);
        byteHelper.setString(this.nodeName1, 30);
        byteHelper.setString(this.nodeName2, 30);
        byteHelper.setString(this.nodeType, 3);
        byteHelper.setInt((int)(this.latitude * 1000000.0));
        byteHelper.setInt((int)(this.longitude * 1000000.0));
        byteHelper.setByte(this.inRadius);
        byteHelper.setByte(this.outRadius);
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.nodeId = byteHelper.getString(10);
        this.nodeName1 = byteHelper.getString(30);
        this.nodeName2 = byteHelper.getString(30);
        this.nodeType = byteHelper.getString(3);
        this.latitude = ByteHelper.unsigned(byteHelper.getInt()) / 1000000.0;
        this.longitude = ByteHelper.unsigned(byteHelper.getInt()) / 1000000.0;
        this.inRadius = byteHelper.getByte();
        this.outRadius = byteHelper.getByte();
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("NODE_ID", nodeId);
        map.put("NODE_NAME1", nodeName1);
        map.put("NODE_NAME2", nodeName2);
        map.put("NODE_TYPE", nodeType);
        map.put("LAT", latitude);
        map.put("LON", longitude);
        map.put("IN_RADIUS", inRadius);
        map.put("OUT_RADIUS", outRadius);

        return map;
    }
}
