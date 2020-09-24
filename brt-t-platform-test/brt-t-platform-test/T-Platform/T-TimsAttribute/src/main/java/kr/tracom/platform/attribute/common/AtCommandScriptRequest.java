package kr.tracom.platform.attribute.common;

import java.util.HashMap;
import java.util.Map;

import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.net.protocol.TimsAttribute;
import kr.tracom.platform.net.protocol.TimsLogBuilder;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@TimsAttribute(attributeId = AtCode.CMD_REQ)
public class AtCommandScriptRequest extends AtData{

	public static final int SIZE = 514;
	
	private short length;
	private String command;
	
	public AtCommandScriptRequest() {
		attrId = AtCode.CMD_REQ;
		length = 0;
		command = "";
	}
	
	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.length = byteHelper.getShort();
		this.command = byteHelper.getString(512);
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setShort(this.length);
		byteHelper.setString(this.command, 512);
	}

	@Override
	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("LENGTH", this.length);
		map.put("COMMAND", this.command);
		
		return map;
	}
	
}
