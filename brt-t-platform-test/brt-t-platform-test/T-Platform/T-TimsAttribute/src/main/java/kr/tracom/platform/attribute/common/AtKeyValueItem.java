package kr.tracom.platform.attribute.common;

import kr.tracom.platform.net.protocol.TimsLogBuilder;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class AtKeyValueItem extends AtData {
	public static final int SIZE = 3;
	
	private byte code;
	private short value;	
	
	public AtKeyValueItem() {
		
	}

	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.code = byteHelper.getByte();
		this.value = byteHelper.getShort();
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setByte(this.code);
		byteHelper.setShort(this.value);
	}

	@Override
	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("CODE", ByteHelper.unsigned(code));
		map.put("VALUE", ByteHelper.unsigned(value));
		
		return map;
	}
}
