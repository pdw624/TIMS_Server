package kr.tracom.platform.bis.domain;

import lombok.Data;

@Data
public class MtNode {
    private String id;
    private String type;
    private String name;
    private double gpsX;
    private double gpsY;
}
