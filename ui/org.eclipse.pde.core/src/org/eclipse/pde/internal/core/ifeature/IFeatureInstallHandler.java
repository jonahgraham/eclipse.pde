/*
 * Copyright (c) 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */
package org.eclipse.pde.internal.core.ifeature;
import java.net.URL;
import org.eclipse.core.runtime.CoreException;
/**
 * @version 	1.0
 * @author
 */
public interface IFeatureInstallHandler extends IFeatureObject {
	String P_URL = "url";
	String P_LIBRARY = "library";
	String P_HANDLER_NAME = "handlerName";

	public URL getURL();
	public String getLibrary();
	public String getHandlerName();
	
	public void setURL(URL url) throws CoreException;
	public void setLibrary(String library) throws CoreException;
	public void setHandlerName(String handlerName) throws CoreException;
}
