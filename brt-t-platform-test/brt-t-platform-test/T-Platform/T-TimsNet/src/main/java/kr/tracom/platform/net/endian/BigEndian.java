package kr.tracom.platform.net.endian;

public class BigEndian implements Endian {

	public short bytesToShort(byte[] b, int index) {
		return (short) (((b[index] & 0xff) << 8) | (b[index + 1] & 0xff));
	}

	public int bytesToInt(byte[] b, int index) {
		return ((b[index] & 0xff) << 24) | ((b[index + 1] & 0xff) << 16)
				| ((b[index + 2] & 0xff) << 8) | (b[index + 3] & 0xff);
	}

	public long bytesToLong(byte[] b, int index) {
		return ((long) bytesToInt(b, index) << 32)
				| ((long) bytesToInt(b, index + 4) & 0xffffffffL);
	}

	public double bytesToDouble(byte[] b, int index) {
		long l = bytesToLong(b, index);
		return Double.longBitsToDouble(l);
	}

	public byte[] shortToBytes(short s) {
		byte[] b = new byte[2];
		b[1] = (byte) (s & 0xff);
		b[0] = (byte) ((s >> 8) & 0xff);
		return b;
	}

	public byte[] intToBytes(int i) {
		byte[] b = new byte[4];
		b[3] = (byte) (i & 0xff);
		b[2] = (byte) ((i >> 8) & 0xff);
		b[1] = (byte) ((i >> 16) & 0xff);
		b[0] = (byte) ((i >> 24) & 0xff);
		return b;
	}

	public byte[] longToBytes(long l) {
		byte[] b = new byte[8];
		b[7] = (byte) (l & 0xff);
		b[6] = (byte) ((l >> 8) & 0xff);
		b[5] = (byte) ((l >> 16) & 0xff);
		b[4] = (byte) ((l >> 24) & 0xff);
		b[3] = (byte) ((l >> 32) & 0xff);
		b[2] = (byte) ((l >> 40) & 0xff);
		b[1] = (byte) ((l >> 48) & 0xff);
		b[0] = (byte) ((l >> 56) & 0xff);
		return b;
	}

	public byte[] doubleToBytes(double d) {
		long l = Double.doubleToLongBits(d);
		byte[] dest = longToBytes(l);
		return dest;
	}
}
