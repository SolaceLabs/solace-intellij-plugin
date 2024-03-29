package community.solace.ep.idea.plugin.nextgen;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import com.intellij.openapi.diagnostic.Logger;

import community.solace.ep.client.model.ApplicationDomain;
import community.solace.ep.client.model.Event;
import community.solace.ep.client.model.EventApi;
import community.solace.ep.client.model.EventApiVersion;
import community.solace.ep.client.model.EventVersion;
import community.solace.ep.client.model.SchemaObject;
import community.solace.ep.client.model.SchemaVersion;
import community.solace.ep.idea.plugin.SolaceEventPortalToolWindowFactory;
import community.solace.ep.idea.plugin.settings.AppSettingsState;
import community.solace.ep.idea.plugin.utils.TimeUtils;
import community.solace.ep.idea.plugin.utils.TopicUtils;
import community.solace.ep.idea.plugin.utils.WordyUtils;
import community.solace.ep.wrapper.EventPortalObjectType;
import community.solace.ep.wrapper.EventPortalWrapper;
import icons.MyIcons;

/**
 * This window represents one of the tabs ("content") on the main PS+Portal tool.  It has a toolbar and a content area.
 */
public class TabEventApis extends GenericTab {
	
	private static final Logger LOG = Logger.getInstance(TabEventApis.class);

	public TabEventApis(SolaceEventPortalToolWindowFactory factory) {
		super(factory);
	}

	@Override
	public void refreshSortByDomain() {
		PortalRowObjectTreeNode root = new PortalRowObjectTreeNode(null, null, "");  // root
		try {
			for (ApplicationDomain domain : EventPortalWrapper.INSTANCE.getDomains()) {
				PortalRowObjectTreeNode row = new PortalRowObjectTreeNode(EventPortalObjectType.DOMAIN, domain, domain.getId());
				row.setIcon(MyIcons.DomainLarge);
				root.addChild(row);
				row.setName(domain.getName());
				row.addDetail(String.format("%d %s",
						domain.getStats().getEventApiCount(),
						WordyUtils.pluralize("Event API",  domain.getStats().getEventApiCount())
						));
				row.setLink(String.format(TopicUtils.DOMAIN_URL, AppSettingsState.getInstance().baseUrl, domain.getId()));
				row.setLastUpdatedTs(TimeUtils.parseTime(domain.getUpdatedTime()));
				row.setLastUpdatedByUser(domain.getChangedBy());
				row.setCreatedByUser(domain.getCreatedBy());

				for (EventApi eventApi : EventPortalWrapper.INSTANCE.getEventApisForDomainId(domain.getId())) {
					generateTree(row, eventApi, domain, false);
				}
			}
			root.expandNext();  // show the level under domains
		} catch (RuntimeException e) {
//			GenericAppsTableDomain lastApp = apps.get(apps.size()-1);
//			LOG.error(lastApp.getType() + " " + lastApp.getName());
			LOG.error(e);
			throw e;
		}
		if (!root.hasChildren()) {
			this.tableView.getEmptyText().setText("No results found for your search criteria");
		} else {
			this.tableModel.setRoot(root);
			this.tableModel.flatten();
		}
	}

	@Override
	public void refreshSortByAlpha() {
		PortalRowObjectTreeNode root = new PortalRowObjectTreeNode(null, null, "");  // root
		try {
			for (EventApi eventApi : EventPortalWrapper.INSTANCE.getEventApis()) {
				generateTree(root, eventApi, EventPortalWrapper.INSTANCE.getDomain(eventApi.getApplicationDomainId()), true);
			}
		} catch (RuntimeException e) {
			LOG.error(e);
			throw e;
		}
		if (!root.hasChildren()) {
			this.tableView.getEmptyText().setText("No results found for your search criteria");
		} else {
			this.tableModel.setRoot(root);
			this.tableModel.flatten();
		}
	}
	
	
	
	private PortalRowObjectTreeNode buildEventVerTree(Icon icon, EventVersion eventVer, ApplicationDomain domain) {
		
		Event event = EventPortalWrapper.INSTANCE.getEvent(eventVer.getEventId());
		ApplicationDomain origDomain = EventPortalWrapper.INSTANCE.getDomain(event.getApplicationDomainId());
		PortalRowObjectTreeNode eventVerPro = new PortalRowObjectTreeNode(EventPortalObjectType.EVENT_VERSION, eventVer, eventVer.getId());
		// is there a schema for this Event?
		eventVerPro.setIcon(icon);
		eventVerPro.setName(String.format("%s%s%s v%s",
				origDomain.getId().equals(domain.getId()) ? "" : "(EXT) ",
				event.getName(),
				event.isShared() ? "*" : "",
				eventVer.getVersion()));
		eventVerPro.setState(EventPortalWrapper.INSTANCE.getState(eventVer.getStateId()).getName());
		eventVerPro.setTopic(TopicUtils.buildTopic(eventVer.getDeliveryDescriptor()));
		eventVerPro.setLink(String.format(TopicUtils.EVENT_VER_URL, AppSettingsState.getInstance().baseUrl, origDomain.getId(), event.getId(), eventVer.getId()));
		eventVerPro.setLastUpdatedTs(TimeUtils.parseTime(eventVer.getUpdatedTime()));
		eventVerPro.setLastUpdatedByUser(eventVer.getChangedBy());
		eventVerPro.setCreatedByUser(eventVer.getCreatedBy());
		String broker = WordyUtils.capitalFirst(eventVer.getDeliveryDescriptor().getBrokerType());
		if (broker == null) broker = "N/A";
		eventVerPro.addDetail(String.format("%s Message", WordyUtils.capitalFirst(broker)));
		if (EventPortalWrapper.INSTANCE.getSchemaVersion(eventVer.getSchemaVersionId()) != null) {
			SchemaVersion schemaVersion = EventPortalWrapper.INSTANCE.getSchemaVersion(eventVer.getSchemaVersionId());
			SchemaObject schema = EventPortalWrapper.INSTANCE.getSchema(schemaVersion.getSchemaId());
//			row.addNote(TopicUtils.capitalFirst(schema.getContentType()) + " Payload");
			eventVerPro.addDetail(String.format("%s Payload", WordyUtils.capitalFirst(schema.getContentType())));
		} else if (eventVer.getSchemaPrimitiveType() != null) {
			eventVerPro.addDetail(String.format("%s Payload", WordyUtils.capitalFirst(eventVer.getSchemaPrimitiveType().getValue())));
		} else {
			eventVerPro.addDetail("No Schema");
		}
		return eventVerPro;	
	}


	private void generateTree(PortalRowObjectTreeNode parent, EventApi api, ApplicationDomain domain, boolean domainInNotes) {
//		List<PortalRowObject> rows = new ArrayList<>();
		PortalRowObjectTreeNode apiPro = new PortalRowObjectTreeNode(EventPortalObjectType.EVENT_API, api, api.getId());
		parent.addChild(apiPro);
		apiPro.setIcon(MyIcons.apiLarge);
		apiPro.setName(api.getName() + (api.isShared() ? "*" : ""));
		if (api.isShared()) apiPro.addDetail("Shared");
		apiPro.addDetail(String.format("%s Event API",
				WordyUtils.capitalFirst(api.getBrokerType().getValue())));
		apiPro.addDetail(String.format("%d %s",
				api.getNumberOfVersions(),
				WordyUtils.pluralize("Version",  api.getNumberOfVersions())));
		
		if (domainInNotes) apiPro.addDetail("Domain: " + domain.getName());
		apiPro.setLink(String.format(TopicUtils.EVENT_API_URL, AppSettingsState.getInstance().baseUrl, domain.getId(), api.getId()));
		apiPro.setLastUpdatedTs(TimeUtils.parseTime(api.getUpdatedTime()));
		apiPro.setLastUpdatedByUser(api.getChangedBy());
		apiPro.setCreatedByUser(api.getCreatedBy());
		
		for (EventApiVersion apiVer : EventPortalWrapper.INSTANCE.getEventApiVersionsForEventApiId(api.getId())) {
//			appVer.get
			PortalRowObjectTreeNode apiVerPro = new PortalRowObjectTreeNode(EventPortalObjectType.EVENT_API_VERSION, apiVer, apiVer.getId());
			apiPro.addChild(apiVerPro);
			apiVerPro.setIcon(MyIcons.apiSmall);
			apiVerPro.setName("v" + apiVer.getVersion());
			
			if (apiVer.getProducedEventVersionIds().size() == 0 && apiVer.getConsumedEventVersionIds().size() == 0) {
				apiVerPro.addDetail("0 Events published or subscribed");
			} else {
				apiVerPro.addDetail(String.format("%d %s",
						apiVer.getProducedEventVersionIds().size(),
						WordyUtils.pluralize("Pub'ed Event", apiVer.getProducedEventVersionIds().size())));
				apiVerPro.addDetail(String.format("%d %s",
						apiVer.getConsumedEventVersionIds().size(),
						WordyUtils.pluralize("Sub'ed Event", apiVer.getConsumedEventVersionIds().size())));
			}
			apiVerPro.setState(EventPortalWrapper.INSTANCE.getState(apiVer.getStateId()).getName());
			apiVerPro.setLink(String.format(TopicUtils.EVENT_API_VER_URL, AppSettingsState.getInstance().baseUrl, domain.getId(), api.getId(), apiVer.getId()));
			apiVerPro.setLastUpdatedTs(TimeUtils.parseTime(apiVer.getUpdatedTime()));
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
				apiVerPro.addChild(buildEventVerTree(MyIcons.EventSmallBoth, eventVer, domain));
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

}

// -Dide.ui.scale=1.5