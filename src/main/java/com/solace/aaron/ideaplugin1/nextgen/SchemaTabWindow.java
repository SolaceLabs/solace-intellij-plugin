package com.solace.aaron.ideaplugin1.nextgen;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.solace.aaron.ideaplugin1.LoadRefreshButton;
import com.solace.aaron.ideaplugin1.utils.TimeDeltaUtils;
import com.solace.aaron.ideaplugin1.utils.TopicUtils;

import community.solace.ep.client.model.Application;
import community.solace.ep.client.model.ApplicationDomain;
import community.solace.ep.client.model.ApplicationVersion;
import community.solace.ep.client.model.Event;
import community.solace.ep.client.model.EventVersion;
import community.solace.ep.client.model.SchemaObject;
import community.solace.ep.client.model.SchemaVersion;
import community.solace.ep.wrapper.EventPortalObjectType;
import community.solace.ep.wrapper.EventPortalWrapper;
import icons.MyIcons;

/**
 * This window represents one of the tabs ("content") on the main PS+Portal tool.  It has a toolbar and a content area.
 */
public class SchemaTabWindow implements LoadRefreshButton.Observer, PortalToolbarListener {
	
    private final JPanel contentToolWindow;  // the whole simple window
    private final PortalTabToolbar applicationsToolbarPanel;
//    private final EPAppsTableModel tableModel;
    private final EPAppsTableModel2 tableModel2;
//    private final AppsTableResultsTable tableView;
    private final AppsTableResultsTable2 tableView2;
    private final AppsTableResultsPanel2 tableResultsPanel;
    
	private static final Logger LOG = Logger.getInstance(SchemaTabWindow.class);

	public SchemaTabWindow() {
    	// init the JPanel
    	SimpleToolWindowPanel sp = new SimpleToolWindowPanel(false, true);  // intellij component
		tableModel2 = new EPAppsTableModel2(EPAppsTableModel2.generateColumnInfo()/* , new ArrayList<>() */);
        tableView2 = new AppsTableResultsTable2(tableModel2);
        this.applicationsToolbarPanel = new PortalTabToolbar(this);

        tableResultsPanel = new AppsTableResultsPanel2(tableView2);

        sp.setToolbar(applicationsToolbarPanel);
        sp.setContent(tableResultsPanel);
        this.contentToolWindow = sp;
    }
    
    public JComponent getContent() {
        return contentToolWindow;
    }


	@Override
	public void refreshEventPortalData() {
		if (this.applicationsToolbarPanel.currentSortStateObjects.get()) {
			refreshObjectsNested();
		} else {
			refreshAlphaNested();
		}
	}
	
	
	private PortalRowObjectTreeNode buildAppVerTree(Icon icon, ApplicationVersion appVer, ApplicationDomain domain) {
		
//		Event event = EventPortalWrapper.INSTANCE.getEvent(eventVer.getEventId());
		Application app = EventPortalWrapper.INSTANCE.getApplication(appVer.getApplicationId());
		ApplicationDomain appDomain = EventPortalWrapper.INSTANCE.getDomain(app.getApplicationDomainId());
		PortalRowObjectTreeNode appVerPro = new PortalRowObjectTreeNode(EventPortalObjectType.APPLICATION_VERSION, appVer.getId());
		// is there a schema for this Event?
		appVerPro.setIcon(icon);
		appVerPro.setName(String.format("%s%s v%s",
				appDomain.getId().equals(domain.getId()) ? "" : "(EXT) ",
				app.getName(),
//				app.isShared() ? "*" : "",
				appVer.getVersion()));
//		appVerPro.addNote(String.format("%s %s App",
//				TopicUtils.capitalFirst(app.getApplicationType()),
//				TopicUtils.capitalFirst(app.getBrokerType().getValue())));
		
		appVerPro.addNote(String.format("%s %s App",
				TopicUtils.capitalFirst(app.getApplicationType()),
				TopicUtils.capitalFirst(app.getBrokerType().getValue())));
//		String broker = TopicUtils.capitalFirst(app.getBrokerType().name());
//		if (broker == null) broker = "N/A";
//		appVerPro.addNote(String.format("%s Application", TopicUtils.capitalFirst(broker)));
//
		
		
		if (!domain.equals(appDomain)) appVerPro.addNote("Domain: " + domain.getName());
		appVerPro.setState(EventPortalWrapper.INSTANCE.getState(appVer.getStateId()).getName());
//		appVerPro.setDetails(TopicUtils.buildTopic(eventVer.getDeliveryDescriptor()));
		appVerPro.setLink(String.format(TopicUtils.APP_VER_URL, appDomain.getId(), app.getId(), appVer.getId()));
		appVerPro.setLastUpdated(TimeDeltaUtils.formatTime(appVer.getUpdatedTime()));
		appVerPro.setLastUpdatedByUser(appVer.getChangedBy());
		appVerPro.setCreatedByUser(appVer.getCreatedBy());
		
		
//		if (EventPortalWrapper.INSTANCE.getSchemaVersion(eventVer.getSchemaVersionId()) != null) {
//			SchemaVersion schemaVersion = EventPortalWrapper.INSTANCE.getSchemaVersion(eventVer.getSchemaVersionId());
//			SchemaObject schema = EventPortalWrapper.INSTANCE.getSchema(schemaVersion.getSchemaId());
////			row.addNote(TopicUtils.capitalFirst(schema.getContentType()) + " Payload");
//			appVerPro.addNote(String.format("%s Payload", TopicUtils.capitalFirst(schema.getContentType())));
//		} else if (eventVer.getSchemaPrimitiveType() != null) {
//			appVerPro.addNote(String.format("%s Payload", TopicUtils.capitalFirst(eventVer.getSchemaPrimitiveType().toString())));
//		} else {
//			appVerPro.addNote("No Schema");
//		}
		return appVerPro;	
	}

	
	private PortalRowObjectTreeNode buildEventVerTree(Icon icon, EventVersion eventVer, ApplicationDomain domain) {
		
		Event event = EventPortalWrapper.INSTANCE.getEvent(eventVer.getEventId());
		ApplicationDomain origDomain = EventPortalWrapper.INSTANCE.getDomain(event.getApplicationDomainId());
		PortalRowObjectTreeNode eventVerPro = new PortalRowObjectTreeNode(EventPortalObjectType.EVENT_VERSION, eventVer.getId());
		// is there a schema for this Event?
		eventVerPro.setIcon(icon);
		eventVerPro.setName(String.format("%s%s%s v%s",
				origDomain.getId().equals(domain.getId()) ? "" : "(EXT) ",
				event.getName(),
				event.isShared() ? "*" : "",
				eventVer.getVersion()));
		eventVerPro.setState(EventPortalWrapper.INSTANCE.getState(eventVer.getStateId()).getName());
		eventVerPro.setDetails(TopicUtils.buildTopic(eventVer.getDeliveryDescriptor()));
		eventVerPro.setLink(String.format(TopicUtils.EVENT_VER_URL, origDomain.getId(), event.getId(), eventVer.getId()));
		eventVerPro.setLastUpdated(TimeDeltaUtils.formatTime(eventVer.getUpdatedTime()));
		eventVerPro.setLastUpdatedByUser(eventVer.getChangedBy());
		eventVerPro.setCreatedByUser(eventVer.getCreatedBy());
		String broker = TopicUtils.capitalFirst(eventVer.getDeliveryDescriptor().getBrokerType());
		if (broker == null) broker = "N/A";
		eventVerPro.addNote(String.format("%s Message", TopicUtils.capitalFirst(broker)));
		if (EventPortalWrapper.INSTANCE.getSchemaVersion(eventVer.getSchemaVersionId()) != null) {
			SchemaVersion schemaVersion = EventPortalWrapper.INSTANCE.getSchemaVersion(eventVer.getSchemaVersionId());
			SchemaObject schema = EventPortalWrapper.INSTANCE.getSchema(schemaVersion.getSchemaId());
//			row.addNote(TopicUtils.capitalFirst(schema.getContentType()) + " Payload");
			eventVerPro.addNote(String.format("%s Payload", TopicUtils.capitalFirst(schema.getContentType())));
		} else if (eventVer.getSchemaPrimitiveType() != null) {
			eventVerPro.addNote(String.format("%s Payload", TopicUtils.capitalFirst(eventVer.getSchemaPrimitiveType().toString())));
		} else {
			eventVerPro.addNote("No Schema");
		}
		return eventVerPro;	
	}

	
	

	private void generateTree(PortalRowObjectTreeNode parent, SchemaObject schema, ApplicationDomain domain, boolean domainInNotes) {
//		List<PortalRowObject> rows = new ArrayList<>();
		PortalRowObjectTreeNode schemaPro = new PortalRowObjectTreeNode(EventPortalObjectType.SCHEMA, schema.getId());
		parent.addChild(schemaPro);
		schemaPro.setIcon(MyIcons.schemaLarge);
		schemaPro.setName(schema.getName() + (schema.isShared() ? "*" : ""));
		if (schema.isShared()) schemaPro.addNote("Shared");
		schemaPro.addNote(String.format("%d %s",
				schema.getNumberOfVersions(),
				TopicUtils.pluralize("Version",  schema.getNumberOfVersions())));
		if (domainInNotes) schemaPro.addNote("Domain: " + domain.getName());
		schemaPro.setLink(String.format(TopicUtils.SCHEMA_URL, domain.getId(), schema.getId()));
		schemaPro.setLastUpdated(TimeDeltaUtils.formatTime(schema.getUpdatedTime()));
		schemaPro.setLastUpdatedByUser(schema.getChangedBy());
		schemaPro.setCreatedByUser(schema.getCreatedBy());

		for (SchemaVersion schemaVer : EventPortalWrapper.INSTANCE.getSchemaVersionsForSchemaId(schema.getId())) {
			PortalRowObjectTreeNode schemaVerPro = new PortalRowObjectTreeNode(EventPortalObjectType.SCHEMA_VERSION, schemaVer.getId());
			schemaPro.addChild(schemaVerPro);
			schemaVerPro.setIcon(MyIcons.schemaSmall);
			schemaVerPro.setName("v" + schemaVer.getVersion());

			schemaVerPro.addNote(String.format("%s Payload", TopicUtils.capitalFirst(schema.getContentType())));
//			} else if (schemaVer.getSchemaPrimitiveType() != null) {
//				schemaVerPro.addNote(String.format("%s Payload", TopicUtils.capitalFirst(schemaVer.getSchemaPrimitiveType().toString())));
//			} else {
//				schemaVerPro.addNote("No Schema");
//			}
			
			if (schemaVer.getReferencedByEventVersionIds() == null || schemaVer.getReferencedByEventVersionIds().size() == 0) {
				schemaVerPro.addNote("0 Events referencing");
			} else {
				schemaVerPro.addNote(String.format("%d %s referencing",
						schemaVer.getReferencedByEventVersionIds().size(),
						TopicUtils.pluralize("Event", schemaVer.getReferencedByEventVersionIds().size())));
			}
			
			
			schemaVerPro.setState(EventPortalWrapper.INSTANCE.getState(schemaVer.getStateId()).getName());
			schemaVerPro.setLink(String.format(TopicUtils.SCHEMA_VER_URL, domain.getId(), schema.getId(), schemaVer.getId()));
			schemaVerPro.setLastUpdated(TimeDeltaUtils.formatTime(schemaVer.getUpdatedTime()));
			schemaVerPro.setLastUpdatedByUser(schemaVer.getChangedBy());
			schemaVerPro.setCreatedByUser(schemaVer.getCreatedBy());
			
			if (schemaVer.getReferencedByEventVersionIds() != null) {
				for (String eventVerId : schemaVer.getReferencedByEventVersionIds()) {
					EventVersion eventVer = EventPortalWrapper.INSTANCE.getEventVersion(eventVerId);
					schemaVerPro.addChild(buildEventVerTree(MyIcons.EventSmall, eventVer, domain));
				}
			}
		}
	}

	public void refreshObjectsNested() {
		PortalRowObjectTreeNode root = new PortalRowObjectTreeNode(null, "");  // root
		try {
			for (ApplicationDomain domain : EventPortalWrapper.INSTANCE.getDomains()) {
				PortalRowObjectTreeNode row = new PortalRowObjectTreeNode(EventPortalObjectType.DOMAIN, domain.getId());
				row.setIcon(MyIcons.DomainLarge);
				root.addChild(row);
				row.setName(domain.getName());
				row.addNote(String.format("%d %s",
						domain.getStats().getSchemaCount(),
						TopicUtils.pluralize("Schema",  domain.getStats().getSchemaCount())
						));
				row.setLink(String.format(TopicUtils.DOMAIN_URL, domain.getId()));
				row.setLastUpdated(TimeDeltaUtils.formatTime(domain.getUpdatedTime()));
				row.setLastUpdatedByUser(domain.getChangedBy());
				row.setCreatedByUser(domain.getCreatedBy());

				for (SchemaObject schema : EventPortalWrapper.INSTANCE.getSchemasForDomainId(domain.getId())) {
					generateTree(row, schema, domain, false);
				}
			}
		} catch (RuntimeException e) {
//			GenericAppsTableDomain lastApp = apps.get(apps.size()-1);
//			LOG.error(lastApp.getType() + " " + lastApp.getName());
			LOG.error(e);
			throw e;
		}
		if (!root.hasChildren()) {
			this.tableView2.getEmptyText().setText("No results found for your search criteria");
		} else {
			this.tableModel2.setRoot(root);
			this.tableModel2.flatten();
		}
	}

	public void refreshAlphaNested() {
		PortalRowObjectTreeNode root = new PortalRowObjectTreeNode(null, "");  // root
		try {
			for (SchemaObject schema : EventPortalWrapper.INSTANCE.getSchemas()) {
				generateTree(root, schema, EventPortalWrapper.INSTANCE.getDomain(schema.getApplicationDomainId()), false);
			}
		} catch (RuntimeException e) {
			LOG.error(e);
			throw e;
		}
		if (!root.hasChildren()) {
			this.tableView2.getEmptyText().setText("No results found for your search criteria");
		} else {
			this.tableModel2.setRoot(root);
			this.tableModel2.flatten();
		}
	}
	
	@Override
	public void sortObjects() {
		refreshObjectsNested();
	}
	
	
	@Override
	public void sortAlpha() {
		refreshAlphaNested();
	}
	
	
	@Override
	public void expandAll() {
		tableModel2.getRoot().expandNext();
		tableModel2.flatten();
	}
	
	
	@Override
	public void collapseAll() {
		tableModel2.getRoot().collapse();
		tableModel2.flatten();
	}

	@Override
	public void hideEmptyDomains() {
		// TODO Auto-generated method stub
		
	}

}

// -Dide.ui.scale=1.5