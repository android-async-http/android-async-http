package com.loopj.android.http.sample;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;

public class FilesSample extends PostSample {

    public static final String LOG_TAG = "PostFilesSample";

    @Override
    public int getSampleTitle() {
        return R.string.title_post_files;
    }

    @Override
    public boolean isRequestBodyAllowed() {
        return false;
    }

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        try {
            RequestParams params = new RequestParams();
            final String contentType = RequestParams.APPLICATION_OCTET_STREAM;
            params.put("fileOne", createTempFile("fileOne", 1020), contentType, "fileOne");
            params.put("fileTwo", createTempFile("fileTwo", 1030), contentType);
            params.put("fileThree", createTempFile("fileThree", 1040), contentType, "customFileThree");
            params.put("fileFour", createTempFile("fileFour", 1050), contentType);
            params.put("fileFive", createTempFile("fileFive", 1060), contentType, "testingFileFive");
            params.setHttpEntityIsRepeatable(true);
            params.setUseJsonStreamer(false);
            return client.post(this, URL, params, responseHandler);
        } catch (FileNotFoundException fnfException) {
            Log.e(LOG_TAG, "executeSample failed with FileNotFoundException", fnfException);
        }
        return null;
    }

    public File createTempFile(String namePart, int byteSize) {
        try {
            File f = File.createTempFile(namePart, "_handled", getCacheDir());
            FileOutputStream fos = new FileOutputStream(f);
            Random r = new Random();
            byte[] buffer = new byte[byteSize];
            r.nextBytes(buffer);
            fos.write(buffer);
            fos.flush();
            fos.close();
            return f;
        } catch (Throwable t) {
            Log.e(LOG_TAG, "createTempFile failed", t);
        }
        return null;
    }
}
