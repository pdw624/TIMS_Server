package kr.tracom.platform.common.util;

public class ByteUtil {
    /*
    public static int sizeof(byte b)  { return 1; }
    public static int sizeof(short s) { return 2; }
    public static int sizeof(int i)   { return 4; }
    public static int sizeof(long l)  { return 8; }
    public static int sizeof(byte[] b) { return b.length; }

    public static byte[] trimString(byte[] buffer) {
        return rTrim(lTrim(buffer));
    }

    public static byte[] rTrim(byte[] buffer) {
        byte[] tempByte;
        int size = buffer.length;
        for (int i = buffer.length - 1; i >= 0 ; i--) {
            if(buffer[i] != 0x00) {
                break;
            }
            size--;
        }
        tempByte = new byte[size];
        System.arraycopy(buffer, 0, tempByte, 0, size);
        return tempByte;
    }

    public static byte[] lTrim(byte[] buffer) {
        byte[] tempByte;
        int pos = 0;
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] != 0x00) {
                break;
            }
            pos++;
        }
        tempByte = new byte[buffer.length - pos];
        System.arraycopy(buffer, pos, tempByte, 0, buffer.length - pos);
        return tempByte;
    }

    public static String toBinaryString(int value) {
        return String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0');
    }

    public static String toHex(byte[] values) {
        StringBuilder buff = new StringBuilder(values.length * 3);
        for (int i = 0; i < values.length; i++) {
            buff.append(toHex(values[i])).append(' ');
        }
        return buff.toString();
    }

    public static String toHex(byte[] values, int offset, int length) {
        StringBuilder buff = new StringBuilder(length * 3);
        for (int i = offset; i < values.length && i < offset + length; i++) {
            buff.append(toHex(values[i])).append(' ');
        }
        return buff.toString();
    }

    public static String toHex(byte value) {
        String x = Integer.toHexString(value & 0x00ff);
        return (x.length() == 1 ? "0" + x : x).toUpperCase();
    }

    public static String toHex(int value) {
        String x = Integer.toHexString(value);
        return (x.length() == 1 ? "0" + x : x).toUpperCase();
    }

    public static byte[] decToBcd(long num) {
        int digits = 0;

        long temp = num;
        while (temp != 0) {
            digits++;
            temp /= 10;
        }

        int byteLen = digits % 2 == 0 ? digits / 2 : (digits + 1) / 2;
        boolean isOdd = digits % 2 != 0;

        byte bcd[] = new byte[byteLen];

        for (int i = 0; i < digits; i++) {
            byte tmp = (byte) (num % 10);

            if (i == digits - 1 && isOdd)
                bcd[i / 2] = tmp;
            else if (i % 2 == 0)
                bcd[i / 2] = tmp;
            else {
                byte foo = (byte) (tmp << 4);
                bcd[i / 2] |= foo;
            }

            num /= 10;
        }

        for (int i = 0; i < byteLen / 2; i++) {
            byte tmp = bcd[i];
            bcd[i] = bcd[byteLen - i - 1];
            bcd[byteLen - i - 1] = tmp;
        }

        return bcd;
    }

    public static String bcdToString(byte bcd) {
        StringBuilder sb = new StringBuilder();

        byte high = (byte) (bcd & 0xf0);
        high >>>= (byte) 4;
        high = (byte) (high & 0x0f);
        byte low = (byte) (bcd & 0x0f);

        sb.append(high);
        sb.append(low);

        return sb.toString();
    }

    public static String bcdToString(byte[] bcd) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bcd.length; i++) {
            sb.append(bcdToString(bcd[i]));
        }

        return sb.toString();
    }

    public static Date byteToDate(byte[] buffer) {
        String str;

        str = String.format("%02d02d-%02d-%02d %02d:%02d:%02d",
                buffer[0] & 0xFF,
                buffer[1] & 0xFF,
                buffer[2] & 0xFF,
                buffer[3] & 0xFF,
                buffer[4] & 0xFF,
                buffer[5] & 0xFF,
                buffer[6] & 0xFF);

        return stringToDate(str, "yyyy-MM-dd HH:mm:ss");
    }

    public static byte[] dateToByte(Date dt) {
        byte[] buffer = new byte[7];
        String str = dateToString(dt, "yyyyMMddHHmmss");
        buffer[0] = Byte.parseByte(str.substring(0, 2));
        buffer[1] = Byte.parseByte(str.substring(2, 4));
        buffer[2] = Byte.parseByte(str.substring(4, 6));
        buffer[3] = Byte.parseByte(str.substring(6, 8));
        buffer[4] = Byte.parseByte(str.substring(8, 10));
        buffer[5] = Byte.parseByte(str.substring(10, 12));
        buffer[6] = Byte.parseByte(str.substring(12, 14));

        return buffer;
    }

    public static Date stringToDate(String from, String dateFormat) {
        // yyyy-MM-dd HH:mm:ss
        SimpleDateFormat transFormat = new SimpleDateFormat(dateFormat);
        try {
            return transFormat.parse(from);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static String dateToString(Date dt, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                pattern, java.util.Locale.KOREA);
        String dateString = formatter.format(dt);
        return dateString;
    }

    public static int stringToUnixTime(String strDate, String pattern) {
        Date dt = stringToDate(strDate, pattern);
        return (int)(dt.getTime() / 1000L);
    }

    public static String unixTimeToString(long unixTime, String pattern) {
        return dateToString(new Date(unixTime), pattern);
    }

    public static double stringToDouble(String value) {
        double ret;
        try {
            ret = Double.parseDouble(value);
        } catch(Exception e) {
            ret = 0.0;
        }
        return ret;
    }

    public static int stringToInt(String value) {
        int ret;
        try {
            ret = Integer.parseInt(value);
        } catch(Exception e) {
            ret = 0;
        }
        return ret;
    }

    public static short stringToShort(String value) {
        short ret;
        try {
            ret = Short.parseShort(value);
        } catch(Exception e) {
            ret = Short.MIN_VALUE;
        }
        return ret;
    }

    public static int[] getBit(byte b) {
        int[] bitFlag = new int[8];
        for(int i=0; i<8; i++) {
            bitFlag[i] = b & (0x01<<i);
        }
        return bitFlag;
    }
    */
}
