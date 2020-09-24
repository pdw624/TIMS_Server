package kr.tracom.platform.launcher.manager;

import kr.tracom.platform.common.util.ZipUtil;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.manager.CodeManager;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileLogManager {
    private static final Logger logger = LoggerFactory.getLogger(FileLogManager.class);

    private static final String PRCS_TYPE_DEL = CodeManager.LogPrcsType.DEL.getValue();
    private static final String PRCS_TYPE_ZIP = CodeManager.LogPrcsType.ZIP.getValue();
    private static final String PRCS_TYPE_ZAD = CodeManager.LogPrcsType.ZAD.getValue();

    public static void execute(String prcsType, DateTime nowDate, String logPath, int deleteDays) {
        DateTime dirDate;
        int gapDays;
        File dirFile = new File(logPath);
        StringBuilder sb = new StringBuilder();

        sb.append("Log file management start");
        for (File logDir : dirFile.listFiles()) {
            if(logDir.isDirectory()) {
                //dirDate = DateTime.parse(logDir.getName(), DateTimeFormat.forPattern(PlatformConfig.PLF_DATE_FORMAT));
                dirDate = new DateTime(logDir.lastModified()).dayOfMonth().roundFloorCopy();
                gapDays = Days.daysBetween(dirDate, nowDate).getDays();

                sb.append(String.format("\r\n%s : %s, %d", logDir.getName(), dirDate.toString(PlatformConfig.PLF_DT_FORMAT), gapDays));
                if(gapDays > 0) {
                    if(PRCS_TYPE_DEL.equals(prcsType)) {
                        deleteDirectory(new File(combine(logPath, logDir.getName())));
                    } else if(PRCS_TYPE_ZIP.equals(prcsType)) {
                        zipDirectory(logPath, logDir.getName());
                    } else if(PRCS_TYPE_ZAD.equals(prcsType)) {
                        zipDirectory(logPath, logDir.getName());
                        deleteDirectory(new File(combine(logPath, logDir.getName())));
                    }
                }
            }
        }
        logger.info(sb.toString());

        // 압축파일은 삭제일을 체크하여 삭제한다.
        if(deleteDays > 0) {
            deleteFileByTime(logPath, nowDate, deleteDays);
        }
    }

    public static void zipDirectory(String parentPath, String directoryName) {
        String directoryPath = combine(parentPath, directoryName);
        File f = new File(directoryPath);
        if(f.isDirectory()) {
            try {
                String outputFile = combine(parentPath, directoryName + ".ZIP");

                ZipUtil.compressDirectory(directoryPath, "log", outputFile);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public static String combine(String path1, String path2) {
        File file1 = new File(path1);
        File file2 = new File(file1, path2);
        return file2.getPath();
    }

    public static void deleteDirectory(File logPath) {
        if(!logPath.exists()) {
            return;
        }

        File[] fileList = logPath.listFiles();

        for(File file : fileList ) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }

        logPath.delete();
    }

    public static void deleteFileByTime(String logPath, DateTime nowDate, int deleteDays) {
        File path = new File(logPath);
        String[] fileList = path.list();
        DateTime fileDate;
        int gapDays;

        for(String file : fileList ) {
            File logFile = new File(combine(logPath, file));
            if(logFile.isFile()) {
                //fileDate = DateTime.parse(logFile.getName(), DateTimeFormat.forPattern(PlatformConfig.PLF_DATE_FORMAT));
                fileDate = new DateTime(logFile.lastModified()).dayOfMonth().roundFloorCopy();
                gapDays = Days.daysBetween(fileDate, nowDate).getDays();

                if (gapDays > deleteDays) {
                    logFile.delete();
                }
            }
        }
    }
}
