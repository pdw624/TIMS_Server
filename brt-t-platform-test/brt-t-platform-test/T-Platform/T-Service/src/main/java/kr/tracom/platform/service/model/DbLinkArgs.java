package kr.tracom.platform.service.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class DbLinkArgs {
    private String schema;
    private String driver;
    private String url;
    private String id;
    private String pw;

    public Map<String, Object> toMap() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("TG_SCHEMA", schema);
        paramMap.put("TG_DRIVER", driver);
        paramMap.put("TG_URL", url);
        paramMap.put("TG_ID", id);
        paramMap.put("TG_PW", pw);

        return paramMap;
    }
}
