/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    https://loopj.com

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

import com.loopj.android.http.interfaces.RequestParamInterface;
import com.loopj.android.http.interfaces.RequestParamsInterface;
import com.loopj.android.http.params.ArrayParam;
import com.loopj.android.http.params.FileParam;
import com.loopj.android.http.params.StreamParam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import cz.msebera.android.httpclient.entity.ContentType;

public final class RequestParams implements RequestParamsInterface {

    protected final static String LOG_TAG = "RequestParams";
    protected final ConcurrentHashMap<String, RequestParamInterface> params = new ConcurrentHashMap<String, RequestParamInterface>();

    @Override
    public RequestParamsInterface putFile(String key, File file, ContentType contentType, String customFileName) throws FileNotFoundException {
        putParam(new FileParam(key, file, contentType));
        return this;
    }

    @Override
    public RequestParamsInterface putStream(String key, InputStream stream, String streamName, ContentType contentType, boolean autoClose) {
        putParam(new StreamParam(key, stream, contentType));
        return this;
    }

    @Override
    public RequestParamsInterface putFileArray(String key, List<File> files, ContentType contentType, List<String> customFileNames) {
        putParam(new ArrayParam<File>(key, files, contentType));
        return this;
    }

    @Override
    public RequestParamsInterface putStreamArray(String key, List<InputStream> files, ContentType contentType, List<String> customStreamNames) {
        putParam(new ArrayParam<InputStream>(key, files, contentType));
        return this;
    }

    @Override
    public RequestParamsInterface putParam(RequestParamInterface param) {
        params.put(param.getName(), param);
        return this;
    }
}
