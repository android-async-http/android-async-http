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

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;

public class PostRequest extends BaseRequestWithEntity {

    public PostRequest(boolean synchronous, String url, Header[] headers, HttpEntity entity, Object TAG) {
        super(synchronous, url, headers, entity, TAG);
    }

    @Override
    public HttpUriRequest build() {
        HttpPost post = new HttpPost(getURL());
        post.setEntity(entity);
        return post;
    }
}
