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
import community.solace.ep.client.model.Event;
import community.solace.ep.client.model.EventApi;
import community.solace.ep.client.model.EventApiVersion;
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
public class EventApisTabWindow implements LoadRefreshButton.Observer, PortalToolbarListener {
	
    private final JPanel contentToolWindow;  // the whole simple window
    private final PortalTabToolbar applicationsToolbarPanel;
    private final EPAppsTableModel2 tableModel2;
    private final AppsTableResultsTable2 tableView2;
    private final AppsTableResultsPanel2 tableResultsPanel;
    
	private static final Logger LOG = Logger.getInstance(EventApisTabWindow.class);

	public EventApisTabWindow() {
    	// init the JPanel
    	SimpleToolWindowPanel sp = new SimpleToolWindowPanel(false, true);  // intellij component
		tableModel2 = new EPAppsTableModel2(EPAppsTableModel2.generateColumnInfo());
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


	private void generateTree(PortalRowObjectTreeNode parent, EventApi api, ApplicationDomain domain, boolean domainInNotes) {
//		List<PortalRowObject> rows = new ArrayList<>();
		PortalRowObjectTreeNode apiPro = new PortalRowObjectTreeNode(EventPortalObjectType.EVENT_API, api.getId());
		parent.addChild(apiPro);
		apiPro.setIcon(MyIcons.apiLarge);
		apiPro.setName(api.getName() + (api.isShared() ? "*" : ""));
		if (api.isShared()) apiPro.addNote("Shared");
		apiPro.addNote(String.format("%s Event API",
				TopicUtils.capitalFirst(api.getBrokerType().getValue())));
		apiPro.addNote(String.format("%d %s",
				api.getNumberOfVersions(),
				TopicUtils.pluralize("Version",  api.getNumberOfVersions())));
		
		if (domainInNotes) apiPro.addNote("Domain: " + domain.getName());
		apiPro.setLink(String.format(TopicUtils.EVENT_API_URL, domain.getId(), api.getId()));
		apiPro.setLastUpdated(TimeDeltaUtils.formatTime(api.getUpdatedTime()));
		apiPro.setLastUpdatedByUser(api.getChangedBy());
		apiPro.setCreatedByUser(api.getCreatedBy());
		
		for (EventApiVersion apiVer : EventPortalWrapper.INSTANCE.getEventApiVersionsForEventApiId(api.getId())) {
//			appVer.get
			PortalRowObjectTreeNode apiVerPro = new PortalRowObjectTreeNode(EventPortalObjectType.EVENT_API_VERSION, apiVer.getId());
			apiPro.addChild(apiVerPro);
			apiVerPro.setIcon(MyIcons.apiSmall);
			apiVerPro.setName("v" + apiVer.getVersion());
			
			if (apiVer.getProducedEventVersionIds().size() == 0 && apiVer.getConsumedEventVersionIds().size() == 0) {
				apiVerPro.addNote("0 Events published or subscribed");
			} else {
				apiVerPro.addNote(String.format("%d %s",
						apiVer.getProducedEventVersionIds().size(),
						TopicUtils.pluralize("Pub'ed Event", apiVer.getProducedEventVersionIds().size())));
				apiVerPro.addNote(String.format("%d %s",
						apiVer.getConsumedEventVersionIds().size(),
						TopicUtils.pluralize("Sub'ed Event", apiVer.getConsumedEventVersionIds().size())));
			}
			apiVerPro.setState(EventPortalWrapper.INSTANCE.getState(apiVer.getStateId()).getName());
			apiVerPro.setLink(String.format(TopicUtils.EVENT_API_VER_URL, domain.getId(), api.getId(), apiVer.getId()));
			apiVerPro.setLastUpdated(TimeDeltaUtils.formatTime(apiVer.getUpdatedTime()));
			apiVerPro.setLastUpdatedByUser(apiVer.getChangedBy());
			apiVerPro.setCreatedByUser(apiVer.getCreatedBy());
			
			// make copies so we don't mess up the originals
			List<String> pubEventVerIds = new ArrayList<>(apiVer.getProducedEventVersionIds());
			List<String> subEventVerIds = new ArrayList<>(apiVer.getConsumedEventVersionIds());
			List<String> both = new ArrayList<>(pubEventVerIds);
			both.retainAll(subEventVerIds);
			pubEventVerIds.removeAll(both);
			subEventVerIds.removeAll(both);
			for (String eventVerId : both) {  // for all EventVersions this AppVer produces...
				EventVersion eventVer = EventPortalWrapper.INSTANCE.getEventVersion(eventVerId);
				apiVerPro.addChild(buildEventVerTree(MyIcons.EvetnSmallBoth, eventVer, domain));
			}
			for (String eventVerId : pubEventVerIds) {  // for all EventVersions this AppVer produces...
				EventVersion eventVer = EventPortalWrapper.INSTANCE.getEventVersion(eventVerId);
				apiVerPro.addChild(buildEventVerTree(MyIcons.EventSmallPub, eventVer, domain));
			}
			for (String eventVerId : subEventVerIds) {  // for all EventVersions this AppVer produces...
				EventVersion eventVer = EventPortalWrapper.INSTANCE.getEventVersion(eventVerId);
				apiVerPro.addChild(buildEventVerTree(MyIcons.EventSmallSub, eventVer, domain));
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
						domain.getStats().getEventApiCount(),
						TopicUtils.pluralize("Event API",  domain.getStats().getEventApiCount())
						));
				row.setLink(String.format(TopicUtils.DOMAIN_URL, domain.getId()));
				row.setLastUpdated(TimeDeltaUtils.formatTime(domain.getUpdatedTime()));
				row.setLastUpdatedByUser(domain.getChangedBy());
				row.setCreatedByUser(domain.getCreatedBy());

				for (EventApi eventApi : EventPortalWrapper.INSTANCE.getEventApisForDomainId(domain.getId())) {
					generateTree(row, eventApi, domain, false);
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
			for (EventApi eventApi : EventPortalWrapper.INSTANCE.getEventApis()) {
				generateTree(root, eventApi, EventPortalWrapper.INSTANCE.getDomain(eventApi.getApplicationDomainId()), true);
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