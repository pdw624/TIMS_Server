package kr.tracom.platform.common.util;

import java.text.NumberFormat;
import java.util.Random;

public class StringUtil {
	public static String nvl(Object obj) {
		return ((obj != null) ? obj.toString() : "");
	}

	public static String nvl(String str) {
		return ((str != null) ? str : "");
	}

	public static String nvl(String str, String value) {
		return ((str != null && str.length() > 0) ? str : value);
	}

	public static String nvl(Object obj, String value) {
		return ((obj != null) ? String.valueOf(obj) : value);
	}

	public static int nvlToInt(String str) {
		int i = 0;
		try {
			i = Integer.parseInt(nvl(str, "0"));
			return i;
		} catch (NumberFormatException e) {

		}
		return 0;
	}

	public static int nvlToInt(String str, String value) {
		int i = 0;
		try {
			i = Integer.parseInt(nvl(str, value));
			return i;
		} catch (NumberFormatException e) {
			i = Integer.parseInt(value);
		}
		return i;
	}
	
	public static float nvlToFloat(String str) {
		return Float.parseFloat(nvl(str, "0"));
	}

	public static double nvlToDouble(String str) {
		return Double.parseDouble(nvl(str, "0"));
	}

	public static boolean tryParseInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
	
	public static boolean tryParseFloat(String value) {
		try {
			Float.parseFloat(value);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public static boolean tryParseDouble(String value) {
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public static boolean isNullOrEmpty(String value) {
		if(value == null) return true;
		else {
			return value.trim().length() == 0 ? true : false;
		}
	}
    
	public static char toChar(String str) {
    	return Character.forDigit(Integer.parseInt(str), 10);
    }
	
	public static String arrayJoin(String array[], String separator) {
		String result = "";

		for (int i = 0; i < array.length; i++) {
			result += array[i];
			if (i < array.length - 1)
				result += separator;
		}
		return result;
	}
	
	public static String randomString(String type, int cnt) {
		StringBuffer strBuffer = new StringBuffer();
		char str[] = new char[1];
		// 특수기호 포함
		if (type.equals("P")) {
			for (int i = 0; i < cnt; i++) {
				str[0] = (char) ((Math.random() * 94) + 33);
				strBuffer.append(str);
			}
			// 대문자로만
		} else if (type.equals("A")) {
			for (int i = 0; i < cnt; i++) {
				str[0] = (char) ((Math.random() * 26) + 65);
				strBuffer.append(str);
			}
			// 소문자로만
		} else if (type.equals("S")) {
			for (int i = 0; i < cnt; i++) {
				str[0] = (char) ((Math.random() * 26) + 97);
				strBuffer.append(str);
			}
			// 숫자형으로
		} else if (type.equals("I")) {
			int strs[] = new int[1];
			for (int i = 0; i < cnt; i++) {
				strs[0] = (int) (Math.random() * 9);
				strBuffer.append(strs[0]);
			}
			// 소문자, 숫자형
		} else if (type.equals("C")) {
			Random rnd = new Random();
			for (int i = 0; i < cnt; i++) {
				if (rnd.nextBoolean()) {
					strBuffer.append((char) ((int) (rnd.nextInt(26)) + 97));
				} else {
					strBuffer.append((rnd.nextInt(10)));
				}
			}
		}
		return strBuffer.toString();
	}

	public static String formatSeperatedByComma(long val) {
		NumberFormat format = NumberFormat.getNumberInstance();
		return format.format(val);
	}

	public static byte stringToByte(String value) {
		return (byte)Short.parseShort(value);
	}
}
