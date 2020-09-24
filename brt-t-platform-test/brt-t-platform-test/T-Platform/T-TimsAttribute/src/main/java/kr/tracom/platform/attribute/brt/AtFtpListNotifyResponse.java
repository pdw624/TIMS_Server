package kr.tracom.platform.attribute.brt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.tracom.platform.attribute.BrtAtCode;
import kr.tracom.platform.net.protocol.TimsAttribute;
import kr.tracom.platform.net.protocol.TimsLogBuilder;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@TimsAttribute(attributeId = BrtAtCode.FTP_LIST_NOTIFICATION_RESPONSE)
public class AtFtpListNotifyResponse extends AtData {
public static final int SIZE = 122;
	
	private byte resultCode;
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
	private String reservationId;
	private String deviceId;
	private byte count;
	private List<AtFtpListPathItem> list;
	
	public AtFtpListNotifyResponse() {
		attrId = BrtAtCode.FTP_LIST_NOTIFICATION_RESPONSE;
		list = new ArrayList<AtFtpListPathItem>();
	}	

	public int getSize() {
		return SIZE + (count * AtFtpListPathItem.SIZE);
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.resultCode = byteHelper.getByte();
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
		this.reservationId = byteHelper.getString(7);
		this.deviceId = byteHelper.getString(16);
		this.count = byteHelper.getByte();
		
		short loop = ByteHelper.unsigned(this.count);
        for(short i = 0; i < loop; i++) {
        	AtFtpListPathItem item = new AtFtpListPathItem();
            item.decode(byteHelper);
            
            list.add(item);
        }
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setByte(this.resultCode);
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
		byteHelper.setString(this.reservationId, 7);
		byteHelper.setString(this.deviceId, 16);
		byteHelper.setByte(this.count);
		
		for(AtFtpListPathItem item : list) {
            item.encode(byteHelper);
        }
	}

	@Override
	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}
	
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("RESULT_CODE", this.resultCode);
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
		map.put("RSV_ID", this.reservationId);
		map.put("MNG_ID", this.deviceId);
		
		return map;
	}
}
