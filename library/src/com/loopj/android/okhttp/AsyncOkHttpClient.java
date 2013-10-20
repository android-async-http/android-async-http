package com.loopj.android.okhttp;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.squareup.okhttp.OkHttpClient;

public class AsyncOkHttpClient {
	
	private ThreadPoolExecutor mThreadPool;
	private Request mRequest;
	private OkHttpClient mClient;
	
	public AsyncOkHttpClient() {
		mThreadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		mRequest = new Request();
		mClient = new OkHttpClient();
	}
	
	public OkHttpClient getOkHttpClient() {
		return mClient;
	}
	
	public void addHeader(String key, String value) {
		mRequest.addHeader(key, value);
	}
	
	public void setThreadPool(ThreadPoolExecutor threadPool) {
		mThreadPool = threadPool;
	}
	
	protected void sendRequest(OkHttpClient client, String url, AsyncHttpResponseHandler response, 
			String contentType, RequestParams params) {
		try {
			mRequest.setUrl(new URL(url));
			HttpURLConnection conn = client.open(this.mRequest.getURL());
			if(contentType != null) {
				conn.setRequestProperty("Content-Type", contentType);
			}
			mThreadPool.submit(new AsyncHttpRequest(conn, response, params, mRequest));
		} catch(MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public void get(String url, String contentType, AsyncHttpResponseHandler response) {
		get(url, contentType, null, response);
	}
	
	public void get(String url, AsyncHttpResponseHandler response) {
		get(url, null, null, response);
	}
	
	public void get(String url, RequestParams params, AsyncHttpResponseHandler response) {
		get(url, null, params, response);
	}
	
	public void get(String url, String contentType, RequestParams params, AsyncHttpResponseHandler response) {
		mRequest.setRequestMethod("GET");
		sendRequest(mClient, Util.getUrlWithQueryString(url, params), response, contentType, params);
	}
	
	public void post(String url, String contentType, AsyncHttpResponseHandler response) {
		this.post(url, contentType, null, response);
	}
	
	public void post(String url, AsyncHttpResponseHandler response) {
		this.post(url, null, null, response);
	}
	
	public void post(String url, RequestParams params, AsyncHttpResponseHandler response) {
		post(url, null, params, response);
	}
	
	public void post(String url, String contentType, RequestParams params, AsyncHttpResponseHandler response) {
		mRequest.setRequestMethod("POST");
		sendRequest(this.mClient, url, response, contentType, params);
	}
	
	public void put(String url, String contentType, RequestParams params, AsyncHttpResponseHandler response) {
		mRequest.setRequestMethod("PUT");
		sendRequest(mClient, url, response, contentType, params);
	}
	
	public void put(String url, AsyncHttpResponseHandler response) {
		put(url, null, null, response);
	}
	
	public void put(String url, String contentType, AsyncHttpResponseHandler response) {
		put(url, contentType, null, response);
	}
	
	public void put(String url, RequestParams params, AsyncHttpResponseHandler response) {
		put(url, null, params, response);
	}
	
	public void delete(String url, String contentType, RequestParams params, AsyncHttpResponseHandler response) {
		mRequest.setRequestMethod("DELETE");
		sendRequest(mClient, url, response, contentType, params);
	}
	
	public void delete(String url, AsyncHttpResponseHandler response) {
		delete(url, null, null, response);
	}
	
	public void delete(String url, String contentType, AsyncHttpResponseHandler response) {
		delete(url, contentType, null, response);
	}
	
	public void delete(String url, RequestParams params, AsyncHttpResponseHandler response) {
		delete(url, null, params, response);
	}
	
}