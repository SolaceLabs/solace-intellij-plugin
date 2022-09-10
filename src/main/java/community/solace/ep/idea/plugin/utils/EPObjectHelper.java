package community.solace.ep.idea.plugin.utils;

import java.awt.Color;

import community.solace.ep.wrapper.EventPortalObjectType;

public class EPObjectHelper {

	
	public static Color getColor(EventPortalObjectType type) {
		switch (type) {
		case DOMAIN:
			return Color.decode("#9aa7b0");
		case APPLICATION:
		case APPLICATION_VERSION:
			return Color.decode("#40b6e0");
		case EVENT:
		case EVENT_VERSION:
			return Color.decode("#00c895");
		case SCHEMA:
		case SCHEMA_VERSION:
			return Color.decode("#b99bf8");
		case ENUM:
		case ENUM_VERSION:
			return Color.RED;
		case EVENT_API:
		case EVENT_API_VERSION:
			return Color.decode("#f4af3d");
		case EVENT_API_PRODUCT:
		case EVENT_API_PRODUCT_VERSION:
			return Color.decode("#F98B9E");
		default: 
			return Color.RED;
		}
	}
	
	public static String getName(EventPortalObjectType type) {
//		switch (type) {
//		case DOMAIN: return "Domain";
//		case APPLICATION: return "Application";
//		case APPLICATION_VERSION: return "App Version";
//		case EVENT: return "Event";
//		case EVENT_VERSION: return "Event Ver";
//		case SCHEMA: return "Schema";
//		case SCHEMA_VERSION: return "Schema Ver";
//		case ENUM: return "Enum";
//		case ENUM_VERSION: return "Enum Ver";
//		case EVENT_API: return "Event API";
//		case EVENT_API_VERSION: return "Event API Ver";
//		case EVENT_API_PRODUCT: return "Event API Product";
//		case EVENT_API_PRODUCT_VERSION: return "Event API Product Ver";
//		default: return "--DEFAULT--";
//		}
		switch (type) {
		case DOMAIN: return "Domain";
		case APPLICATION: return "App";
		case APPLICATION_VERSION: return "vApp";
		case EVENT: return "Event";
		case EVENT_VERSION: return "vEvent";
		case SCHEMA: return "Schema";
		case SCHEMA_VERSION: return "vSchema";
		case ENUM: return "Enum";
		case ENUM_VERSION: return "vEnum";
		case EVENT_API: return "Event API";
		case EVENT_API_VERSION: return "vAPI";
		case EVENT_API_PRODUCT: return "Event API Product";
		case EVENT_API_PRODUCT_VERSION: return "vProduct";
		default: return "--DEFAULT--";
		}
	}
	
	
	
	
	
	
}
