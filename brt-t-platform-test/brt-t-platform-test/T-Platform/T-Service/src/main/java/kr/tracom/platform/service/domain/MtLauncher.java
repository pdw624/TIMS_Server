package kr.tracom.platform.service.domain;

import lombok.Data;

@Data
public class MtLauncher {
	private String platformId;
	private String ip;
	private String city;
	private String langCode;
	private String timeZone;
	private String dateTimeFormat;
	private String dateFormat;
	private String timeFormat;
	private String logLevel;
	private String sinceDate;
}
