package com.solace.aaron.ideaplugin1.test;

import java.util.HashMap;
import java.util.Map;

public class TopicDispatchTest {

	public interface MyCallback {
		public void onMessage(Object message);
	}
	
	
	
	private Map<String,MyCallback> listOfCallbacks = new HashMap<>();	

	
	private String convertTopicSubToRegex(String topicSubscription) {
		
		int multiline = topicSubscription.indexOf('>');
		if (multiline != -1 && multiline != topicSubscription.length()-1) {
			throw new IllegalArgumentException("multi line in wrong place");
		}
		
		boolean trailingMultiLine = topicSubscription.charAt(topicSubscription.length()-1) == '>';
		if (trailingMultiLine) {
			
		}
		if (topicSubscription.matches(topicSubscription)) {
			
		}
		
		
		
		return "";
	}
	
	public void register(String topicSubscription, MyCallback callback) {
		
		
		
	}
	
}
