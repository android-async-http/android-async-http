package com.loopj.android.http.sample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class SampleParentActivity extends Activity {

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private EditText urlEditText, headersEditText, bodyEditText;
    private LinearLayout responseLayout;

    private static final int LIGHTGREEN = Color.parseColor("#00FF66");
    private static final int LIGHTRED = Color.parseColor("#FF3300");
    private static final int YELLOW = Color.parseColor("#FFFF00");
    private static final int LIGHTBLUE = Color.parseColor("#99CCFF");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_layout);
        setTitle(getSampleTitle());

        urlEditText = (EditText) findViewById(R.id.edit_url);
        headersEditText = (EditText) findViewById(R.id.edit_headers);
        bodyEditText = (EditText) findViewById(R.id.edit_body);
        Button runButton = (Button) findViewById(R.id.button_run);
        Button cancelButton = (Button) findViewById(R.id.button_cancel);
        LinearLayout headersLayout = (LinearLayout) findViewById(R.id.layout_headers);
        LinearLayout bodyLayout = (LinearLayout) findViewById(R.id.layout_body);
        responseLayout = (LinearLayout) findViewById(R.id.layout_response);

        urlEditText.setText(getDefaultURL());

        bodyLayout.setVisibility(isRequestBodyAllowed() ? View.VISIBLE : View.GONE);
        headersLayout.setVisibility(isRequestHeadersAllowed() ? View.VISIBLE : View.GONE);

        runButton.setOnClickListener(onClickListener);
        if (isCancelButtonAllowed() && cancelButton != null) {
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setOnClickListener(onClickListener);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_run:
                    executeSample(getAsyncHttpClient(),
                            (urlEditText == null || urlEditText.getText() == null) ? getDefaultURL() : urlEditText.getText().toString(),
                            getRequestHeaders(),
                            getRequestEntity(),
                            getResponseHandler());
                    break;
                case R.id.button_cancel:
                    asyncHttpClient.cancelRequests(SampleParentActivity.this, true);
                    break;
            }
        }
    };

    protected Header[] getRequestHeaders() {
        List<Header> headers = new ArrayList<Header>();
        String headersRaw = headersEditText.getText() == null ? null : headersEditText.getText().toString();

        if (headersRaw != null && headersRaw.length() > 3) {
            String[] lines = headersRaw.split("\\r?\\n");
            for (String line : lines) {
                try {
                    String[] kv = line.split("=");
                    if (kv.length != 2)
                        throw new IllegalArgumentException("Wrong header format, may be 'Key=Value' only");
                    headers.add(new BasicHeader(kv[0].trim(), kv[1].trim()));
                } catch (Throwable t) {
                    Log.e("SampleParentActivity", "Not a valid header line: " + line, t);
                }
            }
        }
        return headers.toArray(new Header[headers.size()]);
    }

    protected HttpEntity getRequestEntity() {
        if (isRequestBodyAllowed() && bodyEditText.getText() != null) {
            try {
                return new StringEntity(bodyEditText.getText().toString());
            } catch (UnsupportedEncodingException e) {
                Log.e("SampleParentActivity", "cannot create String entity", e);
            }
        }
        return null;
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

    private View getColoredView(int bgColor, String msg) {
        TextView tv = new TextView(this);
        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setText(msg);
        tv.setBackgroundColor(bgColor);
        tv.setPadding(10, 10, 10, 10);
        tv.setTextColor(getContrastColor(bgColor));
        return tv;
    }

    protected final void addView(View v) {
        responseLayout.addView(v);
    }

    protected final void clearOutputs() {
        responseLayout.removeAllViews();
    }

    protected boolean isCancelButtonAllowed() {
        return false;
    }

    protected abstract int getSampleTitle();

    protected abstract boolean isRequestBodyAllowed();

    protected abstract boolean isRequestHeadersAllowed();

    protected abstract String getDefaultURL();

    protected abstract AsyncHttpResponseHandler getResponseHandler();

    protected AsyncHttpClient getAsyncHttpClient() {
        return this.asyncHttpClient;
    }

    protected abstract void executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, AsyncHttpResponseHandler responseHandler);
}
