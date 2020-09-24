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
@TimsAttribute(attributeId = AtCode.FTP_RESULT_REQ)
public class AtFtpResultRequest extends AtData {
	public static final int SIZE = 194;

	private byte result;
	private byte operation;
	private String sourcePath;
	private String sourceFile;
	private String destinationPath;
	private String destinationFile;

	public AtFtpResultRequest() {
		attrId = AtCode.FTP_RESULT_REQ;
	}	

	public int getSize() {
		return SIZE;
	}

	public void decode(ByteHelper byteHelper) {
		this.result = byteHelper.getByte();
		this.operation = byteHelper.getByte();
		this.sourcePath = byteHelper.getString(64);
		this.sourceFile = byteHelper.getString(32);
		this.destinationPath = byteHelper.getString(64);
		this.destinationFile = byteHelper.getString(32);
	}

	public void encode(ByteHelper byteHelper) {
		byteHelper.setByte(this.result);
		byteHelper.setByte(this.operation);
		byteHelper.setString(this.sourcePath, 64);
		byteHelper.setString(this.sourceFile, 32);
		byteHelper.setString(this.destinationPath, 64);
		byteHelper.setString(this.destinationFile, 32);
	}	

	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("RESULT", this.result);
		map.put("OPER_TYPE", this.operation);
		map.put("SRC_PATH", this.sourcePath);
		map.put("SRC_FILE", this.sourceFile);
		map.put("DST_PATH", this.destinationPath);
		map.put("DST_FILE", this.destinationFile);
		
		return map;
	}
}
