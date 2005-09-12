/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.internal.ui.util.SWTUtil;
import org.eclipse.pde.ui.launcher.IPDELauncherConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class JREBlock {

	private AbstractLauncherTab fTab;
	private Listener fListener = new Listener();
	private Button fJavawButton;
	private Button fJavaButton;
	private Combo fJreCombo;
	private Text fBootstrap;

	class Listener extends SelectionAdapter implements ModifyListener {		
		public void widgetSelected(SelectionEvent e) {
			fTab.updateLaunchConfigurationDialog();
		}
		public void modifyText(ModifyEvent e) {
			fTab.updateLaunchConfigurationDialog();
		}
	}

	public JREBlock(AbstractLauncherTab tab) {
		fTab = tab;
	}
	
	public void createControl(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(PDEUIMessages.MainTab_jreSection); 
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createJavaExecutableSection(group);
		createJRESection(group);
		createBootstrapEntriesSection(group);
	}
	
	protected void createJRESection(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(PDEUIMessages.BasicLauncherTab_jre); 

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		fJreCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		fJreCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fJreCombo.addSelectionListener(fListener);
		
		Button button = new Button(composite, SWT.PUSH);
		button.setText(PDEUIMessages.BasicLauncherTab_installedJREs); 
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String currentVM = fJreCombo.getText();
				IPreferenceNode node = new InstalledJREsPreferenceNode();
				if (showPreferencePage(node)) {
					fJreCombo.setItems(LauncherUtils.getVMInstallNames());
					fJreCombo.setText(currentVM);
					if (fJreCombo.getSelectionIndex() == -1)
						fJreCombo.setText(LauncherUtils.getDefaultVMInstallName());
				}
			}
			private boolean showPreferencePage(final IPreferenceNode targetNode) {
				PreferenceManager manager = new PreferenceManager();
				manager.addToRoot(targetNode);
				final PreferenceDialog dialog =
					new PreferenceDialog(fTab.getControl().getShell(), manager);
				final boolean[] result = new boolean[] { false };
				BusyIndicator.showWhile(fTab.getControl().getDisplay(), new Runnable() {
					public void run() {
						dialog.create();
						dialog.setMessage(targetNode.getLabelText());
						if (dialog.open() == PreferenceDialog.OK)
							result[0] = true;
					}
				});
				return result[0];
			}
		});
		button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		SWTUtil.setButtonDimensionHint(button);				
	}
	
	protected void createJavaExecutableSection(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(PDEUIMessages.BasicLauncherTab_javaExec); 

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = layout.marginWidth = 0;
		layout.horizontalSpacing = 20;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		fJavawButton = new Button(composite, SWT.RADIO);
		fJavawButton.setText(PDEUIMessages.BasicLauncherTab_javaExecDefault); // 
		fJavawButton.addSelectionListener(fListener);
		
		fJavaButton = new Button(composite, SWT.RADIO);
		fJavaButton.setText("&java");	 //$NON-NLS-1$
		fJavaButton.addSelectionListener(fListener);
	}
			
	private void createBootstrapEntriesSection(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(PDEUIMessages.BasicLauncherTab_bootstrap); 
		
		fBootstrap = new Text(parent, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 300;
		fBootstrap.setLayoutData(gd);
		fBootstrap.addModifyListener(fListener);
	}
	
	public void initializeFrom(ILaunchConfiguration config) throws CoreException {
		initializeJRESection(config);
		initializeBootstrapEntriesSection(config);
	}
	
	private void initializeJRESection(ILaunchConfiguration config) throws CoreException {
		String javaCommand = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_JAVA_COMMAND, "javaw"); //$NON-NLS-1$
		fJavawButton.setSelection(javaCommand.equals("javaw")); //$NON-NLS-1$
		fJavaButton.setSelection(!fJavawButton.getSelection());
		
		fJreCombo.setItems(LauncherUtils.getVMInstallNames());
		String vmInstallName =
			config.getAttribute(IPDELauncherConstants.VMINSTALL, LauncherUtils.getDefaultVMInstallName());
		fJreCombo.setText(vmInstallName);
		if (fJreCombo.getSelectionIndex() == -1)
			fJreCombo.setText(LauncherUtils.getDefaultVMInstallName());
	}
	
	private void initializeBootstrapEntriesSection(ILaunchConfiguration config) throws CoreException {
		fBootstrap.setText(config.getAttribute(IPDELauncherConstants.BOOTSTRAP_ENTRIES, "")); //$NON-NLS-1$
	}
	
	public void performApply(ILaunchConfigurationWorkingCopy config) {
		saveJRESection(config);
		saveBootstrapEntriesSection(config);
	}
	
	protected void saveJRESection(ILaunchConfigurationWorkingCopy config) {	
		try {
			String javaCommand = fJavawButton.getSelection() ? null : "java"; //$NON-NLS-1$
			config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_JAVA_COMMAND, javaCommand);
			
			if (fJreCombo.getSelectionIndex() == -1)
				return;

			String jre = fJreCombo.getText();
			if (config.getAttribute(IPDELauncherConstants.VMINSTALL, (String) null) != null) {
				config.setAttribute(IPDELauncherConstants.VMINSTALL, jre);
			} else {
				config.setAttribute(
						IPDELauncherConstants.VMINSTALL,
					jre.equals(LauncherUtils.getDefaultVMInstallName()) ? null : jre);
			}
		} catch (CoreException e) {
		}
	}

	protected void saveBootstrapEntriesSection(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(IPDELauncherConstants.BOOTSTRAP_ENTRIES, fBootstrap.getText().trim());
	}
	
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {		
		config.setAttribute(IPDELauncherConstants.BOOTSTRAP_ENTRIES, ""); //$NON-NLS-1$
	}
	
	protected IStatus validateJRESelection() {
		if (fJreCombo.getSelectionIndex() == -1) {
			return AbstractLauncherTab.createStatus(
				IStatus.ERROR,
				PDEUIMessages.BasicLauncherTab_noJRE); 
		}
		return AbstractLauncherTab.createStatus(IStatus.OK, ""); //$NON-NLS-1$
	}



}
