package com.brocodefist.testlikeasir.junit;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Our own Runner class.
 * 
 * @author e017645
 */
public class BeanPropertiesRunner extends Suite {

    /**
     * Annotation for a method which provides the bean class to be tested and
     * several other parameters about how to test it.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface BeanClassToTest {
        /**
         * The fully qualified class name of the class to be tested.
         * 
         * @return The fully qualified class name of the class to be tested.
         */
        String testClass();

        /**
         * Whether the inherited fields will be tested too.
         * 
         * @return Whether the inherited fields will be tested too.
         */
        boolean inheritFields() default false;

        /**
         * List of properties to be ignored (not tested).
         * 
         * @return List of properties to be ignored (not tested).
         */
        String[] ignoreProperties() default {};

        /**
         * Whether to test exception methods (constructors and getters for
         * combinations of message and cause) or not.
         * 
         * @return Whether to test exception methods (constructors and getters
         *         for combinations of message and cause) or not.
         */
        boolean testStandardExceptionMethods() default true;

        /**
         * Whether to test the method hashcode or not.
         * 
         * @return Whether to test the method hashcode or not.
         */
        boolean testHashcode() default true;

        /**
         * Whether to test the method equals or not.
         * 
         * @return Whether to test the method equals or not.
         */
        boolean testEquals() default true;

        /**
         * Whether the inherited fields will be tested too for equals and
         * hashcode tests.
         * 
         * @return Whether the inherited fields will be tested too for equals
         *         and hashcode tests.
         */
        boolean inheritIdentityFields() default false;

        /**
         * List of properties to be ignored for equals and hashcode tests.
         * 
         * @return List of properties to be ignored for equals and hashcode
         *         tests.
         */
        String[] ignoreIdentityProperties() default {};
    }

    // Attributes
    Class<?> testClass;
    ArrayList<Runner> runners;

    static final Map<Object, Object> oneNullMap = new HashMap<Object, Object>();
    static {
        oneNullMap.put(null, null);
    }

    static final Map<Class<?>, Object[]> defaultValues = new HashMap<Class<?>, Object[]>();
    static {
        defaultValues.put(Map.class, new Object[] { new HashMap<Object, Object>(), oneNullMap });
        defaultValues.put(List.class, new Object[] { new ArrayList<Object>(), Arrays.asList((Object) null) });
        defaultValues.put(Set.class,
                new Object[] { new HashSet<Object>(), new HashSet<Object>(Arrays.asList((Object) null)) });
        defaultValues.put(String.class, new Object[] { "<test string>", "<test string 2>" });
        defaultValues.put(Boolean.TYPE, new Object[] { Boolean.TRUE, Boolean.FALSE });
        defaultValues.put(Byte.TYPE, new Object[] { (byte) 42, (byte) 0 });
        defaultValues.put(Short.TYPE, new Object[] { 42, 0 });
        defaultValues.put(Integer.TYPE, new Object[] {42, 0 });
        defaultValues.put(Long.TYPE, new Object[] {  42l, 0l });
        defaultValues.put(Float.TYPE, new Object[] {  42f,  0f });
        defaultValues.put(Double.TYPE, new Object[] {  42d,  0d });
        defaultValues.put(Character.TYPE, new Object[] { 'S', new Character('E') });
        defaultValues.put(Boolean.class, new Object[] { Boolean.TRUE, Boolean.FALSE });
        defaultValues.put(Byte.class, new Object[] { (byte) 42,(byte) 0 });
        defaultValues.put(Short.class, new Object[] {  42, 0 });
        defaultValues.put(Integer.class, new Object[] { 42, 0 });
        defaultValues.put(Long.class, new Object[] { 42l, 0l });
        defaultValues.put(Float.class, new Object[] { 42f,  0f });
        defaultValues.put(Double.class, new Object[] { 42d,  0d });
        defaultValues.put(Character.class, new Object[] { 'S', 'E'});
    }

    static final Map<Class<?>, Object[]> defaultArrayValues = new HashMap<Class<?>, Object[]>();
    static {
        defaultArrayValues.put(String.class, new Object[] { new String[] {}, new String[] { "<test string>" } });
        defaultArrayValues.put(Boolean.TYPE, new Object[] { new boolean[] {},
                new boolean[] { Boolean.TRUE, Boolean.FALSE } });
        defaultArrayValues.put(Byte.TYPE, new Object[] { new byte[] {},
                new byte[] { (byte) 42, (byte) 0 } });
        defaultArrayValues.put(Short.TYPE, new Object[] { new short[] {},
                new short[] { 42, 0 } });
        defaultArrayValues.put(Integer.TYPE,
                new Object[] { new int[] {}, new int[] { 42,0 } });
        defaultArrayValues.put(Long.TYPE, new Object[] { new long[] {},
                new long[] { 42l,0l } });
        defaultArrayValues.put(Float.TYPE, new Object[] { new float[] {},
                new float[] { 42f,0f } });
        defaultArrayValues.put(Double.TYPE, new Object[] { new double[] {},
                new double[] { 42d,0d } });
        defaultArrayValues.put(Character.TYPE, new Object[] { new char[] {}, new char[] { 'S'} });
        defaultArrayValues.put(Boolean.class, new Object[] { new Boolean[] {},
                new Boolean[] { Boolean.TRUE, Boolean.FALSE } });
        defaultArrayValues.put(Byte.class, new Object[] { new Byte[] {},
                new Byte[] {(byte) 42, (byte) 0 } });
        defaultArrayValues.put(Short.class, new Object[] { new Short[] {},
                new Short[] {  42, 0 } });
        defaultArrayValues.put(Integer.class, new Object[] { new Integer[] {},
                new Integer[] { 42, 0 } });
        defaultArrayValues.put(Long.class, new Object[] { new Long[] {},
                new Long[] { 42l,0l } });
        defaultArrayValues.put(Float.class, new Object[] { new Float[] {},
                new Float[] { 42f,0f } });
        defaultArrayValues.put(Double.class, new Object[] { new Double[] {},
                new Double[] {42d,0d } });
        defaultArrayValues.put(Character.class, new Object[] { new Character[] {},
                new Character[] { 'S'} });
    }

    // Constructors
    @SuppressWarnings({ "PMD.EmptyCatchBlock", "unchecked" })
    public BeanPropertiesRunner(Class<?> testClass) throws Throwable {
        super(testClass, Collections.<Runner> emptyList());
        this.testClass = testClass;
        runners = new ArrayList<Runner>();

        String realTestClassName = testClass.getAnnotation(BeanClassToTest.class).testClass();
        boolean inheritFields = testClass.getAnnotation(BeanClassToTest.class).inheritFields();
        boolean testStandardExceptionMethods =
                testClass.getAnnotation(BeanClassToTest.class).testStandardExceptionMethods();
        boolean testHashcode = testClass.getAnnotation(BeanClassToTest.class).testHashcode();
        boolean testEquals = testClass.getAnnotation(BeanClassToTest.class).testEquals();

        boolean inheritIdentityFields = testClass.getAnnotation(BeanClassToTest.class).inheritIdentityFields();
        String[] ignoreIdentityPropertiesArray =
                testClass.getAnnotation(BeanClassToTest.class).ignoreIdentityProperties();

        String[] ignorePropertiesArray = testClass.getAnnotation(BeanClassToTest.class).ignoreProperties();
        Set<String> ignoreProperties = new HashSet<String>(Arrays.asList(ignorePropertiesArray));

        Class<?> realTestClass = Class.forName(realTestClassName);

        PropertyDescriptor[] properties = Introspector.getBeanInfo(realTestClass).getPropertyDescriptors();
        for (PropertyDescriptor propertie : properties) {
            try {
                Field field = getFieldFromClass(realTestClass, propertie.getName(), inheritFields);
                if (!ignoreProperties.contains(field.getName())) {
                    Object[] defaultValue = getDefaultValue(testClass, field);
                    Method getMethod = propertie.getReadMethod();
                    Method setMethod = propertie.getWriteMethod();
                    Object instance = getInstance(testClass, realTestClass);
                    if(getMethod != null) {
                        for (Object defaultValue1 : defaultValue) {
                            runners.add(new TestClassRunnerForMethod(BeanPropertyTest.class, new BeanPropertyTest(instance, field, getMethod, false, defaultValue1)));
                        }
                    }
                    if(setMethod != null) {
                        for (Object defaultValue1 : defaultValue) {
                            runners.add(new TestClassRunnerForMethod(BeanPropertyTest.class, new BeanPropertyTest(instance, field, setMethod, true, defaultValue1)));
                        }
                    }
                }
            }catch(NoSuchFieldException e) {
                // This means that a property recovered using the Introspector
                // do not have a
                // equivalent field. So this property will be ignored.
            }
        }

        // The first child runner is the standard test-runner that checks the
        // test of testClass
        try {
            runners.add(new BlockJUnit4ClassRunner(testClass));
        }
        catch(InitializationError e) {
            // This means that there is no other tests
        }

        // If the class is an exception, and testing exception methods isn't
        // disabled, test it too that way.
        if(testStandardExceptionMethods && Throwable.class.isAssignableFrom(realTestClass)) {
            runners.add(new TestClassRunnerForMethod(BeanExceptionTest.class, new BeanExceptionTest(
                    (Class<? extends Throwable>) realTestClass)));
        }

        boolean customEquals =
                !realTestClass.getMethod("equals", Object.class).getDeclaringClass().equals(Object.class);
        boolean customHashCode = !realTestClass.getMethod("hashCode").getDeclaringClass().equals(Object.class);

        // If test hashcode method is enable, test it too
        if(testHashcode && (customEquals || customHashCode)) {
            Object instance1 = getInstance(testClass, realTestClass);
            Object instance2 = getInstance(testClass, realTestClass);
            Object instance3 = getInstance(testClass, realTestClass);

            runners.add(new TestClassRunnerForMethod(BeanHashcodeTest.class, new BeanHashcodeTest(instance1, instance2,
                    instance3, inheritIdentityFields, ignoreIdentityPropertiesArray, testHashcode)));
        }

        // If test equeals method is enable, test it too
        if(testEquals && (customEquals || customHashCode)) {
            Object instance1 = getInstance(testClass, realTestClass);
            Object instance2 = getInstance(testClass, realTestClass);
            Object instance3 = getInstance(testClass, realTestClass);

            runners.add(new TestClassRunnerForMethod(BeanEqualsTest.class, new BeanEqualsTest(instance1, instance2,
                    instance3, inheritIdentityFields, ignoreIdentityPropertiesArray, testHashcode)));
        }
    }

    private Object getInstance(Class<?> testClass, Class<?> realTestClass) throws SecurityException,
            IllegalArgumentException, InstantiationException, IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        if(testClass.newInstance() instanceof BeanPropertyTestingI) {
            Method getInstance = testClass.getMethod("getInstance");
            return getInstance.invoke(testClass.newInstance());
        }
        else {
            return realTestClass.newInstance();
        }
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    // Private Methods
    private Object[] getDefaultValue(final Class<?> testClass, final Field realField) throws IllegalArgumentException,
            IllegalAccessException, InstantiationException, SecurityException, NoSuchMethodException,
            InvocationTargetException {
        Object[] defaultValue = null;
        try {
            Field field = testClass.getDeclaredField(realField.getName());

            Object defaultValueAsObject = field.get(testClass.newInstance());

            int length = Array.getLength(defaultValueAsObject);
            defaultValue = new Object[length];

            for(int i = 0; i < length; i++) {
                defaultValue[i] = Array.get(defaultValueAsObject, i);
            }
        }
        catch(NoSuchFieldException e) {
            defaultValue = getDefaultValueByType(realField.getType());
        }
        return defaultValue;
    }

    public static Object[] getDefaultValueByType(Class<?> type) throws InstantiationException, IllegalAccessException {
        Object[] defaultValue;
        if(defaultValues.containsKey(type)) {
            defaultValue = defaultValues.get(type);
        }
        else if(type.isArray() && defaultArrayValues.containsKey(type.getComponentType())) {
            defaultValue =
                    new Object[] { null, defaultArrayValues.get(type.getComponentType())[0],
                            defaultArrayValues.get(type.getComponentType())[1] };
        }
        else if(type.isArray()) {
            try {
                type.getConstructor();
            }
            catch(NoSuchMethodException e) {
                return new Object[] { null, new Object[] {} };
            }
            defaultValue = new Object[] { null, new Object[] {}, new Object[] { type.newInstance() } };
        }
        else {
            try {
                type.getConstructor();
            }
            catch(NoSuchMethodException e) {
                return new Object[] { null };
            }
            defaultValue = new Object[] { type.newInstance() };
        }
        return defaultValue;
    }

    private Field getFieldFromClass(final Class<?> clazz, final String fieldName, final boolean inheritFields)
            throws SecurityException, NoSuchFieldException {
        try {
            Field res = clazz.getDeclaredField(fieldName);
            return res;
        }
        catch(NoSuchFieldException e) {
            if(inheritFields && clazz.getSuperclass() != null) {
                return getFieldFromClass(clazz.getSuperclass(), fieldName, inheritFields);
            }
            else {
                throw e;
            }
        }
    }

    // Private class for testing a method.
    private class TestClassRunnerForMethod extends BlockJUnit4ClassRunner {
        private final NamedBeanTest test;

        TestClassRunnerForMethod(Class<?> type, NamedBeanTest test) throws InitializationError {
            super(type);
            this.test = test;
        }

        @Override
        protected Description describeChild(FrameworkMethod method) {
            return Description.createTestDescription(method.getMethod().getDeclaringClass(), testName(method),
                    method.getAnnotations());
        }

        @Override
        public Object createTest() {
            return test;
        }

        @Override
        protected String getName() {
            return test.getTestClassName();
        }

        @Override
        protected String testName(final FrameworkMethod method) {
            return test.getTestMethodName(method);
        }

        @Override
        protected void validateConstructor(List<Throwable> errors) {
            validateOnlyOneConstructor(errors);
        }

        @Override
        protected Statement classBlock(RunNotifier notifier) {
            return childrenInvoker(notifier);
        }
    }
}
