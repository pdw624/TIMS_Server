package kr.tracom.platform.service.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class BackupArgs {
	private String serviceId;
	private int batchSize;
	private String timeType;
	private String time;
	private String nowDateTime;
	private String serviceTable;
	private String fieldList;
	private String platformTable;
	private String tableKey;
	private String selectWhere;

	public Map<String, Object> toMap() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("BK_GBN", timeType);
		paramMap.put("BK_TIME", time);
		paramMap.put("BK_NOW", nowDateTime);
		paramMap.put("SVC_TABLE", serviceTable);
		paramMap.put("FIELD_LIST", fieldList);
		paramMap.put("PLF_TABLE", platformTable);
		paramMap.put("TABLE_KEY", tableKey);
		paramMap.put("SELECT_WHERE", selectWhere);

		return paramMap;
	}
}
