package kr.tracom.platform.tcp.manager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AccessManager {
	private static final String ACCESS_KEY = "TRCM";
	
	public static byte[] getKey(String id) {
		String number = id.replaceAll("[^0-9]", "");
		return getKey(Integer.parseInt(number));
	}
	
	public static byte[] getKey(int id) {
		byte[] buf = ACCESS_KEY.getBytes();
		byte[] key = ByteBuffer.allocate(4).putInt(id).order(ByteOrder.BIG_ENDIAN).array();

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				key[j] = (byte) (key[j] ^ buf[j]);
				buf[j] <<= id % 8 + 1;
			}
		}

		return key;
	}

	public static int htonl(int value) {
		if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) {
			
			System.out.println("ByteOrder : " + "big");
			
			return value;
		}

		return Integer.reverseBytes(value);
	}
}
