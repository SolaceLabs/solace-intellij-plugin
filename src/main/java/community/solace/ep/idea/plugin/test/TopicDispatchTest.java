package community.solace.ep.idea.plugin.test;

import java.util.HashMap;
import java.util.Map;

public class TopicDispatchTest {

	public interface MyCallback {
		public void onMessage(Object message);
	}
	
	
	
	private Map<String,MyCallback> listOfCallbacks = new HashMap<>();	

	
	private static String convertTopicSubToRegex(String topicSubscription) {
		
		// basic checks first
		for (int i=0; i<topicSubscription.length(); i++) {
			char c = topicSubscription.charAt(i);
			if (c == '*') {
				if (i < topicSubscription.length()-1 && topicSubscription.charAt(i+1) != '/')
					throw new IllegalArgumentException("Invalid subscription, * in wrong place");
			} else if (c == '>') {
				if (i < topicSubscription.length()-1)
					throw new IllegalArgumentException("Invalid subscription, > must be at end");
			} else if (c == '/') {
				if (i == 0 || i == topicSubscription.length()-1 || topicSubscription.charAt(i+1) == '/')
					throw new IllegalArgumentException("Invalid subscription, shouldn't have empty levels");
			}
		}
		
		String[] levels = topicSubscription.split("/",-1);
		
		
		
		
		return "";
	}
	
	public void register(String topicSubscription, MyCallback callback) {
		
		
		
	}
	
	
	public static void main(String... args) {
		
		convertTopicSubToRegex("a/b/c");
		convertTopicSubToRegex("a/b/*");
		convertTopicSubToRegex("a/*/c");
		convertTopicSubToRegex("a/b*/c");
		try {
			convertTopicSubToRegex("*a/b/c");
			throw new AssertionError();
		} catch (RuntimeException e) { }
		try {
			convertTopicSubToRegex("a/b//c");
			throw new AssertionError();
		} catch (RuntimeException e) { }
		convertTopicSubToRegex("a/*/>");

		
		
		
	}
	
	
}
