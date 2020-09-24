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
@TimsAttribute(attributeId = AtCode.FTP_NOTIFY_REQ)
public class AtFtpNotifyRequest extends AtData {
	public static final int SIZE = 289;
	
	private String ftpIp;
	private short ftpPort;
	private String userId;
	private String password;
	private byte ftpMode;
	private byte protocol;
	private byte transferMode;
	private byte encryption;
	private byte operation;
	private byte fileAttribute;
	private String sourcePath;
	private String sourceFile;
	private String destinationPath;
	private String destinationFile;
	
	public AtFtpNotifyRequest() {
		attrId = AtCode.FTP_NOTIFY_REQ;
		destinationPath = "";
		destinationFile = "";
	}	

	public int getSize() {
		return SIZE;
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.ftpIp = byteHelper.getString(25);
		this.ftpPort = byteHelper.getShort();
		this.userId = byteHelper.getString(32);
		this.password = byteHelper.getString(32);
		this.ftpMode = byteHelper.getByte();
		this.protocol = byteHelper.getByte();
		this.transferMode = byteHelper.getByte();
		this.encryption = byteHelper.getByte();
		this.operation = byteHelper.getByte();
		this.fileAttribute = byteHelper.getByte();
		this.sourcePath = byteHelper.getString(64);
		this.sourceFile = byteHelper.getString(32);
		this.destinationPath = byteHelper.getString(64);
		this.destinationFile = byteHelper.getString(32);
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setString(this.ftpIp, 25);
		byteHelper.setShort(this.ftpPort);
		byteHelper.setString(this.userId, 32);
		byteHelper.setString(this.password, 32);
		byteHelper.setByte(this.ftpMode);
		byteHelper.setByte(this.protocol);
		byteHelper.setByte(this.transferMode);
		byteHelper.setByte(this.encryption);
		byteHelper.setByte(this.operation);
		byteHelper.setByte(this.fileAttribute);
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
		map.put("FTP_IP", this.ftpIp);
		map.put("FTP_PORT", this.ftpPort);
		map.put("USER_ID", this.userId);
		map.put("USER_PW", this.password);
		map.put("FTP_MODE", this.ftpMode);
		map.put("PRTL_TYPE", this.protocol);
		map.put("TRNS_MODE", this.transferMode);
		map.put("ENC_TYPE", this.encryption);
		map.put("OPER_TYPE", this.operation);
		map.put("FILE_ATTR", this.fileAttribute);
		map.put("SRC_PATH", this.sourcePath);
		map.put("SRC_FILE", this.sourceFile);
		map.put("DST_PATH", this.destinationPath);
		map.put("DST_FILE", this.destinationFile);
		
		return map;
	}
}
