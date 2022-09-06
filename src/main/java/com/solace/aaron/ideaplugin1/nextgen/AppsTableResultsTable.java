package com.solace.aaron.ideaplugin1.nextgen;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ListSelectionModel;

import org.jetbrains.annotations.Nullable;

import com.intellij.ide.BrowserUtil;
import com.intellij.ui.hover.TableHoverListener;
import com.intellij.ui.table.TableView;


public class AppsTableResultsTable extends TableView<PortalRowObject> {
	
	private static final long serialVersionUID = 1L;
	final EPAppsTableModel model;

	public AppsTableResultsTable(EPAppsTableModel model) {
        super(model);
        this.model = model;
        this.init();
    }
	
    @Override
    protected @Nullable Color getHoveredRowBackground() {
        int row = TableHoverListener.getHoveredRow(this);
        if (row < 0 || row > getItems().size()) return null;
        PortalRowObject p = getRow(row);
		switch (p.getTypeEnum()) {
		case DOMAIN:
			return (CustomRenderer.blend(getBackground(), CustomRenderer.opposite(getBackground()), 0.05f));
		case APPLICATION:
			return (CustomRenderer.blend(getBackground(), CustomRenderer.GREEN1, 0.2f));
		case APPLICATION_VERSION:
			return (CustomRenderer.blend(getBackground(), CustomRenderer.GREEN1, 0.1f));
		case EVENT:
			return (CustomRenderer.blend(getBackground(), CustomRenderer.GREEN2, 0.2f));
		case EVENT_VERSION:
			return (CustomRenderer.blend(getBackground(), CustomRenderer.GREEN2, 0.1f));
		case SCHEMA:
			return (CustomRenderer.blend(getBackground(), CustomRenderer.GREEN3, 0.2f));
		case SCHEMA_VERSION:
			return (CustomRenderer.blend(getBackground(), CustomRenderer.GREEN3, 0.1f));
		default:
			//				cellComponent.setBackground(blend(table.getBackground(), GREEN1, 0.1f));
			return Color.RED;
		}
    }
    
    private void init() {
        this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setCellSelectionEnabled(false);
//        this.setStriped(true);
//        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        this.setAutoCreateRowSorter(true);

        this.getTableHeader().setReorderingAllowed(false);
//        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);



        this.getEmptyText().setText("Load Event Portal data!");
        
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                int col = columnAtPoint(e.getPoint());
                if ((col == 1 || col == 6) && (row >= 0 && row < getItems().size())) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    getValueAt(row,col);
                } else {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
        
        
        
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int row = rowAtPoint(e.getPoint());
                int col = columnAtPoint(e.getPoint());
                
                // Link column = 2
                
                if ((row >= 0 && row < getItems().size()) && MouseEvent.BUTTON1 == e.getButton()) {
                    if (col == 1) {
                        BrowserUtil.browse(AppsTableResultsTable.this.getValueAt(row, col).toString());
                    } else if (6 == col) {
//                    GenericAppsTableDomain o = (GenericAppsTableDomain)source.getModel().getValueAt(rowAtPoint, columnAtPoint);
                    BrowserUtil.browse(AppsTableResultsTable.this.getValueAt(row, col).toString());

//					Notifications.Bus.notify(new Notification("ep20", "Solace Event Portal", "'" + AppsTableResultsTable.this.getValueAt(rowAtPoint, columnAtPoint).toString() + "'", NotificationType.INFORMATION));
                	
//                	getVal
//                    BrowserUtil.browse(((ActionLink)AppsTableResultsTable.this.getValueAt(rowAtPoint, columnAtPoint)).getActionCommand());
                    }
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }            

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}            

        
        });
        
        
        
    }
}
