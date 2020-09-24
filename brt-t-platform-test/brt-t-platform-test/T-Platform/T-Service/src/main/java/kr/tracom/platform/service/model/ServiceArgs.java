package kr.tracom.platform.service.model;

import kr.tracom.platform.service.ServiceLauncher;
import lombok.Data;

import java.util.List;

@Data
public class ServiceArgs {
    private String id;
    private String name;
    private String version;
    private String attribute;
    private String launcher;
    private String enable;
    private String licenseStartDate;
    private String licenseEndDate;

    private List<Short> attributeList;
    private ServiceLauncher serviceLauncher;
}
