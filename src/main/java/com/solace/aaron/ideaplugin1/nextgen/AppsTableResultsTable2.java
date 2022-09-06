package com.solace.aaron.ideaplugin1.nextgen;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ListSelectionModel;

import org.jdesktop.swingx.plaf.basic.core.BasicTransferable;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.ui.hover.TableHoverListener;
import com.intellij.ui.table.TableView;
import com.solace.aaron.ideaplugin1.utils.EPObjectHelper;

import community.solace.ep.client.model.Application;
import community.solace.ep.client.model.ApplicationVersion;
import community.solace.ep.client.model.EventVersion;
import community.solace.ep.client.model.SchemaObject;
import community.solace.ep.client.model.SchemaVersion;
import community.solace.ep.wrapper.EventPortalObjectType;
import community.solace.ep.wrapper.EventPortalWrapper;
import icons.MyIcons;


public class AppsTableResultsTable2 extends TableView<PortalRowObjectTreeNode> {
	
	private static final long serialVersionUID = 1L;
	final EPAppsTableModel2 model;

	public AppsTableResultsTable2(EPAppsTableModel2 model) {
        super(model);
        this.model = model;
        this.init();
    }
	
    @Override
    protected @Nullable Color getHoveredRowBackground() {
        int row = TableHoverListener.getHoveredRow(this);
        if (row < 0 || row > getItems().size()) return null;
        PortalRowObjectTreeNode p = getRow(row);
        
        return CustomRenderer2.blend(getBackground(), EPObjectHelper.getColor(p.getType()), 0.2f);
    }
    
    
    private static boolean isClickable(PortalRowObjectTreeNode pro, int col) {
    	if (col == 1) {  // the name is always clickable
    		return true;
    	} else if (col == 6) {  // pull down schemas if you want?
            if (pro.getType() == EventPortalObjectType.APPLICATION_VERSION || pro.getType() == EventPortalObjectType.EVENT_VERSION) {
                return true;
            }
    	} else if (col == 0) {
            if (pro.hasChildren()) {
                return true;
            }
    	}
    	return false;
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
                if (row >= 0 && row < getItems().size()) {
                	PortalRowObjectTreeNode pro = (PortalRowObjectTreeNode)getValueAt(row,col);
                	if (isClickable(pro, col)) {
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                	} else {
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                	}
                } else {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
        
        
        
        this.addMouseListener(new MouseListener() {
        	
        	int lastRowClicked = 0;
        	PortalRowObjectTreeNode lastProClicked = null;
        	
            @Override
            public void mouseClicked(MouseEvent e) {

                int row = rowAtPoint(e.getPoint());
                int col = columnAtPoint(e.getPoint());
                
                // Link column = 2


                if ((row >= 0 && row < getItems().size()) && MouseEvent.BUTTON1 == e.getButton()) {
                	PortalRowObjectTreeNode pro = (PortalRowObjectTreeNode)getValueAt(row,col);
                	if (!isClickable(pro, col)) return;
                    if (col == 1) {
                        BrowserUtil.browse(pro.getLink());
                    } else if (6 == col) {
//                    	BrowserUtil.browse("https://news.google.com");

                    	if (pro.getType() == EventPortalObjectType.EVENT_VERSION) {
                    		EventVersion ev = EventPortalWrapper.INSTANCE.getEventVersion(pro.getId());
                    		SchemaVersion schemaVer = EventPortalWrapper.INSTANCE.getSchemaVersion(ev.getSchemaVersionId());
                    		if (schemaVer != null && schemaVer.getContent() != null) {
                    			SchemaObject schema = EventPortalWrapper.INSTANCE.getSchema(schemaVer.getSchemaId());
//                    			String schemaContent = schemaVer.getContent();
                    			JsonObject jo = new Gson().fromJson(schemaVer.getContent(), JsonObject.class);
                    			String pretty = new GsonBuilder().setPrettyPrinting().create().toJson(jo);
                    			Transferable t2 = new BasicTransferable(pretty, pretty);
                    			CopyPasteManager.getInstance().setContents(t2);
                    			String message = String.format("Schema '%s' v%s copied to clipboard", schema.getName(), schemaVer.getVersion());
                    			Notifications.Bus.notify(new Notification("ep20", "Solace Event Portal", message, NotificationType.INFORMATION));
                    			
//                    			Project p = SolaceEventPortalToolWindowFactory.getProject();
//                    			Document d = EditorFactory.getInstance().createDocument("hello world");
//                    			
//                    			if (d == null) Notifications.Bus.notify(new Notification("ep20", "Solace Event Portal", "Document is null!", NotificationType.WARNING));
//                    			
//                    			VirtualFile vf = FileDocumentManager.getInstance().getFile(d);
//                    			if (vf == null) Notifications.Bus.notify(new Notification("ep20", "Solace Event Portal", "Virtual File is null!", NotificationType.WARNING));
//                    			if (vf != null) {
//                    				FileEditorManager.getInstance(p).openTextEditor(new OpenFileDescriptor(p, vf), true);
//                    			}

                    		} else {
                    			Notifications.Bus.notify(new Notification("ep20", "Solace Event Portal", "Could not copy schema to clipboard", NotificationType.WARNING));
                    		}
                    		
                    	} else if (pro.getType() == EventPortalObjectType.APPLICATION_VERSION) {
                    		ApplicationVersion appVer = EventPortalWrapper.INSTANCE.getApplicationVersion(pro.getId());
                    		Application app = EventPortalWrapper.INSTANCE.getApplication(appVer.getApplicationId());
                    		String asyncApi = EventPortalWrapper.INSTANCE.getAsyncApiForAppVerId(pro.getId(), true);
                			Transferable t2 = new BasicTransferable(asyncApi, asyncApi);
                			CopyPasteManager.getInstance().setContents(t2);
                			String message = String.format("AsyncAPI Schema for App '%s' v%s copied to clipboard", app.getName(), appVer.getVersion());
                			Notifications.Bus.notify(new Notification("ep20", "Solace Event Portal", message, NotificationType.INFORMATION));

                    	
                    	}
                    } else if (col == 0) {
                    	if (e.getClickCount() == 2) {
                    		lastProClicked.expandAll();
                    		lastProClicked = pro;
                    	} else {
                    		lastProClicked = pro;
                    		if (pro.isExpanded()) {
                    			pro.collapse();
                    		} else {  // it's collapsed
                    			pro.expandOnce();
                    		}
                    	}
                    	model.setItems(pro.getRoot().flatten());
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
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}            

        
        });
        
        
        
    }
}
