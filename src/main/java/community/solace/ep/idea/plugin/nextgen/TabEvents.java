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
import community.solace.ep.idea.plugin.utils.TimeUtils;
import community.solace.ep.idea.plugin.utils.TopicUtils;
import community.solace.ep.wrapper.EventPortalObjectType;
import community.solace.ep.wrapper.EventPortalWrapper;
import icons.MyIcons;

/**
 * This window represents one of the tabs ("content") on the main PS+Portal tool.  It has a toolbar and a content area.
 */
public class TabEvents extends GenericTab {
	
	private static final Logger LOG = Logger.getInstance(TabEvents.class);

	public TabEvents(SolaceEventPortalToolWindowFactory factory) {
		super(factory);
	}


	@Override
	public void refreshSortByDomain() {
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
				row.setLastUpdatedTs(TimeUtils.parseTime(domain.getUpdatedTime()));
				row.setLastUpdatedByUser(domain.getChangedBy());
				row.setCreatedByUser(domain.getCreatedBy());

				for (Event event : EventPortalWrapper.INSTANCE.getEventsForDomainId(domain.getId())) {
					buildEventNode(row, event, domain, false);
				}
			}
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
		PortalRowObjectTreeNode root = new PortalRowObjectTreeNode(null, "");  // root
		try {
			for (Event event : EventPortalWrapper.INSTANCE.getEvents()) {
				buildEventNode(root, event, EventPortalWrapper.INSTANCE.getDomain(event.getApplicationDomainId()), true);
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
	
	
	private PortalRowObjectTreeNode buildAppVerNode(Icon icon, ApplicationVersion appVer, ApplicationDomain domain) {
		
		Application app = EventPortalWrapper.INSTANCE.getApplication(appVer.getApplicationId());
		ApplicationDomain appDomain = EventPortalWrapper.INSTANCE.getDomain(app.getApplicationDomainId());
		PortalRowObjectTreeNode appVerPro = new PortalRowObjectTreeNode(EventPortalObjectType.APPLICATION_VERSION, appVer.getId());
		appVerPro.setIcon(icon);
		appVerPro.setName(String.format("%s%s v%s",
				appDomain.getId().equals(domain.getId()) ? "" : "(EXT) ",
				app.getName(),
				appVer.getVersion()));
		
		appVerPro.addNote(String.format("%s %s App",
				TopicUtils.capitalFirst(app.getApplicationType()),
				TopicUtils.capitalFirst(app.getBrokerType().getValue())));
		
		if (!domain.equals(appDomain)) appVerPro.addNote("Domain: " + appDomain.getName());
		appVerPro.setState(EventPortalWrapper.INSTANCE.getState(appVer.getStateId()).getName());
		appVerPro.setLink(String.format(TopicUtils.APP_VER_URL, appDomain.getId(), app.getId(), appVer.getId()));
		appVerPro.setLastUpdatedTs(TimeUtils.parseTime(appVer.getUpdatedTime()));
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


	private void buildEventNode(PortalRowObjectTreeNode parent, Event event, ApplicationDomain domain, boolean domainInNotes) {
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
		eventPro.setLastUpdatedTs(TimeUtils.parseTime(event.getUpdatedTime()));
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
			eventVerPro.setLastUpdatedTs(TimeUtils.parseTime(eventVer.getUpdatedTime()));
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
				eventVerPro.addChild(buildAppVerNode(MyIcons.AppSmallBoth, appVer, domain));
			}
			for (String appVerId : pubAppVerIds) {
				ApplicationVersion appVer = EventPortalWrapper.INSTANCE.getApplicationVersion(appVerId);
				eventVerPro.addChild(buildAppVerNode(MyIcons.AppSmallPub, appVer, domain));
			}
			for (String appVerId : subAppVerIds) {
				ApplicationVersion appVer = EventPortalWrapper.INSTANCE.getApplicationVersion(appVerId);
				eventVerPro.addChild(buildAppVerNode(MyIcons.AppSmallSub, appVer, domain));
			}
		}
	}
}
// -Dide.ui.scale=1.5