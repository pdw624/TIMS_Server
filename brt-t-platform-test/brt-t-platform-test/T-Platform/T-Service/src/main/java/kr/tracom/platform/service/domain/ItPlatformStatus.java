package kr.tracom.platform.service.domain;

import kr.tracom.platform.db.base.BaseDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ItPlatformStatus implements BaseDTO {
	private String platformId;
	private String runDateTime;
	private int elapsedTime;
	private String cpuUsage;
	private String ramUsage;
	private String hddUsage;
	private String jvmUsage;
	private String updateDateTime;
	
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("PLF_ID", platformId);
        map.put("RUN_DT", runDateTime);
        map.put("ELAPSED_TIME", elapsedTime);
        map.put("CPU_USAGE", cpuUsage);
        map.put("RAM_USAGE", ramUsage);
        map.put("HDD_USAGE", hddUsage);
        map.put("JVM_USAGE", jvmUsage);
        map.put("UPD_DT", updateDateTime);

		return map;
	}
}
