package kr.tracom.platform.master.bis.v01;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class BusItem implements BaseModel {
    public static final int SIZE = 56;

    private String busId;
    private String busType;
    private String impId;
    private String impType;
    private String plateNumber;

    public BusItem() {

    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.busId, 10);
        byteHelper.setString(this.busType, 3);
        byteHelper.setString(this.impId, 10);
        byteHelper.setString(this.impType, 3);
        byteHelper.setString(this.plateNumber, 30);
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.busId = byteHelper.getString(10);
        this.busType = byteHelper.getString(3);
        this.impId = byteHelper.getString(10);
        this.impType = byteHelper.getString(3);
        this.plateNumber = byteHelper.getString(30);

    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("BUS_ID", busId);
        map.put("BUS_TYPE", busType);
        map.put("BUS_NAME", plateNumber);
        map.put("IMP_ID", impId);
        map.put("IMP_TYPE", impType);

        return map;
    }
}
