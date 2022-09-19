package community.solace.ep.idea.plugin.utils;

public class WordyUtils {

	public static String isPlural(int count) {
		return count == 1 ? "" : "s";
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

	/**
	 * 
	 * @param s
	 * @return null if s is null; empty string if s is empty
	 */
	public static String capitalFirst(String s) {
		if (s == null) return null;
		if (s.isEmpty()) return "";
		String[] words = s.split(" ");
		StringBuilder sb = new StringBuilder();
		for (String word : words) {
			sb.append(word.substring(0,1).toUpperCase()).append(word.substring(1).toLowerCase()).append(' ');
		}
		return sb.toString().trim();
	}

	public static String capitalOnlyFirst(String s) {
		if (s == null) return null;
		if (s.isEmpty()) return "";
		return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
	}

}
