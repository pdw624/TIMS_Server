package kr.tracom.platform.net.protocol.payload;

import kr.tracom.platform.net.protocol.TimsPayload;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class PlGetRequest extends TimsPayload {
	public static final int SIZE = 1;
	public static final int ATTR_ID_SIZE = 2;
	
	private byte attrCount;

	private List<Short> attrList;

	public PlGetRequest() {
		PayloadName = "Get-Request";
		OpCode = PlCode.OP_GET_REQ;

		attrCount = 0;
		attrList = new ArrayList<>();
	}

	public void addAttributeId(short attrId) {
		this.attrList.add(attrId);
	}
	
	@Override
	public int getSize() {
		return SIZE + (this.attrCount * ATTR_ID_SIZE);
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.attrCount = byteHelper.getByte();
		short loop = ByteHelper.unsigned(this.attrCount);

		for (short b = 0; b < loop; b++) {
			attrList.add(byteHelper.getShort());
		}
	}

	@Override
	public void encode(ByteHelper byteHelper) {
		byteHelper.setByte(this.attrCount);
        
        for(Short attrId : attrList) {
        	byteHelper.setShort(attrId);
        }
	}

	@Override
	public String getLog() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName() + " : ");
		sb.append(String.format("%d ", this.attrCount));
		sb.append("\n");
		for(Short attrId : this.attrList) {
			sb.append(String.format("%d ", attrId));
		}
		return sb.toString();
	}
}
