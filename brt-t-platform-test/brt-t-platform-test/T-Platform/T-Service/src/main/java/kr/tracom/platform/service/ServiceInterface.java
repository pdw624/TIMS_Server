package kr.tracom.platform.service;

import kr.tracom.platform.service.model.ServiceGetArgs;
import kr.tracom.platform.service.model.ServiceSetArgs;

public interface ServiceInterface {
	void startup();
	void shutdown();
	void login(String sessionId, String sessionIp);
	void logout(String sessionId, String sessionIp);
	void setServiceArgs(ServiceSetArgs args);
	ServiceGetArgs getServiceGetArgs();
}
