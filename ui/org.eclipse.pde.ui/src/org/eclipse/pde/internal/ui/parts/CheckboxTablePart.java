/*
 * Copyright (c) 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */
package org.eclipse.pde.internal.ui.parts;

import org.eclipse.swt.widgets.*;
import org.eclipse.update.ui.forms.internal.FormWidgetFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;

/**
 * @version 	1.0
 * @author
 */
public class CheckboxTablePart extends StructuredViewerPart {
	public CheckboxTablePart(String [] buttonLabels) {
		super(buttonLabels);
	}

	/*
	 * @see StructuredViewerPart#createStructuredViewer(Composite, FormWidgetFactory)
	 */
	protected StructuredViewer createStructuredViewer(
		Composite parent,
		int style,
		FormWidgetFactory factory) {
		style |= SWT.H_SCROLL | SWT.V_SCROLL;
		if (factory==null) {
			style |= SWT.BORDER;
		}
		else {
			style |= FormWidgetFactory.BORDER_STYLE;
		}
		CheckboxTableViewer	tableViewer = CheckboxTableViewer.newCheckList(parent, style);
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				CheckboxTablePart.this.selectionChanged((IStructuredSelection)e.getSelection());
			}
		});
		tableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				elementChecked(event.getElement(), event.getChecked());
			}
		});
		return tableViewer;
	}
	
	public CheckboxTableViewer getTableViewer() {
		return (CheckboxTableViewer)getViewer();
	}
	
	/*
	 * @see SharedPartWithButtons#buttonSelected(int)
	 */
	protected void buttonSelected(Button button, int index) {
	}
	
	protected void elementChecked(Object element, boolean checked) {
	}
	protected void selectionChanged(IStructuredSelection selection) {
	}
}