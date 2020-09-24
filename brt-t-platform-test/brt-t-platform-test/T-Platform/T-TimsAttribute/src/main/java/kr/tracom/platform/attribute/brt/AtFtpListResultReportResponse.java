package kr.tracom.platform.attribute.brt;

import java.util.HashMap;
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
@TimsAttribute(attributeId = BrtAtCode.FTP_LIST_RESULT_REPORT_RESPONSE)
public class AtFtpListResultReportResponse extends AtData {
	public static final int SIZE = 24;
	
	private byte resultCode;
	private String reservationId;
	private String deviceId;
	
	public AtFtpListResultReportResponse() {
		attrId = BrtAtCode.FTP_LIST_RESULT_REPORT_RESPONSE;
	}
	
	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.resultCode = byteHelper.getByte();
		this.reservationId = byteHelper.getString(7);
		this.deviceId = byteHelper.getString(16);
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setByte(this.resultCode);
		byteHelper.setString(this.reservationId, 7);
		byteHelper.setString(this.deviceId, 16);
	}

	@Override
	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("RESULT_CODE", this.resultCode);
		map.put("RSV_ID", this.reservationId);
		map.put("MNG_ID", this.deviceId);
		
		return map;
	}
}
