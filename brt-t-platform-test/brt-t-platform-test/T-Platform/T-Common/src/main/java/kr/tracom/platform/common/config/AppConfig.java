package kr.tracom.platform.common.config;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AppConfig {
    private static Map<String, String> configMap = new HashMap<String, String>();    
	
    public static void read(String path) {
    	read(AppConfig.getResources(path));
    }
    
    public static void read(InputStream is) {
        Properties props = null;
        try {
        	configMap.clear();
            props = new Properties();
            props.load(is);
            Enumeration<Object> enumeration = props.keys();

            while (enumeration.hasMoreElements()) {
                String bkey = (String) enumeration.nextElement();
                configMap.put(bkey, props.getProperty(bkey));
            }
            is.close();

            //configMap = Collections.unmodifiableMap(configMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void write(String resourceFile) {
    	Properties props = new Properties();
		for (Map.Entry<String, String> elem : configMap.entrySet()) {
			props.setProperty(elem.getKey(), elem.getValue());
		}
		File file = new File(getClasspath(resourceFile));
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(file);
			//props.storeToXML(fileOut, "AppConfig");
            props.store(fileOut, "AppConfig");
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public static String get(String key) {
    	String value = configMap.get(key);
        return value == null ? "" : value.trim();
    }

    public static void set(String key, String value) {
        configMap.put(key, value);
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
    
    public static String getClasspath(String resource) {
    	return getApplicationPath() + resource;
    }
    
    public static InputStream getResources(String resource) {
    	InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(getClasspath(resource));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	return inputStream;
    }
}
