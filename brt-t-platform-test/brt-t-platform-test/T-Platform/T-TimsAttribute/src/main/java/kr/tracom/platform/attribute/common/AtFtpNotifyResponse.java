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
@TimsAttribute(attributeId = AtCode.FTP_NOTIFY_RES)
public class AtFtpNotifyResponse extends AtData {
	public static final int SIZE = 290;

	private byte result;
	private String ip;
	private short port;
	private String userId;
	private String password;
	private byte ftpMode;
	private byte protocol;
	private byte transferMode;
	private byte encryption;
	private byte operation;
	private byte fileCode;
	private String sourcePath;
	private String sourceFile;
	private String destinationPath;
	private String destinationFile;

	public AtFtpNotifyResponse() {
		attrId = AtCode.FTP_NOTIFY_RES;
	}	

	public int getSize() {
		return SIZE;
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.result = byteHelper.getByte();
		this.ip = byteHelper.getString(25);
		this.port = byteHelper.getShort();
		this.userId = byteHelper.getString(32);
		this.password = byteHelper.getString(32);
		this.ftpMode = byteHelper.getByte();
		this.protocol = byteHelper.getByte();
		this.transferMode = byteHelper.getByte();
		this.encryption = byteHelper.getByte();
		this.operation = byteHelper.getByte();
		this.fileCode = byteHelper.getByte();
		this.sourcePath = byteHelper.getString(64);
		this.sourceFile = byteHelper.getString(32);
		this.destinationPath = byteHelper.getString(64);
		this.destinationFile = byteHelper.getString(32);
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setByte(this.result);
		byteHelper.setString(this.ip, 25);
		byteHelper.setShort(this.port);
		byteHelper.setString(this.userId, 32);
		byteHelper.setString(this.password, 32);
		byteHelper.setByte(this.ftpMode);
		byteHelper.setByte(this.protocol);
		byteHelper.setByte(this.transferMode);
		byteHelper.setByte(this.encryption);
		byteHelper.setByte(this.operation);
		byteHelper.setByte(this.fileCode);
		byteHelper.setString(this.sourcePath, 64);
		byteHelper.setString(this.sourceFile, 32);
		byteHelper.setString(this.destinationPath, 64);
		byteHelper.setString(this.destinationFile, 32);
	}

	@Override
	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}
	
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("RESULT", this.result);
		map.put("IP", this.ip);
		map.put("PORT", this.port);
		map.put("USER_ID", this.userId);
		map.put("USER_PW", this.password);
		map.put("FTP_MODE", this.ftpMode);
		map.put("PRTL_TYPE", this.protocol);
		map.put("TRNS_MODE", this.transferMode);
		map.put("ENC_TYPE", this.encryption);
		map.put("OPER_TYPE", this.operation);
		map.put("FILE_CODE", this.fileCode);
		map.put("SRC_PATH", this.sourcePath);
		map.put("SRC_FILE", this.sourceFile);
		map.put("DST_PATH", this.destinationPath);
		map.put("DST_FILE", this.destinationFile);
		
		return map;
	}
}
