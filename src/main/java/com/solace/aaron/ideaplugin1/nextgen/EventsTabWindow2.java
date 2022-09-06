package com.solace.aaron.ideaplugin1.nextgen;

import java.util.ArrayList;
import java.util.List;

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
public class EventsTabWindow2 implements LoadRefreshButton.Observer, PortalToolbarListener {
	
    private final JPanel contentToolWindow;  // the whole simple window
    private final PortalTabToolbar applicationsToolbarPanel;
//    private final EPAppsTableModel tableModel;
    private final EPAppsTableModel2 tableModel2;
//    private final AppsTableResultsTable tableView;
    private final AppsTableResultsTable2 tableView2;
    private final AppsTableResultsPanel2 tableResultsPanel;
    
	private static final Logger LOG = Logger.getInstance(EventsTabWindow2.class);

	public EventsTabWindow2() {
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
		
		
		if (!domain.equals(appDomain)) appVerPro.addNote("Domain: " + appDomain.getName());
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


	private void generateTree(PortalRowObjectTreeNode parent, Event event, ApplicationDomain domain, boolean domainInNotes) {
//		List<PortalRowObject> rows = new ArrayList<>();
		PortalRowObjectTreeNode eventPro = new PortalRowObjectTreeNode(EventPortalObjectType.EVENT, event.getId());
		parent.addChild(eventPro);
		eventPro.setIcon(MyIcons.EventLarge);
		eventPro.setName(event.getName() + (event.isShared() ? "*" : ""));
		if (event.isShared()) eventPro.addNote("Shared");
		eventPro.addNote(String.format("%d %s",
				event.getNumberOfVersions(),
				TopicUtils.pluralize("Version",  event.getNumberOfVersions())));
		if (domainInNotes) eventPro.addNote("Domain: " + domain.getName());
		eventPro.setLink(String.format(TopicUtils.EVENT_URL, domain.getId(), event.getId()));
		eventPro.setLastUpdated(TimeDeltaUtils.formatTime(event.getUpdatedTime()));
		eventPro.setLastUpdatedByUser(event.getChangedBy());
		eventPro.setCreatedByUser(event.getCreatedBy());

		for (EventVersion eventVer : EventPortalWrapper.INSTANCE.getEventVersionsForEventId(event.getId())) {
			PortalRowObjectTreeNode eventVerPro = new PortalRowObjectTreeNode(EventPortalObjectType.EVENT_VERSION, eventVer.getId());
			eventPro.addChild(eventVerPro);
			eventVerPro.setIcon(MyIcons.EventSmall);
			eventVerPro.setName("v" + eventVer.getVersion());
			if (EventPortalWrapper.INSTANCE.getSchemaVersion(eventVer.getSchemaVersionId()) != null) {
				SchemaVersion schemaVersion = EventPortalWrapper.INSTANCE.getSchemaVersion(eventVer.getSchemaVersionId());
				SchemaObject schema = EventPortalWrapper.INSTANCE.getSchema(schemaVersion.getSchemaId());
//				row.addNote(TopicUtils.capitalFirst(schema.getContentType()) + " Payload");
				eventVerPro.addNote(String.format("%s Payload", TopicUtils.capitalFirst(schema.getContentType())));
			} else if (eventVer.getSchemaPrimitiveType() != null) {
				eventVerPro.addNote(String.format("%s Payload", TopicUtils.capitalFirst(eventVer.getSchemaPrimitiveType().getValue())));
			} else {
				eventVerPro.addNote("No Schema");
			}
			
			if (eventVer.getDeclaredProducingApplicationVersionIds().size() == 0 && eventVer.getDeclaredConsumingApplicationVersionIds().size() == 0) {
				eventVerPro.addNote("0 Apps pub'ing or sub'ing");
			} else {
				eventVerPro.addNote(String.format("%d %s",
						eventVer.getDeclaredProducingApplicationVersionIds().size(),
						TopicUtils.pluralize("Pub'ing App", eventVer.getDeclaredProducingApplicationVersionIds().size())));
				eventVerPro.addNote(String.format("%d %s",
						eventVer.getDeclaredConsumingApplicationVersionIds().size(),
						TopicUtils.pluralize("Sub'ing App", eventVer.getDeclaredConsumingApplicationVersionIds().size())));
			}
			
			
			eventVerPro.setState(EventPortalWrapper.INSTANCE.getState(eventVer.getStateId()).getName());
			eventVerPro.setLink(String.format(TopicUtils.EVENT_VER_URL, domain.getId(), event.getId(), eventVer.getId()));
			eventVerPro.setDetails(TopicUtils.buildTopic(eventVer.getDeliveryDescriptor()));
			eventVerPro.setLastUpdated(TimeDeltaUtils.formatTime(eventVer.getUpdatedTime()));
			eventVerPro.setLastUpdatedByUser(eventVer.getChangedBy());
			eventVerPro.setCreatedByUser(eventVer.getCreatedBy());
			
			// make copies so we don't mess up the originals
			List<String> pubAppVerIds = new ArrayList<>(eventVer.getDeclaredProducingApplicationVersionIds());
			List<String> subAppVerIds = new ArrayList<>(eventVer.getDeclaredConsumingApplicationVersionIds());
			List<String> both = new ArrayList<>(pubAppVerIds);
			both.retainAll(subAppVerIds);
			pubAppVerIds.removeAll(both);
			subAppVerIds.removeAll(both);
			
			
			for (String appVerId : both) {
				ApplicationVersion appVer = EventPortalWrapper.INSTANCE.getApplicationVersion(appVerId);
				eventVerPro.addChild(buildAppVerTree(MyIcons.appSmallBoth, appVer, domain));
			}
			for (String appVerId : pubAppVerIds) {
				ApplicationVersion appVer = EventPortalWrapper.INSTANCE.getApplicationVersion(appVerId);
				eventVerPro.addChild(buildAppVerTree(MyIcons.appSmallPub, appVer, domain));
			}
			for (String appVerId : subAppVerIds) {
				ApplicationVersion appVer = EventPortalWrapper.INSTANCE.getApplicationVersion(appVerId);
				eventVerPro.addChild(buildAppVerTree(MyIcons.appSmallSub, appVer, domain));
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
						domain.getStats().getEventCount(),
						TopicUtils.pluralize("Event",  domain.getStats().getEventCount())
						));
				row.setLink(String.format(TopicUtils.DOMAIN_URL, domain.getId()));
				row.setLastUpdated(TimeDeltaUtils.formatTime(domain.getUpdatedTime()));
				row.setLastUpdatedByUser(domain.getChangedBy());
				row.setCreatedByUser(domain.getCreatedBy());

				for (Event event : EventPortalWrapper.INSTANCE.getEventsForDomainId(domain.getId())) {
					generateTree(row, event, domain, false);
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
			for (Event event : EventPortalWrapper.INSTANCE.getEvents()) {
				generateTree(root, event, EventPortalWrapper.INSTANCE.getDomain(event.getApplicationDomainId()), true);
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