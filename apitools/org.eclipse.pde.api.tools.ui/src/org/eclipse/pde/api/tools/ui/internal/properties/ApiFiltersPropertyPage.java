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
package org.eclipse.pde.api.tools.ui.internal.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.pde.api.tools.internal.provisional.ApiPlugin;
import org.eclipse.pde.api.tools.internal.provisional.IApiFilterStore;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiComponent;
import org.eclipse.pde.api.tools.internal.provisional.problems.IApiProblem;
import org.eclipse.pde.api.tools.internal.provisional.problems.IApiProblemFilter;
import org.eclipse.pde.api.tools.internal.util.Util;
import org.eclipse.pde.api.tools.ui.internal.ApiToolsLabelProvider;
import org.eclipse.pde.api.tools.ui.internal.ApiUIPlugin;
import org.eclipse.pde.api.tools.ui.internal.IApiToolsHelpContextIds;
import org.eclipse.pde.api.tools.ui.internal.SWTFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.WorkbenchViewerComparator;

import com.ibm.icu.text.MessageFormat;

/**
 * Property page to allow UI edits to the current set of filters for a given project
 * 
 * @since 1.0.0
 */
public class ApiFiltersPropertyPage extends PropertyPage {
	
	/**
	 * Comparator for the viewer to group filters by {@link IElementDescriptor} type
	 */
	static class ApiFilterComparator extends WorkbenchViewerComparator {
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ViewerComparator#category(java.lang.Object)
		 */
		public int category(Object element) {
			if(element instanceof IApiProblemFilter) {
				return (int)((IApiProblemFilter) element).getUnderlyingProblem().getCategory();
			}
			return -1;
		}
	}
	
	/**
	 * Content provider for the tree
	 */
	class TreeContentProvider implements ITreeContentProvider {

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement) {
			if(parentElement instanceof IResource) {
				try {
					return getFilterStore().getFilters((IResource) parentElement);
				} catch (CoreException e) {
					ApiUIPlugin.log(e);
				}
			}
			return new Object[0];
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element) {
			if(element instanceof IApiProblemFilter) {
				return false;
			}
			if(element instanceof IResource) {
				try {
					return getFilterStore().getFilters((IResource) element).length > 0;
				} catch (CoreException e) {
					ApiUIPlugin.log(e);
				}
			}
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof ArrayList) {
				return ((ArrayList)inputElement).toArray();
			}
			return new Object[0];
		}
		public Object getParent(Object element) {return null;}
		public void dispose() {}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
	}
	
	TreeViewer fViewer = null;
	Button fRemoveButton;
	private IProject fProject = null;
	ArrayList fChangeset = new ArrayList();
	private ArrayList fInputset = null;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite comp = SWTFactory.createComposite(parent, 2, 1, GridData.FILL_BOTH);
		SWTFactory.createWrapLabel(comp, PropertiesMessages.ApiFiltersPropertyPage_55, 2);
		Tree tree = new Tree(comp, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 275;
		gd.heightHint = 300;
		tree.setLayoutData(gd);
		tree.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.character == SWT.DEL && e.stateMask == 0) {
					handleRemove((IStructuredSelection) fViewer.getSelection());
				}
			}
		});
		fViewer = new TreeViewer(tree);
		fViewer.setAutoExpandLevel(2);
		fViewer.setContentProvider(new TreeContentProvider());
		fViewer.setLabelProvider(new ApiToolsLabelProvider());
		fViewer.setComparator(new ApiFilterComparator());
		fViewer.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return !fChangeset.contains(element);
			}
		});
		try {
			IApiFilterStore store = getFilterStore();
			if(store != null) {
				fInputset = new ArrayList(Arrays.asList(store.getResources()));
				fViewer.setInput(fInputset);
			}
		}
		catch(CoreException e) {
			ApiUIPlugin.log(e);
		}
		fViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) event.getSelection();
				fRemoveButton.setEnabled(ss.size() > 0);
			}
		});
		fViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				Object o = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if(fViewer.isExpandable(o)) {
					fViewer.setExpandedState(o, !fViewer.getExpandedState(o));
				}
			}
		});
		Composite bcomp = SWTFactory.createComposite(comp, 1, 1, GridData.FILL_VERTICAL, 0, 0);
		fRemoveButton = SWTFactory.createPushButton(bcomp, PropertiesMessages.ApiFiltersPropertyPage_57, null, SWT.LEFT);
		fRemoveButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) fViewer.getSelection();
				handleRemove(ss);
			}
		});
		fRemoveButton.setEnabled(false);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IApiToolsHelpContextIds.APITOOLS_FILTERS_PROPERTY_PAGE);
		return comp;
	}	

	/**
	 * Performs the remove
	 * @param selection
	 */
	void handleRemove(IStructuredSelection selection) {
		HashSet deletions = collectDeletions(selection);
		if(deletions.size() > 0) {
			fChangeset.addAll(deletions);
			int[] indexes = getIndexes(selection);
			fViewer.remove(deletions.toArray());
			updateParents();
			fViewer.refresh();
			updateSelection(indexes);
		}
	}
	
	/**
	 * Collects the indexes of the first item in the current selection
	 * @param selection
	 * @return an array of indexes (parent, child) of the first item in the current selection
	 */
	private int[] getIndexes(IStructuredSelection selection) {
		int[] indexes = new int[] {0,0};
		TreeSelection tsel = (TreeSelection) selection;
		TreePath path = tsel.getPaths()[0];
		TreeItem parent = (TreeItem) fViewer.testFindItem(path.getFirstSegment());
		if(parent != null) {
			Tree tree = fViewer.getTree();
			//found parent
			indexes[0] = tree.indexOf(parent);
			TreeItem item = (TreeItem) fViewer.testFindItem(path.getLastSegment());
			if(item != null) {
				indexes[1] = parent.indexOf(item);
			}
		}
		return indexes;
	}
	
	/**
	 * Updates the selection in the viewer based on the given indexes.
	 * If there is no item to update at the given indexes then the next logical child is taken, else the parent
	 * is selected, else no selection is made
	 * @param indexes
	 */
	private void updateSelection(int[] indexes) {
		Tree tree = fViewer.getTree();
		TreeItem parent = null;
		if(tree.getItemCount() == 0) {
			return;
		}
		if(indexes[0] < tree.getItemCount()) {
			TreeItem child = null;
			parent = tree.getItem(indexes[0]);
			int childcount = parent.getItemCount();
			if(childcount < 1 || indexes[1] < 0) {
				fViewer.setSelection(new StructuredSelection(parent.getData()));
				return;
			}
			else if (indexes[1] < childcount){
				child = parent.getItem(indexes[1]);
			}
			else {
				child = parent.getItem(childcount-1);
			}
			fViewer.setSelection(new StructuredSelection(child.getData()));
		}
		else {
			parent = tree.getItem(tree.getItemCount()-1);
			fViewer.setSelection(new StructuredSelection(parent.getData()));
		}
	}
	
	/**
	 * Cleans up empty parents once a deletion update has been done
	 * for the parents that have incrementally had all their children removed
	 */
	private void updateParents() {
		Tree tree = fViewer.getTree();
		TreeItem[] items = tree.getItems();
		for(int i = 0; i < items.length; i++) {
			if(items[i].getItems().length < 1) {
				fInputset.remove(items[i].getData());
			}
		}
	}
	
	/**
	 * Collects all of the elements to be deleted
	 * @param selection
	 * @return the set of elements to be added to the change set for deletion
	 */
	private HashSet collectDeletions(IStructuredSelection selection) {
		HashSet filters = new HashSet();
		Object node = null;
		Object[] children = null;
		for(Iterator iter = selection.iterator(); iter.hasNext();) {
			node = iter.next();
			if(node instanceof IResource) {
				children = ((TreeContentProvider)fViewer.getContentProvider()).getChildren(node);
				filters.addAll(Arrays.asList(children));
				fInputset.remove(node);
			}
			else {
				filters.add(node);
			}
		}
		return filters;
	}
	
	/**
	 * @return the backing project for this page, or <code>null</code> if this page was 
	 * somehow opened without a project
	 */
	private IProject getProject() {
		if(fProject == null) {
			fProject = (IProject) getElement().getAdapter(IProject.class);
		}
		return fProject;
	}
	
	/**
	 * @return the {@link IApiFilterStore} from the backing project
	 * @throws CoreException
	 */
	IApiFilterStore getFilterStore() throws CoreException {
		IProject project  = getProject();
		IApiFilterStore store = null;
		if(project != null) {
			IApiComponent component = ApiPlugin.getDefault().getApiBaselineManager().getWorkspaceBaseline().getApiComponent(project);
			if(component != null) {
				return component.getFilterStore();
			}
		}
		return store;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		super.performDefaults();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		try {
			if(fChangeset.size() > 0) {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IApiProblemFilter[] apiProblemFilters = (IApiProblemFilter[]) fChangeset.toArray(new IApiProblemFilter[fChangeset.size()]);
				getFilterStore().removeFilters(apiProblemFilters);
				// we want to make sure that we rebuild only applicable types
				for (int i = 0, max = apiProblemFilters.length; i < max; i++) {
					IApiProblemFilter filter = apiProblemFilters[i];
					IApiProblem apiProblem = filter.getUnderlyingProblem();
					if (apiProblem != null) {
						String resourcePath = apiProblem.getResourcePath();
						if (resourcePath != null) {
							IResource resource = fProject.findMember(resourcePath);
							if (resource != null) {
								Util.touchCorrespondingResource(fProject, resource, apiProblem.getTypeName());
							}
						}
					}
				}
				if (!workspace.isAutoBuilding()) {
					if(MessageDialog.openQuestion(getShell(), PropertiesMessages.ApiFiltersPropertyPage_58, 
							MessageFormat.format(PropertiesMessages.ApiFiltersPropertyPage_59, new String[] {fProject.getName()}))) {
						Util.getBuildJob(new IProject[] {fProject}, IncrementalProjectBuilder.INCREMENTAL_BUILD).schedule();
					}
				}
			}
			fChangeset.clear();
		}
		catch(CoreException e) {
			ApiUIPlugin.log(e);
		} catch(OperationCanceledException e) {
			// ignore
		}
		return super.performOk();
	}
}