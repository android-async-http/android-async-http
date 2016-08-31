package com.loopj.android.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;

import android.os.Message;


public class FileAsyncHttpResponseHandler 		extends AsyncHttpResponseHandler {
	
	private File			mFile;
	
	public FileAsyncHttpResponseHandler(File file) {
		super();
		this.mFile = file;
	}
	
    public void onSuccess(File file) { }
    public void onSuccess(int statusCode, File file) {
        onSuccess(file);
    }
    
    public void onFailure(Throwable e, File response) { }

	
	protected void sendSuccessMessage(int statusCode, File file) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{statusCode, file}));
    }

    protected void sendFailureMessage(Throwable e, File file) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, file}));
    }
    
    protected void handleSuccessMessage(int statusCode, File responseBody) {
        onSuccess(statusCode, responseBody);
    }

    protected void handleFailureMessage(Throwable e, File responseBody) {
        onFailure(e, responseBody);
    }

 // Methods which emulate android's Handler and Message methods
    protected void handleMessage(Message msg) {
        Object[] response;
        switch(msg.what) {
            case SUCCESS_MESSAGE:
                response = (Object[])msg.obj;
                handleSuccessMessage(((Integer) response[0]).intValue() , (File)response[1]);
                break;
            case FAILURE_MESSAGE:
                response = (Object[])msg.obj;
                handleFailureMessage((Throwable)response[0], (File)response[1]);
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }
    
	@Override
    void sendResponseMessage(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        
        try {
	        FileOutputStream	buffer = new FileOutputStream(this.mFile);
	        InputStream			is = response.getEntity().getContent();

	        int					nRead;
	        byte[]				data = new byte[16384];

	        while ((nRead = is.read(data, 0, data.length)) != -1)
	          buffer.write(data, 0, nRead);

	        buffer.flush();
	        buffer.close();

        } catch(IOException e) {
            sendFailureMessage(e, this.mFile);
        }

        if (status.getStatusCode() >= 300) {
            sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), this.mFile);
        } else {
            sendSuccessMessage(status.getStatusCode(), this.mFile);
        }
    }
}
