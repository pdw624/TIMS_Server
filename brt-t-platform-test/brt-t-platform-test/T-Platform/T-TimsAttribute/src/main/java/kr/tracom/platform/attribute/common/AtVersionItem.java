package kr.tracom.platform.attribute.common;

import kr.tracom.platform.net.protocol.TimsLogBuilder;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class AtVersionItem extends AtData {
    public static final int SIZE = 15;

    private String code;
    private String version;

    public AtVersionItem() {

    }

    public int getSize() {
        return SIZE;
    }

    public void decode(ByteHelper byteHelper) {
        this.code = byteHelper.getString(3);
        this.version = byteHelper.getString(12);
    }

    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.code, 3);
        byteHelper.setString(this.version, 12);
    }

    public String getLog() {
        return TimsLogBuilder.getLog(this);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("CODE", this.code);
        map.put("VALUE", this.version);

        return map;
    }
}
