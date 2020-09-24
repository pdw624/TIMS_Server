package kr.tracom.platform.attribute.imp;

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
@TimsAttribute(attributeId = AtCode.IMP_PROCESS_LIST)
public class AtProcess extends AtData {
	public static final int SIZE = 1;

	private byte count;
	private List<AtProcessItem> items;
	
	public AtProcess() {
		attrId = AtCode.IMP_PROCESS_LIST;
		items = new ArrayList<>();
	}

	@Override
	public int getSize() {
		return SIZE + (this.count * AtProcessItem.SIZE);
	}
	
	
	@Override
	public void decode(ByteHelper byteHelper) {
		this.count = byteHelper.getByte();
		short loop = ByteHelper.unsigned(this.count);

		for(short i=0; i<loop; i++) {
			AtProcessItem item = new AtProcessItem();
			item.decode(byteHelper);
			
			items.add(item);
		}
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setByte(this.count);

		for(AtProcessItem item : items) {
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
		map.put("COUNT", ByteHelper.unsigned(this.count));

		return map;
	}
}
