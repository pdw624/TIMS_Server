package kr.tracom.platform.net.endian;

public interface Endian {
	short bytesToShort(byte[] b, int index);
	int bytesToInt(byte[] b, int index);
	long bytesToLong(byte[] b, int index);
	double bytesToDouble(byte[] b, int index);
	
	byte[] shortToBytes(short s);
	byte[] intToBytes(int i);
	byte[] longToBytes(long l);
	byte[] doubleToBytes(double d);
}
