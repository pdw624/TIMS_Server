package kr.tracom.platform.master.base;

import com.google.common.io.Files;
import kr.tracom.platform.common.util.FileUtil;
import kr.tracom.platform.service.dao.ServiceDao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class BaseFile {
    protected final int batchSize = 100;
    protected final String byteOrder = "little";
    protected final String stringEncoding = "EUC-KR";

    protected String serviceId = "-";
    protected String workingDir;
    protected String absoluteDir;
    protected ServiceDao serviceDao;
    protected String versionNumber;
    protected String versionName;
    protected String prefix;
    protected String dbType;
    protected String fileExt = "DAT";
    protected String applyDateTime;

    public abstract String execute(Object args);

    protected String getFileName(String appDir, String device, String type, String applyDateTime) {
        return String.format("%s/%s_%s_%s.%s", appDir, device, type, applyDateTime, fileExt);
    }

    protected void writeFile(String path, byte[] byteData, int size) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(path));
            fos.write(byteData, 0, size);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected byte[] readFile(String filePath) {
        if(!FileUtil.isExist(filePath)) {
            return null;
        }

        byte[] byteData = null;
        try {
            byteData = Files.toByteArray(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteData;
    }
}
