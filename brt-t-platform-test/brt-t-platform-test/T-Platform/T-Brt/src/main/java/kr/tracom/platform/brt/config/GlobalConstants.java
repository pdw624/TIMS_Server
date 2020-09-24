package kr.tracom.platform.brt.config;

import kr.tracom.platform.common.config.AppConfig;

public class GlobalConstants {
	private static final GlobalConstants instance = new GlobalConstants();
	
	private static String ROOT_PATH;
	
	private GlobalConstants() {
		ROOT_PATH = AppConfig.get("sftp.server.root.path");
	}
	
	public static GlobalConstants getInstance() {
		return instance;
	}
	
	public String getServerRootPath() {
		return ROOT_PATH;
	}
}
