package com.loopj.android.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class FileAsyncHttpResponseHandler extends AsyncHttpResponseHandler {

    private File mFile;

    public FileAsyncHttpResponseHandler(File file) {
        super();
        this.mFile = file;
    }

    public void onSuccess(File file) {
    }

    public void onSuccess(int statusCode, File file) {
        onSuccess(file);
    }

    public void onFailure(Throwable e, File response) {
        // By default call lower chain method
        onFailure(e);
    }

    public void onFailure(int statusCode, Throwable e, File response) {
        // By default call lower chain method
        onFailure(e, response);
    }

    public void onFailure(int statusCode, Header[] headers, Throwable e, File response) {
        // By default call lower chain method
        onFailure(statusCode, e, response);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        onFailure(statusCode, headers, error, mFile);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        onSuccess(statusCode, mFile);
    }

    @Override
    byte[] getResponseData(HttpEntity entity) throws IOException {
      if (entity != null) {
          InputStream instream = entity.getContent();
          long contentLength = entity.getContentLength();
          FileOutputStream buffer = new FileOutputStream(this.mFile);
          if (instream != null) {
              try {
                  byte[] tmp = new byte[BUFFER_SIZE];
                  int l, count = 0;
                  // do not send messages if request has been cancelled
                  while ((l = instream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                      count += l;
                      buffer.write(tmp, 0, l);
                      sendProgressMessage(count, (int) contentLength);
                  }
              } finally {
                  instream.close();
                  buffer.flush();
                  buffer.close();
              }
          }
      }
      return null;
  }
  
}
