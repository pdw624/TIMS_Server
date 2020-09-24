package kr.tracom.platform.net.util;

import java.io.UnsupportedEncodingException;

public class ByteHelper {
    public static final int BYTE_SIZE = 1;
    public static final int CHAR_SIZE = 1;
    public static final int SHORT_SIZE = 2;
    public static final int INT_SIZE = 4;
    public static final int LONG_SIZE = 8;

    public static short unsigned(byte value)
    {
        return (short)(value & 0xff);
    }
    public static int unsigned(short value)
    {
        return (value & 0xffff);
    }
    public static long unsigned(int value)
    {
        return (value & 0xffffffffL);
    }

    public static String toBinaryString(int value) {
        return String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0');
    }

    //---------------------------------------------------------------------------------------

    public static String toHex(byte[] values, int offset, int length) {
        StringBuilder buff = new StringBuilder(length * 3);
        for (int i = offset; i < values.length && i < offset + length; i++) {
            buff.append(toHex(values[i])).append(' ');
        }
        return buff.toString();
    }

    public static String toHex(byte[] values) {
        StringBuilder buff = new StringBuilder(values.length * 3);
        for (int i = 0; i < values.length; i++) {
            buff.append(toHex(values[i])).append(' ');
        }
        return buff.toString();
    }

    public static String toHex(byte value) {
        String x = Integer.toHexString(value & 0x00ff);
        return (x.length() == 1 ? "0" + x : x).toUpperCase();
    }

    //---------------------------------------------------------------------------------------

    private byte[] buffer;
    private int index;
    private int size;

    private String stringEncoding;
    private Endian endian;

    public ByteHelper(String byteOrder, String stringEncoding) {
        this.stringEncoding = stringEncoding;
        this.endian = byteOrder.equalsIgnoreCase("little") ? new LittleEndian() : new BigEndian();
        /*
        this.buffer = new byte[timsConfig.getMaxSize()];
        this.index = 0;
        this.size = buffer.length;
        */
    }

    /*
    public ByteHelper(byte[] buffer, int index) {
        this.buffer = buffer;
        this.index = index;
        this.size = buffer.length;
    }

    public ByteHelper(byte[] buffer, int index, int size) {
        this.buffer = buffer;
        this.index = index;
        this.size = size;
    }
    */

    public byte[] getBuffer() {
        return this.buffer;
    }

    public void allocate(int size) {
        this.buffer = new byte[size];
        this.index = 0;
        this.size = size;
    }

    public int getIndex() {
        return this.index;
    }

    public int getSize() {
        return this.size;
    }

    public void allocate(byte[] byteData, int size) {
        this.buffer = byteData;
        this.index = 0;
        this.size = size;
    }

    public void moveIndex(int offset) {
        this.index = offset;
    }

    public byte[] getBufferTrim() {
        byte[] tempBuffer = new byte[this.index];
        System.arraycopy(this.buffer, 0, tempBuffer, 0, tempBuffer.length);
        return tempBuffer;
    }

    //########################################################################################

    public byte getByte() {
        byte value = this.buffer[index];
        this.index += BYTE_SIZE;
        return value;
    }

    public byte[] getBytes(int size) {
        byte[] byteData = new byte[size];
        System.arraycopy(this.buffer, this.index, byteData, 0, size);
        this.index += size;
        return byteData;
    }

    public short getShort() {
        short value = endian.bytesToShort(this.buffer, this.index);
        this.index += SHORT_SIZE;
        return value;
    }

    public int getInt() {
        int value = endian.bytesToInt(this.buffer, this.index);
        this.index += INT_SIZE;
        return value;
    }

    public long getLong() {
        long value = endian.bytesToLong(this.buffer, this.index);
        this.index += LONG_SIZE;
        return value;
    }

    public String getString(int size) {
        String value = "";
        //printStringBuffer(this.buffer);
        try {
            value = new String(this.buffer, this.index, size, this.stringEncoding);
            int i = value.indexOf("\0");
            if(i != -1) {
            	value = value.substring(0, i);
            }
            value = value.trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }  
        this.index += size;

        return value;
    }

    private void printStringBuffer(byte[] buffer) {
    	for(byte b : buffer) {
    		System.out.print(b);
    	}
    	System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");		
	}
    
	public void setByte(byte value) {
        this.buffer[this.index] = value;
        this.index += BYTE_SIZE;
    }

    public void setBytes(byte[] value, int size) {
        System.arraycopy(value, 0, this.buffer, this.index, size);
        this.index += size;
    }

    public void setChar(char value) {
        this.buffer[this.index] = (byte)value;
        this.index += CHAR_SIZE;
    }

    public void setShort(short value) {
        System.arraycopy(endian.shortToBytes(value), 0, this.buffer, this.index, SHORT_SIZE);
        this.index += SHORT_SIZE;
    }

    public void setInt(int value) {
        System.arraycopy(endian.intToBytes(value), 0, this.buffer, this.index, INT_SIZE);
        this.index += INT_SIZE;
    }

    public void setLong(long value) {
        System.arraycopy(endian.longToBytes(value), 0, this.buffer, this.index, LONG_SIZE);
        this.index += LONG_SIZE;
    }

    public void setString(String value, int size) {
        try {
            byte[] strBytes = value.getBytes(this.stringEncoding);

            if(strBytes.length < size) {
                System.arraycopy(strBytes, 0, this.buffer, this.index, strBytes.length);
            } else {
                System.arraycopy(strBytes, 0, this.buffer, this.index, size);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.index += size;
    }

    public byte[] shortToBytes(short value) {
        return endian.shortToBytes(value);
    }
    public byte[] intToBytes(int value) {
        return endian.intToBytes(value) ;
    }
    public byte[] longToBytes(long value) {
        return endian.longToBytes(value) ;
    }
    public byte[] doubleToBytes(double value) {
        return endian.doubleToBytes(value) ;
    }

    //############################################################################################
    //############################################################################################
    //############################################################################################

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

    public class LittleEndian implements Endian {

        public short bytesToShort(byte[] b, int index) {
            return (short) ((b[index + 1] & 0xff) << 8 | (b[index] & 0xff));
        }

        public int bytesToInt(byte[] b, int index) {
            return (b[index + 3] & 0xff) << 24 | (b[index + 2] & 0xff) << 16
                    | (b[index + 1] & 0xff) << 8 | (b[index + 0] & 0xff);
        }

        public long bytesToLong(byte[] b, int index) {
            return ((long) bytesToInt(b, index + 4) << 32)
                    | ((long) bytesToInt(b, index) & 0xffffffffL);
        }

        public double bytesToDouble(byte[] b, int index) {
            long l = bytesToLong(b, index);
            return Double.longBitsToDouble(l);
        }

        public byte[] shortToBytes(short s) {
            byte[] b = new byte[2];
            b[0] = (byte) ((s >> 0) & 0xff);
            b[1] = (byte) ((s >> 8) & 0xff);
            return b;
        }

        public byte[] intToBytes(int i) {
            byte[] b = new byte[4];
            b[0] = (byte) ((i >> 0) & 0xff);
            b[1] = (byte) ((i >> 8) & 0xff);
            b[2] = (byte) ((i >> 16) & 0xff);
            b[3] = (byte) ((i >> 24) & 0xff);
            return b;
        }

        public byte[] longToBytes(long l) {
            byte[] b = new byte[8];
            b[0] = (byte) ((l >> 0) & 0xff);
            b[1] = (byte) ((l >> 8) & 0xff);
            b[2] = (byte) ((l >> 16) & 0xff);
            b[3] = (byte) ((l >> 24) & 0xff);
            b[4] = (byte) ((l >> 32) & 0xff);
            b[5] = (byte) ((l >> 40) & 0xff);
            b[6] = (byte) ((l >> 48) & 0xff);
            b[7] = (byte) ((l >> 56) & 0xff);
            return b;
        }

        public byte[] doubleToBytes(double d) {
            long l = Double.doubleToLongBits(d);
            byte[] dest = longToBytes(l);
            return dest;
        }
    }

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
}
