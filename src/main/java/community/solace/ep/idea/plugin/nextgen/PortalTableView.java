package community.solace.ep.idea.plugin.nextgen;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ListSelectionModel;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.json.JsonFileType;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.hover.TableHoverListener;
import com.intellij.ui.table.TableView;

import community.solace.ep.client.model.Application;
import community.solace.ep.client.model.ApplicationVersion;
import community.solace.ep.client.model.EventVersion;
import community.solace.ep.client.model.SchemaObject;
import community.solace.ep.client.model.SchemaVersion;
import community.solace.ep.idea.plugin.settings.AppSettingsState;
import community.solace.ep.idea.plugin.utils.EPObjectHelper;
import community.solace.ep.wrapper.EventPortalObjectType;
import community.solace.ep.wrapper.EventPortalWrapper;


public class PortalTableView extends TableView<PortalRowObjectTreeNode> {
	
	private static final long serialVersionUID = 1L;
	private final GenericTab myParentTab;
	final PortalTableModel model;

	public PortalTableView(GenericTab myParentTab, PortalTableModel model) {
        super(model);
        this.myParentTab = myParentTab;
        this.model = model;
        this.init();
        this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setCellSelectionEnabled(true);
        this.setRowSelectionAllowed(false);
        this.setColumnSelectionAllowed(false);
        
    }

	/** I found this by digging through a bunch of source code to figure out where this default row highlighter was coming from */
    @Override
    protected @Nullable Color getHoveredRowBackground() {
        int row = TableHoverListener.getHoveredRow(this);
        if (row < 0 || row > getItems().size()) return null;
        PortalRowObjectTreeNode p = getRow(row);
//        if (this.getCursor().getType() == Cursor.DEFAULT_CURSOR) {
//        	return CustomTableCellRenderer.blend(getBackground(), EPObjectHelper.getColor(p.getType()), 0.15f);
//        } else {
        	return CustomTableCellRenderer.blend(getBackground(), EPObjectHelper.getColor(p.getType()), 0.2f);
//        }
    }
    
    
    private static boolean isClickable(PortalRowObjectTreeNode pro, int col) {
    	if (col == 1 && !pro.getLink().isEmpty()) {  // the name is always clickable
    		return true;
    	} else if (col == 6) {  // pull down schemas if you want?
            if (pro.getType() == EventPortalObjectType.APPLICATION_VERSION
            		/* || pro.getType() == EventPortalObjectType.EVENT_VERSION */
            		|| (pro.getType() == EventPortalObjectType.SCHEMA_VERSION && !pro.getId().isEmpty())) {
                return true;
            }
    	} else if (col == 0) {
            if (pro.hasChildren() && !pro.isHidden()) {
                return true;
            }
    	}
    	return false;
    }
    
    
    private void init() {
        this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setCellSelectionEnabled(true);
//        this.setStriped(true);
//        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        this.setAutoCreateRowSorter(true);

        this.getTableHeader().setReorderingAllowed(false);
//        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        if (AppSettingsState.getInstance().tokenId.isEmpty()) {
//        	this.getEmptyText().setText("", SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
        	this.getEmptyText().clear();
        	this.getEmptyText().setCenterAlignText(false);
        	this.getEmptyText().appendLine(AllIcons.General.Settings, "Add token in Event Portal Settings!", SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES, null);
        	this.getEmptyText().appendLine("File  >  Settings...  >  Tools  >  Solace Event Portal");
        	this.getEmptyText().appendLine("");
        	this.getEmptyText().appendLine(AllIcons.Actions.Execute, "Then load Event Portal data!", SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES, null);
//        	this.getEmptyText().appendLine(AllIcons.Actions.Execute, "Then load Event Portal data!", SimpleTextAttributes.LINK_BOLD_ATTRIBUTES, new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					myParentTab.getFactory().clickLoadButton(e);
//				}
//			});
        } else {
        	this.getEmptyText().clear();
//        	this.getEmptyText().setText("Load Event Portal data!", SimpleTextAttributes.LINK_PLAIN_ATTRIBUTES);
        	this.getEmptyText().appendLine(AllIcons.Actions.Execute, "Load Event Portal data!", SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES, null);
//        	this.getEmptyText().appendLine(AllIcons.Actions.Execute, "Load Event Portal data!", SimpleTextAttributes.LINK_BOLD_ATTRIBUTES, new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					myParentTab.getFactory().clickLoadButton();
//				}
//			});
        }
//        StatusText st = StatusText.
//        emptyText.setText(VcsLogBundle.message("vcs.log.changes.no.changes.that.affect.selected.paths.status"))
//        .appendSecondaryText(VcsLogBundle.message("vcs.log.changes.show.all.paths.status.action"),
//                             SimpleTextAttributes.LINK_PLAIN_ATTRIBUTES,
//                             e -> myUiProperties.set(SHOW_ONLY_AFFECTED_CHANGES, false));
        
        
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) { }

            @Override
            public void mouseMoved(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                int col = columnAtPoint(e.getPoint());
                if (row >= 0 && row < getItems().size()) {
                	PortalRowObjectTreeNode pro = (PortalRowObjectTreeNode)getValueAt(row,col);
                	if (isClickable(pro, col)) {
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                        return;
                	}
                }
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        this.addMouseListener(new MouseListener() {
        	
        	PortalRowObjectTreeNode lastProClicked = null;
        	
            @Override
            public void mouseClicked(MouseEvent e) {

                int row = rowAtPoint(e.getPoint());
                int col = columnAtPoint(e.getPoint());
                
                if ((row >= 0 && row < getItems().size()) && MouseEvent.BUTTON1 == e.getButton()) {
                	PortalRowObjectTreeNode pro = (PortalRowObjectTreeNode)getValueAt(row,col);
                	if (!isClickable(pro, col)) return;
                    if (col == 1 && !pro.getLink().isEmpty()) {
                        BrowserUtil.browse(pro.getLink());
                    } else if (6 == col) {
//                    	BrowserUtil.browse("https://news.google.com");

                    	if (pro.getType() == EventPortalObjectType.SCHEMA_VERSION && !pro.getId().isEmpty()) {
                    		SchemaVersion schemaVer = EventPortalWrapper.INSTANCE.getSchemaVersion(pro.getId());
                    		SchemaObject schema = EventPortalWrapper.INSTANCE.getSchema(schemaVer.getSchemaId());
                			JsonObject jo = new Gson().fromJson(schemaVer.getContent(), JsonObject.class);
                			String pretty = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(jo);
//                			pretty = pretty.replaceAll("  ", "\t");
                			String title = String.format("Schema: '%s' v%s (%s)", schema.getName(), schemaVer.getVersion(), schema.getSchemaType());
                			FileType fileType = JsonFileType.INSTANCE;
                			if (schema.getSchemaType().equalsIgnoreCase("json")) {
                				fileType = JsonFileType.INSTANCE;
                			}
                			myParentTab.getFactory().addEditorTab(title, Math.random() > 0.5 ? schemaVer.getContent() : pretty, fileType, false, pro);
                    	} else if (pro.getType() == EventPortalObjectType.EVENT_VERSION) {
                    		if ("a".equals("a")) return;
                    		EventVersion ev = EventPortalWrapper.INSTANCE.getEventVersion(pro.getId());
                    		SchemaVersion schemaVer = EventPortalWrapper.INSTANCE.getSchemaVersion(ev.getSchemaVersionId());
                    		if (schemaVer != null && schemaVer.getContent() != null) {
                    			SchemaObject schema = EventPortalWrapper.INSTANCE.getSchema(schemaVer.getSchemaId());
//                    			String schemaContent = schemaVer.getContent();
                    			JsonObject jo = new Gson().fromJson(schemaVer.getContent(), JsonObject.class);
                    			String pretty = new GsonBuilder().setPrettyPrinting().create().toJson(jo);
//                    			pretty = pretty.replaceAll("  ", "\t");
                    			myParentTab.getFactory().addEditorTab(pro.getId(), pretty, null, false, pro);
//                    			Transferable t2 = new BasicTransferable(pretty, pretty);
//                    			CopyPasteManager.getInstance().setContents(t2);
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
                    		
//                    		e.getP
                    		
                    		
                    		String asyncApi = EventPortalWrapper.INSTANCE.getAsyncApiForAppVerId(pro.getId(), true);
//                			Transferable t2 = new BasicTransferable(asyncApi, asyncApi);
//                			CopyPasteManager.getInstance().setContents(t2);
//                			String message = String.format("AsyncAPI Schema for App '%s' v%s copied to clipboard", app.getName(), appVer.getVersion());
//                			Notifications.Bus.notify(new Notification("ep20", "Solace Event Portal", message, NotificationType.INFORMATION));

//                    		asyncApi = asyncApi.replaceAll("  ", "\t");
                			String title = String.format("AsyncAPI: '%s' v%s", app.getName(), appVer.getVersion());
                			myParentTab.getFactory().addEditorTab(title, asyncApi, JsonFileType.INSTANCE, true, pro);
//                			myParentTab.getFactory().openDownloadAsyncApi();
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
                    	model.setItems(pro.getRoot().flatten(true));
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
