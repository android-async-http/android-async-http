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
	 * Attempts to cancel this request. This attempt will fail if the request has
	 * already completed, has already been cancelled, or could not be cancelled
	 * for some other reason. If successful, and this request has not started
	 * when cancel is called, this request should never run. If the request has
	 * already started, then the mayInterruptIfRunning parameter determines
	 * whether the thread executing this request should be interrupted in an
	 * attempt to stop the request.
	 * 
	 * After this method returns, subsequent calls to isDone() will always
	 * return true. Subsequent calls to isCancelled() will always return true
	 * if this method returned true.
	 * 
	 * @param mayInterruptIfRunning true if the thread executing this request should be interrupted; otherwise, in-progress requests are allowed to complete
	 * @return false if the request could not be cancelled, typically because it has already completed normally; true otherwise
	 */
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (this.request == null) {
			return false;
		}
		return request.cancel(mayInterruptIfRunning);
	}
	
	/**
	 * Returns true if this task completed. Completion may be due to normal termination, an exception, or cancellation -- in all of these cases, this method will return true.
	 * @return true if this task completed
	 */
	public boolean isFinished() {
		if (this.request == null) {
			return true;
		}
		return request.isDone();
	}
	
	/**
	 * Returns true if this task was cancelled before it completed normally.
	 * @return true if this task was cancelled before it completed
	 */
	public boolean isCancelled() {
		if (this.request == null) {
			return false;
		}
		return request.isCancelled();
	}
}