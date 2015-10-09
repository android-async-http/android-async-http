package com.loopj.android.http;

/**
 * Created by chase on 08/10/2015.
 */
import java.security.Principal;

import cz.msebera.android.httpclient.auth.BasicUserPrincipal;
import cz.msebera.android.httpclient.auth.Credentials;

public class TokenCredentials implements Credentials {
    private Principal userPrincipal;

    public TokenCredentials(String token) {
        this.userPrincipal = new BasicUserPrincipal(token);
    }

    @Override
    public Principal getUserPrincipal() {
        return userPrincipal;
    }

    @Override
    public String getPassword() {
        return null;
    }

}
