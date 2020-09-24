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
public class AtFtpListPathItem extends AtData {
	public static final int SIZE = 64;
	
	private String sourcePath;
	
	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.sourcePath = byteHelper.getString(64);
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setString(this.sourcePath, 64);
	}

	@Override
	public String getLog() {
		return TimsLogBuilder.getLog(this);
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("SOURCE_PATH", this.sourcePath);
		
		return map;
	}
}
