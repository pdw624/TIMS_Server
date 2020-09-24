package kr.tracom.platform.attribute.manager;

import kr.tracom.platform.net.protocol.TimsAttribute;
import kr.tracom.platform.net.protocol.factory.TimsAttributeFactory;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AttributeManager {
    public static final String COMMON_ATTRIBUTE = "kr.tracom.platform.attribute.common";
    public static final String IMP_ATTRIBUTE = "kr.tracom.platform.attribute.imp";

    public static List<Short> bind(String attributeClassPath) {
        Reflections reflections = new Reflections(attributeClassPath);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(TimsAttribute.class);
        List<Short> attributeList = new ArrayList<Short>();
        for (Class<?> cls : classes) {
            TimsAttribute attribute = cls.getAnnotation(TimsAttribute.class);
            if(attribute != null) {
                TimsAttributeFactory.attributeClasses.put(attribute.attributeId(), cls);

                attributeList.add(attribute.attributeId());
            }
        }
        return attributeList;
    }

    public static List<Short> getCommonAttributes() {
        Reflections reflections = new Reflections(COMMON_ATTRIBUTE);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(TimsAttribute.class);
        List<Short> attributeList = new ArrayList<>();
        for (Class<?> cls : classes) {
            TimsAttribute attribute = cls.getAnnotation(TimsAttribute.class);
            if(attribute != null) {
                attributeList.add(attribute.attributeId());
            }
        }
        return attributeList;
    }
}
