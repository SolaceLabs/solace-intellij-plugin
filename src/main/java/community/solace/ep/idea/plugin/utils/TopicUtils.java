package community.solace.ep.idea.plugin.utils;

import community.solace.ep.client.model.Address;
import community.solace.ep.client.model.AddressLevel;
import community.solace.ep.client.model.DeliveryDescriptor;

public class TopicUtils {
	
	public static String BASE_URL = "solace-sso.solace.cloud";
	
	public static final String DOMAIN_URL = "https://solace-sso.solace.cloud/ep/designer/domains/%s/applications";
	public static final String APP_URL = "https://solace-sso.solace.cloud/ep/designer/domains/%s/applications?selectedId=%s";
    public static final String APP_VER_URL = "https://solace-sso.solace.cloud/ep/designer/domains/%s/applications/%s?selectedVersionId=%s";
    public static final String EVENT_URL = "https://solace-sso.solace.cloud/ep/designer/domains/%s/events?selectedId=%s";
    public static final String EVENT_VER_URL = "https://solace-sso.solace.cloud/ep/designer/domains/%s/events/%s?selectedVersionId=%s";
    public static final String SCHEMA_URL = "https://solace-sso.solace.cloud/ep/designer/domains/%s/schemas?selectedId=%s";
    public static final String SCHEMA_VER_URL = "https://solace-sso.solace.cloud/ep/designer/domains/%s/schemas/%s?selectedVersionId=%s";
    public static final String EVENT_API_URL = "https://solace-sso.solace.cloud/ep/designer/domains/%s/eventApis?selectedId=%s";
    public static final String EVENT_API_VER_URL = "https://solace-sso.solace.cloud/ep/designer/domains/%s/eventApis/%s?selectedVersionId=%s";
    public static final String EVENT_API_PRODUCT_URL = "https://solace-sso.solace.cloud/ep/designer/domains/%s/eventApiProducts?selectedId=%s";
    public static final String EVENT_API_PRODUCT_VER_URL = "https://solace-sso.solace.cloud/ep/designer/domains/%s/eventApiProducts/%s?selectedVersionId=%s";


    public static String buildTopic(DeliveryDescriptor dd) {
        char levelSeparator = '/';  // default
        if ("kafka".equals(dd.getBrokerType())) levelSeparator = '.';  // could be null
        StringBuilder sb = new StringBuilder("<html><nobr>");
        Address a = dd.getAddress();
        if (a == null) return "";  // no address yet defined
        for (AddressLevel level : a.getAddressLevels()) {
            if (level.getAddressLevelType() == AddressLevel.AddressLevelTypeEnum.LITERAL) {
                sb.append(level.getName());
            } else {
                sb.append("<i>{").append(level.getName()).append("}</i>");
            }
            sb.append(levelSeparator);
        }
        return sb.deleteCharAt(sb.length()-1).append("</nobr></html>").toString();
    }

    /**
     * 
     * @param s
     * @return null if s is null; empty string if s is empty
     */
    public static String capitalFirst(String s) {
    	if (s == null) return null;
    	if (s.isEmpty()) return "";
    	StringBuilder sb = new StringBuilder();
    	sb.append(s.substring(0,1).toUpperCase()).append(s.substring(1));
    	return sb.toString();
    }
    
    /**
     * Only works with words where adding an 's' is all that's needed. Adds s unless count == 1.
     * 0 events, 1 event, 2 events, 3 events...
     * @param string The word to pluralize, no s
     * @param count null if string is null
     * @return
     */
    public static String pluralize(String string, int count) {
    	if (string == null) return null;
    	if (count == 1) return string;
    	return new StringBuilder(string).append("s").toString();
    }
    
    public static String isPlural(int count) {
    	return count == 1 ? "" : "s";
    }
    
    public static void main(String... args) {
    	
    	System.out.println(capitalFirst("s"));
    	
    }
    
    
}
