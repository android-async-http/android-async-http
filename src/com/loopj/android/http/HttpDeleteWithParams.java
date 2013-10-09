/*
 * @creator Storm
 * @created_at Sep 28, 2013 10:05:34 AM
 */

package com.loopj.http;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class HttpDeleteWithParams extends HttpEntityEnclosingRequestBase {
	public static final String METHOD_NAME = "DELETE";

	public HttpDeleteWithParams() {
		super();
	}
	
	public HttpDeleteWithParams(final String uri) {
        super();
        setURI(URI.create(uri));
    }
    public HttpDeleteWithParams(final URI uri) {
        super();
        setURI(uri);
    }

	@Override
	public String getMethod() {
		return METHOD_NAME;
	}
}
