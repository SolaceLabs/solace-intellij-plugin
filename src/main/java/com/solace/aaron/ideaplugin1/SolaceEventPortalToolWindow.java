package com.solace.aaron.ideaplugin1;

import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.solace.aaron.ideaplugin1.panel.ApplicationsToolbarPanel;
import com.solace.aaron.ideaplugin1.panel.ApplicationsToolbarPanelDomains;
import com.solace.aaron.ideaplugin1.panel.TableResultsPanel;
import com.solace.aaron.ideaplugin1.table.ResultsTable;
import com.solace.aaron.ideaplugin1.table.SOTableModel;

/**
 * This window represents one of the tabs ("content") on the main PS+Portal tool.  It has a toolbar and a content area.
 */
public class SolaceEventPortalToolWindow implements LoadRefreshButton.Observer {
	
    private final JPanel contentToolWindow;
    private final ApplicationsToolbarPanelDomains applicationsToolbarPanel;
    
    public JComponent getContent() {
        return contentToolWindow;
    }


	@Override
	public void refreshEventPortalData() {
		applicationsToolbarPanel.refresh();
	}

    
//    private static final Icon JMS_MESSENGER_ICON = getJmsIcon();

	public SolaceEventPortalToolWindow(/* ApplicationsToolbarPanel applicationsToolbarPanel */) {
    	// init the JPanel
    	SimpleToolWindowPanel sp = new SimpleToolWindowPanel(false, true);  // intellij component
//        this.applicationsToolbarPanel = applicationsToolbarPanel;
        SOTableModel tableModel = new SOTableModel(SOTableModel.generateColumnInfo(), new ArrayList<>());
        ResultsTable resultsTable = new ResultsTable(tableModel);
        this.applicationsToolbarPanel = new ApplicationsToolbarPanelDomains(tableModel, resultsTable);

        TableResultsPanel tableResultsPanel = new TableResultsPanel(resultsTable);
//        tableResultsPanel.setBorder(IdeBorderFactory.createBorder(SideBorder.TOP | SideBorder.RIGHT));

//        SearchPanel searchPanel = new SearchPanel(tableModel, resultsTable);
//        searchPanel.setBorder(IdeBorderFactory.createBorder(SideBorder.TOP | SideBorder.RIGHT | SideBorder.BOTTOM));
//
//        OnePixelSplitter horizontalSplitter = new OnePixelSplitter(true, 0.0f);
//        horizontalSplitter.setBorder(BorderFactory.createEmptyBorder());
//        horizontalSplitter.setDividerPositionStrategy(Splitter.DividerPositionStrategy.KEEP_FIRST_SIZE);
//        horizontalSplitter.setResizeEnabled(false);
//        horizontalSplitter.setFirstComponent(searchPanel);
//        horizontalSplitter.setSecondComponent(tableResultsPanel);
//        this.contentToolWindow.add(horizontalSplitter);

        sp.setToolbar(applicationsToolbarPanel);
        sp.setContent(tableResultsPanel);
        this.contentToolWindow = sp;
        
        
    }

}

// -Dide.ui.scale=1.5