package kr.tracom.platform.service.model.json;

import lombok.Data;

@Data
public class JsonIlluminationItem {
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private byte value;
}
