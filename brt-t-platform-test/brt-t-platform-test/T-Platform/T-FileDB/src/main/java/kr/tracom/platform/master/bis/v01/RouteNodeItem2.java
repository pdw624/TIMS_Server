package kr.tracom.platform.master.bis.v01;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class RouteNodeItem2 implements BaseModel {
    public static final int SIZE = 15;

    private short nodeSeq;
    private String nodeId;
    private String direction;

    public RouteNodeItem2() {

    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setShort(this.nodeSeq);
        byteHelper.setString(this.nodeId, 10);
        byteHelper.setString(this.direction, 3);
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.nodeSeq = byteHelper.getShort();
        this.nodeId = byteHelper.getString(10);
        this.direction = byteHelper.getString(3);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("NODE_SEQ", nodeSeq);
        map.put("NODE_ID", nodeId);
        map.put("UPDOWN_DIR", direction);

        return map;
    }
}
