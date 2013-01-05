package com.loopj.android.http;

import java.util.concurrent.Future;

/**
 * A Handle to an AsyncRequest which can be used to cancel a running request.
 * 
 */
public class RequestHandle {
	private final Future<?> request;
	
	public RequestHandle(Future<?> request) {
		this.request = request;
	}
	
	/**
	 * Cancels the request
	 */
	public void cancel() {
		request.cancel(true);
	}
	
	public boolean isFinished() {
		return request.isDone();
	}
	
	public boolean isCancelled() {
		return request.isCancelled();
	}
}
