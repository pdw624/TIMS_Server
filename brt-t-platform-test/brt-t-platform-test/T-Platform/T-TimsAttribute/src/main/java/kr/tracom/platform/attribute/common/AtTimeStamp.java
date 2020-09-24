package kr.tracom.platform.attribute.common;

import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.net.protocol.TimsAttribute;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
@TimsAttribute(attributeId = AtCode.TIMESTAMP)
public class AtTimeStamp extends AtData {
	public static final int SIZE = 7;
	
	private byte year;
	private byte month;
	private byte day;
	private byte hour;	
	private byte minute;
	private byte second;
	private byte millisecond;
	
	public AtTimeStamp() {
		attrId = AtCode.TIMESTAMP;
		year = 0x00;
		month = 0x00;
		day = 0x00;
		hour = 0x00;
		minute = 0x00;
		second = 0x00;
		millisecond = 0x00;
	}

	public AtTimeStamp(String nowDatetime) {
		setDateTime(nowDatetime);
	}

	public static String getTimeStamp(ByteHelper byteHelper) {
		byte year = byteHelper.getByte();
		byte month = byteHelper.getByte();
		byte day = byteHelper.getByte();
		byte hour = byteHelper.getByte();
		byte minute = byteHelper.getByte();
		byte second = byteHelper.getByte();
		byte millisecond = byteHelper.getByte();

		return String.format("%04d%02d%02d%02d%02d%02d%02d", year + 2000, month, day, hour, minute, second, millisecond);
	}

	public static void setTimeStamp(String dateTime, ByteHelper byteHelper) {
		byte year = 0;
		byte month = 0;
		byte day = 0;
		byte hour = 0;
		byte minute = 0;
		byte second = 0;
		byte millisecond = 0;

		if (dateTime.length() >= 14) {
			String yyyy = dateTime.substring(0, 4);
			year = (byte) (Integer.parseInt(yyyy) - 2000);
			month = Byte.parseByte(dateTime.substring(4, 6));
			day = Byte.parseByte(dateTime.substring(6, 8));
			hour = Byte.parseByte(dateTime.substring(8, 10));
			minute = Byte.parseByte(dateTime.substring(10, 12));
			second = Byte.parseByte(dateTime.substring(12, 14));

			if (dateTime.length() == 16) {
				millisecond = Byte.parseByte(dateTime.substring(14, 16));
			}
		}

		byteHelper.setByte(year);
		byteHelper.setByte(month);
		byteHelper.setByte(day);
		byteHelper.setByte(hour);
		byteHelper.setByte(minute);
		byteHelper.setByte(second);
		byteHelper.setByte(millisecond);
	}
	
	public String getDate() {
		return String.format("%04d%02d%02d", year + 2000, month, day);
	}
	
	public String getTime() {
		return String.format("%02d%02d%02d", hour, minute, second);
	}
	
	public String getDateTime() {
		return String.format("%04d%02d%02d%02d%02d%02d", year + 2000, month, day, hour, minute, second);
	}
	
	public String getTimestamp() {
		return String.format("%04d%02d%02d%02d%02d%02d%02d", year + 2000, month, day, hour, minute, second, millisecond);
	}
	
	private void setDateTime(String dateTime) {
		if(dateTime.length() >= 14) {
			String yyyy = dateTime.substring(0,4);
			this.year = (byte)(Integer.parseInt(yyyy) - 2000);
			this.month = Byte.parseByte(dateTime.substring(4, 6));
			this.day = Byte.parseByte(dateTime.substring(6, 8));
			this.hour = Byte.parseByte(dateTime.substring(8, 10));
			this.minute = Byte.parseByte(dateTime.substring(10, 12));
			this.second = Byte.parseByte(dateTime.substring(12, 14));
			this.millisecond = 0x00;
		}
	}
	
	public void setTimeStamp(String dateTime) {
		if(dateTime.length() == 14) {
			setDateTime(dateTime);
			this.millisecond = 0;
		} else if(dateTime.length() == 16) {
			setDateTime(dateTime);
			this.millisecond = Byte.parseByte(dateTime.substring(14, 16));
		} else {
			setDateTime(DateTime.now().toString("yyyyMMddHHmmss"));
		}
	}
	
	public int getSize() {		
		return SIZE;
	}
	
	public void decode(ByteHelper byteHelper) {
		this.year = byteHelper.getByte();
		this.month = byteHelper.getByte();
		this.day = byteHelper.getByte();
		this.hour = byteHelper.getByte();
		this.minute = byteHelper.getByte();
		this.second = byteHelper.getByte();
		this.millisecond = byteHelper.getByte();
	}

	public void encode(ByteHelper byteHelper) {
		byteHelper.setByte(this.year);
		byteHelper.setByte(this.month);
		byteHelper.setByte(this.day);
		byteHelper.setByte(this.hour);
		byteHelper.setByte(this.minute);
		byteHelper.setByte(this.second);
		byteHelper.setByte(this.millisecond);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("YEAR", String.valueOf(year + 2000));
		map.put("MONTH", String.valueOf(month));
		map.put("DAY", String.valueOf(day));
		map.put("HOUR", String.valueOf(hour));
		map.put("MINUTE", String.valueOf(minute));
		map.put("SECOND", String.valueOf(second));
		map.put("MILLI", String.valueOf(millisecond));
		
		return map;
	}

	public String getLog() {
		return String.format("%04d-%02d-%02d %02d:%02d:%02d.%02d", (year + 2000), month, day, hour, minute, second, millisecond);
	}

	public String toString() {
		return getLog();
	}
}
