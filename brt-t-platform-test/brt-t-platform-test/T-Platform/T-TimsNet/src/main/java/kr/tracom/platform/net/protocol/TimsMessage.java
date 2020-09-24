package kr.tracom.platform.net.protocol;

import kr.tracom.platform.common.util.CrcUtil;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.factory.TimsHeaderFactory;
import kr.tracom.platform.net.protocol.factory.TimsPayloadFactory;
import kr.tracom.platform.net.util.ByteHelper;


public class TimsMessage implements TimsObject {	
	private TimsHeader header;
	private TimsPayload payload;
	private TimsTail tail;
	private ByteHelper byteHelper;
	private TimsConfig timsConfig;

	private int error;

	public static int getPayloadSize(TimsConfig timsConfig, byte[] buffer, int size) {
		if(buffer.length < TimsHeader.SIZE) {
			return -1;
		}

		ByteHelper byteHelper = new ByteHelper(timsConfig.getEndianType(), timsConfig.getStrEncoding());
		byteHelper.allocate(buffer, size);
		byteHelper.moveIndex(timsConfig.getApduOffset());

		short payloadSize = byteHelper.getShort();

		return payloadSize;
	}
	
	public TimsMessage(TimsConfig timsConfig) {
		this.timsConfig = timsConfig;
		this.header = TimsHeaderFactory.getHeader(timsConfig);
		this.payload = null;
		this.tail = new TimsTail();        
		
		this.byteHelper = new ByteHelper(timsConfig.getEndianType(), timsConfig.getStrEncoding());
		this.error = 0;
	}

	public TimsMessage(TimsConfig timsConfig, byte[] buffer, int size) {
		this.timsConfig = timsConfig;
		this.header = TimsHeaderFactory.getHeader(timsConfig);
		this.payload = null;
		this.tail = new TimsTail();

		this.byteHelper = new ByteHelper(timsConfig.getEndianType(), timsConfig.getStrEncoding());
		this.byteHelper.allocate(buffer, size);
		this.error = 0;
	}
	
	public TimsHeader getHeader() {
		return this.header;
	}

	public TimsPayload getPayload() {
		return this.payload;
	}

	public TimsTail getTail() {
		return this.tail;
	}
	
	public TimsAddress getSrcAddress() {
		TimsHeaderTypeA h = (TimsHeaderTypeA)this.header;
		return h.getSrcAddress();
	}
	
	public TimsAddress getDstAddress() {
		TimsHeaderTypeA h = (TimsHeaderTypeA)this.header;
		return h.getDstAddress();
	}
	
	public ByteHelper getByteHelper() {
		return this.byteHelper;
	}
	
	public int getError() {
		return this.error;
	}

	public void setPayload(TimsPayload payload) {
		this.payload = payload;
	}

	public void setOpCode(byte opCode) {
		this.header.setOpCode(opCode);
	}
	
	public void setPayloadSize(short size) {
		this.header.setPayloadSize(size);
	}
	
	private boolean parseHeader(ByteHelper byteHelper) {
		boolean bRet = false;
		if(byteHelper.getSize() - byteHelper.getIndex() >= this.header.getSize()) {
			this.header.decode(byteHelper);
			bRet = true;
		}
		return bRet;
    }
    
	private boolean parsePayload(ByteHelper byteHelper) {
		boolean bRet = false;
    	try {
    		this.payload = TimsPayloadFactory.getPayload(this.header.getOpCode());

    		if(this.payload != null) {
    			if(byteHelper.getSize() - byteHelper.getIndex() >= this.payload.getSize()) {
					this.payload.decode(byteHelper);
					bRet = true;
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		} 
    	return bRet;
    }
    
	private boolean parseTail(ByteHelper byteHelper) {		
		boolean bRet = false;

		if(byteHelper.getSize() - byteHelper.getIndex() >= this.tail.getSize()) {
			this.tail.decode(byteHelper);
			bRet = true;
		}
		return bRet;
    }
	
	public byte[] toByte() {
		this.byteHelper.allocate(timsConfig.getMaxPacketSize());

		encode(this.byteHelper);
		byte[] byteData = new byte[this.byteHelper.getIndex()];
		System.arraycopy(this.byteHelper.getBuffer(), 0, byteData, 0, byteData.length);

		return byteData;
	}
	
	public int parse() {
		decode(this.byteHelper);
		
		return this.error;
	}

	//##################################################################
	
	public int getSize() {
		return this.byteHelper.getIndex();
	}

	public void decode(ByteHelper byteHelper) {
		boolean bRet;

		bRet = parseHeader(byteHelper);
		if(!bRet) {
			this.error = -10;
			return;
		}

		bRet = parsePayload(byteHelper);
        if(!bRet) {
			this.error = -20;
			return;
		}

		byteHelper.moveIndex(byteHelper.getSize() - TimsTail.SIZE);

        bRet = parseTail(byteHelper);
        if(!bRet) {
			this.error = -30;
			return;
		}
	}

	public void encode(ByteHelper byteHelper) {
        this.header.encode(byteHelper);
        this.payload.encode(byteHelper);
		this.tail.setCrc(CrcUtil.makeCrc16(byteHelper.getBuffer(), 0, byteHelper.getIndex()));
        this.tail.encode(byteHelper);
	}

	public String getLog() {
        StringBuilder sb = new StringBuilder(256);
        if(this.header != null) {
			sb.append(this.header.getLog() + "\r\n");
        }
        if(this.payload != null) {
			sb.append(this.payload.getLog() + "\r\n");
        }
        if(this.tail != null) {
			sb.append(this.tail.getLog());
        }
        
        return sb.toString();
	}
	
	public String toString() {
		return getLog();
	}
	
	public String forLog() {
        StringBuilder sb = new StringBuilder(256);

        if(this.payload != null) {
			sb.append(this.payload.toString() + "\r\n");
        }
		return sb.toString();
	}
}
