package com.brocodefist.testlikeasir.junit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;

/**
 * Class for testing a getter or a setter.
 *
 * @author e017645
 */
public class BeanPropertyTest implements NamedBeanTest {

    Object instance;
    Field field;
    Method method;
    boolean isSet;
    Object value;

    public BeanPropertyTest(Object instance, Field field, Method method,
            boolean isSet, Object value) {
        this.instance = instance;
        this.field = field;
        this.field.setAccessible(true);
        this.method = method;
        this.isSet = isSet;
        this.value = value;
    }

    @Test
    public void testMethod() throws InstantiationException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Object result;
        if (isSet) {
            method.invoke(instance, value);
            result = field.get(instance);
        } else {
            if (Modifier.isFinal(field.getModifiers())) {
                result = method.invoke(instance);
                value = field.get(instance);
            } else {
                field.set(instance, value);
                result = method.invoke(instance);
            }
        }

        assertEquals("Expected and resulting values of field " + field
                + " were different when using value " + value + ".", result,
                value);
    }

    @Override
    public String getTestClassName() {
        String valueAsString = value == null ? "null" : value.toString();
        return String
                .format("Test %s with %s", method.getName(), valueAsString);
    }

    @Override
    public String getTestMethodName(FrameworkMethod method) {
        String valueAsString = value == null ? "null" : value.toString();
        return String
                .format("Test %s with %s", method.getName(), valueAsString);
    }
}
