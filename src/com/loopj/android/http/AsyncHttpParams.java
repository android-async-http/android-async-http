package com.loopj.android.http;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

public class AsyncHttpParams {
    private static String ENCODING = "UTF-8";

    private ConcurrentHashMap<String,String> urlParams;

    public AsyncHttpParams() {
        init();
    }

    public AsyncHttpParams(String key, String value) {
        init();
        put(key, value);
    }

    public void put(String key, String value){
        urlParams.put(key,value);
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