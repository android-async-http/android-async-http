package com.loopj.android.http;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * All reponse details can acccess into single object.
 */
public class AAHResponseObject {

    public JSONArray getJsonResponseArray() {
        return jsonResponseArray;
    }

    public void setJsonResponseArray(JSONArray jsonResponseArray) {
        this.jsonResponseArray = jsonResponseArray;
    }

    public JSONObject getJsonResponseObject() {
        return jsonResponseObject;
    }

    public void setJsonResponseObject(JSONObject jsonResponseObject) {
        this.jsonResponseObject = jsonResponseObject;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    private JSONArray jsonResponseArray;
    private JSONObject jsonResponseObject;
    private int statusCode;
    private Header[] headers;
    private String responseString;

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }
}
