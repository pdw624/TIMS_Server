package kr.tracom.platform.attribute.brt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
@TimsAttribute(attributeId = BrtAtCode.COOKIE_NEWS_INFO)
public class AtCookieNews extends AtData {
	public static final int SIZE = 15;
	
	private AtTimeStamp notiStDt;
	private AtTimeStamp notiEdDt;
	private byte count;
	private List<AtCookieNewsItem> list;
	
	public AtCookieNews() {
		attrId = BrtAtCode.COOKIE_NEWS_INFO;
		
		this.notiStDt = new AtTimeStamp();
		this.notiEdDt = new AtTimeStamp();
		list = new ArrayList<AtCookieNewsItem>();
	}
	
	@Override
	public int getSize() {
		return SIZE + (count * AtCookieNewsItem.SIZE);
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.notiStDt.decode(byteHelper);
		this.notiEdDt.decode(byteHelper);
        this.count = byteHelper.getByte();

        short loop = ByteHelper.unsigned(this.count);
        for(short i = 0; i < loop; i++) {
        	AtCookieNewsItem item = new AtCookieNewsItem();
            item.decode(byteHelper);
            
            list.add(item);
        }
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		this.notiStDt.encode(byteHelper);
		this.notiEdDt.encode(byteHelper);
		byteHelper.setByte(this.count);
		
		for(AtCookieNewsItem item : list) {
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
		map.put("NOTI_ST_DT", this.notiStDt);
		map.put("NOTI_ED_DT", this.notiEdDt);
		map.put("NEWS_COUNT", this.count);
		map.put("NEWS_LIST", this.list);
		
		return map;
	}
}
