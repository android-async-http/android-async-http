/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    https://loopj.com

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

package com.loopj.android.http;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

public abstract class FileAsyncHttpResponseHandler extends AsyncHttpResponseHandler {

    private static final String LOG_TAG = "FileAsyncHttpRH";
    protected final File file;
    protected final boolean append;
    protected final boolean renameIfExists;
    protected File frontendFile;

    /**
     * Obtains new FileAsyncHttpResponseHandler and stores response in passed file
     *
     * @param file File to store response within, must not be null
     */
    public FileAsyncHttpResponseHandler(File file) {
        this(file, false);
    }

    /**
     * Obtains new FileAsyncHttpResponseHandler and stores response in passed file
     *
     * @param file   File to store response within, must not be null
     * @param append whether data should be appended to existing file
     */
    public FileAsyncHttpResponseHandler(File file, boolean append) {
        this(file, append, false);
    }

    /**
     * Obtains new FileAsyncHttpResponseHandler and stores response in passed file
     *
     * @param file                     File to store response within, must not be null
     * @param append                   whether data should be appended to existing file
     * @param renameTargetFileIfExists whether target file should be renamed if it already exists
     */
    public FileAsyncHttpResponseHandler(File file, boolean append, boolean renameTargetFileIfExists) {
        this(file,append,renameTargetFileIfExists,false);
    }


    /**
     * Obtains new FileAsyncHttpResponseHandler and stores response in passed file
     *
     * @param file                     File to store response within, must not be null
     * @param append                   whether data should be appended to existing file
     * @param renameTargetFileIfExists whether target file should be renamed if it already exists
     * @param usePoolThread Whether to use the pool's thread to fire callbacks
     */
    public FileAsyncHttpResponseHandler(File file, boolean append, boolean renameTargetFileIfExists,boolean usePoolThread) {
        super(usePoolThread);
        Utils.asserts(file != null, "File passed into FileAsyncHttpResponseHandler constructor must not be null");
        if (!file.isDirectory() && !file.getParentFile().isDirectory()) {
            Utils.asserts(file.getParentFile().mkdirs(), "Cannot create parent directories for requested File location");
        }
        if (file.isDirectory()) {
            if (!file.mkdirs()) {
                AsyncHttpClient.log.d(LOG_TAG, "Cannot create directories for requested Directory location, might not be a problem");
            }
        }
        this.file = file;
        this.append = append;
        this.renameIfExists = renameTargetFileIfExists;
    }

    /**
     * Obtains new FileAsyncHttpResponseHandler against context with target being temporary file
     *
     * @param context Context, must not be null
     */
    public FileAsyncHttpResponseHandler(Context context) {
        super();
        this.file = getTemporaryFile(context);
        this.append = false;
        this.renameIfExists = false;
    }

    /**
     * Attempts to delete file with stored response
     *
     * @return false if the file does not exist or is null, true if it was successfully deleted
     */
    public boolean deleteTargetFile() {
        return getTargetFile() != null && getTargetFile().delete();
    }

    /**
     * Used when there is no file to be used when calling constructor
     *
     * @param context Context, must not be null
     * @return temporary file or null if creating file failed
     */
    protected File getTemporaryFile(Context context) {
        Utils.asserts(context != null, "Tried creating temporary file without having Context");
        try {
            return File.createTempFile("temp_", "_handled", context.getCacheDir());
        } catch (IOException e) {
            AsyncHttpClient.log.e(LOG_TAG, "Cannot create temporary file", e);
        }
        return null;
    }

    /**
     * Retrieves File object in which the response is stored
     *
     * @return File file in which the response was to be stored
     */
    protected File getOriginalFile() {
        Utils.asserts(file != null, "Target file is null, fatal!");
        return file;
    }

    /**
     * Retrieves File which represents response final location after possible renaming
     *
     * @return File final target file
     */
    public File getTargetFile() {
        if (frontendFile == null) {
            frontendFile = getOriginalFile().isDirectory() ? getTargetFileByParsingURL() : getOriginalFile();
        }
        return frontendFile;
    }

    /**
     * Will return File instance for file representing last URL segment in given folder.
     * If file already exists and renameTargetFileIfExists was set as true, will try to find file
     * which doesn't exist, naming template for such cases is "filename.ext" =&gt; "filename (%d).ext",
     * or without extension "filename" =&gt; "filename (%d)"
     *
     * @return File in given directory constructed by last segment of request URL
     */
    protected File getTargetFileByParsingURL() {
        Utils.asserts(getOriginalFile().isDirectory(), "Target file is not a directory, cannot proceed");
        Utils.asserts(getRequestURI() != null, "RequestURI is null, cannot proceed");
        String requestURL = getRequestURI().toString();
        String filename = requestURL.substring(requestURL.lastIndexOf('/') + 1, requestURL.length());
        File targetFileRtn = new File(getOriginalFile(), filename);
        if (targetFileRtn.exists() && renameIfExists) {
            String format;
            if (!filename.contains(".")) {
                format = filename + " (%d)";
            } else {
                format = filename.substring(0, filename.lastIndexOf('.')) + " (%d)" + filename.substring(filename.lastIndexOf('.'), filename.length());
            }
            int index = 0;
            while (true) {
                targetFileRtn = new File(getOriginalFile(), String.format(format, index));
                if (!targetFileRtn.exists())
                    return targetFileRtn;
                index++;
            }
        }
        return targetFileRtn;
    }

    @Override
    public final void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable) {
        onFailure(statusCode, headers, throwable, getTargetFile());
    }

    /**
     * Method to be overriden, receives as much of file as possible Called when the file is
     * considered failure or if there is error when retrieving file
     *
     * @param statusCode http file status line
     * @param headers    file http headers if any
     * @param throwable  returned throwable
     * @param file       file in which the file is stored
     */
    public abstract void onFailure(int statusCode, Header[] headers, Throwable throwable, File file);

    @Override
    public final void onSuccess(int statusCode, Header[] headers, byte[] responseBytes) {
        onSuccess(statusCode, headers, getTargetFile());
    }

    /**
     * Method to be overriden, receives as much of response as possible
     *
     * @param statusCode http response status line
     * @param headers    response http headers if any
     * @param file       file in which the response is stored
     */
    public abstract void onSuccess(int statusCode, Header[] headers, File file);

    @Override
    protected byte[] getResponseData(HttpEntity entity) throws IOException {
        if (entity != null) {
            InputStream instream = entity.getContent();
            long contentLength = entity.getContentLength();
            FileOutputStream buffer = new FileOutputStream(getTargetFile(), this.append);
            if (instream != null) {
                try {
                    byte[] tmp = new byte[BUFFER_SIZE];
                    int l, count = 0;
                    // do not send messages if request has been cancelled
                    while ((l = instream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                        count += l;
                        buffer.write(tmp, 0, l);
                        sendProgressMessage(count, contentLength);
                    }
                } finally {
                    AsyncHttpClient.silentCloseInputStream(instream);
                    buffer.flush();
                    AsyncHttpClient.silentCloseOutputStream(buffer);
                }
            }
        }
        return null;
    }

}
