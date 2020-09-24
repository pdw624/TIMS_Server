package kr.tracom.platform.attribute.common;

import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.net.protocol.TimsAttribute;
import kr.tracom.platform.net.protocol.TimsLogBuilder;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
@TimsAttribute(attributeId = AtCode.POWER_CONTROL_REQ)
public class AtPowerControlRequest extends AtData {
    public static final int SIZE = 8;

    public static byte IMP_MAIN_POWER = 0x11;

    private byte targetId;
    private byte controlType;
    private byte delayTime;
    private byte resetLapse;
    private byte[] reserved;

    public AtPowerControlRequest() {
        attrId = AtCode.POWER_CONTROL_REQ;
        reserved = new byte[4];
    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.targetId = byteHelper.getByte();
        this.controlType = byteHelper.getByte();
        this.delayTime = byteHelper.getByte();
        this.resetLapse = byteHelper.getByte();
        this.reserved = byteHelper.getBytes(4);
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setByte(this.targetId);
        byteHelper.setByte(this.controlType);
        byteHelper.setByte(this.delayTime);
        byteHelper.setByte(this.resetLapse);
        byteHelper.setBytes(this.reserved, 4);
    }

    @Override
    public String getLog() {
        return TimsLogBuilder.getLog(this);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("TARGET_ID", this.targetId);
        map.put("CONTROL_TYPE", this.controlType);
        map.put("DELAY_TIME", this.delayTime);
        map.put("RESET_LAPSE", this.resetLapse);
        map.put("RESERVED", this.reserved);

        return map;
    }
}
