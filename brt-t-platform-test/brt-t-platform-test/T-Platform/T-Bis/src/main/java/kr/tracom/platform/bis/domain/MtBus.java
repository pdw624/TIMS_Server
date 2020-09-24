package kr.tracom.platform.bis.domain;

import lombok.Data;

@Data
public class MtBus {
    private String id;
    private String type;
    private String impId;
    private String impType;
    private String plateNumber;
}
