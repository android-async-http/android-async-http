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

    protected ConcurrentHashMap<String,String> urlParams;

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
            urlParams.put(key,value);
        }
    }

    public void remove(String key){
        urlParams.remove(key);
    }

    public String getParamString() {
        return URLEncodedUtils.format(getParamsList(), ENCODING);
    }

    public HttpEntity getEntity() {
        HttpEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(getParamsList(), ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return entity;
    }

    private void init(){
        urlParams = new ConcurrentHashMap<String,String>();
    }

    private List<BasicNameValuePair> getParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

        for(ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return lparams;
    }
}