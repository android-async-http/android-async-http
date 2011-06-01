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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

public class RequestParams {
    private static String ENCODING = "UTF-8";

    protected ConcurrentHashMap<String, String> stringParams;
    protected ConcurrentHashMap<String, FileWrapper> fileParams;

    public RequestParams() {
        init();
    }

    public RequestParams(Map<String, String> source) {
        init();

        for(Map.Entry<String, String> entry : source.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public RequestParams(String key, String value) {
        init();

        put(key, value);
    }

    public void put(String key, String value){
        if(key != null && value != null) {
            stringParams.put(key, value);
        }
    }

    public void put(String key, ByteArrayInputStream filedata) {
        put(key, filedata, null, null);
    }

    public void put(String key, ByteArrayInputStream filedata, String filename) {
        put(key, filedata, filename, null);
    }

    public void put(String key, ByteArrayInputStream filedata, String filename, String contentType) {
        if(key != null && filedata != null) {
            fileParams.put(key, new FileWrapper(filedata, filename, contentType));
        }
    }

    public void remove(String key){
        stringParams.remove(key);
        fileParams.remove(key);
    }

    public String getParamString() {
        if(!fileParams.isEmpty()) {
            throw new RuntimeException("Uploading files is not supported with Http GET requests.");
        }

        return URLEncodedUtils.format(getParamsList(), ENCODING);
    }

    public HttpEntity getEntity() {
        HttpEntity entity = null;

        if(!fileParams.isEmpty()) {
            SimpleMultipartEntity multipartEntity = new SimpleMultipartEntity();

            // Add string params
            for(ConcurrentHashMap.Entry<String, String> entry : stringParams.entrySet()) {
                multipartEntity.addPart(entry.getKey(), entry.getValue());
            }

            // Add file params
            for(ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
                FileWrapper file = entry.getValue();
                if(file.bytes != null) {
                    String filename = file.filename;
                    if(filename == null) {
                        filename = "nofilename";
                    }

                    if(file.contentType != null) {
                        multipartEntity.addPart(entry.getKey(), filename, file.bytes, file.contentType);
                    } else {
                        multipartEntity.addPart(entry.getKey(), filename, file.bytes);
                    }
                }
            }

            entity = multipartEntity;
        } else {
            try {
                entity = new UrlEncodedFormEntity(getParamsList(), ENCODING);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return entity;
    }

    private void init(){
        stringParams = new ConcurrentHashMap<String, String>();
        fileParams = new ConcurrentHashMap<String, FileWrapper>();
    }

    private List<BasicNameValuePair> getParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

        for(ConcurrentHashMap.Entry<String, String> entry : stringParams.entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return lparams;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for(ConcurrentHashMap.Entry<String, String> entry : stringParams.entrySet()) {
            if(result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }

        for(ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
            if(result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append("FILE");
        }

        return result.toString();
    }

    private static class FileWrapper {
        public ByteArrayInputStream bytes;
        public String filename;
        public String contentType;

        public FileWrapper(ByteArrayInputStream bytes, String filename, String contentType) {
            this.bytes = bytes;
            this.filename = filename;
            this.contentType = contentType;
        }
    }
}