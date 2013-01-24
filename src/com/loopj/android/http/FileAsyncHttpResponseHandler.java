package com.loopj.android.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;

/**
 * 
 * @author sweetlilmre
 *
 *  Implements a response handler that will store the response in the provided
 *  File object.
 *  
 *  Events will be sent as per the AsyncHttpResponseHandler base class, however
 *  all byte[] values returned will be null.
 */
public class FileAsyncHttpResponseHandler extends AsyncHttpResponseHandler {
  
    private File mFile;

    public File getFile() {
      return (mFile);
    }
    
    public FileAsyncHttpResponseHandler(File file) {
        super();
        this.mFile = file;
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
                  int l, count = 0;;
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
      return (null);
  }
  
}
