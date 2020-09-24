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
@TimsAttribute(attributeId = AtCode.FTP_RESULT_RES)
public class AtFtpResultResponse extends AtData {
	public static final int SIZE = 1;

	private byte result;

	public AtFtpResultResponse() {
		attrId = AtCode.FTP_RESULT_RES;
	}	

	public int getSize() {
		return SIZE;
	}

	public void decode(ByteHelper byteHelper) {
		this.result = byteHelper.getByte();
	}

	public void encode(ByteHelper byteHelper) {
		byteHelper.setByte(this.result);
	}	

	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("RESULT", this.result);
		
		return map;
	}
}
