package kr.tracom.platform.service.domain;

import lombok.Data;

@Data
public class MtChannel {
	private String platformId;
	private String channelName;
	private int channelPort;
	private int readTimeout;
	private int writeTimeout;
	private String headerType;
	private String strEncoding;
	private String endianType;
	private int maxPacketSize;
	private int maxPacketCount;
	private byte indicator;
	private byte version;
	private byte apduOffset;
	private byte retryCount;
	private byte responseTimeout;
	private byte transferRoute;
	private byte crcEnable;
	private byte remoteControl;
	private byte localControl;
}
