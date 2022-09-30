package community.solace.ep.idea.plugin.nextgen;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import com.intellij.openapi.diagnostic.Logger;

import community.solace.ep.client.model.Application;
import community.solace.ep.client.model.ApplicationDomain;
import community.solace.ep.client.model.ApplicationVersion;
import community.solace.ep.client.model.Event;
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
import community.solace.ep.wrapper.EventPortalWrapper.LoadStatus;
import icons.MyIcons;

/**
 * This window represents one of the tabs ("content") on the main PS+Portal tool.  It has a toolbar and a content area.
 */
public class TabApplications extends GenericTab {
	
	private static final Logger LOG = Logger.getInstance(TabApplications.class);

	public TabApplications(SolaceEventPortalToolWindowFactory factory) {
		super(factory);
	}

	@Override
	public void refreshSortByDomain() {
		if (EventPortalWrapper.INSTANCE.getLoadStatus() == LoadStatus.UNINITIALIZED) return;
		PortalRowObjectTreeNode root = new PortalRowObjectTreeNode(null, null, "");  // root
		try {
			for (ApplicationDomain domain : EventPortalWrapper.INSTANCE.getDomains()) {
				PortalRowObjectTreeNode row = new PortalRowObjectTreeNode(EventPortalObjectType.DOMAIN, domain, domain.getId());
				row.setIcon(MyIcons.DomainLarge);
//				row.setIcon(MyIcons.Portal);
				if (toolbar.hideEmptyDomains && domain.getStats().getApplicationCount() == 0) continue;
				root.addChild(row);
				row.setName(domain.getName());
				row.addDetail(String.format("%d %s",
						domain.getStats().getApplicationCount(),
						WordyUtils.pluralize("Application",  domain.getStats().getApplicationCount())
						));
				row.setLink(String.format(TopicUtils.DOMAIN_URL, AppSettingsState.getInstance().baseUrl, domain.getId()));
				row.setLastUpdatedTs(TimeUtils.parseTime(domain.getUpdatedTime()));
				row.setLastUpdatedByUser(domain.getChangedBy());
				row.setCreatedByUser(domain.getCreatedBy());
//				row.expandOnce();
				for (Application app : EventPortalWrapper.INSTANCE.getApplicationsForDomainId(domain.getId())) {
					generateTree(row, app, domain, false);
				}
			}
			root.expandNext();  // show all the apps
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

	public void refreshSortByAlpha() {
		if (EventPortalWrapper.INSTANCE.getLoadStatus() == LoadStatus.UNINITIALIZED) return;
		PortalRowObjectTreeNode root = new PortalRowObjectTreeNode(null, null, "");  // root
		try {
			for (Application app : EventPortalWrapper.INSTANCE.getApplications()) {
				generateTree(root, app, EventPortalWrapper.INSTANCE.getDomain(app.getApplicationDomainId()), true);
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
	
	private PortalRowObjectTreeNode buildEventVerTree(Icon icon, String specificText, EventVersion eventVer, ApplicationDomain domain) {
		String which = "DEF";
		if (icon == MyIcons.EventSmallBoth) which = "PUB/SUB";
		if (icon == MyIcons.EventSmallPub) which = "PUB";
		if (icon == MyIcons.EventSmallSub) which = "SUB";
		
		Event event = EventPortalWrapper.INSTANCE.getEvent(eventVer.getEventId());
		ApplicationDomain origDomain = EventPortalWrapper.INSTANCE.getDomain(event.getApplicationDomainId());
		PortalRowObjectTreeNode eventVerPro = new PortalRowObjectTreeNode(EventPortalObjectType.EVENT_VERSION, eventVer, eventVer.getId());
		// is there a schema for this Event?
		eventVerPro.setIcon(icon);
		eventVerPro.setName(String.format("%s: %s%s%s v%s",
				which,
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
		eventVerPro.addDetail(String.format("%s %s Message", specificText, WordyUtils.capitalFirst(broker)));
		
		// current / old
		if (EventPortalWrapper.INSTANCE.getSchemaVersion(eventVer.getSchemaVersionId()) != null) {
			SchemaVersion schemaVersion = EventPortalWrapper.INSTANCE.getSchemaVersion(eventVer.getSchemaVersionId());
			SchemaObject schema = EventPortalWrapper.INSTANCE.getSchema(schemaVersion.getSchemaId());
//			eventVerPro.addNote(String.format("%s Payload", TopicUtils.capitalFirst(schema.getContentType())));
			PortalRowObjectTreeNode proSchemaVer = new PortalRowObjectTreeNode(EventPortalObjectType.SCHEMA_VERSION, schemaVersion, schemaVersion.getId());
			proSchemaVer.setName(schema.getName() + (schema.isShared() ? "*" : "") + " v" + schemaVersion.getVersion());
			proSchemaVer.addDetail(String.format("%s Payload", WordyUtils.capitalFirst(schema.getContentType())));
			proSchemaVer.setLink(String.format(TopicUtils.SCHEMA_VER_URL, AppSettingsState.getInstance().baseUrl, domain.getId(), schema.getId(), schemaVersion.getId()));
			proSchemaVer.setIcon(MyIcons.SchemaSmall);
			if (schema.isShared()) proSchemaVer.addDetail("Shared");
			proSchemaVer.setState(EventPortalWrapper.INSTANCE.getState(schemaVersion.getStateId()).getName());
			proSchemaVer.setLastUpdatedTs(TimeUtils.parseTime(schemaVersion.getUpdatedTime()));
			proSchemaVer.setLastUpdatedByUser(schemaVersion.getChangedBy());
			proSchemaVer.setCreatedByUser(schemaVersion.getCreatedBy());
			eventVerPro.addChild(proSchemaVer);
		} else if (eventVer.getSchemaPrimitiveType() != null) {
//			eventVerPro.addNote(String.format("%s Payload", TopicUtils.capitalFirst(eventVer.getSchemaPrimitiveType().getValue())));
			PortalRowObjectTreeNode proSchemaVer = new PortalRowObjectTreeNode(EventPortalObjectType.SCHEMA_VERSION, null, "");
			proSchemaVer.setName(String.format("Primitive %s Payload", WordyUtils.capitalFirst(eventVer.getSchemaPrimitiveType().getValue())));
			proSchemaVer.setIcon(MyIcons.SchemaPrimitive);
			
			eventVerPro.addChild(proSchemaVer);
		} else {
//			eventVerPro.addNote("No Schema");
			PortalRowObjectTreeNode proSchemaVer = new PortalRowObjectTreeNode(EventPortalObjectType.SCHEMA_VERSION, null, "");
			proSchemaVer.setName("No Schema");
			proSchemaVer.setIcon(MyIcons.SchemaNone);
			eventVerPro.addChild(proSchemaVer);
		}
		
		
		
		return eventVerPro;	
	}


	private void generateTree(PortalRowObjectTreeNode parent, Application app, ApplicationDomain domain, boolean domainInNotes) {
//		List<PortalRowObject> rows = new ArrayList<>();
		PortalRowObjectTreeNode appPro = new PortalRowObjectTreeNode(EventPortalObjectType.APPLICATION, app, app.getId());
		parent.addChild(appPro);
		appPro.setIcon(MyIcons.AppLarge);
		appPro.setName(app.getName());
		appPro.addDetail(String.format("%s %s App",
				WordyUtils.capitalFirst(app.getApplicationType()),
				WordyUtils.capitalFirst(app.getBrokerType().getValue())));
		appPro.addDetail(String.format("%d %s",
				app.getNumberOfVersions(),
				WordyUtils.pluralize("Version",  app.getNumberOfVersions())));
		if (domainInNotes) appPro.addDetail("Domain: " + domain.getName());
		appPro.setLink(String.format(TopicUtils.APP_URL, AppSettingsState.getInstance().baseUrl, domain.getId(), app.getId()));
		appPro.setLastUpdatedTs(TimeUtils.parseTime(app.getUpdatedTime()));
		appPro.setLastUpdatedByUser(app.getChangedBy());
		appPro.setCreatedByUser(app.getCreatedBy());

		for (ApplicationVersion appVer : EventPortalWrapper.INSTANCE.getApplicationVersionsForApplicationId(app.getId())) {
//			appVer.get
			PortalRowObjectTreeNode appVerPro = new PortalRowObjectTreeNode(EventPortalObjectType.APPLICATION_VERSION, appVer, appVer.getId());
			appPro.addChild(appVerPro);
			appVerPro.setIcon(MyIcons.appSmall);
			appVerPro.setName("v" + appVer.getVersion());
			
			// make copies so we don't mess up the originals
			List<String> pubEventVerIds = new ArrayList<>(appVer.getDeclaredProducedEventVersionIds());
			List<String> subEventVerIds = new ArrayList<>(appVer.getDeclaredConsumedEventVersionIds());
			List<String> both = new ArrayList<>(pubEventVerIds);
			both.retainAll(subEventVerIds);
			pubEventVerIds.removeAll(both);
			subEventVerIds.removeAll(both);
			
			if (appVer.getDeclaredProducedEventVersionIds().size() == 0 && appVer.getDeclaredConsumedEventVersionIds().size() == 0) {
				appVerPro.addDetail("0 Events published or subscribed");
			} else {
				appVerPro.addDetail(String.format("%d %s",
						pubEventVerIds.size() + subEventVerIds.size() + both.size(),
						WordyUtils.pluralize("Referenced Event", pubEventVerIds.size() + subEventVerIds.size() + both.size())));
			}
			appVerPro.setState(EventPortalWrapper.INSTANCE.getState(appVer.getStateId()).getName());
			appVerPro.setLink(String.format(TopicUtils.APP_VER_URL, AppSettingsState.getInstance().baseUrl, domain.getId(), app.getId(), appVer.getId()));
			appVerPro.setLastUpdatedTs(TimeUtils.parseTime(appVer.getUpdatedTime()));
			appVerPro.setLastUpdatedByUser(appVer.getChangedBy());
			appVerPro.setCreatedByUser(appVer.getCreatedBy());
			
			for (String eventVerId : both) {  // for all EventVersions this AppVer produces...
				EventVersion eventVer = EventPortalWrapper.INSTANCE.getEventVersion(eventVerId);
				appVerPro.addChild(buildEventVerTree(MyIcons.EventSmallBoth, "Pub/Sub'ed", eventVer, domain));
			}
			for (String eventVerId : pubEventVerIds) {  // for all EventVersions this AppVer produces...
				EventVersion eventVer = EventPortalWrapper.INSTANCE.getEventVersion(eventVerId);
				appVerPro.addChild(buildEventVerTree(MyIcons.EventSmallPub, "Published", eventVer, domain));
			}
			for (String eventVerId : subEventVerIds) {  // for all EventVersions this AppVer produces...
				EventVersion eventVer = EventPortalWrapper.INSTANCE.getEventVersion(eventVerId);
				appVerPro.addChild(buildEventVerTree(MyIcons.EventSmallSub, "Subscribed", eventVer, domain));
			}
		}
	}

	

}

// -Dide.ui.scale=1.5