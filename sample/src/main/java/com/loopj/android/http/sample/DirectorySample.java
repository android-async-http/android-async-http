/*
    Android Asynchronous Http Client Sample
    Copyright (c) 2014 Marek Sebera <marek.sebera@gmail.com>
    https://github.com/android-async-http/android-async-http

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.loopj.android.http.sample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.sample.util.FileUtil;

import java.io.File;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

public class DirectorySample extends SampleParentActivity {
    private static final String LOG_TAG = "DirectorySample";
    private FileAsyncHttpResponseHandler lastResponseHandler = null;
    private CheckBox cbAppend, cbRename;

    @Override
    public int getSampleTitle() {
        return R.string.title_directory_sample;
    }

    @Override
    public boolean isRequestBodyAllowed() {
        return false;
    }

    @Override
    public boolean isRequestHeadersAllowed() {
        return true;
    }

    @Override
    public String getDefaultURL() {
        return "https://httpbin.org/robots.txt";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button deleteTargetFile = new Button(this);
        deleteTargetFile.setText(R.string.button_delete_target_file);
        deleteTargetFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearOutputs();
                if (lastResponseHandler != null) {
                    File toBeDeleted = lastResponseHandler.getTargetFile();
                    debugResponse(LOG_TAG, String.format(Locale.US, "File was deleted? %b", toBeDeleted.delete()));
                    debugResponse(LOG_TAG, String.format(Locale.US, "Delete file path: %s", toBeDeleted.getAbsolutePath()));
                } else {
                    debugThrowable(LOG_TAG, new Error("You have to Run example first"));
                }
            }
        });
        cbAppend = new CheckBox(this);
        cbAppend.setText("Constructor \"append\" is true?");
        cbAppend.setChecked(false);
        cbRename = new CheckBox(this);
        cbRename.setText("Constructor \"renameTargetFileIfExists\" is true?");
        cbRename.setChecked(true);
        customFieldsLayout.addView(deleteTargetFile);
        customFieldsLayout.addView(cbAppend);
        customFieldsLayout.addView(cbRename);
    }

    @Override
    public ResponseHandlerInterface getResponseHandler() {
        lastResponseHandler = new FileAsyncHttpResponseHandler(getCacheDir(), cbAppend.isChecked(), cbRename.isChecked()) {
            @Override
            public void onStart() {
                clearOutputs();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                debugHeaders(LOG_TAG, headers);
                debugStatusCode(LOG_TAG, statusCode);
                debugFile(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                debugHeaders(LOG_TAG, headers);
                debugStatusCode(LOG_TAG, statusCode);
                debugThrowable(LOG_TAG, throwable);
                debugFile(file);
            }

            private void debugFile(File file) {
                if (file == null || !file.exists()) {
                    debugResponse(LOG_TAG, "Response is null");
                    return;
                }
                try {
                    debugResponse(LOG_TAG, file.getAbsolutePath() + "\r\n\r\n" + FileUtil.getStringFromFile(file));
                } catch (Throwable t) {
                    Log.e(LOG_TAG, "Cannot debug file contents", t);
                }
            }
        };
        return lastResponseHandler;
    }

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        return client.get(this, URL, headers, null, responseHandler);
    }
}
