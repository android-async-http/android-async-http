package com.loopj.android.http.sample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;

public class MainActivity extends Activity implements View.OnClickListener {

    private AsyncHttpClient aclient = new AsyncHttpClient(false, 80, 443);
    private TextView statusCode, headers, contents, state, error;
    private EditText url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button get = (Button) findViewById(R.id.request_get);
        statusCode = (TextView) findViewById(R.id.return_code);
        headers = (TextView) findViewById(R.id.return_headers);
        contents = (TextView) findViewById(R.id.return_data);
        state = (TextView) findViewById(R.id.current_state);
        error = (TextView) findViewById(R.id.return_error);
        url = (EditText) findViewById(R.id.request_url);

        get.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.request_get:
                if (verifyUrl()) {
                    startRequest();
                }
                break;
        }
    }

    private void startRequest() {
        aclient.get(this, getURLString(), new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String content) {
                setStatusMessage("Succeeded", Color.parseColor("#DD00FF00"));
                printHeaders(headers);
                printContents(content);
                printStatusCode(statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, String content) {
                setStatusMessage("Failed", Color.parseColor("#99FF0000"));
                printThrowable(error);
                printHeaders(headers);
                printContents(content);
                printStatusCode(statusCode);
            }

            @Override
            public void onStart() {
                setStatusMessage("Started", Color.parseColor("#EE00FF00"));
            }

            @Override
            public void onFinish() {
                setStatusMessage("Finished", 0);
            }
        });
    }

    private void printThrowable(Throwable error) {
        if (this.error != null) {
            if (error != null) {
                StringWriter sw = new StringWriter();
                error.printStackTrace(new PrintWriter(sw));
                this.error.setText(sw.toString());
            } else {
                this.error.setText(null);
            }
        }
    }

    private void printStatusCode(int statusCode) {
        if (this.statusCode != null) {
            this.statusCode.setText(String.format("HTTP Status Code: %d", statusCode));
        }
    }

    private void printContents(String content) {
        if (this.contents != null) {
            if (content == null)
                contents.setText("Return is NULL");
            else
                contents.setText(content);
        }
    }

    private void printHeaders(Header[] headers) {
        if (this.headers != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Headers:");
            if (headers != null) {
                for (Header h : headers) {
                    sb.append("\n").append(h.getName()).append(": ").append(h.getValue());
                }
            }
            this.headers.setText(sb.toString());
        }
    }

    private void setStatusMessage(String message, int color) {
        if (state != null) {
            state.setText(String.format("Status: %s", message));
            if (color != 0)
                state.setBackgroundColor(color);
        }
    }

    private String getURLString() {
        return url.getText() != null ? url.getText().toString() : null;
    }

    private boolean verifyUrl() {
        String contents = getURLString();
        if (contents != null) {
            try {
                URI.create(contents);
                return true;
            } catch (Throwable t) {
                Toast.makeText(this, "Given URL is not valid", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                return false;
            }
        }
        Toast.makeText(this, "You must fill in URL", Toast.LENGTH_SHORT).show();
        return false;
    }
}
