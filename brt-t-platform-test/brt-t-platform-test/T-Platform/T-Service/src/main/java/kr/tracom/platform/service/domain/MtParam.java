package kr.tracom.platform.service.domain;

import lombok.Data;

@Data
public class MtParam {
    private String appId;
    private String paramKey;
    private String paramValue;
    private String remark;
    private String enable;
}
