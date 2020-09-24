package kr.tracom.platform.net.protocol.factory;

import kr.tracom.platform.net.protocol.attribute.AtData;

import java.util.HashMap;
import java.util.Map;

public class TimsAttributeFactory {
	public static Map<Short, Class<?>> attributeClasses = new HashMap<>();
	
	public static AtData getAttribute(short attributeId) {
		Class<?> cls = attributeClasses.get(attributeId);
		AtData table = null;
		if(cls != null) {
			try {
				table =  (AtData) cls.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return table;
	}
}
