package com.xzymon.jee7.eden.web;

public enum RequestAttributeName {
	FEEDER_BEAN("FeederBean");

	private String name;

	RequestAttributeName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
