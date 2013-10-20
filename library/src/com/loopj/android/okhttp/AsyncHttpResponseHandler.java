package com.loopj.android.okhttp;

import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.http.client.HttpResponseException;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class AsyncHttpResponseHandler implements Handler.Callback {
	
	protected static final int SUCCESS = 0;
	protected static final int FAIL = 1;
	protected static final int START = 2;
	protected static final int FINISH = 3;
	
	private Handler mHandler;
	
	public AsyncHttpResponseHandler() {
		if(Looper.myLooper() != null) {
			mHandler = new Handler(this);
		}
	}
	
	public void onStart() { }
	
	public void onFinish() { }
	
	public void onSuccess(int statusCode, String content) { }
	
	public void onError(Throwable error, String content) { }
	
	protected void sendSuccessMessage(int statusCode, String responseBody) {
		sendMessage(obtainMessage(SUCCESS, new Object[] {Integer.valueOf(statusCode), responseBody}));
	}
	
	protected void sendFailMessage(Throwable error, String content) {
		sendMessage(obtainMessage(FAIL, new Object[] {error, content}));
	}
	
	protected void sendStartMessage() {
		sendMessage(obtainMessage(START, null));
	}
	
	protected void sendEndMessage() {
		sendMessage(obtainMessage(FINISH, null));
	}
	
	protected void handleSuccessMessage(int statusCode, String responseBody) {
		onSuccess(statusCode, responseBody);
	}
	
	protected void handleFailMessage(Throwable error, String responseBody) {
		onError(error, responseBody);
	}
	
	@Override
	public boolean handleMessage(Message message) {
		switch(message.what) {
			case START:
				onStart();
				return true;
			case FINISH:
				onFinish();
				return true;
			case SUCCESS:
				Object[] successResponse = (Object[])message.obj;
				handleSuccessMessage(((Integer) successResponse[0]).intValue(), (String) successResponse[1]);
				return true;
			case FAIL:
				Object[] failResponse = (Object[])message.obj;
				handleFailMessage((Throwable)failResponse[0], (String) failResponse[1]);
				return true;
		}
		return false;
	}
	
	protected void sendMessage(Message message) {
		if(mHandler != null) {
			mHandler.sendMessage(message);
		} else {
			handleMessage(message);
		}
	}
	
	protected Message obtainMessage(int responseMessage, Object response) {
		Message message = null;
		if(mHandler != null) {
			message = mHandler.obtainMessage(responseMessage, response);
		} else {
			message = Message.obtain();
			message.what = responseMessage;
			message.obj = response;
		}
		return message;
	}
	
	void sendResponseMessage(HttpURLConnection connection) {
		String responseBody = null;
		InputStream response = null;
		try {
			response = connection.getInputStream();
			if(response != null) {
				responseBody = Util.inputStreamToString(response);
			}
			final int responseCode = connection.getResponseCode();
			if(responseCode >= 300) {
				sendFailMessage(new HttpResponseException(responseCode, 
						connection.getResponseMessage()), responseBody);
			} else {
				sendSuccessMessage(responseCode, responseBody);
			}
		} catch(Exception e) {
			sendFailMessage(e, null);
		} finally {
			if(response != null) {
				Util.closeQuietly(response);
			}
		}
	}
	
}