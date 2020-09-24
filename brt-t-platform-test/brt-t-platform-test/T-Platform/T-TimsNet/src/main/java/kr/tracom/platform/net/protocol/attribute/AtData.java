package kr.tracom.platform.net.protocol.attribute;

import kr.tracom.platform.net.protocol.TimsObject;

import java.util.Map;


public abstract class AtData implements TimsObject {
	protected short attrId = 0;
	
	public short getAttrId() {
		return this.attrId;
	}

	public abstract Map<String, Object> toMap();
}

