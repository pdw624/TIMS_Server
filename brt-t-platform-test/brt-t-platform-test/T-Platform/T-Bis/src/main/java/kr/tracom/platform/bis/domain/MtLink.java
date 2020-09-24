package kr.tracom.platform.bis.domain;

import lombok.Data;

@Data
public class MtLink {
    private String id;
    private String type;
    private String name;
    private String fromNodeId;
    private String toNodeId;
    private int linkLength;
}
