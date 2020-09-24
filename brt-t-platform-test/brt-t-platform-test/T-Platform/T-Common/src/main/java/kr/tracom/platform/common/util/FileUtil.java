package kr.tracom.platform.common.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static long getSize(String path) {
        File file = new File(path);
        if(file.exists()) {
            return file.length();
        } else {
            return -1;
        }
    }

    public static String getFileName(String absolutePath) {
        File f = new File(absolutePath);
        return f.getName();
    }

    public static String combine(String... paths) {
        File file = new File(paths[0]);

        for (int i = 1; i < paths.length ; i++) {
            file = new File(file, paths[i]);
        }
        return file.getPath();
    }
    
    public static String[] getFileList(String path) {
        List<String> fileList = new ArrayList<>();
        File dirFile = new File(path);
        try {
            for (File f : dirFile.listFiles()) {
                if (f.isFile()) {
                    fileList.add(f.getCanonicalPath());
                }
            }
        } catch (IOException e) { 
            e.printStackTrace();
        }
        return fileList.toArray(new String[0]);
    }
    
    public static void deleteFileInDirectory(String path) {
        File[] listFile = new File(path).listFiles();
        try {
            if (listFile.length > 0) {
                for (int i = 0; i < listFile.length; i++) {
                    if (listFile[i].isFile()) {
                        listFile[i].delete();
                    } else {
                        deleteFileInDirectory(listFile[i].getPath());
                    }
                    listFile[i].delete();
                }
            }
        } catch (Exception e) {
            System.err.println(System.err);
        }
    }

    public static void makeDirectory(String path) {
        File f = new File(path);
        if(!f.exists()) {
            f.mkdir();
        }
    }


    public static void deleteFile(String path) {
        File f = new File(path);
        if(f.exists()) {
            f.delete();
        }
    }
    
	public static void deleteLog(String logPath, long deleteDay) {
		File path = new File(logPath);
		String[] fileList = path.list();
		long nowDt = System.currentTimeMillis();
		
		for(String file : fileList ) {
			File logFile = new File(logPath + File.separator + file);
			if(nowDt - logFile.lastModified() > deleteDay) {
				logFile.delete();
			}
		}
	}
	
    public static boolean isExist(String path) {
        File f = new File(path);
        
        return f.isFile() || f.isDirectory();
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

    public static void deleteByExtension(String path, String fileExt) {
        FileDelete fd = new FileDelete();
        int affect = fd.deleteFiles(path, fileExt);

        //System.out.println("delete files : " + affect);
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }
    public static String getFtpPath(String ftpPath) {
        return ftpPath.substring(0, ftpPath.lastIndexOf("/") + 1);
    }

    public static String getFtpFile(String ftpPath) {
        return ftpPath.substring(ftpPath.lastIndexOf("/") + 1, ftpPath.length());
    }

}
