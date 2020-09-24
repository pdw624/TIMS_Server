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
@TimsAttribute(attributeId = AtCode.TIMESTAMP_REQUEST)
public class AtTimeStampRequest extends AtData {
	public static final int SIZE = 7;

	private AtTimeStamp timeStamp;

	public AtTimeStampRequest() {
		attrId = AtCode.TIMESTAMP_REQUEST;
	}

	public int getSize() {
		return SIZE;
	}

	public void decode(ByteHelper byteHelper) {
		this.timeStamp.decode(byteHelper);
	}

	public void encode(ByteHelper byteHelper) {
		this.timeStamp.encode(byteHelper);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("TIMESTAMP", this.timeStamp.getTimestamp());

		return map;
	}

	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}
}
