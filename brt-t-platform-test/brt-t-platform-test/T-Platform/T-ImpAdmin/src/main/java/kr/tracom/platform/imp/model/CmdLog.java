package kr.tracom.platform.imp.model;

import lombok.Data;

@Data
public class CmdLog {
	private String appID;
	private int seq;
	private String sessionID;
	private String message;
	private String logDt;
	private String category;
}
