package kr.tracom.platform.net.protocol;

import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TimsHeaderTypeA extends TimsHeader {
	public static final int SIZE = TimsHeader.SIZE + 16;
	
	private TimsAddress srcAddress;
	private TimsAddress dstAddress;
	private short currentIndex;
	private short totalIndex;
	private short reserved1;
	private short reserved2;
	
	public TimsHeaderTypeA(TimsConfig timsConfig) {
		super(timsConfig);
		
		super.headerSize = SIZE;
		this.srcAddress = new TimsAddress();
		this.dstAddress = new TimsAddress();
		this.currentIndex = 0;
		this.totalIndex = 0;
		this.reserved1 = 0;
		this.reserved2 = 0;
	}

	public int getSize() {
		return SIZE;
	}
	
	public void decode(ByteHelper byteHelper) {
		super.decode(byteHelper);
        this.srcAddress.decode(byteHelper);
        this.dstAddress.decode(byteHelper);
        this.currentIndex = byteHelper.getShort();
        this.totalIndex = byteHelper.getShort();
        this.reserved1 = byteHelper.getShort();
        this.reserved2 = byteHelper.getShort();
	}

	public void encode(ByteHelper byteHelper) {
		super.encode(byteHelper);
		this.srcAddress.encode(byteHelper);
		this.dstAddress.encode(byteHelper);
		byteHelper.setShort(this.currentIndex);
		byteHelper.setShort(this.totalIndex);
		byteHelper.setShort(this.reserved1);
		byteHelper.setShort(this.reserved2);
	}
	
	public String toLog() {
		String log = String.format("%s, srcaddr(%d). dstaddr(%d), cindex(%d), tindex(%d)", 
				super.getLog(), this.srcAddress.getLog(), this.dstAddress.getLog(), this.currentIndex & 0xFF, this.totalIndex & 0xFF);

		return log;
	}
}
