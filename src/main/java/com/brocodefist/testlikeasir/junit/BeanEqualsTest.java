package com.brocodefist.testlikeasir.junit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;

/**
 * @author e017645
 */
public class BeanEqualsTest extends BeanComparisonTest implements NamedBeanTest {

    public BeanEqualsTest(Object firstInstance, Object secondInstance, Object thirdInstance,
            boolean checkInheritedFields, String[] fieldsToIgnore, boolean testHashCode) {
        super(firstInstance, secondInstance, thirdInstance, checkInheritedFields, fieldsToIgnore, testHashCode);
    }

    
    @Test
    @SuppressWarnings("PMD")
    public void testNotEqualsNull() {
        assertFalse("Objects of class " + firstInstance.getClass() + " return true when calling equals(null) on them.",
                firstInstance.equals(null));
    }

    @Test
    public void testNotSameType() {
        Object obj = new Object();
        assertFalse("An object of type " + firstInstance.getClass()
                + " return true when calling equals() on a java.lang.Object instance.", firstInstance.equals(obj));
        assertTrue("The instances were different -equals returns false- but their hashcodes were the same, when comparing an object of class "
                + firstInstance.getClass() + " and a java.lang.Object instance.", obj.hashCode() != firstInstance.hashCode());
    }

    /**
     * Test that the equals method is reflexive: for any non-null reference
     * value x, x.equals(x) should return true.
     */
    @Test
    public void testIsReflexive() {
        assertEquals("One identical object of class " + firstInstance.getClass() + " isn't equals() to itself.",
                firstInstance, firstInstance);
    }

    /**
     * Test that the equals method is symmetric: for any non-null reference
     * values x and y, x.equals(y) should return true if and only if y.equals(x)
     * returns true.
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SecurityException
     */
    @Test
    public void testIsSymmetric() throws SecurityException, InstantiationException, IllegalAccessException,
            NoSuchFieldException {
        initializeInstancesWithDefaultValues();
        assertTrue("The equals method of Class " + firstInstance.getClass()
                + " is not symmetric: one is equals two another but the second one is not equals to the first one.",
                firstInstance.equals(secondInstance) && firstInstance.equals(secondInstance));
    }

    /**
     * Test that the equals method is transitive: for any non-null reference
     * values x, y, and z, if x.equals(y) returns true and y.equals(z) returns
     * true, then x.equals(z) should return true.
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SecurityException
     */
    @Test
    public void testIsTransitive() throws SecurityException, InstantiationException, IllegalAccessException,
            NoSuchFieldException {
        initializeInstancesWithDefaultValues();

        assertTrue(
                "The equals method is not transitive. Three instances of " + firstInstance.getClass()
                + " with the same fields are not equal.",
                firstInstance.equals(secondInstance) && firstInstance.equals(thirdInstance)
                && secondInstance.equals(thirdInstance));
    }

    /**
     * Test that the equals method is consistent: for any non-null reference
     * values x and y, multiple invocations of x.equals(y) consistently return
     * true or consistently return false, provided no information used in equals
     * comparisons on the objects is modified.
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SecurityException
     */
    @Test
    public void testIsConsistent() throws SecurityException, InstantiationException, IllegalAccessException,
            NoSuchFieldException {
        initializeInstancesWithDefaultValues();

        assertTrue("The equals method of Class " + firstInstance.getClass()
                + " is not consistent: multiple invocations return a different value.",
                firstInstance.equals(secondInstance) && firstInstance.equals(secondInstance));
    }

    @Test
    public void testTwoDifferentHashCodeImpliesNotEquals() throws SecurityException, InstantiationException,
            IllegalAccessException, NoSuchFieldException {
        if (testHashCode) {
            initializeInstancesWithDefaultValues();
            Class<?> clss = firstInstance.getClass();

            while (clss != null) {
                Field[] fs = firstInstance.getClass().getDeclaredFields();
                for (Field f : fs) {
                    if (!fieldsToIgnore.contains(f.getName())) {
                        Object[] fieldValues = BeanPropertiesRunner.getDefaultValueByType(f.getType());
                        if (fieldValues.length > 1 && !Modifier.isFinal(f.getModifiers())
                                && !Modifier.isStatic(f.getModifiers())) {
                            f.setAccessible(true);
                            f.set(secondInstance, fieldValues[1]);
                            if (firstInstance.hashCode() != secondInstance.hashCode()) {
                                assertFalse("Objects of class " + firstInstance.getClass()
                                        + " return true for equals() even when hash codes are different.",
                                        firstInstance.equals(secondInstance));
                            }
                            f.set(secondInstance, fieldValues[0]);
                        }
                    }
                }
                if (checkInheritedFields) {
                    clss = clss.getSuperclass();
                } else {
                    clss = null;
                }
            }
        }
    }

    @Test
    public void testEqualsTrueImpliesSameHashCode() throws SecurityException, InstantiationException, IllegalAccessException,
            NoSuchFieldException {
        if (testHashCode) {
            initializeInstancesWithDefaultValues();

            assertTrue("Equals objects of Class " + firstInstance.getClass() + " do not have the same hash codes.",
                    firstInstance.equals(secondInstance) && firstInstance.hashCode() == secondInstance.hashCode());
        }
    }

    @Test
    public void testDifferentObjectsWithSameFields() throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, SecurityException, NoSuchFieldException {
        initializeInstancesWithDefaultValues();
        assertEquals("Two instances of " + firstInstance.getClass() + " with the same fields are not equal.",
                firstInstance, secondInstance);
    }

    @Test
    public void testDifferentObjectsDifferentFields() throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, SecurityException, NoSuchFieldException {
        initializeInstancesWithDefaultValues();
        Class<?> cls = firstInstance.getClass();

        while (cls != null) {
            Field[] fields = firstInstance.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!fieldsToIgnore.contains(field.getName())) {
                    Object[] fieldValues = BeanPropertiesRunner.getDefaultValueByType(field.getType());
                    if (fieldValues.length > 1 && !Modifier.isFinal(field.getModifiers())
                            && !Modifier.isStatic(field.getModifiers())) {
                        field.setAccessible(true);
                        field.set(secondInstance, fieldValues[1]);
                        assertFalse("Objects of class " + firstInstance.getClass()
                                + " return true for equals() even when the field " + field
                                + " is set to different value in two instances.", firstInstance.equals(secondInstance));
                        assertTrue("The instances were different -equals returns false- but their hashcodes were the same; suspect field is "
                                + field + ".", firstInstance.hashCode() != secondInstance.hashCode());
                        field.set(secondInstance, fieldValues[0]);
                    }
                }
            }
            if (checkInheritedFields) {
                cls = cls.getSuperclass();
            } else {
                cls = null;
            }
        }
    }

    @Override
    public String getTestClassName() {
        return String.format("Test equals method for %s", firstInstance.getClass().getName());
    }

    @Override
    public String getTestMethodName(FrameworkMethod method) {
        return method.getName() + "()";
    }
}
