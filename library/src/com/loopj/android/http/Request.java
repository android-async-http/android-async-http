package com.loopj.android.http;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Request {
	
	private String mRequestMethod;
	private Map<String, String> mHeaders;
	private URL mUrl;
	
	public Request(URL url) {
		mHeaders = new HashMap<String, String>();
		mRequestMethod = "GET";
		mUrl = url;
	}
	
	public Request() {
		mHeaders = new HashMap<String, String>();
		mRequestMethod = "GET";
	}
	
	public void setUrl(URL url) {
		mUrl = url;
	}
	
	public URL getURL() {
		return mUrl;
	}
	
	public void setRequestMethod(String requestMethod) {
		mRequestMethod = requestMethod;
	}
	
	public void addHeader(String key, String value) {
		mHeaders.put(key, value);
	}
	
	public Map<String, String> getHeaders() {
		return mHeaders;
	}
	
	public String getRequestMethod() {
		return mRequestMethod;
	}
	
}