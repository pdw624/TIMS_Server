package kr.tracom.platform.bis.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MtRoute {
    private String id;
    private String type;
    private String name;
    private String fromStationId;
    private String turnStationId;
    private String toStationId;
    private String runType;

    private List<MtNode> nodeList;
    private List<MtStation> stationList;
    private List<MtLink> linkList;

    public MtRoute() {
        nodeList = new ArrayList<>();
        stationList = new ArrayList<>();
        linkList = new ArrayList<>();
    }
}
