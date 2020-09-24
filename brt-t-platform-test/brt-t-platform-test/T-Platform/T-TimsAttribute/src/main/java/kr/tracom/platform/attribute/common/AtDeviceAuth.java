package kr.tracom.platform.attribute.common;

import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.net.protocol.TimsAttribute;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
@TimsAttribute(attributeId = AtCode.DEVICE_AUTH)
public class AtDeviceAuth extends AtData {
	public static final int SIZE = 14;
	
	private String deviceId;
	private byte[] accessKey;
	
	public AtDeviceAuth() {
		attrId = AtCode.DEVICE_AUTH;
		accessKey = new byte[4];
	}	

	public int getSize() {
		return SIZE;
	}

	public void decode(ByteHelper byteHelper) {
		this.deviceId = byteHelper.getString(10);
		this.accessKey = byteHelper.getBytes(4);
	}

	public void encode(ByteHelper byteHelper) {
		byteHelper.setString(deviceId, 10);
		byteHelper.setBytes(accessKey, 4);
	}	

	public String getLog() {
		return String.format("%s, %s", deviceId, ByteHelper.toHex(accessKey));
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("SESSION_ID", this.deviceId);
		map.put("ACCESS_KEY", ByteHelper.toHex(accessKey));

		return map;
	}
}
