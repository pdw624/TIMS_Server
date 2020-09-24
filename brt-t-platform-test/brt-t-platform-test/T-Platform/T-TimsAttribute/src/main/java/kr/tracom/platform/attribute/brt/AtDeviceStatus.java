package kr.tracom.platform.attribute.brt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
@TimsAttribute(attributeId = BrtAtCode.DEVICE_STATUS_INFO)
public class AtDeviceStatus extends AtData {
	public static final int SIZE = 1;
	
	private byte count;
	private List<AtDeviceStatusItem> list;
	
	public AtDeviceStatus() {
		attrId = BrtAtCode.DEVICE_STATUS_INFO;
		list = new ArrayList<AtDeviceStatusItem>();
	}
	
	@Override
	public int getSize() {
		return SIZE + (count * AtDeviceStatusItem.SIZE);
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.count = byteHelper.getByte();
		
		short loop = ByteHelper.unsigned(this.count);
        for(short i = 0; i < loop; i++) {
        	AtDeviceStatusItem item = new AtDeviceStatusItem();
            item.decode(byteHelper);
            
            list.add(item);
        }
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setByte(this.count);
		
		for(AtDeviceStatusItem item : list) {
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
		map.put("STATUS_LIST", this.list);
		
		return map;
	}
}
