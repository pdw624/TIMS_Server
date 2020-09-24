package kr.tracom.platform.common.log;

import org.joda.time.DateTime;

public class Debug {
    public static void log(String className, Object... args) {
        System.out.print(DateTime.now().toString("HH:mm:ss"));
        System.out.println(" +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println(className);
        for (Object arg : args) {
            System.out.println(" - " + arg);
        }
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
}
