package kr.tracom.platform.common.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

public class SysUtil {
	public static boolean isAleadyRunning(String fileName) {
		try {
			final File file = new File(fileName);
			final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
			final FileLock fileLock = randomAccessFile.getChannel().tryLock();
			if (fileLock != null) {
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						try {
							fileLock.release();
							randomAccessFile.close();
							file.delete();
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
					}
				});
				return false;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return true;
	}
}
