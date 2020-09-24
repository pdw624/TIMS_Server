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
@TimsAttribute(attributeId = BrtAtCode.ROUTE_INFO_RESPONSE)
public class AtRouteInfoResponse extends AtData {
	public static final int SIZE = 381;
	
	private AtTimeStamp pubDate;
	private String routId;
	private String routNmKo;
	private String routNmEn;
	private String line1Str;
	private String line2Str;
	private String line1SatStr;
	private String line2SatStr;
	private String line1SunStr;
	private String line2SunStr;
	
	public AtRouteInfoResponse() {
		attrId = BrtAtCode.ROUTE_INFO_RESPONSE;
		pubDate = new AtTimeStamp();
		routId = "";
		routNmKo = "";
		routNmEn = "";
		line1Str = "";
		line2Str = "";
		line1SatStr = "";
		line2SatStr = "";
		line1SunStr = "";
		line2SunStr = "";
	}
	
	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.pubDate.decode(byteHelper);
		this.routId = byteHelper.getString(10);
		this.routNmKo = byteHelper.getString(32);
		this.routNmEn = byteHelper.getString(32);
		this.line1Str = byteHelper.getString(50);
		this.line2Str = byteHelper.getString(50);
		this.line1SatStr = byteHelper.getString(50);
		this.line2SatStr = byteHelper.getString(50);
		this.line1SunStr = byteHelper.getString(50);
		this.line2SunStr = byteHelper.getString(50);
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		this.pubDate.encode(byteHelper);
		byteHelper.setString(this.routId, 10);
		byteHelper.setString(this.routNmKo, 32);
		byteHelper.setString(this.routNmEn, 32);
		byteHelper.setString(this.line1Str, 50);
		byteHelper.setString(this.line2Str, 50);
		byteHelper.setString(this.line1SatStr, 50);
		byteHelper.setString(this.line2SatStr, 50);
		byteHelper.setString(this.line1SunStr, 50);
		byteHelper.setString(this.line2SunStr, 50);
	}

	@Override
	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("PUB_DATE", this.pubDate);
		map.put("ROUTE_ID", this.routId);
		map.put("ROUTE_NM_KO", this.routNmKo);
		map.put("ROUTE_NM_EN", this.routNmEn);
		map.put("LINE1_STR", this.line1Str);
		map.put("LINE2_STR", this.line2Str);
		map.put("LINE1_SATSTR", this.line1SatStr);
		map.put("LINE2_SATSTR", this.line2SatStr);
		map.put("LINE1_SUNSTR", this.line1SunStr);
		map.put("LINE2_SUNSTR", this.line2SunStr);
		
		return map;
	}
	
	public void setParseMap(Map<String, Object> map) {
		String pubDate = map.get("PUB_DATE").toString();
		this.pubDate.setTimeStamp(pubDate);
		this.routId = map.get("ROUT_ID").toString() + '\0';
		this.routNmKo = map.get("ROUT_NM_KO").toString() + '\0';
		
		String routeNmEn = map.get("ROUT_NM_EN").toString();
		this.routNmEn = routeNmEn.equals("") ? routeNmEn : routeNmEn + '\0';
		
		String line1Str = map.get("LINE1_STR").toString(); 
		this.line1Str = line1Str.equals("") ? line1Str : line1Str + '\0';
		
		String line2Str = map.get("LINE2_STR").toString();
		this.line2Str = line2Str.equals("") ? line2Str : line2Str + '\0';
		
		String line1SatStr = map.get("LINE1_SATSTR").toString();
		this.line1SatStr = line1SatStr.equals("") ? line1SatStr : line1SatStr + '\0';
		
		String line2SatStr = map.get("LINE2_SATSTR").toString();
		this.line2SatStr = line2SatStr.equals("") ? line2SatStr : line2SatStr + '\0';
		
		String line1SunStr = map.get("LINE1_SUNSTR").toString();
		this.line1SunStr = line1SunStr.equals("") ? line1SunStr : line1SunStr + '\0';
		
		String line2SunStr = map.get("LINE2_SUNSTR").toString();
		this.line2SunStr = line2SunStr.equals("") ? line2SunStr : line2SunStr + '\0';
	}
}
