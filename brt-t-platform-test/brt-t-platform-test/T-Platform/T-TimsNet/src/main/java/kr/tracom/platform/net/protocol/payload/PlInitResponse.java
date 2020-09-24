package kr.tracom.platform.net.protocol.payload;

import kr.tracom.platform.net.protocol.TimsPayload;
import kr.tracom.platform.net.protocol.attribute.AtMessage;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class PlInitResponse extends TimsPayload {
	public static final int SIZE = 1;

	private byte attrCount;

	private List<AtMessage> attrList;

	public PlInitResponse() {
		PayloadName = "Init-Response";
		OpCode = PlCode.OP_INIT_RES;

		attrCount = 0;
		attrList = new ArrayList<>();
	} 

	public void addAttrMessage(AtMessage atMessage) {
		this.attrList.add(atMessage);
		this.attrCount++;
	}	

	@Override
	public int getSize() {
		int size = 0;
		for(AtMessage msg : attrList) {
			size += msg.getSize();
		}
		return SIZE + size;
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.attrCount = byteHelper.getByte();
		for(int i = 0; i<this.attrCount; i++) {
			AtMessage msg = new AtMessage();
			msg.decode(byteHelper);
			this.attrList.add(msg);
		}
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setByte(this.attrCount);
        for(AtMessage msg : attrList) {
			msg.encode(byteHelper);
		}
	}

	@Override
	public String getLog() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName() + " : ");
		sb.append(String.format("%d ", this.attrCount));
		for(AtMessage msg : attrList) {
			sb.append(msg.getLog());
		}
		
		return sb.toString();
	}
}
