package kr.tracom.platform.attribute.common;

import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.net.protocol.TimsAttribute;
import kr.tracom.platform.net.protocol.TimsLogBuilder;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
@TimsAttribute(attributeId = AtCode.DEVICE_AUTH_RESULT)
public class AtDeviceAuthResult extends AtData {
	public static final int SIZE = 18;
	
	private String deviceId;
	private byte result;
	private AtTimeStamp timeStamp;
	
	public AtDeviceAuthResult() {
		attrId = AtCode.DEVICE_AUTH_RESULT;
		timeStamp = new AtTimeStamp();
	}	

	public int getSize() {
		return SIZE;
	}

	public void decode(ByteHelper byteHelper) {
		this.deviceId = byteHelper.getString(10);
		this.result = byteHelper.getByte();
		this.timeStamp.decode(byteHelper);
	}

	public void encode(ByteHelper byteHelper) {
		byteHelper.setString(this.deviceId, 10);
		byteHelper.setByte(this.result);
		this.timeStamp.encode(byteHelper);
	}

	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("DEVICE_ID", this.deviceId);
		map.put("RESULT", this.result);
		map.put("TIMESTAMP", this.timeStamp.getDateTime());
		
		return map;
	}
}
