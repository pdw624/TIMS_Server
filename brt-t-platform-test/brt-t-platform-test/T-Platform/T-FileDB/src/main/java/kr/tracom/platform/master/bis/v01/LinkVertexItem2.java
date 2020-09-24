package kr.tracom.platform.master.bis.v01;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class LinkVertexItem2 implements BaseModel {
    public static final int SIZE = 10;

    private short vertexSeq;
    private double latitude;
    private double longitude;

    public LinkVertexItem2() {
        super();
    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setShort(this.vertexSeq);
        byteHelper.setInt((int)(this.latitude * 1000000.0));
        byteHelper.setInt((int)(this.longitude * 1000000.0));
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.vertexSeq = byteHelper.getShort();
        this.latitude = ByteHelper.unsigned(byteHelper.getInt()) / 1000000.0;
        this.longitude = ByteHelper.unsigned(byteHelper.getInt()) / 1000000.0;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("VERTEX_SEQ", vertexSeq);
        map.put("LAT", latitude);
        map.put("LON", longitude);

        return map;
    }
}
