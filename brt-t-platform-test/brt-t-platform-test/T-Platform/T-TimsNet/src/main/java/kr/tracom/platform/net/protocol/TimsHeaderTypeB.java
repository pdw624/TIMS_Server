package kr.tracom.platform.net.protocol;

import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TimsHeaderTypeB extends TimsHeader {
	public static final int SIZE = TimsHeader.SIZE + 4;
	
	private byte currentIndex;
	private byte totalIndex;
	private short reserved;
	
	public TimsHeaderTypeB(TimsConfig timsConfig) {
		super(timsConfig);
		
		super.headerSize = SIZE;
		this.currentIndex = 0;
		this.totalIndex = 0;		
		this.reserved = 0;
	}

	public int getSize() {
		return SIZE;
	}
	
	public void decode(ByteHelper byteHelper) {
		super.decode(byteHelper);
        this.currentIndex = byteHelper.getByte();
        this.totalIndex = byteHelper.getByte();
        this.reserved = byteHelper.getShort();
	}

	public void encode(ByteHelper byteHelper) {
		super.encode(byteHelper);
		byteHelper.setByte(this.currentIndex);
		byteHelper.setByte(this.totalIndex);
		byteHelper.setShort(this.reserved);
	}
	
	public String toLog() {
		String log = String.format("%s, cindex(%d), tindex(%d), rsvd(%d)", 
				super.getLog(), this.currentIndex, this.totalIndex, this.reserved);
		return log;
	}
}
