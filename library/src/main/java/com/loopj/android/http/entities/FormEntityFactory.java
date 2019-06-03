package com.loopj.android.http.entities;

import com.loopj.android.http.interfaces.RequestParamInterface;
import com.loopj.android.http.interfaces.RequestParamsInterface;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class FormEntityFactory {

    public static HttpEntity getFormEntity(RequestParamsInterface params) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, RequestParamInterface> param : params.getParams()) {
            pairs.add(new BasicNameValuePair(param.getKey(), param.getValue().getValue()));
        }
        try {
            return new UrlEncodedFormEntity(pairs);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
