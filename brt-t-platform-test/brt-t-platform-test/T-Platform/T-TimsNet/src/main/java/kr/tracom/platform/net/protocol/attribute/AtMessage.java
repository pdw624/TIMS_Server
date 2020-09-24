package kr.tracom.platform.net.protocol.attribute;

import kr.tracom.platform.net.config.TimsCode;
import kr.tracom.platform.net.protocol.TimsObject;
import kr.tracom.platform.net.protocol.factory.TimsAttributeFactory;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AtMessage implements TimsObject {
	public static int SIZE = 4;
	
	private short attrId;
	private short attrSize;
	private AtData attrData;
	
	private byte errorCode;
	
	public AtMessage() {
		errorCode = 0x00;
	}
	
	public int getSize() {
		return SIZE + (this.attrSize == 0 ? 1 : this.attrSize);
	}
	
	public void decode(ByteHelper byteHelper) {
		this.attrId = byteHelper.getShort();
		this.attrSize = byteHelper.getShort();
		
		if(this.attrSize == 0) {
			this.errorCode = byteHelper.getByte();
			this.attrData = null;
		} else {		
			this.attrData = TimsAttributeFactory.getAttribute(attrId);
			if(this.attrData == null) {
				this.errorCode = 0x10;
			} else {
				this.errorCode = 0x00;
				this.attrData.decode(byteHelper);
			}
		}
	}
	
	public void encode(ByteHelper byteHelper) {
		byteHelper.setShort(this.attrId);
		byteHelper.setShort(this.attrSize);

		if(this.attrSize == 0) {
			byteHelper.setByte(this.errorCode);
		} else {
			this.attrData.encode(byteHelper);
		}
	}
	
	public String getLog() {
		String log = String.format("attr(%d) size(%d)\r\n", this.attrId, this.attrSize);
		if(this.errorCode == 0x00 && this.attrData != null) {
			log += this.attrData.getLog();
		} else {
			log += String.format("Attribute Error : %s", TimsCode.getAttributeError(this.errorCode));
		}
		return log;
	}
}
