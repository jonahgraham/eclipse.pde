/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package a.classes.constructors;

/**
 * Tests add a private constructor.
 */
public class AddSinglePrivateConstructor {
	
	public int publicMethod(String arg) {
		return -1;
	}
	
	private AddSinglePrivateConstructor(int i) {
		
	}
	
}