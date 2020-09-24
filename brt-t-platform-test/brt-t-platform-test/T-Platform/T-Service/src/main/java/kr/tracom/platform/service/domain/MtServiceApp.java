package kr.tracom.platform.service.domain;

import lombok.Data;

@Data
public class MtServiceApp {
	private String id;
	private String name;
	private String version;
	private String attribute;
	private String launcher;
	private String enable;
	private String licenseStartDate;
	private String licenseEndDate;
}
