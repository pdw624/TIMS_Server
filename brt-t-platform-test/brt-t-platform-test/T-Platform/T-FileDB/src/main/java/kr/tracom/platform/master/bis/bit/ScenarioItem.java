package kr.tracom.platform.master.bis.bit;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class ScenarioItem implements BaseModel {
    public static final int SIZE = 22;

    private byte order;
    private String fileName;
    private byte runTime;

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setByte(this.order);
        byteHelper.setString(this.fileName, 20);
        byteHelper.setByte(this.runTime);
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.order = byteHelper.getByte();
        this.fileName = byteHelper.getString(20);
        this.runTime = byteHelper.getByte();
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}
