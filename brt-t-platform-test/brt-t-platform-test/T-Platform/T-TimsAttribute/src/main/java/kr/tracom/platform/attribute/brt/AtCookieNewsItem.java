package kr.tracom.platform.attribute.brt;

import java.util.HashMap;
import java.util.Map;

import kr.tracom.platform.net.protocol.TimsLogBuilder;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AtCookieNewsItem extends AtData {
	public static final int SIZE = 101;
	
	private String content;
	
	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.content = byteHelper.getString(101);
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setString(this.content, 101);
	}

	@Override
	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("CONTENT", this.content);
		
		return map;
	}
	
	public void setParseMap(Map<String, Object> map) {
		this.content = map.get("CONTENT").toString() + '\0';
	}
}
