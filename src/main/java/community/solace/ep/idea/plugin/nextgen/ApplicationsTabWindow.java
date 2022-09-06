package community.solace.ep.idea.plugin.nextgen;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.SimpleToolWindowPanel;

import community.solace.ep.client.model.Application;
import community.solace.ep.client.model.ApplicationDomain;
import community.solace.ep.client.model.ApplicationVersion;
import community.solace.ep.client.model.Event;
import community.solace.ep.client.model.EventVersion;
import community.solace.ep.client.model.SchemaObject;
import community.solace.ep.client.model.SchemaVersion;
import community.solace.ep.idea.plugin.LoadRefreshButton;
import community.solace.ep.idea.plugin.utils.TimeDeltaUtils;
import community.solace.ep.idea.plugin.utils.TopicUtils;
import community.solace.ep.wrapper.EventPortalObjectType;
import community.solace.ep.wrapper.EventPortalWrapper;
import icons.MyIcons;

/**
 * This window represents one of the tabs ("content") on the main PS+Portal tool.  It has a toolbar and a content area.
 */
public class ApplicationsTabWindow implements LoadRefreshButton.Observer, PortalToolbarListener {
	
    private final JPanel contentToolWindow;  // the whole simple window
    private final PortalTabToolbar toolbarPanel;
//    private final EPAppsTableModel tableModel;
    private final EPAppsTableModel2 tableModel2;
//    private final AppsTableResultsTable tableView;
    private final AppsTableResultsTable2 tableView2;
    private final AppsTableResultsPanel2 tableResultsPanel;
    
	private static final Logger LOG = Logger.getInstance(ApplicationsTabWindow.class);

	public ApplicationsTabWindow() {
    	// init the JPanel
    	SimpleToolWindowPanel sp = new SimpleToolWindowPanel(false, true);  // intellij component
		tableModel2 = new EPAppsTableModel2(EPAppsTableModel2.generateColumnInfo());
        tableView2 = new AppsTableResultsTable2(tableModel2);
        this.toolbarPanel = new PortalTabToolbar(this);

        tableResultsPanel = new AppsTableResultsPanel2(tableView2);

        sp.setToolbar(toolbarPanel);
        sp.setContent(tableResultsPanel);
        this.contentToolWindow = sp;
    }
    
    public JComponent getContent() {
        return contentToolWindow;
    }


	@Override
	public void refreshEventPortalData() {
		if (this.toolbarPanel.currentSortStateObjects.get()) {
			refreshObjectsNested();
		} else {
			refreshAlphaNested();
		}
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
			eventVerPro.addNote(String.format("%s Payload", TopicUtils.capitalFirst(eventVer.getSchemaPrimitiveType().getValue())));
		} else {
			eventVerPro.addNote("No Schema");
		}
		return eventVerPro;	
	}


	private void generateTree(PortalRowObjectTreeNode parent, Application app, ApplicationDomain domain, boolean domainInNotes) {
//		List<PortalRowObject> rows = new ArrayList<>();
		PortalRowObjectTreeNode appPro = new PortalRowObjectTreeNode(EventPortalObjectType.APPLICATION, app.getId());
		parent.addChild(appPro);
		appPro.setIcon(MyIcons.appLarge);
		appPro.setName(app.getName());
		appPro.addNote(String.format("%s %s App",
				TopicUtils.capitalFirst(app.getApplicationType()),
				TopicUtils.capitalFirst(app.getBrokerType().getValue())));
		appPro.addNote(String.format("%d %s",
				app.getNumberOfVersions(),
				TopicUtils.pluralize("Version",  app.getNumberOfVersions())));
		if (domainInNotes) appPro.addNote("Domain: " + domain.getName());
		appPro.setLink(String.format(TopicUtils.APP_URL, domain.getId(), app.getId()));
		appPro.setLastUpdated(TimeDeltaUtils.formatTime(app.getUpdatedTime()));
		appPro.setLastUpdatedByUser(app.getChangedBy());
		appPro.setCreatedByUser(app.getCreatedBy());

		for (ApplicationVersion appVer : EventPortalWrapper.INSTANCE.getApplicationVersionsForApplicationId(app.getId())) {
//			appVer.get
			PortalRowObjectTreeNode appVerPro = new PortalRowObjectTreeNode(EventPortalObjectType.APPLICATION_VERSION, appVer.getId());
			appPro.addChild(appVerPro);
			appVerPro.setIcon(MyIcons.appSmall);
			appVerPro.setName("v" + appVer.getVersion());
			
			if (appVer.getDeclaredProducedEventVersionIds().size() == 0 && appVer.getDeclaredConsumedEventVersionIds().size() == 0) {
				appVerPro.addNote("0 Events published or subscribed");
			} else {
				appVerPro.addNote(String.format("%d %s",
						appVer.getDeclaredProducedEventVersionIds().size(),
						TopicUtils.pluralize("Pub'ed Event", appVer.getDeclaredProducedEventVersionIds().size())));
				appVerPro.addNote(String.format("%d %s",
						appVer.getDeclaredConsumedEventVersionIds().size(),
						TopicUtils.pluralize("Sub'ed Event", appVer.getDeclaredConsumedEventVersionIds().size())));
			}
			appVerPro.setState(EventPortalWrapper.INSTANCE.getState(appVer.getStateId()).getName());
			appVerPro.setLink(String.format(TopicUtils.APP_VER_URL, domain.getId(), app.getId(), appVer.getId()));
			appVerPro.setLastUpdated(TimeDeltaUtils.formatTime(appVer.getUpdatedTime()));
			appVerPro.setLastUpdatedByUser(appVer.getChangedBy());
			appVerPro.setCreatedByUser(appVer.getCreatedBy());
			
			// make copies so we don't mess up the originals
			List<String> pubEventVerIds = new ArrayList<>(appVer.getDeclaredProducedEventVersionIds());
			List<String> subEventVerIds = new ArrayList<>(appVer.getDeclaredConsumedEventVersionIds());
			List<String> both = new ArrayList<>(pubEventVerIds);
			both.retainAll(subEventVerIds);
			pubEventVerIds.removeAll(both);
			subEventVerIds.removeAll(both);
			for (String eventVerId : both) {  // for all EventVersions this AppVer produces...
				EventVersion eventVer = EventPortalWrapper.INSTANCE.getEventVersion(eventVerId);
				appVerPro.addChild(buildEventVerTree(MyIcons.EvetnSmallBoth, eventVer, domain));
			}
			for (String eventVerId : pubEventVerIds) {  // for all EventVersions this AppVer produces...
				EventVersion eventVer = EventPortalWrapper.INSTANCE.getEventVersion(eventVerId);
				appVerPro.addChild(buildEventVerTree(MyIcons.EventSmallPub, eventVer, domain));
			}
			for (String eventVerId : subEventVerIds) {  // for all EventVersions this AppVer produces...
				EventVersion eventVer = EventPortalWrapper.INSTANCE.getEventVersion(eventVerId);
				appVerPro.addChild(buildEventVerTree(MyIcons.EventSmallSub, eventVer, domain));
			}
		}
	}

	public void refreshObjectsNested() {
		PortalRowObjectTreeNode root = new PortalRowObjectTreeNode(null, "");  // root
		try {
			for (ApplicationDomain domain : EventPortalWrapper.INSTANCE.getDomains()) {
				PortalRowObjectTreeNode row = new PortalRowObjectTreeNode(EventPortalObjectType.DOMAIN, domain.getId());
				row.setIcon(MyIcons.DomainLarge);
				if (toolbarPanel.hideEmptyDomains && domain.getStats().getApplicationCount() == 0) continue;
				root.addChild(row);
				row.setName(domain.getName());
				row.addNote(String.format("%d %s",
						domain.getStats().getApplicationCount(),
						TopicUtils.pluralize("Application",  domain.getStats().getApplicationCount())
						
//						domain.getStats().getEventCount(),
//						TopicUtils.pluralize("Event",  domain.getStats().getEventCount()),
//						domain.getStats().getSchemaCount(),
//						TopicUtils.pluralize("Schema",  domain.getStats().getSchemaCount()),
//						domain.getStats().getEventApiCount(),
//						TopicUtils.pluralize("Event API",  domain.getStats().getEventApiCount()
						));
//				row.setState("");
				row.setLink(String.format(TopicUtils.DOMAIN_URL, domain.getId()));
				row.setLastUpdated(TimeDeltaUtils.formatTime(domain.getUpdatedTime()));
				row.setLastUpdatedByUser(domain.getChangedBy());
				row.setCreatedByUser(domain.getCreatedBy());

				for (Application app : EventPortalWrapper.INSTANCE.getApplicationsForDomainId(domain.getId())) {
					generateTree(row, app, domain, false);
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
			for (Application app : EventPortalWrapper.INSTANCE.getApplications()) {
				generateTree(root, app, EventPortalWrapper.INSTANCE.getDomain(app.getApplicationDomainId()), true);
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
		refreshEventPortalData();
	}

}

// -Dide.ui.scale=1.5