package kr.tracom.platform.net.protocol.payload;

import kr.tracom.platform.net.protocol.TimsAddress;
import kr.tracom.platform.net.protocol.TimsPayload;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class PlProtocolResponse extends TimsPayload {
	public static final int SIZE = 12;

	private byte resultCode;
	private byte opCode;
	private TimsAddress responderId;
	private byte errorCode;
	private byte packetId;
	private byte[] reserved;

	public PlProtocolResponse() {
		PayloadName = "Protocol-Response";
		OpCode = PlCode.OP_PROTOCOL_RES;

		responderId = new TimsAddress();
		reserved = new byte[4];
	}

	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.resultCode = byteHelper.getByte();
		this.opCode = byteHelper.getByte();
		this.responderId.decode(byteHelper);
		this.errorCode = byteHelper.getByte();
		this.packetId = byteHelper.getByte();
		this.reserved = byteHelper.getBytes(4);
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setByte(this.resultCode);
		byteHelper.setByte(this.opCode);
		this.responderId.encode(byteHelper);
		byteHelper.setByte(this.errorCode);
		byteHelper.setByte(this.packetId);
		byteHelper.setBytes(this.reserved, 4);
	}

	@Override
	public String getLog() {
		return String.format("0x%02X, 0x%02X, %s, 0x%02X %d %s",
				resultCode, opCode, responderId.getLog(), errorCode,
				ByteHelper.unsigned(packetId), ByteHelper.toHex(reserved));
	}
}