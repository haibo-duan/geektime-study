package com.dhb.hazelcast.demo.bean;

public class CommandResponse {
	
	private String value;

	public CommandResponse(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
