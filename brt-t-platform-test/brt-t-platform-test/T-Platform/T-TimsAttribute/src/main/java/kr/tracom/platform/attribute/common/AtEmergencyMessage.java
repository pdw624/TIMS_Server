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
@TimsAttribute(attributeId = AtCode.EMERGENCY_MESSAGE)
public class AtEmergencyMessage extends AtData {
	public static final int SIZE = 134;
	
	private int messageId;
	private byte code;
	private byte option;
	private String text;
	
	public AtEmergencyMessage() {
		attrId = AtCode.EMERGENCY_MESSAGE;
	}	

	public int getSize() {
		return SIZE;
	}

	public void decode(ByteHelper byteHelper) {
		this.messageId = byteHelper.getInt();
		this.code = byteHelper.getByte();
		this.option = byteHelper.getByte();
		this.text = byteHelper.getString(128);
	}

	public void encode(ByteHelper byteHelper) {
		byteHelper.setInt(this.messageId);
		byteHelper.setByte(this.code);
		byteHelper.setByte(this.option);
		byteHelper.setString(this.text, 128);
	}	

	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("MSG_ID", ByteHelper.unsigned(this.messageId));
		map.put("MSG_CODE", this.code);
		map.put("MSG_OPT", this.option);
		map.put("MSG_TEXT", this.text);
		
		return map;
	}
}
