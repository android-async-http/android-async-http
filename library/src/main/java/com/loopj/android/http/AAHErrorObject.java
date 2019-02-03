package com.loopj.android.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
/**
 * All error details can acccess into single object.
 */
public class AAHErrorObject {

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

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    private JSONArray jsonResponseArray;
    private JSONObject jsonResponseObject;

    public JSONException getJsonException() {
        return jsonException;
    }

    public void setJsonException(JSONException jsonException) {
        this.jsonException = jsonException;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    private JSONException jsonException;
    private int statusCode;
    private Header[] headers;
    private Throwable error;
    private String responseString;

}
