package kr.tracom.platform.service.job;

import lombok.Data;

@Data
public class JobArgs {
    private String appId;
    private String period;
    private String exeType;
}
