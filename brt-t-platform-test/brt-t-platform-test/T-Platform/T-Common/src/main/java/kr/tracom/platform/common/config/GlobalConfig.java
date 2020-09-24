package kr.tracom.platform.common.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalConfig {
    public static Map<String, Object> configMap = new ConcurrentHashMap<>();
}
