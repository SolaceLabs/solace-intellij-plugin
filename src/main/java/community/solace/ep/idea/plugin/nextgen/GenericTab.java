package community.solace.ep.idea.plugin.nextgen;

import javax.swing.JComponent;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.SimpleToolWindowPanel;

import community.solace.ep.idea.plugin.LoadRefreshButton;
import community.solace.ep.idea.plugin.SolaceEventPortalToolWindowFactory;

/**
 * This window represents one of the tabs ("content") on the main PS+Portal tool.  It has a toolbar and a content area.
 */
abstract class GenericTab implements LoadRefreshButton.Observer, PortalToolbarListener {
	
	private final SolaceEventPortalToolWindowFactory factory;
    protected final SimpleToolWindowPanel contentToolWindow;  // the panel for the whole tab (this)
    protected final PortalTabToolbar toolbar;
    protected final PortalTableModel tableModel;
    protected final PortalTableView tableView;
    protected final PortalTableViewPanel tablePanel;
    
	private static final Logger LOG = Logger.getInstance(GenericTab.class);

	protected GenericTab(SolaceEventPortalToolWindowFactory factory) {
		this.factory = factory;
    	SimpleToolWindowPanel sp = new SimpleToolWindowPanel(false, true);
        toolbar = new PortalTabToolbar(this);
		tableModel = new PortalTableModel(PortalTableModel.generateColumnInfo());
        tableView = new PortalTableView(this, tableModel);
        tablePanel = new PortalTableViewPanel(tableView);
        sp.setToolbar(toolbar);
        sp.setContent(tablePanel);
        this.contentToolWindow = sp;
    }
    
    public JComponent getContent() {
        return contentToolWindow;
    }
    
    public SolaceEventPortalToolWindowFactory getFactory() {
    	return factory;
    }
    
    

    protected abstract void refreshSortByDomain();
    protected abstract void refreshSortByAlpha();

	@Override
	public void refreshEventPortalData() {
		if (this.toolbar.currentSortStateObjects.get()) {
			refreshSortByDomain();
		} else {
			refreshSortByAlpha();
		}
	}
	
	@Override
	public void sortDomainClicked() {
		refreshSortByDomain();
	}
	
	
	@Override
	public void sortAlphaClicked() {
		refreshSortByAlpha();
	}
	
	
	@Override
	public void expandNextClicked() {
		if (tableModel != null && tableModel.getRoot() != null) {
			tableModel.getRoot().expandNext();
			tableModel.flatten();
		}
	}
	
	
	@Override
	public void collapseAllClicked() {
		if (tableModel != null && tableModel.getRoot() != null) {
			tableModel.getRoot().collapse();
			tableModel.flatten();
		}
	}

	@Override
	public void hideEmptyDomains() {
		// TODO Auto-generated method stub
		
	}

}

// -Dide.ui.scale=1.5