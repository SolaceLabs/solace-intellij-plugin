package com.solace.aaron.ideaplugin1.panel;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.table.TableView;
import com.solace.aaron.ideaplugin1.domain.EventPortalDomain;

public class TableResultsPanel extends NonOpaquePanel
{
    private TableView<EventPortalDomain> resultsTable;

    public TableResultsPanel(TableView<EventPortalDomain> resultsTable)
    {
        this.resultsTable = resultsTable;
        this.init();
    }

    private void init()
    {
        this.setBorder(BorderFactory.createEmptyBorder());
        JPanel scrollPanel = new JPanel();
        scrollPanel.setBorder(BorderFactory.createEmptyBorder());
        scrollPanel.setLayout(new BorderLayout());
        scrollPanel.add(ScrollPaneFactory.createScrollPane(this.resultsTable), BorderLayout.CENTER);
        this.setLayout(new BorderLayout());
        this.add(scrollPanel, BorderLayout.CENTER);
    }
}
