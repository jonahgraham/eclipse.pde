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

import junit.framework.Test;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.pde.api.tools.internal.builder.BuilderMessages;

/**
 * Tests invalid javadoc tags on interface methods
 * 
 * @since 1.0
 */
public class InvalidInterfaceMethodTagTests extends InvalidMethodTagTests {

	/**
	 * Constructor
	 * @param name
	 */
	public InvalidInterfaceMethodTagTests(String name) {
		super(name);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTests#getTestSourcePath()
	 */
	protected IPath getTestSourcePath() {
		return super.getTestSourcePath().append("interface");
	}
	
	/**
	 * @return the test for this class
	 */
	public static Test suite() {
		return buildTestSuite(InvalidInterfaceMethodTagTests.class);
	}
	
	public void testInvalidInterfaceMethodTag1I() {
		x1(true);
	}
	
	public void testInvalidInterfaceMethodTag1F() {
		x1(false);
	}

	/**
	 * Tests the unsupported @noextend tag on a variety of inner / outer interface methods
	 */
	private void x1(boolean inc) {
		setExpectedProblemIds(getDefaultProblemSet(6));
		setExpectedMessageArgs(new String[][] {
				{"@noextend", BuilderMessages.TagValidator_an_interface_method},
				{"@noextend", BuilderMessages.TagValidator_an_interface_method},
				{"@noextend", BuilderMessages.TagValidator_an_interface_method},
				{"@noextend", BuilderMessages.TagValidator_an_interface_method},
				{"@noextend", BuilderMessages.TagValidator_an_interface_method},
				{"@noextend", BuilderMessages.TagValidator_an_interface_method}
		});
		deployTagTest(TESTING_PACKAGE, 
				"test1", 
				true, 
				inc ? IncrementalProjectBuilder.INCREMENTAL_BUILD : IncrementalProjectBuilder.FULL_BUILD, 
				true);
	}
	
	public void testInvalidInterfaceMethodTag2I() {
		x2(true);
	}
	
	public void testInvalidInterfaceMethodTag2F() {
		x2(false);
	}

	/**
	 * Tests the unsupported @noextend tag on interface methods in the default package
	 */
	private void x2(boolean inc) {
		setExpectedProblemIds(getDefaultProblemSet(2));
		setExpectedMessageArgs(new String[][] {
				{"@noextend", BuilderMessages.TagValidator_an_interface_method},
				{"@noextend", BuilderMessages.TagValidator_an_interface_method}
		});
		deployTagTest("", 
				"test2", 
				true, 
				inc ? IncrementalProjectBuilder.INCREMENTAL_BUILD : IncrementalProjectBuilder.FULL_BUILD, 
				true);
	}
	
	public void testInvalidInterfaceMethodTag3I() {
		x3(true);
	}
	
	public void testInvalidInterfaceMethodTag3F() {
		x3(false);
	}

	/**
	 * Tests the unsupported @noinstantiate tag on a variety of inner / outer interface methods
	 */
	private void x3(boolean inc) {
		setExpectedProblemIds(getDefaultProblemSet(6));
		setExpectedMessageArgs(new String[][] {
				{"@noinstantiate", BuilderMessages.TagValidator_an_interface_method},
				{"@noinstantiate", BuilderMessages.TagValidator_an_interface_method},
				{"@noinstantiate", BuilderMessages.TagValidator_an_interface_method},
				{"@noinstantiate", BuilderMessages.TagValidator_an_interface_method},
				{"@noinstantiate", BuilderMessages.TagValidator_an_interface_method},
				{"@noinstantiate", BuilderMessages.TagValidator_an_interface_method}
		});
		deployTagTest(TESTING_PACKAGE, 
				"test3", 
				true, 
				inc ? IncrementalProjectBuilder.INCREMENTAL_BUILD : IncrementalProjectBuilder.FULL_BUILD, 
				true);
	}

	public void testInvalidInterfaceMethodTag4I() {
		x4(true);
	}
	
	public void testInvalidInterfaceMethodTag4F() {
		x4(false);
	}	

	/**
	 * Tests the unsupported @noinstantiate tag on interface methods in the default package
	 */
	private void x4(boolean inc) {
		setExpectedProblemIds(getDefaultProblemSet(2));
		setExpectedMessageArgs(new String[][] {
				{"@noinstantiate", BuilderMessages.TagValidator_an_interface_method},
				{"@noinstantiate", BuilderMessages.TagValidator_an_interface_method}
		});
		deployTagTest("", 
				"test4", 
				true, 
				inc ? IncrementalProjectBuilder.INCREMENTAL_BUILD : IncrementalProjectBuilder.FULL_BUILD, 
				true);
	}
	
	public void testInvalidInterfaceMethodTag5I() {
		x5(true);
	}
	
	public void testInvalidInterfaceMethodTag5F() {
		x5(false);
	}

	/**
	 * Tests the unsupported @noimplement tag on a variety of inner / outer interface methods
	 */
	private void x5(boolean inc) {
		setExpectedProblemIds(getDefaultProblemSet(6));
		setExpectedMessageArgs(new String[][] {
				{"@noimplement", BuilderMessages.TagValidator_an_interface_method},
				{"@noimplement", BuilderMessages.TagValidator_an_interface_method},
				{"@noimplement", BuilderMessages.TagValidator_an_interface_method},
				{"@noimplement", BuilderMessages.TagValidator_an_interface_method},
				{"@noimplement", BuilderMessages.TagValidator_an_interface_method},
				{"@noimplement", BuilderMessages.TagValidator_an_interface_method}
		});
		deployTagTest(TESTING_PACKAGE, 
				"test5", 
				true, 
				inc ? IncrementalProjectBuilder.INCREMENTAL_BUILD : IncrementalProjectBuilder.FULL_BUILD, 
				true);
	}

	public void testInvalidInterfaceMethodTag6I() {
		x6(true);
	}
	
	public void testInvalidInterfaceMethodTag6F() {
		x6(false);
	}
	
	/**
	 * Tests the unsupported @noimplement tag on interface methods in the default package
	 */
	private void x6(boolean inc) {
		setExpectedProblemIds(getDefaultProblemSet(2));
		setExpectedMessageArgs(new String[][] {
				{"@noimplement", BuilderMessages.TagValidator_an_interface_method},
				{"@noimplement", BuilderMessages.TagValidator_an_interface_method}
		});
		deployTagTest("", 
				"test6", 
				true, 
				inc ? IncrementalProjectBuilder.INCREMENTAL_BUILD : IncrementalProjectBuilder.FULL_BUILD, 
				true);
	}
	
	public void testInvalidInterfaceMethodTag7I() {
		x7(true);
	}
	
	
	public void testInvalidInterfaceMethodTag7F() {
		x7(false);
	}

	/**
	 * Tests the unsupported @nooverride tag on a variety of inner / outer interface methods
	 */
	private void x7(boolean inc) {
		setExpectedProblemIds(getDefaultProblemSet(6));
		setExpectedMessageArgs(new String[][] {
				{"@nooverride", BuilderMessages.TagValidator_an_interface_method},
				{"@nooverride", BuilderMessages.TagValidator_an_interface_method},
				{"@nooverride", BuilderMessages.TagValidator_an_interface_method},
				{"@nooverride", BuilderMessages.TagValidator_an_interface_method},
				{"@nooverride", BuilderMessages.TagValidator_an_interface_method},
				{"@nooverride", BuilderMessages.TagValidator_an_interface_method}
		});
		deployTagTest(TESTING_PACKAGE, 
				"test7", 
				true, 
				inc ? IncrementalProjectBuilder.INCREMENTAL_BUILD : IncrementalProjectBuilder.FULL_BUILD, 
				true);
	}
	
	public void testInvalidInterfaceMethodTag8I() {
		x8(true);
	}
	
	public void testInvalidInterfaceMethodTag8F() {
		x8(false);
	}

	/**
	 * Tests the unsupported @nooverride tag on interface methods in the default package
	 */
	private void x8(boolean inc) {
		setExpectedProblemIds(getDefaultProblemSet(2));
		setExpectedMessageArgs(new String[][] {
				{"@nooverride", BuilderMessages.TagValidator_an_interface_method},
				{"@nooverride", BuilderMessages.TagValidator_an_interface_method}
		});
		deployTagTest("", 
				"test8", 
				true, 
				inc ? IncrementalProjectBuilder.INCREMENTAL_BUILD : IncrementalProjectBuilder.FULL_BUILD, 
				true);
	}
}