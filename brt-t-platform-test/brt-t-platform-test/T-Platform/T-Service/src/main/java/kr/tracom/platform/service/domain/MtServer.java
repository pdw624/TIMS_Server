package kr.tracom.platform.service.domain;

import lombok.Data;

@Data
public class MtServer {
    private String platformId;
    private int groupId;
    private String serverIp;
    private int serverPort;
    private String protocolType;
    private String userId;
    private String userPw;
}
