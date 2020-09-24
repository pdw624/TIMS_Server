package kr.tracom.platform.attribute.imp;

import kr.tracom.platform.attribute.common.AtTimeStamp;
import kr.tracom.platform.net.protocol.TimsLogBuilder;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class AtProcessItem extends AtData {
    public static final int SIZE = 200;

    private int processId;
    private int processIndex;
    private byte versionHigh;
    private byte versionMiddle;
    private byte versionLow;
    private String processName;
    private String buildDate;
    private int policy;
    private int priority;
    private String fileName;
    private String creationOption;
    private int maxWaitTime;
    private byte operationFlag;
    private String startTime;
    private String endTime;
    private String lastRunTime;
    private int lastRunTick;
    private String statusScript;
    private String note;

    public AtProcessItem() {

    }

    public int getSize() {
        return SIZE;
    }

    public void decode(ByteHelper byteHelper) {
        this.processId = byteHelper.getInt();
        this.processIndex = byteHelper.getInt();
        this.versionHigh = byteHelper.getByte();
        this.versionMiddle = byteHelper.getByte();
        this.versionLow = byteHelper.getByte();
        this.processName = byteHelper.getString(32);
        this.buildDate = AtTimeStamp.getTimeStamp(byteHelper);
        this.policy = byteHelper.getInt();
        this.priority = byteHelper.getInt();
        this.fileName = byteHelper.getString(32);
        this.creationOption = byteHelper.getString(16);
        this.maxWaitTime = byteHelper.getInt();
        this.operationFlag = byteHelper.getByte();
        this.startTime = AtTimeStamp.getTimeStamp(byteHelper);
        this.endTime = AtTimeStamp.getTimeStamp(byteHelper);
        this.lastRunTime = AtTimeStamp.getTimeStamp(byteHelper);
        this.lastRunTick = byteHelper.getInt();
        this.statusScript = byteHelper.getString(32);
        this.note = byteHelper.getString(32);
    }

    public void encode(ByteHelper byteHelper) {
        byteHelper.setInt(this.processId);
        byteHelper.setInt(this.processIndex);
        byteHelper.setByte(this.versionHigh);
        byteHelper.setByte(this.versionMiddle);
        byteHelper.setByte(this.versionLow);
        byteHelper.setString(this.processName, 32);
        AtTimeStamp.setTimeStamp(this.buildDate, byteHelper);
        byteHelper.setInt(this.policy);
        byteHelper.setInt(priority);
        byteHelper.setString(this.fileName, 32);
        byteHelper.setString(this.creationOption, 16);
        byteHelper.setInt(this.maxWaitTime);
        byteHelper.setByte(this.operationFlag);
        AtTimeStamp.setTimeStamp(this.startTime, byteHelper);
        AtTimeStamp.setTimeStamp(this.endTime, byteHelper);
        AtTimeStamp.setTimeStamp(this.lastRunTime, byteHelper);
        byteHelper.setInt(this.lastRunTick);
        byteHelper.setString(this.statusScript, 32);
        byteHelper.setString(this.note, 32);
    }

    public String getLog() {
        return TimsLogBuilder.getLog(this);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("P_ID", processId);
        map.put("P_INDEX", processIndex);
        map.put("VER_HGH", versionHigh);
        map.put("VER_MID", versionMiddle);
        map.put("VER_LOW", versionLow);
        map.put("P_NAME", processName);
        map.put("BUILD_DATE", buildDate);
        map.put("POLICY", policy);
        map.put("PRIOTY", priority);
        map.put("FILE_NM", fileName);
        map.put("RUN_OPT", creationOption);
        map.put("START_TIME", startTime);
        map.put("END_TIME", endTime);
        map.put("LAST_RUN_TIME", lastRunTime);
        map.put("LAST_RUN_TICK", lastRunTick);
        map.put("STATUS_SCRIPT", statusScript);
        map.put("NOTE", note);

        return map;
    }
}
