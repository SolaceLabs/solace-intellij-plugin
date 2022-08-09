package com.solace.aaron.ideaplugin1;

import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.SideBorder;
import com.solace.aaron.ideaplugin1.panel.SearchPanel;
import com.solace.aaron.ideaplugin1.panel.TableResultsPanel;
import com.solace.aaron.ideaplugin1.table.ResultsTable;
import com.solace.aaron.ideaplugin1.table.SOTableModel;

public class SolaceEventPortalToolWindow
{
    private final JPanel contentToolWindow;

    public JComponent getContent()
    {
        return this.contentToolWindow;
    }

    public SolaceEventPortalToolWindow() {

        this.contentToolWindow = new SimpleToolWindowPanel(true, true);
        SOTableModel tableModel = new SOTableModel(SOTableModel.generateColumnInfo(), new ArrayList<>());
        ResultsTable resultsTable = new ResultsTable(tableModel);

        TableResultsPanel tableResultsPanel = new TableResultsPanel(resultsTable);
        tableResultsPanel.setBorder(IdeBorderFactory.createBorder(SideBorder.TOP | SideBorder.RIGHT));
        SearchPanel searchPanel = new SearchPanel(tableModel, resultsTable);
        searchPanel.setBorder(IdeBorderFactory.createBorder(SideBorder.TOP | SideBorder.RIGHT | SideBorder.BOTTOM));
        OnePixelSplitter horizontalSplitter = new OnePixelSplitter(true, 0.0f);
        horizontalSplitter.setBorder(BorderFactory.createEmptyBorder());
        horizontalSplitter.setDividerPositionStrategy(Splitter.DividerPositionStrategy.KEEP_FIRST_SIZE);
        horizontalSplitter.setResizeEnabled(false);
        horizontalSplitter.setFirstComponent(searchPanel);
        horizontalSplitter.setSecondComponent(tableResultsPanel);
        this.contentToolWindow.add(horizontalSplitter);

    }
}

// -Dide.ui.scale=1.5