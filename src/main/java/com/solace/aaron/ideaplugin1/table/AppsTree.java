package com.solace.aaron.ideaplugin1.table;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ListSelectionModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.intellij.ui.treeStructure.Tree;

import community.solace.ep.client.model.Address;
import community.solace.ep.client.model.AddressLevel;
import community.solace.ep.client.model.Application;
import community.solace.ep.client.model.ApplicationDomain;
import community.solace.ep.client.model.ApplicationVersion;
import community.solace.ep.client.model.DeliveryDescriptor;
import community.solace.ep.client.model.Event;
import community.solace.ep.client.model.EventVersion;
import community.solace.ep.client.model.SchemaObject;
import community.solace.ep.client.model.SchemaVersion;
import community.solace.ep.wrapper.EventPortalWrapper;

public class AppsTree extends Tree {


    public AppsTree(Object data) {
        super();
        this.init();
    }

    public void setEmptyText(String empty) {

    }

    private void init() {
        this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        this.setAutoCreateRowSorter(true);
        this.getEmptyText().setText("No Event Portal Applications Loaded");
        this.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                System.out.println("GOT CLICKED: "+e);
//                JTable source = (JTable) e.getSource();
//                int rowAtPoint = source.rowAtPoint(e.getPoint());
//                int columnAtPoint = source.columnAtPoint(e.getPoint());
//                // Link column = 2
//                if (2 == columnAtPoint && MouseEvent.BUTTON1 == e.getButton()) {
//                    BrowserUtil.browse(ResultsTable.this.getValueAt(rowAtPoint, columnAtPoint).toString());
//                }
                super.mousePressed(e);
            }
        });

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        Tree aTree = new Tree(drawAppsTree());

//        this.se





    }

    public static String makeItalic(String s) {
        return "<html><i>" + s + "</i></html>";
    }

    public static String makeBold(String s) {
        return "<html><b>" + s + "</b></html>";
    }
    
    public static String helperBuildTopic(DeliveryDescriptor dd) {
    	char levelSeparator = '/';  // default
    	if ("kafka".equals(dd.getBrokerType())) levelSeparator = '.';  // could be null
    	StringBuilder sb = new StringBuilder();
    	Address a = dd.getAddress();
    	if (a == null) return "";  // no address yet defined
    	for (AddressLevel level : a.getAddressLevels()) {
    		if (level.getAddressLevelType() == AddressLevel.AddressLevelTypeEnum.LITERAL) {
        		sb.append(level.getName());
    		} else {
        		sb.append("$").append(level.getName());
    		}
    		sb.append(levelSeparator);
    	}
    	return sb.deleteCharAt(sb.length()-1).toString();
    }


    public static MutableTreeNode drawAppsTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        for (ApplicationDomain domain : EventPortalWrapper.INSTANCE.getDomains()) {  // for all domains...
//    		o.printf("DOMAIN '%s': # apps=%d, # events=%d%n", domain.getName(), domain.getStats().getApplicationCount(), domain.getStats().getEventCount());
    		String s = String.format("DOMAIN '%s': # apps=%d, # events=%d%n", domain.getName(), domain.getStats().getApplicationCount(), domain.getStats().getEventCount());
            DefaultMutableTreeNode domainNode = new DefaultMutableTreeNode(s);
            root.add(domainNode);
            for (Application app : EventPortalWrapper.INSTANCE.getApplicationsForDomainId(domain.getId())) {  // otherwise, for all apps...
//    			o.printf(" |-> APP '%s': type=%s, # vers=%d  [%s]%n", app.getName(), app.getApplicationType(), app.getNumberOfVersions(), app.getId());
    			s = String.format("APP '%s': type=%s, # vers=%d  [%s]%n", app.getName(), app.getApplicationType(), app.getNumberOfVersions(), app.getId());
                DefaultMutableTreeNode applicationNode = new DefaultMutableTreeNode(s);
                domainNode.add(applicationNode);
                for (ApplicationVersion appVer : EventPortalWrapper.INSTANCE.getApplicationVersionsForApplicationId(app.getId())) {
    				s = String.format("ver=%s, state=%s, # pub=%d, # sub=%d  [%s]%n",
    						appVer.getVersion(),
    						EventPortalWrapper.INSTANCE.getState(appVer.getStateId()).getName(),
    						appVer.getDeclaredProducedEventVersionIds().size(),
    						appVer.getDeclaredConsumedEventVersionIds().size(),
    						appVer.getId());
                    DefaultMutableTreeNode appVerNode = new DefaultMutableTreeNode(s);
                    applicationNode.add(appVerNode);
                    for (String eventVerId : appVer.getDeclaredProducedEventVersionIds()) {  // for all EventVersions this AppVer produces...
                        EventVersion ev = EventPortalWrapper.INSTANCE.getEventVersion(eventVerId);
                        Event event = EventPortalWrapper.INSTANCE.getEvent(ev.getEventId());
    					ApplicationDomain origDomain = EventPortalWrapper.INSTANCE.getDomain(event.getApplicationDomainId());
                        // is there a schema for this Event?
                        if (EventPortalWrapper.INSTANCE.getSchemaVersion(ev.getSchemaVersionId()) != null) {
                            SchemaVersion schemaVersion = EventPortalWrapper.INSTANCE.getSchemaVersion(ev.getSchemaVersionId());
                            SchemaObject schema = EventPortalWrapper.INSTANCE.getSchema(schemaVersion.getSchemaId());
                            s = String.format("PUB EVENT: '%s'%s, v%s, state=%s, broker=%s, topic='%s', schema='%s', type=%s, schema v%s  [%s]  [%s]%n",
        							event.getName(),
        							origDomain.getId().equals(domain.getId()) ? "" : " (EXT)",
        							ev.getVersion(),
									EventPortalWrapper.INSTANCE.getState(ev.getStateId()).getName(),
									ev.getDeliveryDescriptor().getBrokerType(),
									helperBuildTopic(ev.getDeliveryDescriptor()),
									schema.getName(),
									schema.getSchemaType(),
									schemaVersion.getVersion(),
									eventVerId,
									schemaVersion.getId());
                            DefaultMutableTreeNode eventNode = new DefaultMutableTreeNode(s);
                            appVerNode.add(eventNode);
                        } else {
                            s = String.format("PUB EVENT: '%s'%s, v%s, state=%s, broker=%s, topic='%s', schema=not defined  [%s]%n",
                                    event.getName(),
                                    origDomain.getId().equals(domain.getId()) ? "" : " (EXT)",
                                    ev.getVersion(),
                                    EventPortalWrapper.INSTANCE.getState(ev.getStateId()).getName(),
                                    ev.getDeliveryDescriptor().getBrokerType(),
                                    helperBuildTopic(ev.getDeliveryDescriptor()),
                                    eventVerId);
                            DefaultMutableTreeNode eventNode = new DefaultMutableTreeNode(s);
                            appVerNode.add(eventNode);
                        }
                    }
                    for (String eventVerId : appVer.getDeclaredConsumedEventVersionIds()) {
                        EventVersion ev = EventPortalWrapper.INSTANCE.getEventVersion(eventVerId);
                        Event event = EventPortalWrapper.INSTANCE.getEvent(ev.getEventId());
    					ApplicationDomain origDomain = EventPortalWrapper.INSTANCE.getDomain(event.getApplicationDomainId());
                        // is there a schema for this Event?
                        if (EventPortalWrapper.INSTANCE.getSchemaVersion(ev.getSchemaVersionId()) != null) {
                            SchemaVersion schemaVersion = EventPortalWrapper.INSTANCE.getSchemaVersion(ev.getSchemaVersionId());
                            SchemaObject schema = EventPortalWrapper.INSTANCE.getSchema(schemaVersion.getSchemaId());
                            s = String.format("SUB EVENT: '%s'%s, v%s, state=%s, broker=%s, topic='%s', schema='%s', type=%s, schema v%s  [%s]  [%s]%n",
                                    event.getName(),
                                    origDomain.getId().equals(domain.getId()) ? "" : " (EXT)",
                                    ev.getVersion(),
                                    EventPortalWrapper.INSTANCE.getState(ev.getStateId()).getName(),
                                    ev.getDeliveryDescriptor().getBrokerType(),
                                    helperBuildTopic(ev.getDeliveryDescriptor()),
                                    schema.getName(),
                                    schema.getSchemaType(),
                                    schemaVersion.getVersion(),
                                    eventVerId,
                                    schemaVersion.getId());
                            DefaultMutableTreeNode eventNode = new DefaultMutableTreeNode(s);
                            appVerNode.add(eventNode);
                        } else {
                            s = String.format("SUB EVENT: '%s'%s, v%s, state=%s, broker=%s, topic='%s', schema=not defined  [%s]%n",
                                    event.getName(),
                                    origDomain.getId().equals(domain.getId()) ? "" : " (EXT)",
                                    ev.getVersion(),
                                    EventPortalWrapper.INSTANCE.getState(ev.getStateId()).getName(),
                                    ev.getDeliveryDescriptor().getBrokerType(),
                                    helperBuildTopic(ev.getDeliveryDescriptor()),
                                    eventVerId);
                            DefaultMutableTreeNode eventNode = new DefaultMutableTreeNode(s);
                            appVerNode.add(eventNode);
                        }
                    }
                }
            }
        }
        return root;
    }


}
