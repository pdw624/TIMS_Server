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
public class AtServerConnectionItem extends AtData {
    public static final int SIZE = 156;

    private byte code;
    private String name;
    private String ip;
    private short port;
    private byte protocolType;
    private String userId;
    private String password;
    private String reserved;
    private String description;

    public AtServerConnectionItem() {

    }

    public int getSize() {
        return SIZE;
    }

    public void decode(ByteHelper byteHelper) {
        this.code = byteHelper.getByte();
        this.name = byteHelper.getString(32);
        this.ip = byteHelper.getString(24);
        this.port = byteHelper.getShort();
        this.protocolType = byteHelper.getByte();
        this.userId = byteHelper.getString(32);
        this.password = byteHelper.getString(32);
        this.reserved = byteHelper.getString(8);
        this.description = byteHelper.getString(24);
    }

    public void encode(ByteHelper byteHelper) {
        byteHelper.setByte(this.code);
        byteHelper.setString(this.name, 32);
        byteHelper.setString(this.ip, 20);
        byteHelper.setShort(this.port);
        byteHelper.setByte(this.protocolType);
        byteHelper.setString(this.userId, 32);
        byteHelper.setString(this.password, 32);
        byteHelper.setString(this.reserved, 8);
        byteHelper.setString(this.description, 24);
    }

    public String getLog() {
        return TimsLogBuilder.getLog(this);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("CODE", this.code);
        map.put("NAME", this.name);
        map.put("IP", this.ip);
        map.put("PORT", ByteHelper.unsigned(this.port));
        map.put("PRTL_TYPE", ByteHelper.unsigned(this.protocolType));
        map.put("USER_ID", this.userId);
        map.put("USER_PW", this.password);
        map.put("RESERVED", this.reserved);
        map.put("DESCRIPTION", this.description);

        return map;
    }
}
