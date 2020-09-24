package kr.tracom.platform.attribute.common;

import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.net.protocol.TimsAttribute;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
@TimsAttribute(attributeId = AtCode.STATUS)
public class AtStatus extends AtData {
	public static final int SIZE = 11;
	
	private String deviceId;
	private byte count;
	private List<AtStatusItem> items;
	
	public AtStatus() {
		attrId = AtCode.STATUS;
		items = new ArrayList<>();
	}	

	public int getSize() {
		return SIZE + (this.count * AtStatusItem.SIZE);
	}

	public void decode(ByteHelper byteHelper) {
		this.deviceId = byteHelper.getString(10);
		this.count = byteHelper.getByte();
		
		for(int i=0; i<this.count; i++) {
			AtStatusItem item = new AtStatusItem();
			item.decode(byteHelper);
			
			items.add(item);
		}
	}

	public void encode(ByteHelper byteHelper) {
		byteHelper.setString(this.deviceId, 10);
		byteHelper.setByte(this.count);

		short loop = ByteHelper.unsigned(this.count);
		for(short i=0; i<loop; i++) {
			AtStatusItem item = items.get(i);
			item.encode(byteHelper);
		}
	}	

	public String getLog() {
		return "Status [deviceId=" + deviceId + ", count=" + count + ", items=" + items + "]";
	}
	
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("SESSION_ID", this.deviceId);
		map.put("COUNT", this.count);

		return map;
	}
}
