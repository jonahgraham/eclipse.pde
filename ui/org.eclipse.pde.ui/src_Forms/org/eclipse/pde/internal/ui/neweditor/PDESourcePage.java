/*
 * Created on Jan 26, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.pde.internal.ui.neweditor;

import org.eclipse.pde.core.*;
import org.eclipse.pde.internal.ui.*;
import org.eclipse.pde.internal.ui.neweditor.context.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.*;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.texteditor.*;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class PDESourcePage extends TextEditor implements IFormPage, IGotoMarker {
	private PDEFormEditor editor;
	private Control control;
	private int index;
	private String id;
	private String title;
	private InputContext inputContext;
	private IContentOutlinePage outlinePage;
	
	/**
	 * 
	 */
	public PDESourcePage(PDEFormEditor editor, String id, String title) {
		this.id = id;
		this.title = title;
		initialize(editor);
		setPreferenceStore(PDEPlugin.getDefault().getPreferenceStore());
		setRangeIndicator(new DefaultRangeIndicator());
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
	 */
	public void initialize(FormEditor editor) {
		this.editor = (PDEFormEditor)editor;
	}
	public void dispose() {
		if (outlinePage != null) {
			outlinePage.dispose();
			outlinePage = null;
		}
		super.dispose();
	}
	
	protected IContentOutlinePage createOutlinePage() {
		return null;
	}

	public IContentOutlinePage getContentOutline() {
		if (outlinePage==null)
			outlinePage = createOutlinePage();
		return outlinePage;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getEditor()
	 */
	public FormEditor getEditor() {
		return editor;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getManagedForm()
	 */
	public IManagedForm getManagedForm() {
		// not a form page
		return null;
	}
	protected void firePropertyChange(int type) {
		if (type == PROP_DIRTY) {
			editor.fireSaveNeeded(getEditorInput(), true);
		} else
			super.firePropertyChange(type);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#setActive(boolean)
	 */
	public void setActive(boolean active) {
		IContentOutlinePage outline = getContentOutline();
		if (outline != null && outline instanceof IModelChangedListener) {
			IModelChangedListener listener = (IModelChangedListener)outline;
			IModel model = getInputContext().getModel();
			if (model instanceof IModelChangeProvider) {
				if (active)
					((IModelChangeProvider)model).addModelChangedListener(listener);
				else
					((IModelChangeProvider)model).removeModelChangedListener(listener);
			}
		}
		if (active) {
			// page becomes visible; notify the context
			inputContext.setSourceEditingMode(true);
		}
		else {
			// page becomes invisible; notify the context
			inputContext.setSourceEditingMode(false);
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#isActive()
	 */
	public boolean isActive() {
		return this.equals(editor.getActivePageInstance());
	}
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		Control[] children = parent.getChildren();
		control = children[children.length - 1];
		
		WorkbenchHelp.setHelp(control, IHelpContextIds.MANIFEST_SOURCE_PAGE);
		
		//IDocument document =
		//	getDocumentProvider().getDocument(getEditorInput());
		//unregisterGlobalActions();
		// Important - must reset the provider to the multi-page
		// editor.
		// See 32622
		//getSite().setSelectionProvider(getEditor());
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getPartControl()
	 */
	public Control getPartControl() {
		return control;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getId()
	 */
	public String getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getIndex()
	 */
	public int getIndex() {
		return index;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#setIndex(int)
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#isSource()
	 */
	public boolean isEditor() {
		return true;
	}
	/**
	 * @return Returns the inputContext.
	 */
	public InputContext getInputContext() {
		return inputContext;
	}
	/**
	 * @param inputContext The inputContext to set.
	 */
	public void setInputContext(InputContext inputContext) {
		this.inputContext = inputContext;
		setDocumentProvider(inputContext.getDocumentProvider());
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#focusOn(java.lang.Object)
	 */
	public boolean selectReveal(Object object) {
		return false;
	}
}