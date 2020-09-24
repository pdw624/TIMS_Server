package kr.tracom.platform.net.protocol.factory;

import kr.tracom.platform.net.protocol.TimsPayload;
import kr.tracom.platform.net.protocol.payload.*;

import java.util.HashMap;
import java.util.Map;

public class TimsPayloadFactory {
	public static Map<Byte, Class<?>> payloadClasses = new HashMap<>();
	
	static {	
		payloadClasses.put((byte) 0x10, PlGetRequest.class);
		payloadClasses.put((byte) 0x11, PlGetResponse.class);
		payloadClasses.put((byte) 0x20, PlSetRequest.class);
		payloadClasses.put((byte) 0x21, PlSetResponse.class);
		payloadClasses.put((byte) 0x30, PlActionRequest.class);
		payloadClasses.put((byte) 0x31, PlActionResponse.class);		
		payloadClasses.put((byte) 0x40, PlEventRequest.class);
		payloadClasses.put((byte) 0x41, PlEventResponse.class);
		payloadClasses.put((byte) 0x60, PlInitRequest.class);
		payloadClasses.put((byte) 0x61, PlInitResponse.class);
		payloadClasses.put((byte) 0x90, PlProtocolResponse.class);
	}
	
	public static TimsPayload getPayload(byte opCode) {
		Class<?> cls = payloadClasses.get(opCode);
		TimsPayload payload = null;
		if(cls != null) {
			try {
				payload =  (TimsPayload) cls.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return payload;
	}
}
