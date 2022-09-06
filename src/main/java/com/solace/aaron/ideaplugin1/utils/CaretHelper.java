package com.solace.aaron.ideaplugin1.utils;

public enum CaretHelper {
	PUB("Pub'ing", "◑ "),
	SUB("Sub'ing", "◐ "),
	BOTH("Pub/Sub", "◉ "),
	CARET("", "▶ ")
	;
	
	public final String name;
	public final String icon;
	
	CaretHelper(String name, String icon) {
		this.name = name;
		this.icon = icon;
	}
}

