package kr.tracom.platform.net.protocol.payload;

import kr.tracom.platform.net.protocol.TimsPayload;
import kr.tracom.platform.net.protocol.attribute.AtMessage;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class PlActionRequest extends TimsPayload {
	public static final int SIZE = AtMessage.SIZE;

	private AtMessage atMessage;
	
	public PlActionRequest() {
		PayloadName = "Action-Request";
		OpCode = PlCode.OP_ACTION_REQ;

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