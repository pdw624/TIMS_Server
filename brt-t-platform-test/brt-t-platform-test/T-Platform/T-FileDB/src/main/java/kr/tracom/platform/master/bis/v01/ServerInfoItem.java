package kr.tracom.platform.master.bis.v01;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ServerInfoItem implements BaseModel {
    public static final int SIZE = 125;

    private String serverId;
    private String serverName;
    private String serverIp;
    private short serverPort;
    private byte protocolType;
    private String userId;
    private String password;
    private byte option1;
    private byte option2;
    private byte option3;
    private byte option4;

    public ServerInfoItem() {

    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.serverId, 10);
        byteHelper.setString(this.serverName, 20);
        byteHelper.setString(this.serverIp, 24);
        byteHelper.setShort(this.serverPort);
        byteHelper.setByte(this.protocolType);
        byteHelper.setString(this.userId, 32);
        byteHelper.setString(this.password, 32);
        byteHelper.setByte(this.option1);
        byteHelper.setByte(this.option2);
        byteHelper.setByte(this.option3);
        byteHelper.setByte(this.option4);
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.serverId = byteHelper.getString(10);
        this.serverName = byteHelper.getString(20);
        this.serverIp = byteHelper.getString(24);
        this.serverPort = byteHelper.getShort();
        this.protocolType = byteHelper.getByte();
        this.userId = byteHelper.getString(32);
        this.password = byteHelper.getString(32);
        this.option1 = byteHelper.getByte();
        this.option2 = byteHelper.getByte();
        this.option3 = byteHelper.getByte();
        this.option4 = byteHelper.getByte();
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("SVR_ID", serverId);
        map.put("SVR_NAME", serverName);
        map.put("SVR_IP", serverIp);
        map.put("SVR_PORT", serverPort);
        map.put("PRTL_TYPE", protocolType);
        map.put("USER_ID", userId);
        map.put("USER_PW", password);
        map.put("OPT_1", option1);
        map.put("OPT_2", option2);
        map.put("OPT_3", option3);
        map.put("OPT_4", option4);

        return map;
    }
}
