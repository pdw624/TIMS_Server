package kr.tracom.platform.service.domain;

import kr.tracom.platform.db.base.BaseDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class HtErrorLog implements BaseDTO {
	private String appId;
	private String logDate;
	private String className;
	private String methodName;
	private String parameterData;
	private String errorMessage;
	private String systemDateTime;

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("APP_ID", appId);
		paramMap.put("LOG_DT", logDate);
		paramMap.put("CLASS", className);
		paramMap.put("METHOD", methodName);
		paramMap.put("PARAM", parameterData);
		paramMap.put("MESSAGE", errorMessage);
		paramMap.put("SYS_DT", systemDateTime);
		return paramMap;
	}
}
