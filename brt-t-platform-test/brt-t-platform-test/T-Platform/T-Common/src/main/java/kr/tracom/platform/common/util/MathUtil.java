package kr.tracom.platform.common.util;

public class MathUtil {
    public static double cutDecimal(double value, int num) {
        return Math.floor(value * Math.pow(10, num) + 0.5) / Math.pow(10, num);
    }
}
