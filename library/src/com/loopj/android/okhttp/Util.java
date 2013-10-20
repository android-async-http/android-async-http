package com.loopj.android.okhttp;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

final class Util {
	
	private Util() { }
	
	public static String inputStreamToString(InputStream source) throws Exception {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(source));
	    StringBuilder out = new StringBuilder();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        out.append(line);
	    }
	    return out.toString();
	}
	
	public static String getUrlWithQueryString(String url, RequestParams requestParams) {
		if(requestParams != null) {
			String paramUrl = requestParams.toString();
			if(url.indexOf('?') == -1) {
				url += "?"+paramUrl;
			} else {
				url += "&"+paramUrl;
			}
		}
		return url;
	}
	
	public static void closeQuietly(Closeable closeable) {
		try {
			closeable.close();
		} catch(IOException ignored) { }
	}
	
}