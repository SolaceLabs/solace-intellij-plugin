package com.solace.aaron.ideaplugin1.nextgen;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import com.intellij.icons.AllIcons;

import community.solace.ep.wrapper.EventPortalObjectType;
import community.solace.ep.wrapper.EventPortalWrapper;

public class PortalRowObjectTreeNode {

//    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_INSTANT;
//    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ss.AAAZ");
    /**
     *     {
     *             "createdTime": "2022-07-12T20:07:57.808Z",
     *             "updatedTime": "2022-07-12T20:07:57.808Z",
     *             "createdBy": "67tr8tku41",
     *             "changedBy": "67tr8tku41",
     *             "id": "x4oo4skfh5e",
     *             "name": "Aaron Test 1",
     *             "description": "this is a test",
     *             "uniqueTopicAddressEnforcementEnabled": true,
     *             "topicDomainEnforcementEnabled": false,
     *             "type": "applicationDomain"
     *         },
     */

	
	
	final EventPortalObjectType type;
	final String id;
	String name = "";
	
    String link = "";
//    String type = "";
    String state = "";
    List<String> notes = new ArrayList<>();
    String details = "";
    String lastUpdated = "";
    String lastUpdatedByUserId = "";
    String createdByUserId = "";
//    Object swaggerObject = null;
    Icon icon = AllIcons.Actions.Cancel;
    PortalRowObjectTreeNode parent = null;
    List<PortalRowObjectTreeNode> children = null;
    private int depth = 0;
    volatile boolean expanded = true;
    volatile boolean hidden = false;

    public PortalRowObjectTreeNode(EventPortalObjectType type, String id) {
    	this.type = type;
    	this.id = id;
    }
    
    public String getId() {
    	return id;
    }
    
   
    public int getDepth() {
    	return depth;
    }
    
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public EventPortalObjectType getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public List<String> getNotes() {
		return notes;
	}
	public void addNote(String note) {
		this.notes.add(note);
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public String getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getLastUpdatedByUserId() {
		return lastUpdatedByUserId;
	}
	public void setLastUpdatedByUser(String userId) {
		this.lastUpdatedByUserId = userId;
	}
	public String getCreatedByUserId() {
		return createdByUserId;
	}
	public void setCreatedByUser(String userId) {
		this.createdByUserId = userId;
	}
	public Icon getIcon() {
		return this.icon;
	}
	public void setIcon(Icon icon) {
		this.icon = icon;
	}
	
	/** Iterative method to walk the tree and add */
	public List<PortalRowObjectTreeNode> flatten() {
		assert parent == null;  // I'm the root, right?
		List<PortalRowObjectTreeNode> rows = new ArrayList<>();
		List<PortalRowObjectTreeNode> stack = new ArrayList<>();
		stack.addAll(children);  // should be domains usually
		while (!stack.isEmpty()) {
			PortalRowObjectTreeNode node = stack.remove(0);
			if (!hidden) rows.add(node);
			if (node.isExpanded() && node.hasChildren()) {
				stack.addAll(0, node.children);
			}
		}
		return rows;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<(10*depth); i++) {
			sb.append(" ");
		}
		sb.append(String.format("'%s' %s", name, type.toString()));
		
		return sb.toString();
	}
	

	
	public boolean hasChildren() {
		return this.children != null && !this.children.isEmpty();
	}
	
	public void addChild(PortalRowObjectTreeNode child) {
		if (this.children == null) this.children = new ArrayList<>();
		this.children.add(child);
		child.parent = this;
		child.depth = depth+1;
		child.expanded = false;  // collapsed
	}

	
	public boolean isExpanded() {
		return expanded;
	}
	
	public void collapse() {
		if (hasChildren()) {
			for (PortalRowObjectTreeNode child : children) {
				if (child.expanded) child.collapse();
			}
		}
		expanded = false;
	}
	
	public void expandOnce() {
		expanded = true;  // not children
	}
	
	public void expandAll() {
		if (hasChildren()) {
			for (PortalRowObjectTreeNode child : children) {
				if (!child.expanded) child.expandAll();
			}
		}
		expanded = true;
	}
	
	public void expandNext() {
		if (hasChildren()) {
			for (PortalRowObjectTreeNode child : children) {
				if (child.expanded) child.expandNext();
				child.expanded = true;
			}
		}
		expanded = true;
	}


	public PortalRowObjectTreeNode getRoot() {
		if (parent != null) return parent.getRoot();
		return this;
	}
	
}
