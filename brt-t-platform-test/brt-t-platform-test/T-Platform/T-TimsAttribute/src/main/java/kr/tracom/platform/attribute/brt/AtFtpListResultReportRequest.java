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
@TimsAttribute(attributeId = BrtAtCode.FTP_LIST_RESULT_REPORT_REQUEST)
public class AtFtpListResultReportRequest extends AtData {
	public static final int SIZE = 26;
	
	private byte resultCode;
	private byte operation;
	private String reservationId;
	private String deviceId;
	private byte count;
	private List<AtFtpListPathItem> list;
	
	public AtFtpListResultReportRequest() {
		attrId = BrtAtCode.FTP_LIST_RESULT_REPORT_REQUEST;
		list = new ArrayList<AtFtpListPathItem>();
	}
	
	@Override
	public int getSize() {
		return SIZE + (count * AtFtpListPathItem.SIZE);
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.resultCode = byteHelper.getByte();
		this.operation = byteHelper.getByte();
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
		byteHelper.setByte(this.operation);
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
		map.put("OPER_TYPE", this.operation);
		map.put("RSV_ID", this.reservationId);
		map.put("MNG_ID", this.deviceId);
		
		return map;
	}
}
