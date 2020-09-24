package kr.tracom.platform.attribute.common;

import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class AtStatusItem extends AtData {
    public static final int SIZE = 5;

    private String code;
    private short value;

    public AtStatusItem() {

    }

    public int getSize() {
        return SIZE;
    }

    public void decode(ByteHelper byteHelper) {
        this.code = byteHelper.getString(3);
        this.value = byteHelper.getShort();
    }

    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.code, 3);
        byteHelper.setShort(this.value);
    }

    public String getLog() {
        return String.format("%s, %d", this.code, ByteHelper.unsigned(this.value));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("CODE", this.code);
        map.put("VALUE", ByteHelper.unsigned(this.value));

        return map;
    }
}
