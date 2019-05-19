/*
    Copyright (c) 2015 Marek Sebera <marek.sebera@gmail.com>

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
package com.loopj.android.http.requests;

import com.loopj.android.http.interfaces.RequestInterface;

import java.net.URI;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

public abstract class BaseRequest implements RequestInterface {

    protected Header[] headers;
    protected URI url;
    protected boolean synchronous;
    protected Object tag;

    public BaseRequest(boolean synchronous, String url, Header[] headers, Object TAG) {
        this.headers = headers;
        this.url = URI.create(url);
        this.synchronous = synchronous;
        this.tag = TAG;
    }

    @Override
    public boolean isSynchronous() {
        return this.synchronous;
    }

    @Override
    public URI getURL() {
        return this.url;
    }

    @Override
    public Header[] getHeaders() {
        return this.headers;
    }

    @Override
    public Object getTAG() {
        return tag;
    }

    @Override
    public HttpEntity getEntity() {
        return null;
    }
}
