package com.loopj.android.http;

import android.content.Context;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public abstract class FileAsyncHttpResponseHandler extends AsyncHttpResponseHandler {

    private File mFile;
    private static final String LOG_TAG = "FileAsyncHttpResponseHandler";

    public FileAsyncHttpResponseHandler(File file) {
        super();
        assert (file != null);
        this.mFile = file;
    }

    public FileAsyncHttpResponseHandler(Context c) {
        super();
        assert (c != null);
        this.mFile = getTemporaryFile(c);
    }

    public boolean deleteTargetFile() {
        return getTargetFile() == null || getTargetFile().delete();
    }

    protected File getTemporaryFile(Context c) {
        try {
            return File.createTempFile("temp_", "_handled", c.getCacheDir());
        } catch (Throwable t) {
            Log.e(LOG_TAG, "Cannot create temporary file", t);
        }
        return null;
    }

    protected File getTargetFile() {
        assert (mFile != null);
        return mFile;
    }

    @Override
    public final void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        onFailure(statusCode, headers, error, getTargetFile());
    }

    public abstract void onFailure(int statusCode, Header[] headers, Throwable e, File response);

    @Override
    public final void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        onSuccess(statusCode, headers, getTargetFile());
    }

    public abstract void onSuccess(int statusCode, Header[] headers, File file);

    @Override
    protected byte[] getResponseData(HttpEntity entity) throws IOException {
        if (entity != null) {
            InputStream instream = entity.getContent();
            long contentLength = entity.getContentLength();
            FileOutputStream buffer = new FileOutputStream(getTargetFile());
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
