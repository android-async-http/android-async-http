package com.loopj.android.http.sample;

import android.os.Bundle;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.TextHttpResponseHandler;
import com.loopj.android.http.sample.util.API8Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

public class RequestParamsDebug extends SampleParentActivity {

    public static final String LOG_TAG = "RequestParamsDebug";
    private static final String DEMO_RP_CONTENT = "array=java\n" +
            "array=C\n" +
            "list=blue\n" +
            "list=yellow\n" +
            "set=music\n" +
            "set=art\n" +
            "map=first_name\n" +
            "map=last_name\n";
    private EditText customParams;

    @Override
    public ResponseHandlerInterface getResponseHandler() {
        return new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                debugStatusCode(LOG_TAG, statusCode);
                debugHeaders(LOG_TAG, headers);
                debugResponse(LOG_TAG, responseString);
                debugThrowable(LOG_TAG, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                debugStatusCode(LOG_TAG, statusCode);
                debugHeaders(LOG_TAG, headers);
                debugResponse(LOG_TAG, responseString);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customParams = new EditText(this);
        customParams.setLines(8);
        customParams.setText(DEMO_RP_CONTENT);
        customFieldsLayout.addView(customParams);
    }

    @Override
    public String getDefaultURL() {
        return PROTOCOL + "httpbin.org/get";
    }

    @Override
    public boolean isRequestHeadersAllowed() {
        return false;
    }

    @Override
    public boolean isRequestBodyAllowed() {
        return false;
    }

    @Override
    public int getSampleTitle() {
        return R.string.title_request_params_debug;
    }

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        return getAsyncHttpClient().get(this, getDefaultURL(), getRequestParams(), getResponseHandler());
    }

    // TODO: allow parsing multiple values for each type, maybe like "type.key=value" ?
    private RequestParams getRequestParams() {
        RequestParams rp = new RequestParams();
        // contents of customParams custom field view
        String customParamsText = customParams.getText().toString();
        String[] pairs = customParamsText.split("\n");
        // temp content holders
        Map<String, Map<String, String>> mapOfMaps = new HashMap<>();
        Map<String, List<String>> mapOfLists = new HashMap<>();
        Map<String, String[]> mapOfArrays = new HashMap<>();
        Map<String, Set<String>> mapOfSets = new HashMap<>();
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length != 2)
                continue;
            String key = kv[0].trim();
            String value = kv[1].trim();
            if ("array".equals(key)) {
                String[] values = mapOfArrays.get(key);
                if (values == null) {
                    values = new String[]{value};
                } else {
                    values = API8Util.copyOfRange(values, 0, values.length + 1);
                    values[values.length - 1] = value;
                }
                mapOfArrays.put(key, values);
            } else if ("list".equals(key)) {
                List<String> values = mapOfLists.get(key);
                if (values == null) {
                    values = new ArrayList<>();
                }
                values.add(value);
                mapOfLists.put(key, values);
            } else if ("set".equals(key)) {
                Set<String> values = mapOfSets.get(key);
                if (values == null) {
                    values = new HashSet<>();
                }
                values.add(value);
                mapOfSets.put(key, values);
            } else if ("map".equals(key)) {
                Map<String, String> values = mapOfMaps.get(key);
                if (values == null) {
                    values = new HashMap<>();
                }
                values.put(key + values.size(), value);
                mapOfMaps.put(key, values);
            }
        }
        // fill in string list
        for (Map.Entry<String, List<String>> entry : mapOfLists.entrySet()) {
            rp.put(entry.getKey(), entry.getValue());
        }
        // fill in string array
        for (Map.Entry<String, String[]> entry : mapOfArrays.entrySet()) {
            rp.put(entry.getKey(), entry.getValue());
        }
        // fill in string set
        for (Map.Entry<String, Set<String>> entry : mapOfSets.entrySet()) {
            rp.put(entry.getKey(), entry.getValue());
        }
        // fill in string map
        for (Map.Entry<String, Map<String, String>> entry : mapOfMaps.entrySet()) {
            rp.put(entry.getKey(), entry.getValue());
        }
        // debug final URL construction into UI
        debugResponse(LOG_TAG, rp.toString());
        return rp;
    }
}
