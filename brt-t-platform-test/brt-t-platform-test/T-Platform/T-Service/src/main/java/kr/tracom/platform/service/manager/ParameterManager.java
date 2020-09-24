package kr.tracom.platform.service.manager;

import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.dao.PlatformMapper;

public class ParameterManager {

    private static String getValue(String appId, String paramKey) {
        PlatformDao platformDao = new PlatformDao();
        String value = (String)platformDao.select(PlatformMapper.PARAMETER_GETVALUE,
                platformDao.buildMap("APP_ID", appId, "PARAM_KEY", paramKey));

        return value;
    }

    public static String getString(String appId, String paramKey) {
        return getValue(appId, paramKey);
    }

    public static int getInt(String appId, String paramKey) {
        return Integer.parseInt(getValue(appId, paramKey));
    }

    public static double getDouble(String appId, String paramKey) {
        return Double.parseDouble(getValue(appId, paramKey));
    }
}
