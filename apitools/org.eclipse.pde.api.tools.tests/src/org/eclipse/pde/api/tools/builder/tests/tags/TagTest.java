/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.builder.tests.tags;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.tests.junit.extension.TestCase;
import org.eclipse.pde.api.tools.builder.tests.ApiBuilderTest;
import org.eclipse.pde.api.tools.builder.tests.ApiTestingEnvironment;


/**
 * Tests the builder to make sure it correctly finds and reports
 * unsupported tag usage.
 * 
 * @since 1.0
 */
public abstract class TagTest extends ApiBuilderTest {

	protected final String TESTING_PACKAGE = "a.b.c";
	
	/**
	 * Constructor
	 */
	public TagTest(String name) {
		super(name);
	}
	
	/**
	 * Sets the message arguments we are expecting for the given test, the number of times denoted
	 * by count
	 * @param tagname
	 * @param context
	 * @param count
	 */
	protected void setExpectedMessageArgs(String tagname, String context, int count) {
		String[][] args = new String[count][];
		for(int i = 0; i < count; i++) {
			args[i] = new String[] {tagname, context};
		}
		setExpectedMessageArgs(args);
	}
	
	/**
	 * @return all of the child test classes of this class
	 */
	private static Class[] getAllTestClasses() {
		Class[] classes = new Class[] {
			InvalidClassTagTests.class,
			ValidClassTagTests.class,
			InvalidInterfaceTagTests.class,
			ValidInterfaceTagTests.class,
			InvalidFieldTagTests.class,
			ValidFieldTagTests.class,
			InvalidMethodTagTests.class,
			ValidMethodTagTests.class,
			InvalidEnumTagTests.class,
			InvalidAnnotationTagTests.class,
			InvalidDuplicateTagsTests.class
		};
		return classes;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		// for the first compatibility test create workspace projects
		ApiTestingEnvironment env = getEnv();
		if (env != null) {
			env.setRevert(true);
		}
		super.setUp();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTest#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		getEnv().setRevert(false);
		super.tearDown();
	}
	/**
	 * Collects tests from the getAllTestClasses() method into the given suite
	 * @param suite
	 */
	private static void collectTests(TestSuite suite) {
		// Hack to load all classes before computing their suite of test cases
		// this allow to reset test cases subsets while running all Builder tests...
		Class[] classes = getAllTestClasses();

		// Reset forgotten subsets of tests
		TestCase.TESTS_PREFIX = null;
		TestCase.TESTS_NAMES = null;
		TestCase.TESTS_NUMBERS = null;
		TestCase.TESTS_RANGE = null;
		TestCase.RUN_ONLY_ID = null;

		/* tests */
		for (int i = 0, length = classes.length; i < length; i++) {
			Class clazz = classes[i];
			Method suiteMethod;
			try {
				suiteMethod = clazz.getDeclaredMethod("suite", new Class[0]);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				continue;
			}
			Object test;
			try {
				test = suiteMethod.invoke(null, new Object[0]);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				continue;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				continue;
			}
			suite.addTest((Test) test);
		}
	}
	
	/**
	 * @return the tests for this class
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite(TagTest.class.getName());
		collectTests(suite);
		return suite;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTests#setBuilderOptions()
	 */
	protected void setBuilderOptions() {
		//only care about unsupported tags
		enableUnsupportedTagOptions(true);
		
		//disable the rest
		enableBaselineOptions(false);
		enableCompatibilityOptions(false);
		enableLeakOptions(false);
		enableSinceTagOptions(false);
		enableUsageOptions(false);
		enableVersionNumberOptions(false);
	}
	
	/**
	 * Returns an array composed only of the specified number of {@link #PROBLEM_ID}
	 * @param problemcount
	 * @return an array of {@link #PROBLEM_ID} of the specified size, or an empty array if the specified
	 * size is < 1
	 */
	protected int[] getDefaultProblemSet(int problemcount) {
		if(problemcount < 1) {
			return new int[0];
		}
		int[] array = new int[problemcount];
		int defaultproblem = getDefaultProblemId();
		for(int i = 0; i < problemcount; i++) {
			array[i] = defaultproblem;
		}
		return array;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTests#getTestSourcePath()
	 */
	protected IPath getTestSourcePath() {
		return new Path("tags");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTests#getTestingProjectName()
	 */
	protected String getTestingProjectName() {
		return "tagtest";
	}
	
	/**
	 * Deploys a full build test for API Javadoc tags using the given source file in the specified package,
	 * looking for problems specified from {@link #getExpectedProblemIds()()}
	 * @param packagename
	 * @param sourcename
	 * @param expectingproblems
	 * @param buildtype the type of build to perform. One of:
	 * <ol>
	 * <li>IncrementalProjectBuilder#FULL_BUILD</li>
	 * <li>IncrementalProjectBuilder#INCREMENTAL_BUILD</li>
	 * <li>IncrementalProjectBuilder#CLEAN_BUILD</li>
	 * </ol>
	 * @param buildworkspace true if the workspace should be built, false if the created project should be built
	 */
	protected void deployTagTest(String packagename, String sourcename, boolean expectingproblems, int buildtype, boolean buildworkspace) {
		try {
			IPath path = assertProject(sourcename, packagename);
			doBuild(buildtype, (buildworkspace ? null : path));
			expectingNoJDTProblems();
			IJavaProject jproject = getEnv().getJavaProject(path);
			IType type = jproject.findType(packagename, sourcename);
			assertNotNull("The type "+sourcename+" from package "+packagename+" must exist", type);
			IPath sourcepath = type.getPath();
			if(expectingproblems) {
				expectingOnlySpecificProblemsFor(sourcepath, getExpectedProblemIds());
				assertProblems(getEnv().getProblems());
			}
			else {
				expectingNoProblemsFor(sourcepath);
			}
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
	}
}