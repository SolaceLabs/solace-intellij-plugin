package community.solace.ep.idea.plugin.nextgen;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.table.TableView;

public class AppsTableResultsPanel2 extends NonOpaquePanel
{
	private static final long serialVersionUID = 1L;
	private TableView<PortalRowObjectTreeNode> resultsTable;

    public AppsTableResultsPanel2(TableView<PortalRowObjectTreeNode> resultsTable) {
        this.resultsTable = resultsTable;
        this.init();
    }

    private void init() {
        this.setBorder(BorderFactory.createEmptyBorder());
        JPanel scrollPanel = new JPanel();
        scrollPanel.setBorder(BorderFactory.createEmptyBorder());
        scrollPanel.setLayout(new BorderLayout());
        scrollPanel.add(ScrollPaneFactory.createScrollPane(this.resultsTable), BorderLayout.CENTER);
        this.setLayout(new BorderLayout());
        this.add(scrollPanel, BorderLayout.CENTER);
    }
}
