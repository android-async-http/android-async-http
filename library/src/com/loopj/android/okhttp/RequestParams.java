package com.loopj.android.okhttp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestParams {
	
	private final ConcurrentHashMap<String, String> params;
	
	public RequestParams() {
		params = new ConcurrentHashMap<String, String>();
	}
	
	public void put(Map<String, String> source) {
		for(Map.Entry<String, String> entry : source.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
	
	public void put(String key, String value) {
		if(key != null && value != null) {
			params.put(key, value);
		}
	}
	
	String getParams() {
		StringBuilder result = new StringBuilder();
		for(ConcurrentHashMap.Entry<String, String> entry : params.entrySet()) {
			if(result.length() > 0) {
				result.append("&");
			}
			result.append(entry.getKey());
			result.append("=");
			result.append(entry.getValue());
		}
		return result.toString();
	}
	
	@Override
	public String toString() {
		return getParams();
	}
	
}