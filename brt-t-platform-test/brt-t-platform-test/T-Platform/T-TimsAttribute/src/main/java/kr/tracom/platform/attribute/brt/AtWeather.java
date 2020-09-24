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
@TimsAttribute(attributeId = BrtAtCode.WEATHER_INFO)
public class AtWeather extends AtData {
	public static final int SIZE = 18;
	
	private AtTimeStamp notiDt;
	private byte skyCond;
	private short tempc;
	private short tempMini;
	private short tempHigh;
	private byte humi;
	private byte rainPro;
	private short rainFall;
	
	public AtWeather() {
		attrId = BrtAtCode.WEATHER_INFO;
		notiDt = new AtTimeStamp();
	}
	
	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.notiDt.decode(byteHelper);
		this.skyCond = byteHelper.getByte();
		this.tempc = byteHelper.getShort();
		this.tempMini = byteHelper.getShort();
		this.tempHigh = byteHelper.getShort();
		this.humi = byteHelper.getByte();
		this.rainPro = byteHelper.getByte();
		this.rainFall = byteHelper.getShort();
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		this.notiDt.encode(byteHelper);
		byteHelper.setByte(this.skyCond);
		byteHelper.setShort(this.tempc);
		byteHelper.setShort(this.tempMini);
		byteHelper.setShort(this.tempHigh);
		byteHelper.setByte(this.humi);
		byteHelper.setByte(this.rainPro);
		byteHelper.setShort(this.rainFall);
	}

	@Override
	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("NOTI_DT", this.notiDt);
		map.put("SKY_COND", this.skyCond & 0xFF);
		map.put("TEMPC", this.tempc);
		map.put("TEMP_MINI", this.tempMini);
		map.put("TEMP_HIGH", this.tempHigh);
		map.put("HUMI", this.humi & 0xFF);
		map.put("RAIN_PRO", this.rainPro & 0xFF);
		map.put("RAINFALL", this.rainFall);
		
		return map;
	}
	
	public void setParseMap(Map<String, Object> map) {
		this.notiDt.setTimeStamp(map.get("NOTI_DT").toString());
		this.skyCond = Byte.parseByte(map.get("SKY_COND").toString());
		
		short _tempc = Short.parseShort(map.get("TEMPC").toString());
		this.tempc = _tempc == -999 ? (short)0xFFFF : _tempc;
		
		short _tempMini = Short.parseShort(map.get("TEMP_MINI").toString());
		this.tempMini = _tempMini == -999 ? (short)0xFFFF : _tempMini;
		
		short _tempHigh = Short.parseShort(map.get("TEMP_HIGH").toString()); 
		this.tempHigh = _tempHigh == -999 ? (short)0xFFFF : _tempHigh;
		
		this.humi = Byte.parseByte(map.get("HUMI").toString());
		this.rainPro = Byte.parseByte(map.get("RAIN_PRO").toString());
		this.rainFall = Short.parseShort(map.get("RAINFALL").toString());
	}
}
