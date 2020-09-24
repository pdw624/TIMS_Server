package kr.tracom.platform.net.config;

import java.util.HashMap;
import java.util.Map;

public class TimsCode {
	public static Map<Byte, String> attributeErrorCode = new HashMap<Byte, String>();
	static {
		attributeErrorCode.put((byte)0x00, "Normal");
		attributeErrorCode.put((byte)0x10, "Attribute Not Defined");
		attributeErrorCode.put((byte)0x11, "Cannot Service Requested Attribute ");
		attributeErrorCode.put((byte)0x12, "Attribute Version Mismatch");
		attributeErrorCode.put((byte)0x13, "Data Is Not Ready");
	}

	public static Map<Byte, String> protocolErrorCode = new HashMap<Byte, String>();
	static {
		protocolErrorCode.put((byte)0x00, "No Error");
		protocolErrorCode.put((byte)0x21, "CRC/Checksum Error ");
		protocolErrorCode.put((byte)0x22, "Protocol Version Error");
		protocolErrorCode.put((byte)0x23, "Header Size Error");
		protocolErrorCode.put((byte)0x24, "APDU Size Filed Error");
		protocolErrorCode.put((byte)0x25, "Timeout");
		protocolErrorCode.put((byte)0x26, "Packet ID Error");
		protocolErrorCode.put((byte)0x27, "Packet Index Error");
		protocolErrorCode.put((byte)0x28, "Packet Option Error");
		protocolErrorCode.put((byte)0x29, "OP Code Error");
		protocolErrorCode.put((byte)0x2A, "Source ID Error");
		protocolErrorCode.put((byte)0x2B, "Destination ID Error");
		protocolErrorCode.put((byte)0x2C, "APDU Length is too big");

	}

	public static String getAttributeError(byte code) {
		if(attributeErrorCode.containsKey(code)) {
			return attributeErrorCode.get(code);
		} else {
			return String.format("%02X", code);
		}
	}

	public static String getProtocolError(byte code) {
		if(protocolErrorCode.containsKey(code)) {
			return protocolErrorCode.get(code);
		} else {
			return String.format("%02X", code);
		}
	}
}
