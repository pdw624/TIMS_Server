package kr.tracom.platform.service.manager;

import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.dao.PlatformMapper;
import kr.tracom.platform.service.domain.MtRouting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutingManager {
    public static int getGroupId(String clientId) {
        PlatformDao dao = new PlatformDao();
        MtRouting mtRouting = (MtRouting)dao.select(PlatformMapper.ROUTING_GROUP_ID,
                dao.buildMap("SYS_NAME", clientId));

        return mtRouting.getGroupId();
    }

    public static List<Object> getSystemName(String groupNameWhere) {
        String[] grpNames = groupNameWhere.split(",");
        Map<String, Object> map = new HashMap<>();
        map.put("GRP_LIST", grpNames);

        PlatformDao dao = new PlatformDao();
        List<Object> list = dao.selectList(PlatformMapper.ROUTING_SYSTEM_LIST, map);

        return list;
    }

    /*
    public static MtRouting getSessionId(String clientId, String serviceId) {
        PlatformDao dao = new PlatformDao();
        MtRouting mtRouting = (MtRouting)dao.select(PlatformMapper.ROUTING_SELECT_ID,
                dao.buildMap("SYS_NAME", clientId, "SVC_NAME", serviceId));

        return mtRouting;
    }
    */
}
