package com.brocodefist.testlikeasir.junit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class BeanComparisonTest {

    protected Object firstInstance;
    protected Object secondInstance;
    protected Object thirdInstance;

    protected boolean checkInheritedFields;
    protected Set<String> fieldsToIgnore = new HashSet<String>();
    
    protected boolean testHashCode;

    public BeanComparisonTest(Object firstInstance, Object secondInstance, Object thirdInstance,
            boolean checkInheritedFields, String[] fieldsToIgnore, boolean testHashCode) {
        this.firstInstance = firstInstance;
        this.secondInstance = secondInstance;
        this.thirdInstance = thirdInstance;
        this.checkInheritedFields = checkInheritedFields;
        this.fieldsToIgnore.addAll(Arrays.asList(fieldsToIgnore));
        this.testHashCode = testHashCode;
    }

    protected void initializeInstancesWithDefaultValues() throws InstantiationException, IllegalAccessException,
            SecurityException, NoSuchFieldException {

        Class<?> cls = firstInstance.getClass();

        while(cls != null) {
            Field[] fields = cls.getDeclaredFields();
            for(Field field : fields) {
                if(!fieldsToIgnore.contains(field.getName()) && !Modifier.isStatic(field.getModifiers())) {
                    Object[] fieldValues = BeanPropertiesRunner.getDefaultValueByType(field.getType());
                    if(fieldValues.length >= 1) {
                        field.setAccessible(true);
                        field.set(firstInstance, fieldValues[0]);
                        field.set(secondInstance, fieldValues[0]);
                        field.set(thirdInstance, fieldValues[0]);
                    }
                }
            }

            if(checkInheritedFields) {
                cls = cls.getSuperclass();
            }
            else {
                cls = null;
            }
        }
    }
}
