package kr.tracom.platform.launcher.manager;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.service.ServiceLauncher;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileLock;
import java.util.Arrays;

public class RuntimeManager {
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
	
	public static void loadJar(String jarFilePath) {
		try {			
			File jarFile = new File(getApplicationPath() + jarFilePath);
			final URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			final Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
				
			if (jarFile.toString().toLowerCase().contains(".jar")) {
				try {
					method.invoke(loader, new Object[] { jarFile.toURI().toURL() });	
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static ServiceLauncher loadService(String jarFilePath, String launcherClass) {
		try {			
			File jarFile = new File(getApplicationPath() + jarFilePath);
			final URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			final Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
				
			if (jarFile.toString().toLowerCase().contains(".jar")) {
				try {
					method.invoke(loader, new Object[] { jarFile.toURI().toURL() });

					if(launcherClass != null && !launcherClass.isEmpty()) {
						Class<?> cls = Class.forName(launcherClass, true, loader);
						ServiceLauncher launcher = (ServiceLauncher)cls.newInstance();

						return launcher;
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return null;
	}

	public static ServiceLauncher loadService(String launcherClass) {
		try {
			final URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			if(launcherClass != null && !launcherClass.isEmpty()) {
				Class<?> cls = Class.forName(launcherClass, true, loader);
				ServiceLauncher launcher = (ServiceLauncher)cls.newInstance();

				return launcher;
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;
	}
	
	public static void configLogback(String path) {
	    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();	    
	    try {
	      JoranConfigurator configurator = new JoranConfigurator();
	      configurator.setContext(context);
	      context.reset(); 
	      configurator.doConfigure(getApplicationPath() + path);
	    } catch (JoranException je) {
	      // StatusPrinter will read this
	    }
	    StatusPrinter.printInCaseOfErrorsOrWarnings(context);
	}
	
	public static String getApplicationPath() {
        File dir = new File ("./");
        String path;
        
        try {
            path = dir.getCanonicalPath();
        } catch (IOException e) {
            path = "";
        }
        return path;
    }

	public static void restartApplication() {
		String script = AppConfig.getApplicationPath() + File.separator + AppConfig.get("platform.startup");
		String param = String.format("/c start %s ^& exit", script);
		try {
			ProcessBuilder builder = new ProcessBuilder("cmd", param);
			builder.start();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String lockString(Object cls) {
		final String CHK_KEY = "checkKey";
		StringBuilder sb = new StringBuilder();
		String csv;
		Object value;
		Class<?> thisClass;

		String[] primitive = new String[] { "class java.lang.String", "int", "short", "byte", "double", "float" };
		try {
			thisClass = Class.forName(cls.getClass().getName());

			Field[] aClassFields = thisClass.getDeclaredFields();
			for(Field f : aClassFields){
				f.setAccessible(true);

				if(!Arrays.asList(primitive).contains(f.getType().toString())) {
					continue;
				}

				if(!f.getName().equals(CHK_KEY)) {
					value = f.get(cls);
					if(value != null) {
						sb.append(f.get(cls) + ",");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		csv = sb.toString();

		if(csv.length() > 0) {
			csv = csv.substring(0, csv.length() - 1);
		}
		return csv;
	}
}
