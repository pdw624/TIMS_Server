package kr.tracom.platform.net.protocol.payload;

import kr.tracom.platform.net.config.TimsCode;
import kr.tracom.platform.net.protocol.TimsPayload;
import kr.tracom.platform.net.protocol.attribute.AtResult;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class PlSetResponse extends TimsPayload {
	public static final int SIZE = 1;

	private byte attrCount;

	private List<AtResult> resultList;

	public PlSetResponse() {
		PayloadName = "Set-Response";
		OpCode = PlCode.OP_SET_RES;

		attrCount = 0;
		resultList = new ArrayList<>();
	} 

	public void addAttrResult(AtResult atResult) {
		this.resultList.add(atResult);
	}

	@Override
	public int getSize() {
		return SIZE + (this.attrCount * AtResult.SIZE);
	}

	@Override
	public void decode(ByteHelper byteHelper) {
		this.attrCount = byteHelper.getByte();
		
		for (byte a = 0; a < this.attrCount; a++) {
			AtResult result = new AtResult();
			result.decode(byteHelper);
			
			this.resultList.add(result);
		}
	}

	@Override
	public void encode(ByteHelper byteHelper) {
        byteHelper.setByte(this.attrCount);
        
        for(AtResult result : resultList) {
        	result.encode(byteHelper);
        }
	}

	@Override
	public String getLog() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName() + " : ");
		sb.append(String.format("%d ", this.attrCount & 0xFF));
		sb.append("\n");
		for(AtResult result : this.resultList) {
			sb.append(String.format("%d %s", result.getAtId(), TimsCode.getAttributeError(result.getResult())));
		}
		return sb.toString();
	}
}
