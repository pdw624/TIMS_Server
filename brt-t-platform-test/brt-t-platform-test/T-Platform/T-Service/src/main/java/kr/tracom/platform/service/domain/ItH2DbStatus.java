package kr.tracom.platform.service.domain;

import lombok.Data;

@Data
public class ItH2DbStatus {
    private String nowDateTime;
    private int memoryFree;
    private int memoryUsed;
}
