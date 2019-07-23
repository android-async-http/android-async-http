/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    https://github.com/android-async-http/android-async-http

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package com.loopj.android.http;

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
