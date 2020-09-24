package kr.tracom.platform.net.protocol;

import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.util.ByteHelper;

public class TimsHeaderOption implements TimsObject {
	public static final int SIZE = 1;
	
	private String strOption;
	
	private byte retryCount;
	private byte timeOut;
	private byte transferRoute;
	private byte crcEnable;
	private byte remoteFlowControl;
	private byte localFlowControl;
	
	public TimsHeaderOption(TimsConfig timsConfig) {
		this.retryCount = timsConfig.getRetryCount();
		this.timeOut = timsConfig.getResponseTimeOut();
		this.transferRoute = timsConfig.getTransferRoute();
		this.crcEnable = timsConfig.getCrcEnable();
		this.remoteFlowControl = timsConfig.getRemoteFlowControl();
		this.localFlowControl = timsConfig.getLocalFlowControl();

		this.strOption = "";
		this.strOption += ByteHelper.toBinaryString(this.localFlowControl).substring(7, 8);
		this.strOption += ByteHelper.toBinaryString(this.remoteFlowControl).substring(7, 8);
		this.strOption += ByteHelper.toBinaryString(this.crcEnable).substring(7, 8);
		this.strOption += ByteHelper.toBinaryString(this.transferRoute).substring(7, 8);
		this.strOption += ByteHelper.toBinaryString(this.timeOut).substring(6, 8);
		this.strOption += ByteHelper.toBinaryString(this.retryCount).substring(6, 8);
	}	
	
	public byte getRetryCount() {
		if(strOption != null && strOption.length() == 8) {
			return Byte.parseByte(strOption.substring(6, 8), 2);
		} else {
			return (byte)0;
		}
	}
	public byte getTimeOut() {
		if(strOption != null && strOption.length() == 8) {
			return Byte.parseByte(strOption.substring(4, 6), 2);
		} else {
			return (byte)0;
		}
	}
	public byte getTransferRoute() {
		if(strOption != null && strOption.length() == 8) {
			return Byte.parseByte(strOption.substring(3, 4), 2);
		} else {
			return (byte)0;
		}
	}
	public byte getCrcEnable() {
		if(strOption != null && strOption.length() == 8) {
			return Byte.parseByte(strOption.substring(2, 3), 2);
		} else {
			return (byte)0;
		}
	}
	public byte getRemoteFlowControl() {
		if(strOption != null && strOption.length() == 8) {
			return Byte.parseByte(strOption.substring(1, 2), 2);
		} else {
			return (byte)0;
		}
	}
	public byte getLocalFlowControl() {
		if(strOption != null && strOption.length() == 8) {
			return Byte.parseByte(strOption.substring(0, 1), 2);
		} else {
			return (byte)0;
		}
	}

	/*
	public void setRetryCount(byte retryCount) {
		this.retryCount = retryCount;
	}
	public void setTimeOut(byte timeOut) {
		this.timeOut = timeOut;
	}
	public void setTransferRoute(byte transferRoute) {
		this.transferRoute = transferRoute;
	}
	public void setCrcEnable(byte crcEnable) {
		this.crcEnable = crcEnable;
	}
	public void setRemoteFlowControl(byte remoteFlowControl) {
		this.remoteFlowControl = remoteFlowControl;
	}
	public void setLocalFlowControl(byte localFlowControl) {
		this.localFlowControl = localFlowControl;
	}
	*/

	public int getSize() {
		return SIZE;
	}
	
    public void decode(ByteHelper byteHelper) {
    	this.strOption = ByteHelper.toBinaryString(byteHelper.getByte() & 0xFF);
    }
    
    public void encode(ByteHelper byteHelper) {
		this.strOption = "";
		this.strOption += ByteHelper.toBinaryString(this.localFlowControl).substring(7, 8);
		this.strOption += ByteHelper.toBinaryString(this.remoteFlowControl).substring(7, 8);
		this.strOption += ByteHelper.toBinaryString(this.crcEnable).substring(7, 8);
		this.strOption += ByteHelper.toBinaryString(this.transferRoute).substring(7, 8);
		this.strOption += ByteHelper.toBinaryString(this.timeOut).substring(6, 8);
		this.strOption += ByteHelper.toBinaryString(this.retryCount).substring(6, 8);
		
		byteHelper.setByte((byte)(int)Integer.valueOf(this.strOption, 2));
    }
    
	public String getLog() {
		String log;

		/*
		log = String.format("RC:%d, TO:%d, TR:%d, CE:%d, RF:%d, LF:%d", 
				getRetryCount(), getTimeOut(), getTransferRoute(),
				getCrcEnable(), getRemoteFlowControl(), getLocalFlowControl());
		*/

		log = String.format("RC:%d, TO:%d, TR:%d, CE:%d, RF:%d, LF:%d",
				retryCount, timeOut, transferRoute,
				crcEnable, remoteFlowControl, localFlowControl);

		return log;
	}
}
