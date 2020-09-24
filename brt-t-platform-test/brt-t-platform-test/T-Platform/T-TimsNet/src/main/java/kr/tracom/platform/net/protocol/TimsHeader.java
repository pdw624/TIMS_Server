package kr.tracom.platform.net.protocol;

import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TimsHeader implements TimsObject {
	public static final int SIZE = 8;
	
	protected byte indicator;
	protected byte headerSize;
	protected byte protocolVersion;
	protected byte packetId;
	protected TimsHeaderOption headerOption;
	protected byte opCode;
	protected short payloadSize;
	
	public TimsHeader(TimsConfig timsConfig) {
		this.indicator = timsConfig.getIndicator();
		this.headerSize = SIZE;
		this.protocolVersion = timsConfig.getVersion();
		this.packetId = 0;
		this.headerOption = new TimsHeaderOption(timsConfig);
	}

	public int getSize() {
		return SIZE;
	}
	
	public void decode(ByteHelper byteHelper) {
		this.indicator = byteHelper.getByte();
		this.headerSize = byteHelper.getByte();
		this.protocolVersion = byteHelper.getByte();
		this.packetId = byteHelper.getByte();
		this.headerOption.decode(byteHelper);
		this.opCode = byteHelper.getByte();
		this.payloadSize = byteHelper.getShort();
	}
	
	public void encode(ByteHelper byteHelper) {
		byteHelper.setByte(this.indicator);
		byteHelper.setByte(this.headerSize);
		byteHelper.setByte(this.protocolVersion);
		byteHelper.setByte(this.packetId);
		this.headerOption.encode(byteHelper);
		byteHelper.setByte(this.opCode);
		byteHelper.setShort(this.payloadSize);
	}
	
	public String getLog() {
		String log = String.format("stx(%02X), hsize(%d), ver(%d), packetId(%d), option(%s) opcode(0x%02X), psize(%d)",
				this.indicator, this.headerSize & 0xFF, this.protocolVersion, this.packetId & 0xFF, 
				this.headerOption.getLog(), 
				this.opCode, this.payloadSize);
		return log;
	}
}
