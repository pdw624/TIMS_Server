package kr.tracom.platform.net.protocol.attribute;

import kr.tracom.platform.net.protocol.TimsObject;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AtResult implements TimsObject {
	public static final int SIZE = 3;
	
	private short atId;
	private byte result;	
	
	public int getSize() {
		return SIZE;
	}
	
	public void decode(ByteHelper byteHelper) {
		this.atId = byteHelper.getShort();
		this.result = byteHelper.getByte();
	}
	
	public void encode(ByteHelper byteHelper) {
		byteHelper.setShort(this.atId);
		byteHelper.setByte(this.result);
	}
	
	public String getLog() {
		String log = String.format("%d %02X ", this.atId, this.result);
		return log;
	}
}
