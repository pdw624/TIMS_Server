package kr.tracom.platform.launcher.http.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonBuilder {
    public static final int SUCS_CD = 1;
    public static final int FAIL_CD = 0;

    public static final String SUCS_MESSAGE = "Success";

    public static final String FAIL_TO_LOGIN = "Invalid ID/PW";
    public static final String FAIL_NOT_CONNECTED = "Not connected device";
    public static final String FAIL_TIMEOUT = "Timeout exception";
    public static final String FAIL_OVER_CMD = "Command too many";
    public static final String FAIL_SHUTDOWN = "Launcher shutdown";
    public static final String FAIL_INVALID_TYPE = "Invalid device type";
    public static final String FAIL_INVALID_PARAM = "Invalid parameter";
    public static final String FAIL_INVALID_DATA = "Invalid data";
    public static final String FAIL_INVALID_FILE_FORMAT = "Invalid file format";
    public static final String FAIL_UNKNOWN = "Unknown error";

    public static String toJsonP(String callback, String json) {
        return String.format("%s(%s)", callback, json);
    }

    public static String pertty(Object value) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }

    public static String getJsonFormat(String callback, int code, String message, String... args) {
        if(callback == null || callback.isEmpty()) {
            return getJsonP(callback, code, message, args);
        } else {
            return getJson(code, message, args);
        }
    }

    public static String getJsonP(String callback, int code, String message, String... args) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("ret_code", code);
        jsonMap.put("ret_message", message);

        for(int i=0; i<args.length; i=i+2) {
            jsonMap.put(args[i], args[i + 1]);
        }

        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            json = "{\"ret_code\":0, \"ret_message\":\"Unknown Error\"}";
        }

        return toJsonP(callback, json);
    }

    public static String getJson(int code, String message, String... args) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("ret_code", code);
        jsonMap.put("ret_message", message);

        for(int i=0; i<args.length; i=i+2) {
            jsonMap.put(args[i], args[i + 1]);
        }

        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            json = "{\"ret_code\":0, \"ret_message\":\"Unknown Error\"}";
        }

        return json;
    }

    public static String getJson(int total, List<Object> rows) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("total", total);
        jsonMap.put("rows", rows);

        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            json = "{\"ret_code\":0, \"ret_message\":\"Unknown Error\"}";
        }

        return json;
    }

    public static String getJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            json = "{\"ret_code\":0, \"ret_message\":\"Unknown Error\"}";
        }

        return json;
    }
}
