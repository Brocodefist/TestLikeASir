package com.brocodefist.testlikeasir.junit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;

/**
 * @author e017645
 */
public class BeanExceptionTest implements NamedBeanTest {
	Class<? extends Throwable> exceptionClass;

    private static final String MESSAGE = "<Test message>";
    private static final Throwable THROWABLE = new Exception("<Test throwable>.");

	public BeanExceptionTest(Class<? extends Throwable> exceptionClass) {
		this.exceptionClass = exceptionClass;
	}

	@Test
	public void testDefaultConstructorWorks() throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Constructor<? extends Throwable> c = exceptionClass.getConstructor();
		assertTrue("The constructed instance of " + exceptionClass
				+ " via constructor " + c
				+ " is not a subclass of java.lang.Throwable.",
				c.newInstance() instanceof Throwable);
	}

	@Test
	public void testMessageConstructorWorks() throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Constructor<? extends Throwable> c = exceptionClass
				.getConstructor(String.class);
		Throwable t = c.newInstance(MESSAGE);
		assertEquals("The constructed instance of " + exceptionClass
				+ " via constructor " + c + " has not the expected message.",
				MESSAGE, t.getMessage());
	}

	@Test
	public void testThrowableConstructorWorks() throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Constructor<? extends Throwable> c = exceptionClass
				.getConstructor(Throwable.class);
		Throwable t = c.newInstance(THROWABLE);
		assertEquals("The constructed instance of " + exceptionClass
				+ " via constructor " + c + " has not the expected cause.",
				THROWABLE, t.getCause());
	}

	@Test
	public void testMessageAndThrowableConstructorWorks()
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		Constructor<? extends Throwable> c = exceptionClass.getConstructor(
				String.class, Throwable.class);
		Throwable t = c.newInstance(MESSAGE, THROWABLE);
		assertEquals("The constructed instance of " + exceptionClass
				+ " via constructor " + c + " has not the expected message.",
				MESSAGE, t.getMessage());
		assertEquals("The constructed instance of " + exceptionClass
				+ " via constructor " + c + " has not the expected cause.",
				THROWABLE, t.getCause());
	}

	@Override
	public String getTestClassName() {
		return String.format("Test Throwable constructors and getters for %s",
				exceptionClass.getName());
	}

	@Override
	public String getTestMethodName(FrameworkMethod method) {
		return method.getName() + "()";
	}
}
