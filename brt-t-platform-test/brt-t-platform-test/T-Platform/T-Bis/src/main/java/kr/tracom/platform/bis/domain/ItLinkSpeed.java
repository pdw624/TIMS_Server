package kr.tracom.platform.bis.domain;

import kr.tracom.platform.db.base.BaseDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ItLinkSpeed implements BaseDTO {
    private String linkId;
    private int speed;
    private int runTime;
    private int stopTime;
    private String updateDateTime;

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("LINK_ID", this.linkId);
        map.put("SPEED", this.speed);
        map.put("RUN_TIME", this.runTime);
        map.put("STOP_TIME", this.stopTime);
        map.put("UPD_DT", this.updateDateTime);

        return map;
    }
}
