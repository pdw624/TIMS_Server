package kr.tracom.platform.master.bis.bit;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class IlluminationItem implements BaseModel {
    public static final int SIZE = 17;

    private String startDate;
    private String endDate;
    private String onTime;
    private String offTime;
    private byte value;

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.startDate, 4);
        byteHelper.setString(this.endDate, 4);
        byteHelper.setString(this.onTime, 4);
        byteHelper.setString(this.offTime, 4);
        byteHelper.setByte(this.value);
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.startDate = byteHelper.getString(4);
        this.endDate = byteHelper.getString(4);
        this.onTime = byteHelper.getString(4);
        this.offTime = byteHelper.getString(4);
        this.value = byteHelper.getByte();
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}
