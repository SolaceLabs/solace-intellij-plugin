package community.solace.ep.idea.plugin.nextgen;

import javax.swing.Icon;

import com.intellij.openapi.diagnostic.Logger;

import community.solace.ep.client.model.ApplicationDomain;
import community.solace.ep.client.model.EventApi;
import community.solace.ep.client.model.EventApiProduct;
import community.solace.ep.client.model.EventApiProductVersion;
import community.solace.ep.client.model.EventApiVersion;
import community.solace.ep.client.model.Plan;
import community.solace.ep.client.model.SolaceMessagingService;
import community.solace.ep.idea.plugin.SolaceEventPortalToolWindowFactory;
import community.solace.ep.idea.plugin.utils.TimeUtils;
import community.solace.ep.idea.plugin.utils.TopicUtils;
import community.solace.ep.wrapper.EventPortalObjectType;
import community.solace.ep.wrapper.EventPortalWrapper;
import icons.MyIcons;

/**
 * This window represents one of the tabs ("content") on the main PS+Portal tool.  It has a toolbar and a content area.
 */
public class TabEventApiProducts extends GenericTab {
	
	private static final Logger LOG = Logger.getInstance(TabEventApiProducts.class);

	public TabEventApiProducts(SolaceEventPortalToolWindowFactory factory) {
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
						domain.getStats().getEventApiCount(),
						TopicUtils.pluralize("Event API",  domain.getStats().getEventApiCount())
						));
				row.setLink(String.format(TopicUtils.DOMAIN_URL, domain.getId()));
				row.setLastUpdatedTs(TimeUtils.parseTime(domain.getUpdatedTime()));
				row.setLastUpdatedByUser(domain.getChangedBy());
				row.setCreatedByUser(domain.getCreatedBy());

				for (EventApiProduct apiProduct : EventPortalWrapper.INSTANCE.getEventApiProductsForDomainId(domain.getId())) {
					generateTree(row, apiProduct, domain, false);
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
			for (EventApiProduct product : EventPortalWrapper.INSTANCE.getEventApiProducts()) {
				generateTree(root, product, EventPortalWrapper.INSTANCE.getDomain(product.getApplicationDomainId()), true);
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
	

	
	private PortalRowObjectTreeNode buildEventApiVerNode(Icon icon, EventApiVersion apiVer, ApplicationDomain domain) {
		
		EventApi api = EventPortalWrapper.INSTANCE.getEventApi(apiVer.getEventApiId());
		ApplicationDomain origDomain = EventPortalWrapper.INSTANCE.getDomain(api.getApplicationDomainId());
		PortalRowObjectTreeNode proApiVer = new PortalRowObjectTreeNode(EventPortalObjectType.EVENT_API_VERSION, apiVer.getId());
		// is there a schema for this Event?
		proApiVer.setIcon(icon);
		proApiVer.setName(String.format("%s%s%s v%s",
				origDomain.getId().equals(domain.getId()) ? "" : "(EXT) ",
				api.getName(),
				api.isShared() ? "*" : "",
				apiVer.getVersion()));
		proApiVer.setState(EventPortalWrapper.INSTANCE.getState(apiVer.getStateId()).getName());
//		eventVerPro.setDetails(TopicUtils.buildTopic(apiVer.getDeliveryDescriptor()));
		
/*		eventVerPro.setLink(String.format(TopicUtils.EVENT_VER_URL, origDomain.getId(), event.getId(), apiVer.getId()));
		eventVerPro.setLastUpdated(TimeDeltaUtils.formatTime(apiVer.getUpdatedTime()));
		eventVerPro.setLastUpdatedByUser(apiVer.getChangedBy());
		eventVerPro.setCreatedByUser(apiVer.getCreatedBy());
		String broker = TopicUtils.capitalFirst(apiVer.getDeliveryDescriptor().getBrokerType());
		if (broker == null) broker = "N/A";
		eventVerPro.addNote(String.format("%s Message", TopicUtils.capitalFirst(broker)));
		if (EventPortalWrapper.INSTANCE.getSchemaVersion(apiVer.getSchemaVersionId()) != null) {
			SchemaVersion schemaVersion = EventPortalWrapper.INSTANCE.getSchemaVersion(apiVer.getSchemaVersionId());
			SchemaObject schema = EventPortalWrapper.INSTANCE.getSchema(schemaVersion.getSchemaId());
//			row.addNote(TopicUtils.capitalFirst(schema.getContentType()) + " Payload");
			eventVerPro.addNote(String.format("%s Payload", TopicUtils.capitalFirst(schema.getContentType())));
		} else if (apiVer.getSchemaPrimitiveType() != null) {
			eventVerPro.addNote(String.format("%s Payload", TopicUtils.capitalFirst(apiVer.getSchemaPrimitiveType().toString())));
		} else {
			eventVerPro.addNote("No Schema");
		}
*/		return proApiVer;	
	}


	private void generateTree(PortalRowObjectTreeNode parent, EventApiProduct product, ApplicationDomain domain, boolean domainInNotes) {
//		List<PortalRowObject> rows = new ArrayList<>();
		PortalRowObjectTreeNode proProduct = new PortalRowObjectTreeNode(EventPortalObjectType.EVENT_API_PRODUCT, product.getId());
		parent.addChild(proProduct);
		proProduct.setIcon(MyIcons.ApiProductLarge);
		proProduct.setName(product.getName() + (product.isShared() ? "*" : ""));
		if (product.isShared()) proProduct.addNote("Shared");
		proProduct.addNote(String.format("%s Event API Product",
				TopicUtils.capitalFirst(product.getBrokerType().getValue())));
		proProduct.addNote(String.format("%d %s",
				product.getNumberOfVersions(),
				TopicUtils.pluralize("Version",  product.getNumberOfVersions())));
		if (domainInNotes) proProduct.addNote("Domain: " + domain.getName());
		proProduct.setLink(String.format(TopicUtils.EVENT_API_PRODUCT_URL, domain.getId(), product.getId()));
		proProduct.setLastUpdatedTs(TimeUtils.parseTime(product.getUpdatedTime()));
		proProduct.setLastUpdatedByUser(product.getChangedBy());
		proProduct.setCreatedByUser(product.getCreatedBy());
		
		
		
		for (EventApiProductVersion productVersion : EventPortalWrapper.INSTANCE.getEventApiProductVersionsForEventApiProductId(product.getId())) {
//			appVer.get
			PortalRowObjectTreeNode proProductVer = new PortalRowObjectTreeNode(EventPortalObjectType.EVENT_API_PRODUCT_VERSION, productVersion.getId());
			proProduct.addChild(proProductVer);
			proProductVer.setIcon(MyIcons.apiProductSmall);
			proProductVer.setName("v" + productVersion.getVersion());
			
			if (productVersion.getEventApiVersionIds().size() == 0) {
				proProductVer.addNote("0 Event APIs referenced");
			} else {
				proProductVer.addNote(String.format("%d %s referenced",
						productVersion.getEventApiVersionIds().size(),
						TopicUtils.pluralize("Event API", productVersion.getEventApiVersionIds().size())));
			}
			proProductVer.setState(EventPortalWrapper.INSTANCE.getState(productVersion.getStateId()).getName());
			proProductVer.setLink(String.format(TopicUtils.EVENT_API_PRODUCT_VER_URL, domain.getId(), product.getId(), productVersion.getId()));
			proProductVer.setLastUpdatedTs(TimeUtils.parseTime(productVersion.getUpdatedTime()));
			proProductVer.setLastUpdatedByUser(productVersion.getChangedBy());
			proProductVer.setCreatedByUser(productVersion.getCreatedBy());
			
			SolaceMessagingService msgSvc = productVersion.getSolaceMessagingService();
//			msgSvc.get
			StringBuilder sb = new StringBuilder();
			for (Plan plan : productVersion.getPlans()) {
				sb.append(plan.getName()).append(" Plan, ");
//				plan.getSolaceClassOfServicePolicy().isGuaranteedMessaging() // null
			}
			proProductVer.setDetails(sb.toString());
			
			for (String apiVerId : productVersion.getEventApiVersionIds()) {  // for all EventVersions this AppVer produces...
				EventApiVersion apiVer = EventPortalWrapper.INSTANCE.getEventApiVersion(apiVerId);
				proProductVer.addChild(buildEventApiVerNode(MyIcons.apiSmall, apiVer, domain));
			}
		}
	}
}

// -Dide.ui.scale=1.5