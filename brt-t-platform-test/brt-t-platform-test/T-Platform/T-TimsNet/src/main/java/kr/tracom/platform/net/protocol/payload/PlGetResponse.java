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
public class PlGetResponse extends TimsPayload {
	public static final int SIZE = 1;

	private byte attrCount;

	private List<AtMessage> attrList;

	public PlGetResponse() {
		PayloadName = "Get-Response";
		OpCode = PlCode.OP_GET_RES;

		attrCount = 0;
		attrList = new ArrayList<>();
	} 
	
	@Override
	public int getSize() {
		int size = 0;
		for(AtMessage msg : attrList) {
			size += msg.getSize();
		}
		return SIZE + size;
	}
	
	public void addAttribute(AtMessage msg) {
		attrList.add(msg);
		attrCount++;
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.attrCount = byteHelper.getByte();
		for(int i = 0; i<this.attrCount; i++) {
			AtMessage msg = new AtMessage();
			msg.decode(byteHelper);

			if(msg.getErrorCode() == 0x10) {
				// TODO : 대처방안이 현재 음따 
			}

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
