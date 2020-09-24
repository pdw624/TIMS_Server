package kr.tracom.platform.service.model;

import kr.tracom.platform.net.config.TimsConfig;
import lombok.Data;

@Data
public class ServiceSetArgs {
    private String platformId;
    private TimsConfig timsConfig;
}
