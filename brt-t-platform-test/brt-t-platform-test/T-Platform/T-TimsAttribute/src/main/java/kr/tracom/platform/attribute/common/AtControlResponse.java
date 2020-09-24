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
@TimsAttribute(attributeId = AtCode.CONTROL_RES)
public class AtControlResponse extends AtData {
	public static final int SIZE = 16;

	private byte resultCode;
	private String deviceId;
	private String code;
	private short value;

	public AtControlResponse() {
		attrId = AtCode.CONTROL_RES;
	}

	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.resultCode = byteHelper.getByte();
		this.deviceId = byteHelper.getString(10);
		this.code = byteHelper.getString(3);
		this.value = byteHelper.getShort();
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setByte(this.resultCode);
		byteHelper.setString(this.deviceId, 10);
		byteHelper.setString(this.code, 3);
		byteHelper.setShort(this.value);
	}

	@Override
	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("RESULT", this.resultCode);
		map.put("DEVICE_ID", this.deviceId);
		map.put("CODE", this.code);
		map.put("VALUE", this.value);
		
		return map;
	}
}
