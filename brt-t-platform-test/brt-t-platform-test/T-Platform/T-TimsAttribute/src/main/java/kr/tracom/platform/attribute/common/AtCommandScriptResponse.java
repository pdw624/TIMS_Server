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
@TimsAttribute(attributeId = AtCode.CMD_RES)
public class AtCommandScriptResponse extends AtData{

	public static final int SIZE = 1027;
	
	private String cResult;
	private short usLength;
	private String szResp;
	
	public AtCommandScriptResponse() {
		attrId = AtCode.CMD_RES;
		cResult = "";
		usLength = 0;
		szResp = "";
	}
	
	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.cResult = byteHelper.getString(1);
		this.usLength = byteHelper.getShort();
		this.szResp = byteHelper.getString(1024);
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setString(this.cResult, 1);
		byteHelper.setShort(this.usLength);
		byteHelper.setString(this.szResp, 1024);
	}

	@Override
	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("RESULT", this.cResult);
		map.put("LENGTH", this.usLength);
		map.put("RESP", this.szResp);
		
		return map;
	}

}
