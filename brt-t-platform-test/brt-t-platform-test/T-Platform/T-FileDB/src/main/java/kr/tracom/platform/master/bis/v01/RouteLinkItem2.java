package kr.tracom.platform.master.bis.v01;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class RouteLinkItem2 implements BaseModel {
    public static final int SIZE = 15;

    private short linkSeq;
    private String linkId;
    private String direction;

    public RouteLinkItem2() {

    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setShort(this.linkSeq);
        byteHelper.setString(this.linkId, 10);
        byteHelper.setString(this.direction, 3);
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.linkSeq = byteHelper.getShort();
        this.linkId = byteHelper.getString(10);
        this.direction = byteHelper.getString(3);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("LINK_SEQ", linkSeq);
        map.put("LINK_ID", linkId);
        map.put("UPDOWN_DIR", direction);

        return map;
    }
}
