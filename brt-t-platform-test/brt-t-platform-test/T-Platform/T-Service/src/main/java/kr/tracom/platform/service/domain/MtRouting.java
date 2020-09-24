package kr.tracom.platform.service.domain;

import lombok.Data;

@Data
public class MtRouting {
    private byte groupId;
    private short systemId;
    private byte serviceId;

    private String groupName;
    private String systemName;
    private String serviceName;
}
