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
@TimsAttribute(attributeId = BrtAtCode.ROUTE_INFO_REQUEST)
public class AtRouteInfoRequest extends AtData {
	public static final int SIZE = 10;
	
	private String routId;
	
	public AtRouteInfoRequest() {
		attrId = BrtAtCode.ROUTE_INFO_REQUEST;
		routId = "";
	}
	
	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.routId = byteHelper.getString(10);
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setString(this.routId, 10);
	}

	@Override
	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("ROUT_ID", this.routId != null && !this.routId.equals("") ? this.routId.substring(0, 9) : "");
		
		return map;
	}
}
