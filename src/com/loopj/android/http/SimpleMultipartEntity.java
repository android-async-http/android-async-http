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

/*
    This code is taken from Rafael Sanches' blog.
    http://blog.rafaelsanches.com/2011/01/29/upload-using-multipart-post-using-httpclient-in-android/
*/

package com.loopj.android.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * Simplified multipart entity mainly used for sending one or more files.
 */
class SimpleMultipartEntity implements HttpEntity {

    private static final byte[] CR_LF = ("\r\n").getBytes();
    private static final byte[] TRANSFER_ENCODING_BINARY = "Content-Transfer-Encoding: binary\r\n"
            .getBytes();

    private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private String boundary;
    private byte[] boundaryLine;
    private byte[] boundaryEnd;

    // The buffer we use for building the message excluding the last boundary
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public SimpleMultipartEntity() {
        final StringBuffer buf = new StringBuffer();
        final Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        boundary = buf.toString();
        boundaryLine = ("--" + boundary + "\r\n").getBytes();
        boundaryEnd = ("\r\n--" + boundary + "--\r\n").getBytes();
    }

    public void addPart(final String key, final String value) {
        try {
            out.write(boundaryLine);
            out.write(createContentDisposition(key));
            out.write(CR_LF);
            out.write(value.getBytes());
            out.write(CR_LF);
        } catch (final IOException e) {
            // Can't happen on ByteArrayOutputStream
        }
    }

    public void addPart(final String key, final String fileName, final InputStream fin) {
        addPart(key, fileName, fin, "application/octet-stream");
    }

    public void addPart(final String key, final String fileName, final InputStream fin, String type) {
        try {
            out.write(boundaryLine);

            // Headers
            out.write(createContentDisposition(key, fileName));
            out.write(createContentType(type));
            out.write(TRANSFER_ENCODING_BINARY);
            out.write(CR_LF);

            final byte[] tmp = new byte[4096];
            int l = 0;
            while ((l = fin.read(tmp)) != -1) {
                out.write(tmp, 0, l);
            }
            out.write(CR_LF);
        } catch (final IOException e) {
            // Can't happen on ByteArrayOutputStream
        } finally {
            try {
                fin.close();
            } catch (final IOException e) {
                // Ignore
            }
        }
    }

    public void addPart(final String key, final File value)
            throws FileNotFoundException {
        addPart(key, value.getName(), new FileInputStream(value));
    }

    private byte[] createContentType(String type) {
        String result = "Content-Type: " + type + "\r\n";
        return result.getBytes();
    }

    private byte[] createContentDisposition(final String key) {
        StringBuilder builder = new StringBuilder();
        builder.append("Content-Disposition: form-data; name=\"");
        builder.append(key);
        builder.append("\"\r\n");
        return builder.toString().getBytes();
    }

    private byte[] createContentDisposition(final String key, final String fileName) {
        StringBuilder builder = new StringBuilder();
        builder.append("Content-Disposition: form-data; name=\"");
        builder.append(key);
        builder.append("\"; filename=\"");
        builder.append(fileName);
        builder.append("\"\r\n");
        return builder.toString().getBytes();
    }

    // The following methods are from the HttpEntity interface

    @Override
    public long getContentLength() {
        return out.size() + boundaryEnd.length;
    }

    @Override
    public Header getContentType() {
        return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        out.writeTo(outstream);
        outstream.write(boundaryEnd);
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public void consumeContent() throws IOException,
    UnsupportedOperationException {
        if (isStreaming()) {
            throw new UnsupportedOperationException(
            "Streaming entity does not implement #consumeContent()");
        }
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "getContent() is not supported. Use writeTo() instead.");
    }
}