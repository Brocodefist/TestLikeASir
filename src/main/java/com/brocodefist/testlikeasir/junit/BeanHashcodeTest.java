package com.brocodefist.testlikeasir.junit;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;

/**
 * @author e017645
 */
public class BeanHashcodeTest extends BeanComparisonTest implements NamedBeanTest {

    public BeanHashcodeTest(Object firstInstance, Object secondInstance, Object thirdInstance,
            boolean checkInheritedFields, String[] fieldsToIgnore, boolean testHashCode) {
        super(firstInstance, secondInstance, thirdInstance, checkInheritedFields, fieldsToIgnore, testHashCode);
    }

    @Test
    public void testTheSameObject() {
        assertEquals("The hashcode of the same instance of class " + firstInstance.getClass()
                + " returns different values on successive calls.", firstInstance.hashCode(), firstInstance.hashCode());
    }

    @Test
    public void testDifferentObjectsWithSameFields() throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, SecurityException, NoSuchFieldException {
        initializeInstancesWithDefaultValues();
        assertEquals("The hash code of two instances of class " + firstInstance.getClass()
                + " is different when fields are filled in with the same values.", firstInstance.hashCode(),
                secondInstance.hashCode());
    }

    @Override
    public String getTestClassName() {
        return String.format("Test hashcode method for %s", firstInstance.getClass().getName());
    }

    @Override
    public String getTestMethodName(FrameworkMethod method) {
        return method.getName() + "()";
    }
}
