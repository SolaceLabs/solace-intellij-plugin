package com.solace.aaron.ideaplugin1.nextgen;

import community.solace.ep.wrapper.EventPortalObjectType;

public class PortalRowObject {

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

    String link = "";
    String type = "";
    String name = "";
    String state = "";
    String notes = "";
    String details = "";
    String lastUpdated = "";
    EventPortalObjectType typeEnum;
    
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public EventPortalObjectType getTypeEnum() {
		return typeEnum;
	}
	public void setTypeEnum(EventPortalObjectType typeEnum) {
		this.typeEnum = typeEnum;
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
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
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


}
