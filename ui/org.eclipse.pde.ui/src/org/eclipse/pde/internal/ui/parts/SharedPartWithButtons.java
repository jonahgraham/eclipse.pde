/*
 * Copyright (c) 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */
package org.eclipse.pde.internal.ui.parts;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.update.ui.forms.internal.FormWidgetFactory;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

/**
 * @version 	1.0
 * @author
 */
public abstract class SharedPartWithButtons extends SharedPart {
	private String [] buttonLabels;
	private Control [] controls;
	private Composite buttonContainer;
	
	private class SelectionHandler implements SelectionListener {
		public void widgetSelected(SelectionEvent e) {
			buttonSelected(e);
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			buttonSelected(e);
		}
		private void buttonSelected(SelectionEvent e) {
			Integer index = (Integer)e.widget.getData();
			SharedPartWithButtons.this.buttonSelected((Button)e.widget, index.intValue());
		}
	}
	
	public SharedPartWithButtons(String [] buttonLabels) {
		this.buttonLabels = buttonLabels;
	}
	
	public void setButtonEnabled(int index, boolean enabled) {
		if (controls!=null && index >=0 && controls.length>index) {
			Control c = controls[index];
			if (c instanceof Button)
				c.setEnabled(enabled);
		}
	}
	
	protected abstract void createMainControl(Composite parent, int style, int span, FormWidgetFactory factory);
	protected abstract void buttonSelected(Button button, int index);

	/*
	 * @see SharedPart#createControl(Composite, FormWidgetFactory)
	 */
	public void createControl(Composite parent, int style, int span, FormWidgetFactory factory) {
		createMainLabel(parent, span, factory);
		createMainControl(parent, style, span-1, factory);
		if (buttonLabels!=null && buttonLabels.length>0) {
			buttonContainer = createComposite(parent, factory);
			GridData gd = new GridData(GridData.FILL_VERTICAL);
			buttonContainer.setLayoutData(gd);
			buttonContainer.setLayout(createButtonsLayout());
			
			controls = new Control[buttonLabels.length];
			SelectionHandler listener = new SelectionHandler();
			for (int i=0; i<buttonLabels.length; i++) {
				String label = buttonLabels[i];
				if (label!=null) {
					Button button = createButton(buttonContainer, label, i, factory);
					button.addSelectionListener(listener);
					controls[i] = button;
				}
				else {
					createEmptySpace(buttonContainer, 1, factory);
				}
			}
		}
	}
	
	protected GridLayout createButtonsLayout() {
		GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 0;
		return layout;
	}
	
	protected Button createButton(Composite parent, String label, int index, FormWidgetFactory factory) {
		Button button;
		
		if (factory!=null)
			button = factory.createButton(parent, label, SWT.PUSH);
		else {
			button = new Button(parent, SWT.PUSH);
			button.setText(label);
		}
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		button.setLayoutData(gd);
		button.setData(new Integer(index));
		return button;
	}

	protected void updateEnabledState() {
		for (int i=0; i<controls.length; i++) {
			Control c = controls[i];
			if (c instanceof Button)
				c.setEnabled(isEnabled());		
		}
	}
	
	protected void createMainLabel(Composite parent, int span, FormWidgetFactory factory) {
	}
}