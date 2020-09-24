package kr.tracom.platform.master.bis.obe;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class ObeConfig implements BaseModel {
    public static final int SIZE = 1;

    private byte languageCode;

    public ObeConfig() {

    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setShort(this.languageCode);

    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.languageCode = byteHelper.getByte();
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}
