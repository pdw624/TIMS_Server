package kr.tracom.platform.attribute.brt;

import java.util.HashMap;
import java.util.Map;

import kr.tracom.platform.attribute.BrtAtCode;
import kr.tracom.platform.attribute.common.AtTimeStamp;
import kr.tracom.platform.net.protocol.TimsAttribute;
import kr.tracom.platform.net.protocol.TimsLogBuilder;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@TimsAttribute(attributeId = BrtAtCode.DEVICE_LOCATION_INFO)
public class AtDeviceLocation extends AtData {
	public static final int SIZE = 71;

	private AtTimeStamp gpsTimeStamp;
	private String latitude;
	private String longitude;
	private int speed;
	private int heading;
	private String deviceId;
	private String employeeId;
	private String routeId;
	
	public AtDeviceLocation() {
		attrId = BrtAtCode.DEVICE_LOCATION_INFO;
		gpsTimeStamp = new AtTimeStamp();
	}

	public int getSize() {
		return SIZE;
	}

	public void decode(ByteHelper byteHelper) {
		this.gpsTimeStamp.decode(byteHelper);
		this.latitude = byteHelper.getString(12);
		this.longitude = byteHelper.getString(12);
		this.speed = byteHelper.getInt();
		this.heading = byteHelper.getInt();
		this.deviceId = byteHelper.getString(16);
		this.employeeId = byteHelper.getString(6);
		this.routeId = byteHelper.getString(10);
	}

	public void encode(ByteHelper byteHelper) {
		this.gpsTimeStamp.encode(byteHelper);
        byteHelper.setString(this.latitude, 12);
        byteHelper.setString(this.longitude, 12);
        byteHelper.setInt(this.speed);
        byteHelper.setInt(this.heading);
        byteHelper.setString(this.deviceId, 16);
        byteHelper.setString(this.employeeId, 6);
        byteHelper.setString(this.routeId, 10);
	}
	
	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("GPS_DT", this.gpsTimeStamp.getDateTime());
		map.put("LATITUDE", this.latitude);
		map.put("LONGITUDE", this.longitude);
		map.put("SPEED", this.speed);
		map.put("HEADING", this.heading);
		map.put("DEVICE_ID", this.deviceId);
		map.put("EMPLOYEE_ID", this.employeeId);
		map.put("ROUTE_ID", this.routeId);
		
		return map;
	}
}
