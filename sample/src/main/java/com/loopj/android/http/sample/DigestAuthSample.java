package com.loopj.android.http.sample;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;

public class DigestAuthSample extends GetSample {

    private EditText usernameField, passwordField;

    @Override
    public String getDefaultURL() {
        return PROTOCOL + "httpbin.org/digest-auth/auth/user/passwd2";
    }

    @Override
    public int getSampleTitle() {
        return R.string.title_digest_auth;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usernameField = new EditText(this);
        passwordField = new EditText(this);
        usernameField.setHint("Username");
        passwordField.setHint("Password");
        usernameField.setText("user");
        passwordField.setText("passwd2");
        customFieldsLayout.addView(usernameField);
        customFieldsLayout.addView(passwordField);
    }

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        setCredentials(client, URL);
        return client.get(this, URL, headers, null, responseHandler);
    }

    @Override
    public boolean isCancelButtonAllowed() {
        return true;
    }

    @Override
    public boolean isRequestHeadersAllowed() {
        return true;
    }

    @Override
    public boolean isRequestBodyAllowed() {
        return false;
    }

    private void setCredentials(AsyncHttpClient client, String URL) {
        Uri parsed = Uri.parse(URL);
        client.clearCredentialsProvider();
        client.setCredentials(
                new AuthScope(parsed.getHost(), parsed.getPort() == -1 ? 80 : parsed.getPort()),
                new UsernamePasswordCredentials(
                        usernameField.getText().toString(),
                        passwordField.getText().toString()
                )
        );
    }
}
