package com.brocodefist.testlikeasir.junit;

import org.junit.runners.model.FrameworkMethod;

/**
 * Common interface for named bean subtests.
 * 
 * @author u514252
 */

public interface NamedBeanTest {
	/**
	 * Return the name of the test class.
	 * 
	 * @return the name of the test class.
	 */
	public String getTestClassName();
	
	/**
	 * Return the name of the test method.
	 * 
	 * @return the name of the test method.
	 */
	public String getTestMethodName(FrameworkMethod method);	
}
