package kr.tracom.platform.service.domain;

import lombok.Data;

@Data
public class MtScheduleJob {
    private String appId;
    private String jobClass;
    private String jobName;
    private String args;
    private String period;
    private String enable;
}
