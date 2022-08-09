package com.solace.aaron.ideaplugin1.table;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import com.intellij.ide.BrowserUtil;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ListTableModel;
import com.solace.aaron.ideaplugin1.domain.EventPortalDomain;


public class ResultsTable extends TableView<EventPortalDomain>
{
    public ResultsTable(ListTableModel<EventPortalDomain> model)
    {
        super(model);
        this.init();
    }

    private void init()
    {
        this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setCellSelectionEnabled(true);
        this.setStriped(true);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.setAutoCreateRowSorter(true);
        this.getEmptyText().setText("Search for StackOverflow questions!");
        this.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                JTable source = (JTable) e.getSource();
                int rowAtPoint = source.rowAtPoint(e.getPoint());
                int columnAtPoint = source.columnAtPoint(e.getPoint());
                // Link column = 2
                if (2 == columnAtPoint && MouseEvent.BUTTON1 == e.getButton())
                {
                    BrowserUtil.browse(ResultsTable.this.getValueAt(rowAtPoint, columnAtPoint).toString());
                }
                super.mousePressed(e);
            }
        });
    }
}
