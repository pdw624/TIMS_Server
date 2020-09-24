package kr.tracom.platform.common.util;

public class ClassUtil {
    public static Class getClass(String path) {
        try {
            Class cls = Class.forName(path);

            return cls;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();

            return null;
        }
    }
}
