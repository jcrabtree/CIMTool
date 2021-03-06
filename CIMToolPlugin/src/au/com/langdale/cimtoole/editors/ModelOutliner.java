package au.com.langdale.cimtoole.editors;

import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import au.com.langdale.jena.TreeModelBase.Node;
import au.com.langdale.kena.OntResource;

public class ModelOutliner extends ContentOutlinePage  {
	public ModelOutliner(ModelEditor master) {
		this.master = master;
	}

	ModelEditor master;
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		TreeViewer viewer = getTreeViewer();
		viewer.setUseHashlookup(true);
		viewer.setContentProvider(master.getProvider());
		viewer.setInput(master.getTree());
		master.listenToDoubleClicks(viewer);
		master.configureOutline(this);
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		fireSelectionChanged(event.getSelection());
		master.doRefresh();
	}

	public void drillTo(OntResource subject) {
		TreeViewer viewer = getTreeViewer();
		if( viewer == null )
			return;
		
		Node root = master.getTree().getRoot();
		Node top = root.findChild(subject);
		if( top != null ) {
			TreePath path = new TreePath(new Object[]{root, top});
			viewer.setSelection(new TreeSelection(path), true);
		}
		else {
			ITreeSelection selection = (ITreeSelection) viewer.getSelection();
			TreePath[] paths = selection.getPaths();
			if( paths.length == 1) {
				TreePath current = paths[0];
				Node parent = (Node) current.getLastSegment();
				Node child = parent.findChild(subject);
				if( child != null ) {
					TreePath path = current.createChildPath(child);
					viewer.setSelection(new TreeSelection(path), true);
				}
			}
		}
	}

	public void drillTo(Node[] path) {
		getTreeViewer().setSelection(new TreeSelection(new TreePath(path)), true);
	}
}
