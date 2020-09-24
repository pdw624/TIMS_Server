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
@TimsAttribute(attributeId = AtCode.TIMESTAMP_RESPONSE)
public class AtTimeStampResponse extends AtData {
	public static final int SIZE = 15;

	private byte resultCode;
	private AtTimeStamp prevTimeStamp;
	private AtTimeStamp nextTimeStamp;

	public AtTimeStampResponse() {
		attrId = AtCode.TIMESTAMP_RESPONSE;
		resultCode = 0x00;

		this.prevTimeStamp = new AtTimeStamp();
		this.nextTimeStamp = new AtTimeStamp();
	}
	
	public int getSize() {		
		return SIZE;
	}
	
	public void decode(ByteHelper byteHelper) {
		this.resultCode = byteHelper.getByte();
		this.prevTimeStamp.decode(byteHelper);
		this.nextTimeStamp.decode(byteHelper);
	}

	public void encode(ByteHelper byteHelper) {
		byteHelper.setByte(this.resultCode);
		this.prevTimeStamp.encode(byteHelper);
		this.nextTimeStamp.encode(byteHelper);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("RET_CODE", this.resultCode);
		map.put("PREV_TS", this.prevTimeStamp.getTimestamp());
		map.put("NEXT_TS", this.nextTimeStamp.getTimestamp());
		
		return map;
	}

	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}
}
