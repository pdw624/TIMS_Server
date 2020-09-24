package kr.tracom.platform.net.protocol;

import kr.tracom.platform.net.util.ByteHelper;

public class TimsTail implements TimsObject {
	public static final int SIZE = 2;
	
	private short crc;

	//======================================================================
	public short getCrc() {
		return crc;
	}

	public void setCrc(short crc) {
		this.crc = crc;
	}
	//======================================================================
	public int getSize() {
		return SIZE;
	}

	public void decode(ByteHelper byteHelper) {
        this.crc = byteHelper.getShort();
	}

	public void encode(ByteHelper byteHelper) {
        byteHelper.setShort(this.crc);
	}
	
	public String getLog() {
		String log = String.format("CRC : %d", ByteHelper.unsigned(this.crc));
		return log;
	}
}
