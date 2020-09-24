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
@TimsAttribute(attributeId = BrtAtCode.AIR_QUALITY_INFO)
public class AtAirQuality extends AtData {
	public static final int SIZE = 19;
	
	private AtTimeStamp measDt;
	private short dustc;
	private short sDustc;
	private short sdc;
	private short cmc;
	private short ozonec;
	private short ndc;
	
	public AtAirQuality() {
		attrId = BrtAtCode.AIR_QUALITY_INFO;
		measDt = new AtTimeStamp();
	}
	
	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.measDt.decode(byteHelper);
		this.dustc = byteHelper.getShort();
		this.sDustc = byteHelper.getShort();
		this.sdc = byteHelper.getShort();
		this.cmc = byteHelper.getShort();
		this.ozonec = byteHelper.getShort();
		this.ndc = byteHelper.getShort();
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		this.measDt.encode(byteHelper);
		byteHelper.setShort(this.dustc);
		byteHelper.setShort(this.sDustc);
		byteHelper.setShort(this.sdc);
		byteHelper.setShort(this.cmc);
		byteHelper.setShort(this.ozonec);
		byteHelper.setShort(this.ndc);
	}

	@Override
	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("MEAS_DT", this.measDt);
		map.put("DUSTC", this.dustc);
		map.put("SDUSTC", this.sDustc);
		map.put("SDC", this.sdc);
		map.put("CMC", this.cmc);
		map.put("OZONEC", this.ozonec);
		map.put("NDC", this.ndc);
		
		return map;
	}
	
	public void setParseMap(Map<String, Object> map) {
		this.measDt.setTimeStamp(map.get("MEAS_DT").toString());
		
		String _dustc = map.get("DUSTC").toString();
		this.dustc = _dustc.equals("-") ? (short)0xFFFF : Short.parseShort(_dustc);
		
		String _sDustc = map.get("SDUSTC").toString();
		this.sDustc = _sDustc.equals("-") ? (short)0xFFFF : Short.parseShort(_sDustc);
		
		double _sdc = Double.parseDouble(map.get("SDC").toString());
		this.sdc = (short) (_sdc * 1000);
		
		double _cmc = Double.parseDouble(map.get("CMC").toString());
		this.cmc = (short) (_cmc * 1000);
		
		double _ozonec = Double.parseDouble(map.get("OZONEC").toString());
		this.ozonec = (short) (_ozonec * 1000);
		
		double _ndc = Double.parseDouble(map.get("NDC").toString());
		this.ndc = (short) (_ndc * 1000);
	}
}
