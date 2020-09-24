package kr.tracom.platform.common.util;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    private static final int BUFFER_SIZE = 1024;

    public static void compressDirectory(String path, String allowExt, String outputFileName) {
        File file = new File(path);
        int pos = outputFileName.lastIndexOf(".");
        if(!outputFileName.substring(pos).equalsIgnoreCase(".ZIP")){
            outputFileName += ".ZIP";
        }
        // 압축 경로 체크
        if(!file.exists()){
            return;
        }

        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try{
            fos = new FileOutputStream(new File(outputFileName));
            zos = new ZipOutputStream(fos);
            // 디렉토리 검색
            searchDirectory(file, allowExt, zos);
        } catch(Exception e){
            System.err.println(e.getMessage());
        } finally{
            if(zos != null) try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(fos != null) try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void compressFile(String path, String outputFileName) {
        File file = new File(path);
        int pos = outputFileName.lastIndexOf(".");
        if(!outputFileName.substring(pos).equalsIgnoreCase(".ZIP")){
            outputFileName += ".ZIP";
        }

        // 압축 경로 체크
        if(file.isFile()) {
            FileOutputStream fos = null;
            ZipOutputStream zos = null;
            try {
                fos = new FileOutputStream(new File(outputFileName));
                zos = new ZipOutputStream(fos);
                String root = file.getPath().substring(0, file.getPath().lastIndexOf(File.separator));
                compressZip(file, root, zos);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            } finally {
                if (zos != null) try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (fos != null) try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void searchDirectory(File file, String allowExt, ZipOutputStream zos) {
        searchDirectory(file, file.getPath(), allowExt, zos);
    }

    private static void searchDirectory(File file, String root, String allowExt, ZipOutputStream zos) {
        //지정된 파일이 디렉토리인지 파일인지 검색
        if(file.isDirectory()){
            //디렉토리일 경우 재탐색(재귀)
            File[] files = file.listFiles();
            for(File f : files){
                searchDirectory(f, root, allowExt, zos);
            }
        } else {
            String fileExt = Files.getFileExtension(file.getPath());
            //String fileExt = FileUtil.getFileExtension(file);
            //파일일 경우 압축을 한다.
            if(fileExt.equalsIgnoreCase(allowExt)) {
                compressZip(file, root, zos);
            }
        }
    }

    private static void compressZip(File file, String root, ZipOutputStream zos) {
        FileInputStream fis = null;
        try{
            String zipName = file.getPath().replace(root+"\\", "");
            String ext = Files.getFileExtension(zipName);

            if("zip".equalsIgnoreCase(ext)) {
                return;
            }

            fis = new FileInputStream(file);
            ZipEntry zipentry = new ZipEntry(zipName);
            zos.putNextEntry(zipentry);

            byte[] bytes = new byte[BUFFER_SIZE];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }

            zos.closeEntry();
        } catch(Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if(fis != null) try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void decompress(String zipFileName, String directory) {
        File zipFile = new File(zipFileName);
        FileInputStream fis = null;
        ZipInputStream zis = null;
        ZipEntry zipentry = null;
        try {
            fis = new FileInputStream(zipFile);
            zis = new ZipInputStream(fis);
            while ((zipentry = zis.getNextEntry()) != null) {
                String filename = zipentry.getName();
                File file = new File(directory, filename);
                if (zipentry.isDirectory()) {
                    file.mkdirs();
                } else {
                    createFile(file, zis);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (zis != null)
                try {
                    zis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private static void createFile(File file, ZipInputStream zis) {
        File parentDir = new File(file.getParent());
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int size;
            while ((size = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, size);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
