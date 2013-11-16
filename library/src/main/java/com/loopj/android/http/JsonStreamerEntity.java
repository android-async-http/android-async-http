/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.loopj.android.http;

import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

/**
 * HTTP entity to upload JSON data using streams.
 * This has very low memory footprint; suitable for uploading large
 * files using base64 encoding.
 */
class JsonStreamerEntity implements HttpEntity {

    private static final String LOG_TAG = "JsonStreamerEntity";

    private static final UnsupportedOperationException ERR_UNSUPPORTED =
        new UnsupportedOperationException("Unsupported operation in this implementation.");

    private static final byte[] JSON_TRUE = "true".getBytes();
    private static final byte[] JSON_FALSE = "false".getBytes();
    private static final byte[] STREAM_NAME = escape("name", true).getBytes();
    private static final byte[] STREAM_TYPE = escape("type", true).getBytes();
    private static final byte[] STREAM_CONTENTS = escape("contents", true).getBytes();
    private static final byte[] STREAM_ELAPSED = escape("_elapsed", true).getBytes();

    private static final Header HEADER_JSON =
        new BasicHeader("Content-Type", "application/json");
    private static final String APPLICATION_OCTET_STREAM =
        "application/octet-stream";

    // Size of the byte-array buffer used to read from files.
    private static final int BUFFER_SIZE = 2048;

    // K/V objects to be uploaded.
    private final Map<String, Object> kvParams = new HashMap<String, Object>();

    // Streams and their associated meta-data to be uploaded.
    private final Map<String, RequestParams.StreamWrapper> streamParams = new HashMap<String, RequestParams.StreamWrapper>();

    // Whether to use gzip compression while uploading
    private final Header contentEncoding;

    public JsonStreamerEntity(boolean contentEncoding) {
        this.contentEncoding = contentEncoding
            ? new BasicHeader("Content-Encoding", "gzip")
            : null;
    }

    public void addPart(String key, Object value) {
        kvParams.put(key, value);
    }

    public void addPart(String key, File file, String type) throws IOException {
        addPart(key, new FileInputStream(file), file.getName(), type);
    }

    public void addPart(String key, InputStream inputStream, String name, String type) {
        if (type == null) {
            type = APPLICATION_OCTET_STREAM;
        }
        streamParams.put(key, new RequestParams.StreamWrapper(inputStream, name, type));
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public long getContentLength() {
        return -1;
    }

    @Override
    public Header getContentEncoding() {
        return contentEncoding;
    }

    @Override
    public Header getContentType() {
        return HEADER_JSON;
    }

    @Override
    public void consumeContent() throws IOException, UnsupportedOperationException {
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        throw ERR_UNSUPPORTED;
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalStateException("Output stream cannot be null.");
        }

        long now = System.currentTimeMillis();
        Log.i(LOG_TAG, "Started dumping at: " + now);

        OutputStream upload;

        // GZIPOutputStream is available only from API level 8 and onward.
        if(null != contentEncoding) {
            upload = new GZIPOutputStream(new BufferedOutputStream(outstream), BUFFER_SIZE);
        } else {
            upload = new BufferedOutputStream(outstream);
        }

        // Always send a JSON object.
        upload.write('{');

        // Keys used by the HashMaps.
        Set<String> keys;

        // Send the K/V values.
        keys = kvParams.keySet();
        for (String key : keys) {
            // Write the JSON object's key.
            upload.write(escape(key, true).getBytes());
            upload.write(':');

            // Evaluate the value (which cannot be null).
            Object value = kvParams.get(key);

            if (value instanceof Boolean) {
                upload.write((Boolean) value ? JSON_TRUE : JSON_FALSE);
            } else if (value instanceof Long) {
                upload.write((((Number)value).longValue() + "").getBytes());
            } else if (value instanceof Double) {
                upload.write((((Number)value).doubleValue() + "").getBytes());
            } else if (value instanceof Float) {
                upload.write((((Number)value).floatValue() + "").getBytes());
            } else if (value instanceof Integer) {
                upload.write((((Number)value).intValue() + "").getBytes());
            } else {
                upload.write(value.toString().getBytes());
            }

            upload.write(',');
        }

        // Buffer used for reading from input streams.
        byte[] buffer = new byte[BUFFER_SIZE];

        // Send the stream params.
        keys = streamParams.keySet();
        for(String key : keys) {
            RequestParams.StreamWrapper entry = streamParams.get(key);

            // Write the JSON object's key.
            upload.write(escape(key, true).getBytes());

            // All uploads are sent as an object containing the file's details.
            upload.write(":{".getBytes());

            // Send the streams's name.
            upload.write(STREAM_NAME);
            upload.write(':');
            upload.write(escape(entry.name, true).getBytes());

            // Send the streams's content type.
            upload.write(STREAM_TYPE);
            upload.write(':');
            upload.write(escape(entry.contentType, true).getBytes());

            // Prepare the file content's key.
            upload.write(STREAM_CONTENTS);
            upload.write(':');
            upload.write('"');

            // Write the file's contents in Base64.
            Base64OutputStream outputStream = new Base64OutputStream(upload, Base64.NO_CLOSE | Base64.NO_WRAP);
            int bytesRead;
            while(-1 != (bytesRead = entry.inputStream.read(buffer))) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close the output stream.
            outputStream.close();

            // Close the file's object.
            upload.write('"');
            upload.write('}');
            upload.write(',');
        }

        // Include the elapsed time taken to upload everything.
        upload.write(STREAM_ELAPSED);
        upload.write(':');
        long elapsedTime = System.currentTimeMillis() - now;
        upload.write((elapsedTime + "}").getBytes());

        Log.i(LOG_TAG, "JSON was uploaded in " + Math.floor(elapsedTime / 1000) + " seconds");

        // Flush the contents up the stream.
        upload.flush();
        upload.close();
    }

    // Curtosy of Simple-JSON:
    // http://goo.gl/XoW8RF
    private static String escape(String string, boolean quotes) {
        StringBuilder sb = new StringBuilder();
        int length = string.length(), pos = -1;
        if (quotes) {
          sb.append('"');
        }
        while (++pos < length) {
            char ch = string.charAt(pos);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    // Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String intString = Integer.toHexString(ch);
                        sb.append("\\u");
                        int intLength = 4 - intString.length();
                        for (int zero = 0; zero < intLength; zero++) {
                            sb.append('0');
                        }
                        sb.append(intString.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
                    break;
            }
        }
        if (quotes) {
          sb.append('"');
        }
        return sb.toString();
    }
}
