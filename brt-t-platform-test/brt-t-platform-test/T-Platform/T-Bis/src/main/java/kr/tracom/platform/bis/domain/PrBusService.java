package kr.tracom.platform.bis.domain;

import kr.tracom.platform.db.base.BaseDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PrBusService implements BaseDTO {
    private String systemDateTime;
    private String routeId;
    private String busId;
    private short nodeSeq;

    private String pprvBusId;
    private String pprvBusName;
    private short pprvRelSeq;
    private String pprvStnName;
    private short pprvDist;
    private short pprvTime;

    private String prevBusId;
    private String prevBusName;
    private short prevRelSeq;
    private String prevStnName;
    private short prevDist;
    private short prevTime;

    private String nextBusId;
    private String nextBusName;
    private short nextRelSeq;
    private String nextStnName;
    private short nextDist;
    private short nextTime;

    private String nnxtBusId;
    private String nnxtBusName;
    private short nnxtRelSeq;
    private String nnxtStnName;
    private short nnxtDist;
    private short nnxtTime;

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("SYS_DT", systemDateTime);
        map.put("BUS_ID", busId);
        map.put("ROUTE_ID", routeId);

        map.put("PPRV_BUS_ID", pprvBusId);
        map.put("PREV_BUS_ID", prevBusId);
        map.put("NEXT_BUS_ID", nextBusId);
        map.put("NNXT_BUS_ID", nnxtBusId);

        map.put("PPRV_BUS_NAME", pprvBusName);
        map.put("PPRV_REL_SEQ", pprvRelSeq);
        map.put("PPRV_STN_NAME", pprvStnName);
        map.put("PPRV_DIST", pprvDist);
        map.put("PPRV_TIME", pprvTime);

        map.put("PREV_BUS_NAME", prevBusName);
        map.put("PREV_REL_SEQ", prevRelSeq);
        map.put("PREV_STN_NAME", prevStnName);
        map.put("PREV_DIST", prevDist);
        map.put("PREV_TIME", prevTime);

        map.put("NEXT_BUS_NAME", nextBusName);
        map.put("NEXT_REL_SEQ", nextRelSeq);
        map.put("NEXT_STN_NAME", nextStnName);
        map.put("NEXT_DIST", nextDist);
        map.put("NEXT_TIME", nextTime);

        map.put("NNXT_BUS_NAME", nnxtBusName);
        map.put("NNXT_REL_SEQ", nnxtRelSeq);
        map.put("NNXT_STN_NAME", nnxtStnName);
        map.put("NNXT_DIST", nnxtDist);
        map.put("NNXT_TIME", nnxtTime);

        return map;
    }
}
