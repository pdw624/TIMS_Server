package kr.tracom.platform.net.protocol;

import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TimsAddress implements TimsObject {
    public static final int SIZE = 4;

    private byte serviceId;
    private byte groupId;
    private short systemId;

    public TimsAddress() {
        this.serviceId = 0;
        this.groupId = 0;
        this.systemId = 0;
    }

    public TimsAddress(byte serviceId, byte groupId, short systemId) {
        this.serviceId = serviceId;
        this.groupId = groupId;
        this.systemId = systemId;
    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.serviceId = byteHelper.getByte();
        this.groupId = byteHelper.getByte();
        this.systemId = byteHelper.getShort();
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setByte(this.serviceId);
        byteHelper.setByte(this.groupId);
        byteHelper.setShort(this.systemId);
    }

    @Override
    public String getLog() {
        //return TimsLogBuilder.getLog(this);
        return String.format("%d %d %d",
                ByteHelper.unsigned(this.serviceId),
                ByteHelper.unsigned(this.groupId),
                ByteHelper.unsigned(this.systemId));
    }
}
