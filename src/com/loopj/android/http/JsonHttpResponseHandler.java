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

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonHttpResponseHandler extends AsyncHttpResponseHandler {
    @Override
    protected void handleResponseMessage(String responseBody) {
        super.handleResponseMessage(responseBody);

        try {
            Object jsonResponse = new JSONTokener(responseBody).nextValue();
            if(jsonResponse instanceof JSONObject) {
                onSuccess((JSONObject)jsonResponse);
            } else if(jsonResponse instanceof JSONArray) {
                onSuccess((JSONArray)jsonResponse);
            }
        } catch(JSONException e) {
            onFailure(e);
        }
    }

    // Public callbacks
    public void onSuccess(JSONObject response) {}
    public void onSuccess(JSONArray response) {}
}