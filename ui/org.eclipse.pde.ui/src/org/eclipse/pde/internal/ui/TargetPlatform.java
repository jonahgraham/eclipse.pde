/*
 * Copyright (c) 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */
package org.eclipse.pde.internal.ui;

import java.io.*;
import java.net.*;
import java.util.*;

import org.eclipse.core.boot.BootLoader;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.core.*;
import org.eclipse.pde.internal.ui.util.Choice;

/**
 * @version 	1.0
 * @author
 */
public class TargetPlatform implements IEnvironmentVariables {

	public static File createPropertiesFile() throws CoreException {
		return createPropertiesFile(getVisibleModels(), null);
	}

	public static URL[] createPluginPath() throws CoreException {
		return createPluginPath(getVisibleModels());
	}

	public static URL[] createPluginPath(IPluginModelBase[] models)
		throws CoreException {
		URL urls[] = new URL[models.length];
		for (int i = 0; i < urls.length; i++) {
			IPluginModelBase model = models[i];
			String urlName = createURL(model);
			try {
				urls[i] = new URL(urlName);
			} catch (MalformedURLException e) {
				PDEPlugin.logException(e);
				return new URL[0];
			}
		}
		return urls;
	}

	private static IPluginModelBase[] getVisibleModels() {
		Vector result = new Vector();
		/*
		WorkspaceModelManager wmanager =
			PDECore.getDefault().getWorkspaceModelManager();
		IPluginModelBase[] wplugins = wmanager.getWorkspacePluginModels();
		IPluginModelBase[] wfragments = wmanager.getWorkspaceFragmentModels();
		IPluginModelBase[] eplugins =
			PDECore.getDefault().getExternalModelManager().getModels();
		IPluginModelBase[] efragments =
			PDECore.getDefault().getExternalModelManager().getFragmentModels(
				null);

		addFromList(result, wplugins);
		addFromList(result, wfragments);
		addFromList(result, eplugins);
		addFromList(result, efragments);
		*/
		PluginModelManager manager = PDECore.getDefault().getModelManager();
		addFromList(result, manager.getPlugins());
		IPluginModelBase[] array =
			(IPluginModelBase[]) result.toArray(
				new IPluginModelBase[result.size()]);
		return array;
	}

	private static void addFromList(Vector result, IPluginModelBase[] list) {
		for (int i = 0; i < list.length; i++) {
			IPluginModelBase model = list[i];
			if (model.isEnabled())
				result.add(list[i]);
		}
	}

	public static File createPropertiesFile(
		IPluginModelBase[] plugins,
		IPath data)
		throws CoreException {
		try {
			String dataSuffix = createDataSuffix(data);
			IPath statePath = PDECore.getDefault().getStateLocation();
			IPath pluginPath;
			String fileName = "plugin_path.properties";
			File dir = new File(statePath.toOSString());

			if (dataSuffix.length() > 0) {
				dir = new File(dir, dataSuffix);
				if (!dir.exists()) {
					dir.mkdir();
				}
			}
			File pluginFile = new File(dir, fileName);
			Properties properties = new Properties();

			for (int i = 0; i < plugins.length; i++) {
				IPluginModelBase curr = plugins[i];
				String key = getKey(curr);
				properties.setProperty(key, createURL(curr));
			}

			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(pluginFile);
				properties.store(fos, null);
			} finally {
				if (fos != null) {
					fos.close();
				}
			}
			return pluginFile;
		} catch (IOException e) {
			throw new CoreException(
				new Status(
					IStatus.ERROR,
					PDECore.getPluginId(),
					IStatus.ERROR,
					e.getMessage(),
					e));
		}
	}
	
	private static String getKey(IPluginModelBase model) {
		if (model.isLoaded()) {
			return model.getPluginBase().getId();
		}
		IResource resource = model.getUnderlyingResource();
		if (resource!=null) return resource.getProject().getName();
		return model.getInstallLocation();
	}

	private static String createDataSuffix(IPath data) {
		if (data == null)
			return "";
		String suffix = data.toOSString();
		// replace file and device separators with underscores
		suffix = suffix.replace(File.separatorChar, '_');
		return suffix.replace(':', '_');
	}

	private static String createURL(IPluginModelBase model) {
		String prefix = "file:" + model.getInstallLocation() + File.separator;

		if (model instanceof IPluginModel) {
			return prefix + "plugin.xml";
		} else if (model instanceof IFragmentModel) {
			return prefix + "fragment.xml";
		} else
			return "";
	}

	public static String getOS() {
		initializeDefaults();
		return getProperty(OS);
	}

	public static String getWS() {
		initializeDefaults();
		return getProperty(WS);
	}

	public static String getNL() {
		initializeDefaults();
		return getProperty(NL);
	}

	public static String getOSArch() {
		initializeDefaults();
		return getProperty(ARCH);
	}

	private static String getProperty(String key) {
		return PDEPlugin.getDefault().getPreferenceStore().getString(key);
	}

	public static void initializeDefaults() {
		IPreferenceStore store = PDEPlugin.getDefault().getPreferenceStore();
		store.setDefault(OS, BootLoader.getOS());
		store.setDefault(WS, BootLoader.getWS());
		store.setDefault(NL, BootLoader.getNL());
		store.setDefault(ARCH, BootLoader.getOSArch());
	}

	private static Choice[] getKnownChoices(String[] values) {
		Choice[] choices = new Choice[values.length];
		for (int i = 0; i < choices.length; i++) {
			choices[i] = new Choice(values[i], values[i]);
		}
		return choices;
	}

	public static Choice[] getOSChoices() {
		return getKnownChoices(BootLoader.knownOSValues());
	}

	public static Choice[] getWSChoices() {
		return getKnownChoices(BootLoader.knownWSValues());
	}

	public static Choice[] getNLChoices() {
		Locale[] locales = Locale.getAvailableLocales();
		Choice[] choices = new Choice[locales.length];
		for (int i = 0; i < locales.length; i++) {
			Locale locale = locales[i];
			choices[i] = new Choice(locale.toString(), locale.getDisplayName());
		}
		CoreArraySorter.INSTANCE.sortInPlace(choices);
		return choices;
	}
	public static Choice[] getArchChoices() {
		return getKnownChoices(BootLoader.knownOSArchValues());
	}
}