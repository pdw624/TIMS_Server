package kr.tracom.platform.service.domain;

import kr.tracom.platform.db.base.BaseDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class HtTransaction implements BaseDTO {
    private String registerDateTime;
    private String platformId;
    private String sessionId;
    private short packetId;
    private String remoteType;
    private String payloadType;
    private short attributeCount;
    private String attributeList;
    private String stringData;
    private short retryCount;
    private String sendState;
    private String sendDateTime;
    private String systemDateTime;

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
