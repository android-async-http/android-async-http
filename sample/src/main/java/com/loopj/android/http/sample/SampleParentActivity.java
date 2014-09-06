/*
    Android Asynchronous Http Client Sample
    Copyright (c) 2014 Marek Sebera <marek.sebera@gmail.com>
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

package com.loopj.android.http.sample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public abstract class SampleParentActivity extends Activity implements SampleInterface {

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient() {

        @Override
        protected AsyncHttpRequest newAsyncHttpRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, ResponseHandlerInterface responseHandler, Context context) {
            AsyncHttpRequest httpRequest = getHttpRequest(client, httpContext, uriRequest, contentType, responseHandler, context);
            return httpRequest == null
                    ? super.newAsyncHttpRequest(client, httpContext, uriRequest, contentType, responseHandler, context)
                    : httpRequest;
        }
    };
    private EditText urlEditText, headersEditText, bodyEditText;
    private LinearLayout responseLayout;
    public LinearLayout customFieldsLayout;
    private final List<RequestHandle> requestHandles = new LinkedList<RequestHandle>();
    private static final String LOG_TAG = "SampleParentActivity";

    private static final int MENU_USE_HTTPS = 0;
    private static final int MENU_CLEAR_VIEW = 1;

    private boolean useHttps = true;

    protected static final String PROTOCOL_HTTP = "http://";
    protected static final String PROTOCOL_HTTPS = "https://";

    protected static String PROTOCOL = PROTOCOL_HTTPS;
    protected static final int LIGHTGREEN = Color.parseColor("#00FF66");
    protected static final int LIGHTRED = Color.parseColor("#FF3300");
    protected static final int YELLOW = Color.parseColor("#FFFF00");
    protected static final int LIGHTBLUE = Color.parseColor("#99CCFF");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_layout);
        setTitle(getSampleTitle());

        setHomeAsUpEnabled();

        urlEditText = (EditText) findViewById(R.id.edit_url);
        headersEditText = (EditText) findViewById(R.id.edit_headers);
        bodyEditText = (EditText) findViewById(R.id.edit_body);
        customFieldsLayout = (LinearLayout) findViewById(R.id.layout_custom);
        Button runButton = (Button) findViewById(R.id.button_run);
        Button cancelButton = (Button) findViewById(R.id.button_cancel);
        LinearLayout headersLayout = (LinearLayout) findViewById(R.id.layout_headers);
        LinearLayout bodyLayout = (LinearLayout) findViewById(R.id.layout_body);
        responseLayout = (LinearLayout) findViewById(R.id.layout_response);

        urlEditText.setText(getDefaultURL());
        headersEditText.setText(getDefaultHeaders());

        bodyLayout.setVisibility(isRequestBodyAllowed() ? View.VISIBLE : View.GONE);
        headersLayout.setVisibility(isRequestHeadersAllowed() ? View.VISIBLE : View.GONE);

        runButton.setOnClickListener(onClickListener);
        if (cancelButton != null) {
            if (isCancelButtonAllowed()) {
                cancelButton.setVisibility(View.VISIBLE);
                cancelButton.setOnClickListener(onClickListener);
            } else {
                cancelButton.setEnabled(false);
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem useHttpsMenuItem = menu.findItem(MENU_USE_HTTPS);
        if (useHttpsMenuItem != null) {
            useHttpsMenuItem.setChecked(useHttps);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_USE_HTTPS, Menu.NONE, R.string.menu_use_https).setCheckable(true);
        menu.add(Menu.NONE, MENU_CLEAR_VIEW, Menu.NONE, R.string.menu_clear_view);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_USE_HTTPS:
                useHttps = !useHttps;
                PROTOCOL = useHttps ? PROTOCOL_HTTPS : PROTOCOL_HTTP;
                urlEditText.setText(getDefaultURL());
                return true;
            case MENU_CLEAR_VIEW:
                clearOutputs();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public AsyncHttpRequest getHttpRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, ResponseHandlerInterface responseHandler, Context context) {
        return null;
    }

    public List<RequestHandle> getRequestHandles() {
        return requestHandles;
    }

    @Override
    public void addRequestHandle(RequestHandle handle) {
        if (null != handle) {
            requestHandles.add(handle);
        }
    }

    public void onRunButtonPressed() {
        addRequestHandle(executeSample(getAsyncHttpClient(),
                getUrlText(getDefaultURL()),
                getRequestHeaders(),
                getRequestEntity(),
                getResponseHandler()));
    }

    public void onCancelButtonPressed() {
        asyncHttpClient.cancelRequests(SampleParentActivity.this, true);
    }

    protected View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_run:
                    onRunButtonPressed();
                    break;
                case R.id.button_cancel:
                    onCancelButtonPressed();
                    break;
            }
        }
    };

    public List<Header> getRequestHeadersList() {
        List<Header> headers = new ArrayList<Header>();
        String headersRaw = headersEditText.getText() == null ? null : headersEditText.getText().toString();

        if (headersRaw != null && headersRaw.length() > 3) {
            String[] lines = headersRaw.split("\\r?\\n");
            for (String line : lines) {
                try {
                    int equalSignPos = line.indexOf('=');
                    if (1 > equalSignPos) {
                        throw new IllegalArgumentException("Wrong header format, may be 'Key=Value' only");
                    }

                    String headerName = line.substring(0, equalSignPos).trim();
                    String headerValue = line.substring(1 + equalSignPos).trim();
                    Log.d(LOG_TAG, String.format("Added header: [%s:%s]", headerName, headerValue));

                    headers.add(new BasicHeader(headerName, headerValue));
                } catch (Throwable t) {
                    Log.e(LOG_TAG, "Not a valid header line: " + line, t);
                }
            }
        }
        return headers;
    }

    public Header[] getRequestHeaders() {
        List<Header> headers = getRequestHeadersList();
        return headers.toArray(new Header[headers.size()]);
    }

    public HttpEntity getRequestEntity() {
        String bodyText;
        if (isRequestBodyAllowed() && (bodyText = getBodyText()) != null) {
            try {
                return new StringEntity(bodyText);
            } catch (UnsupportedEncodingException e) {
                Log.e(LOG_TAG, "cannot create String entity", e);
            }
        }
        return null;
    }

    public String getUrlText() {
        return getUrlText(null);
    }

    public String getUrlText(String defaultText) {
        return urlEditText != null && urlEditText.getText() != null
                ? urlEditText.getText().toString()
                : defaultText;
    }

    public String getBodyText() {
        return getBodyText(null);
    }

    public String getBodyText(String defaultText) {
        return bodyEditText != null && bodyEditText.getText() != null
                ? bodyEditText.getText().toString()
                : defaultText;
    }

    public String getHeadersText() {
        return getHeadersText(null);
    }

    public String getHeadersText(String defaultText) {
        return headersEditText != null && headersEditText.getText() != null
                ? headersEditText.getText().toString()
                : defaultText;
    }

    protected final void debugHeaders(String TAG, Header[] headers) {
        if (headers != null) {
            Log.d(TAG, "Return Headers:");
            StringBuilder builder = new StringBuilder();
            for (Header h : headers) {
                String _h = String.format(Locale.US, "%s : %s", h.getName(), h.getValue());
                Log.d(TAG, _h);
                builder.append(_h);
                builder.append("\n");
            }
            addView(getColoredView(YELLOW, builder.toString()));
        }
    }

    protected static String throwableToString(Throwable t) {
        if (t == null)
            return null;

        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    protected final void debugThrowable(String TAG, Throwable t) {
        if (t != null) {
            Log.e(TAG, "AsyncHttpClient returned error", t);
            addView(getColoredView(LIGHTRED, throwableToString(t)));
        }
    }

    protected final void debugResponse(String TAG, String response) {
        if (response != null) {
            Log.d(TAG, "Response data:");
            Log.d(TAG, response);
            addView(getColoredView(LIGHTGREEN, response));
        }
    }

    protected final void debugStatusCode(String TAG, int statusCode) {
        String msg = String.format(Locale.US, "Return Status Code: %d", statusCode);
        Log.d(TAG, msg);
        addView(getColoredView(LIGHTBLUE, msg));
    }

    public static int getContrastColor(int color) {
        double y = (299 * Color.red(color) + 587 * Color.green(color) + 114 * Color.blue(color)) / 1000;
        return y >= 128 ? Color.BLACK : Color.WHITE;
    }

    protected View getColoredView(int bgColor, String msg) {
        TextView tv = new TextView(this);
        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setText(msg);
        tv.setBackgroundColor(bgColor);
        tv.setPadding(10, 10, 10, 10);
        tv.setTextColor(getContrastColor(bgColor));
        return tv;
    }

    @Override
    public String getDefaultHeaders() {
        return null;
    }

    protected final void addView(View v) {
        responseLayout.addView(v);
    }

    protected final void clearOutputs() {
        responseLayout.removeAllViews();
    }

    public boolean isCancelButtonAllowed() {
        return false;
    }

    public AsyncHttpClient getAsyncHttpClient() {
        return this.asyncHttpClient;
    }

    @Override
    public void setAsyncHttpClient(AsyncHttpClient client) {
        this.asyncHttpClient = client;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setHomeAsUpEnabled() {
        if (Integer.valueOf(Build.VERSION.SDK) >= 11) {
            if (getActionBar() != null)
                getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
