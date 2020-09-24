package kr.tracom.platform.attribute.common;

import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.net.protocol.TimsAttribute;
import kr.tracom.platform.net.protocol.TimsLogBuilder;
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
@TimsAttribute(attributeId = AtCode.VERSION)
public class AtVersion extends AtData {
	public static final int SIZE = 11;
	
	private String deviceId;
	private byte count;
	private List<AtVersionItem> items;
	
	public AtVersion() {
		attrId = AtCode.VERSION;
		items = new ArrayList<>();
	}

	@Override
	public int getSize() {
		return SIZE + (this.count * AtVersionItem.SIZE);
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.deviceId = byteHelper.getString(10);
		this.count = byteHelper.getByte();
		
		for(int i=0; i<this.count; i++) {
			AtVersionItem item = new AtVersionItem();
			item.decode(byteHelper);
			
			items.add(item);
		}
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setString(this.deviceId, 10);
		byteHelper.setByte(this.count);

		short loop = ByteHelper.unsigned(this.count);
		for(short i=0; i<loop; i++) {
			AtVersionItem item = items.get(i);
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
		map.put("DEVICE_ID", this.deviceId);
		map.put("COUNT", ByteHelper.unsigned(this.count));

		return map;
	}
}
