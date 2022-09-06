package com.solace.aaron.ideaplugin1;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.solace.aaron.ideaplugin1.panel.ApplicationsTreeResultPanel;
import com.solace.aaron.ideaplugin1.panel.ApplicationsToolbarPanel;

public class EventPortalAppsTreeWindow implements LoadRefreshButton.Observer {

    private final JPanel contentToolWindow;
    private final ApplicationsToolbarPanel applicationsToolbarPanel;
    ApplicationsTreeResultPanel treeResultsPanel;

    public JComponent getContent() {
        return contentToolWindow;
    }


	@Override
	public void refreshEventPortalData() {
		// TODO Auto-generated method stub
		treeResultsPanel.load();
	}


    public EventPortalAppsTreeWindow(ApplicationsToolbarPanel applicationsToolbarPanel) {
    	// init the JPanel
//        this.contentToolWindow = new SimpleToolWindowPanel(true, true);  // intellij component
//        SOTableModel tableModel = new SOTableModel(SOTableModel.generateColumnInfo(), new ArrayList<>());
//        ResultsTable resultsTable = new ResultsTable(tableModel);
    	this.applicationsToolbarPanel = applicationsToolbarPanel;

        SimpleToolWindowPanel sp = new SimpleToolWindowPanel(false, true);
//        sp.
//        sp.set

        treeResultsPanel = new ApplicationsTreeResultPanel(null);

//        TableResultsPanel tableResultsPanel = new TableResultsPanel(resultsTable);
//        tableResultsPanel.setBorder(IdeBorderFactory.createBorder(SideBorder.TOP));
//        AppsTree tree = new AppsTree(null);
//        ApplicationsToolbarPanel applicationsToolbarPanel = new ApplicationsToolbarPanel(null);//tree);//tableModel, resultsTable);
//        applicationsPanel.setBorder(IdeBorderFactory.createBorder(SideBorder.RIGHT | SideBorder.BOTTOM));
        sp.setToolbar(applicationsToolbarPanel);
        sp.setContent(treeResultsPanel);

//        OnePixelSplitter horizontalSplitter = new OnePixelSplitter(false, 0.0f);
//        horizontalSplitter.setBorder(BorderFactory.createEtchedBorder());//createEmptyBorder());
//        horizontalSplitter.setDividerPositionStrategy(Splitter.DividerPositionStrategy.KEEP_FIRST_SIZE);
//        horizontalSplitter.setResizeEnabled(false);
//        horizontalSplitter.setFirstComponent(applicationsPanel);
//        horizontalSplitter.setSecondComponent(tableResultsPanel);
//        this.contentToolWindow.add(horizontalSplitter);


        contentToolWindow = sp;

    }
}

// -Dide.ui.scale=1.5