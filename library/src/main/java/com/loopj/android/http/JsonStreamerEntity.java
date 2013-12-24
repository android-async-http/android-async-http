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

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;
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

    // Size of the byte-array buffer used to read from streams.
    private static final int BUFFER_SIZE = 2048;

    // Reusable StringBuilder used by escape() method.
    // Base64, at worst, will make a binary stream grow in size by approximately
    // (n + 2 - ((n + 2) % 3)) / 3 * 4, which is roughly 1.3333333% for a
    // large 'n'.
    private static final StringBuilder BUILDER =
        new StringBuilder((int)(BUFFER_SIZE * 1.35f));

    private static final byte[] JSON_TRUE = "true".getBytes();
    private static final byte[] JSON_FALSE = "false".getBytes();
    private static final byte[] JSON_NULL = "null".getBytes();
    private static final byte[] STREAM_NAME = escape("name");
    private static final byte[] STREAM_TYPE = escape("type");
    private static final byte[] STREAM_CONTENTS = escape("contents");
    private static final byte[] STREAM_ELAPSED = escape("_elapsed");

    private static final Header HEADER_JSON_CONTENT =
        new BasicHeader("Content-Type", "application/json");
    private static final Header HEADER_GZIP_ENCODING =
        new BasicHeader("Content-Encoding", "gzip");
    private static final String APPLICATION_OCTET_STREAM =
        "application/octet-stream";

    // K/V objects to be uploaded.
    private final Map<String, Object> kvParams =
        new HashMap<String, Object>();

    // Streams and their associated meta-data to be uploaded.
    private final Map<String, RequestParams.StreamWrapper> streamParams =
        new HashMap<String, RequestParams.StreamWrapper>();

    // Whether to use gzip compression while uploading
    private final Header contentEncoding;

    public JsonStreamerEntity(boolean contentEncoding) {
        this.contentEncoding = contentEncoding ? HEADER_GZIP_ENCODING : null;
    }

    public void addPart(String key, Object value) {
        kvParams.put(key, value);
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
        return HEADER_JSON_CONTENT;
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

        // Record the time when uploading started.
        long now = System.currentTimeMillis();

        // Keys used by the HashMaps.
        Set<String> keys;

        // Use GZIP compression when sending streams, otherwise just use
        // a buffered output stream to speed things up a bit.
        OutputStream upload;
        if (null != contentEncoding) {
            upload = new GZIPOutputStream(new BufferedOutputStream(outstream), BUFFER_SIZE);
        } else {
            upload = new BufferedOutputStream(outstream);
        }

        // Always send a JSON object.
        upload.write('{');

        // Send the K/V values.
        keys = kvParams.keySet();
        for (String key : keys) {
            // Write the JSON object's key.
            upload.write(escape(key));
            upload.write(':');

            // Evaluate the value (which cannot be null).
            Object value = kvParams.get(key);

            if (value instanceof Boolean) {
                upload.write((Boolean)value ? JSON_TRUE : JSON_FALSE);
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
        for (String key : keys) {
            RequestParams.StreamWrapper entry = streamParams.get(key);

            // Write the JSON object's key.
            upload.write(escape(key));

            // All uploads are sent as an object containing the file's details.
            upload.write(':');
            upload.write('{');

            // Send the streams's name.
            upload.write(STREAM_NAME);
            upload.write(':');
            upload.write(escape(entry.name));
            upload.write(',');

            // Send the streams's content type.
            upload.write(STREAM_TYPE);
            upload.write(':');
            upload.write(escape(entry.contentType));
            upload.write(',');

            // Prepare the file content's key.
            upload.write(STREAM_CONTENTS);
            upload.write(':');
            upload.write('"');

            // Upload the file's contents in Base64.
            Base64OutputStream outputStream =
                new Base64OutputStream(upload, Base64.NO_CLOSE | Base64.NO_WRAP);

            // Read from input stream until no more data's left to read.
            int bytesRead;
            while ((bytesRead = entry.inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close the Base64 output stream.
            outputStream.close();

            // End the file's object and prepare for next one.
            upload.write('"');
            upload.write('}');
            upload.write(',');
        }

        // Include the elapsed time taken to upload everything.
        // This might be useful for somebody, but it serves us well since
        // there will almost always be a ',' as the last sent character.
        upload.write(STREAM_ELAPSED);
        upload.write(':');
        long elapsedTime = System.currentTimeMillis() - now;
        upload.write((elapsedTime + "}").getBytes());

        Log.i(LOG_TAG, "Uploaded JSON in " + Math.floor(elapsedTime / 1000) + " seconds");

        // Flush the contents up the stream.
        upload.flush();
        upload.close();
    }

    // Curtosy of Simple-JSON: http://goo.gl/XoW8RF
    // Changed a bit to suit our needs in this class.
    static byte[] escape(String string) {
        // If it's null, just return prematurely.
        if (string == null) {
          return JSON_NULL;
        }

        // Surround with quotations.
        BUILDER.append('"');

        int length = string.length(), pos = -1;
        while (++pos < length) {
            char ch = string.charAt(pos);
            switch (ch) {
                case '"':
                    BUILDER.append("\\\"");
                    break;
                case '\\':
                    BUILDER.append("\\\\");
                    break;
                case '\b':
                    BUILDER.append("\\b");
                    break;
                case '\f':
                    BUILDER.append("\\f");
                    break;
                case '\n':
                    BUILDER.append("\\n");
                    break;
                case '\r':
                    BUILDER.append("\\r");
                    break;
                case '\t':
                    BUILDER.append("\\t");
                    break;
                default:
                    // Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String intString = Integer.toHexString(ch);
                        BUILDER.append("\\u");
                        int intLength = 4 - intString.length();
                        for (int zero = 0; zero < intLength; zero++) {
                            BUILDER.append('0');
                        }
                        BUILDER.append(intString.toUpperCase(Locale.US));
                    } else {
                        BUILDER.append(ch);
                    }
                    break;
            }
        }

        // Surround with quotations.
        BUILDER.append('"');

        try {
          return BUILDER.toString().getBytes();
        } finally {
          // Empty the String buffer.
          // This is 20-30% faster than instantiating a new object.
          BUILDER.setLength(0);
        }
    }
}
