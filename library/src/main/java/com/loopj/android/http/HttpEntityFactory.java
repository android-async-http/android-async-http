package com.loopj.android.http;

import com.loopj.android.http.interfaces.RequestParamInterface;
import com.loopj.android.http.interfaces.RequestParamsInterface;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.entity.EntityBuilder;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

public class HttpEntityFactory {

    /**
     * convert and mix @RequestParams and @HttpEntity into one @HttpEntity
     * @param requestParams
     * @param httpEntity
     * @return
     */
    public static HttpEntity getHttpEntity(RequestParams requestParams, HttpEntity httpEntity) {
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, RequestParamInterface> param : requestParams.getParams()) {
            pairs.add(new BasicNameValuePair(param.getKey(), param.getValue().getValue()));
        }

        String sEntity = "";
        try {
            sEntity = EntityUtils.toString(httpEntity);
            sEntity = sEntity.concat(EntityUtils.toString(new UrlEncodedFormEntity(pairs)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return EntityBuilder.create().setText(sEntity).build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get @UrlEncodedFormEntity from @RequestParams object
     * @param params
     * @return
     */
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
