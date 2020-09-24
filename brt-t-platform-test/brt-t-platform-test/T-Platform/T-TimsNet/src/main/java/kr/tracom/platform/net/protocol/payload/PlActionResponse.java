package kr.tracom.platform.net.protocol.payload;

import kr.tracom.platform.net.protocol.TimsPayload;
import kr.tracom.platform.net.protocol.attribute.AtMessage;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class PlActionResponse extends TimsPayload {
	public static final int SIZE = AtMessage.SIZE;

	private AtMessage atMessage;

	public PlActionResponse() {
		PayloadName = "Action-Response";
		OpCode = PlCode.OP_ACTION_RES;
		this.atMessage = new AtMessage();
	}

	@Override
	public int getSize() {
		return this.atMessage.getSize();
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.atMessage.decode(byteHelper);
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		this.atMessage.encode(byteHelper);
	}

	@Override
	public String getLog() {
		return this.atMessage.getLog();
	}
}
