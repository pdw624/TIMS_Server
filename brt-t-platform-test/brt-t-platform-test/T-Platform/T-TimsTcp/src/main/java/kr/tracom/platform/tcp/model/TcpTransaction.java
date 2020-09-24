package kr.tracom.platform.tcp.model;

import kr.tracom.platform.db.base.BaseDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TcpTransaction implements BaseDTO {
    private String registerDateTime;
    private String platformId;
    private String sessionId;
    private short packetId;
    private String remoteType;
    private String payloadType;
    private int attributeCount;
    private String attributeList;
    private String stringData;
    private int retryCount;
    private String sendState;
    private String sendDateTime;
    private String systemDateTime;

    public TcpTransaction() {

    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("REG_DT", registerDateTime);
        map.put("PLF_ID", platformId);
        map.put("SESSION_ID", sessionId);
        map.put("PACKET_ID", packetId);
        map.put("REMOTE_TYPE", remoteType);
        map.put("PAYLOAD_TYPE", payloadType);
        map.put("ATTR_COUNT", attributeCount);
        map.put("ATTR_LIST", attributeList);
        map.put("STR_DATA", stringData);
        map.put("RETRY_COUNT", retryCount);
        map.put("SEND_STATE", sendState);
        map.put("SEND_DT", sendDateTime);
        map.put("SYS_DT", systemDateTime);

        return map;
    }
}
