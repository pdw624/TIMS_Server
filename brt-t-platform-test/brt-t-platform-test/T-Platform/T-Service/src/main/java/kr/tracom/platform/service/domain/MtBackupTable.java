package kr.tracom.platform.service.domain;

import lombok.Data;

@Data
public class MtBackupTable {
    private String appId;
    private String platformTableName;
    private String serviceTableName;
    private String fieldList;
    private String type;
    private String period;
    private String tableKey;
    private String dbLinkMapper;
    private String deleteMapper;
    private String selectWhere;
}
