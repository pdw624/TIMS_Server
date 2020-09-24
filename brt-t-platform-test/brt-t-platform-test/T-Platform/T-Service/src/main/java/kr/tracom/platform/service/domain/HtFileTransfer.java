package kr.tracom.platform.service.domain;

import kr.tracom.platform.db.base.BaseDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class HtFileTransfer implements BaseDTO {
    private String plfId;
    private String sessionId;
    private String fileName;
    private String fileCode;
    private String sendType;
    private String filePath;
    private int fileSize;
    private int filePointer;
    private String sendState;
    private String startDateTime;
    private String endDateTime;
    private String systemDateTime;

    public Map<String, Object> toMap() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("PLF_ID", plfId);
        paramMap.put("SESSION_ID", sessionId);
        paramMap.put("FILE_NAME", fileName);
        paramMap.put("FILE_CD", fileCode);
        paramMap.put("FILE_PATH", filePath);
        paramMap.put("FILE_SIZE", fileSize);
        paramMap.put("FILE_POINTER", filePointer);
        paramMap.put("SEND_TYPE", sendType);
        paramMap.put("SEND_STATE", sendState);
        paramMap.put("SEND_ST_DT", startDateTime);
        paramMap.put("SEND_ED_DT", endDateTime);
        paramMap.put("SYS_DT", systemDateTime);

        return paramMap;
    }
}
