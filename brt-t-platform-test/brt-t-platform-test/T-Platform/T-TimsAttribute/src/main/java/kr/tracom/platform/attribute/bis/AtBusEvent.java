package kr.tracom.platform.attribute.bis;

import kr.tracom.platform.attribute.BisAtCode;
import kr.tracom.platform.attribute.common.AtTimeStamp;
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
@TimsAttribute(attributeId = BisAtCode.BUS_EVENT)
public class AtBusEvent extends AtData {
	public static final int SIZE = 77;

	private String impId;
	private String busId;
	private String routeId;
	private String linkId;
	private String eventCode;
	private String eventData;
	private short eventSequence;
	private String runType;
	private String gpsTimeStamp;
	private int latitude;
	private int longitude;
	private short heading;
	private byte speed;
	private byte stopTime;

	public AtBusEvent() {
		attrId = BisAtCode.BUS_EVENT;
		//gpsTimeStamp = new AtTimeStamp();
	}

	public int getSize() {
		return SIZE;
	}

	public void decode(ByteHelper byteHelper) {
		this.impId = byteHelper.getString(10);
		this.busId = byteHelper.getString(10);
		this.routeId = byteHelper.getString(10);
		this.linkId = byteHelper.getString(10);
		this.eventCode = byteHelper.getString(3);
		this.eventData = byteHelper.getString(10);
		this.eventSequence = byteHelper.getShort();
		this.runType = byteHelper.getString(3);
		this.gpsTimeStamp = AtTimeStamp.getTimeStamp(byteHelper);
		this.latitude = byteHelper.getInt();
		this.longitude = byteHelper.getInt();
		this.heading = byteHelper.getShort();
		this.speed = byteHelper.getByte();
		this.stopTime = byteHelper.getByte();
	}

	public void encode(ByteHelper byteHelper) {
		byteHelper.setString(this.impId, 10);
		byteHelper.setString(this.busId, 10);
		byteHelper.setString(this.routeId, 10);
		byteHelper.setString(this.linkId, 10);
		byteHelper.setString(this.eventCode, 3);
		byteHelper.setString(this.eventData, 10);
		byteHelper.setShort(this.eventSequence);
		byteHelper.setString(this.runType, 3);
        //this.gpsTimeStamp.encode(byteHelper);
		AtTimeStamp.setTimeStamp(this.gpsTimeStamp, byteHelper);
        byteHelper.setInt((int)(this.latitude * 1000000.0f));
		byteHelper.setInt((int)(this.longitude * 1000000.0f));
        byteHelper.setShort(this.heading);
		byteHelper.setByte(this.speed);
        byteHelper.setByte(this.stopTime);
	}
	
	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("GPS_DT", this.gpsTimeStamp.substring(0, 14));
		map.put("IMP_ID", this.impId);
		map.put("BUS_ID", this.busId);
		map.put("ROUTE_ID", this.routeId);
		map.put("LINK_ID", this.linkId);
		map.put("EVENT_CD", this.eventCode);
		map.put("EVENT_DATA", this.eventData);
		map.put("EVENT_SEQ", ByteHelper.unsigned(this.eventSequence));
		map.put("RUN_TYPE", this.runType);
		//map.put("LAT", MathUtil.cutDecimal((double)(ByteHelper.unsigned(this.latitude) / 1000000.0f), 6));
		//map.put("LON", MathUtil.cutDecimal((double)(ByteHelper.unsigned(this.longitude) / 1000000.0f), 6));
		map.put("LAT", ByteHelper.unsigned(this.latitude) / 1000000.0f);
		map.put("LON", ByteHelper.unsigned(this.longitude) / 1000000.0f);
		map.put("HEADING", ByteHelper.unsigned(this.heading));
		map.put("SPEED", ByteHelper.unsigned(this.speed));
		map.put("STOP_TM", ByteHelper.unsigned(this.stopTime));
		
		return map;
	}
}
