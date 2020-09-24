package kr.tracom.platform.attribute.brt;

import java.util.HashMap;
import java.util.Map;

import kr.tracom.platform.net.protocol.TimsLogBuilder;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AtDeviceStatusItem extends AtData {
	public static final int SIZE = 29;
	
	private String deviceId;
	private byte statusCode;
	private String version;
	
	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.deviceId = byteHelper.getString(16);
		this.statusCode = byteHelper.getByte();
		this.version = byteHelper.getString(12);
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setString(this.deviceId, 16);
		byteHelper.setByte(this.statusCode);
		byteHelper.setString(this.version, 12);
	}

	@Override
	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("DEVICE_ID", this.deviceId);
		map.put("STATUS_CODE", this.statusCode);
		map.put("VERSION", this.version);
		
		return map;
	}
}
