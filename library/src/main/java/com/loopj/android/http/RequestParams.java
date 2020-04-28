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
import com.loopj.android.http.interfaces.ResponseHandlerInterface;
import com.loopj.android.http.params.ArrayParam;
import com.loopj.android.http.params.FileParam;
import com.loopj.android.http.params.StreamParam;
import com.loopj.android.http.params.StringListParam;
import com.loopj.android.http.params.StringMapParam;
import com.loopj.android.http.params.StringParam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cz.msebera.android.httpclient.HttpEntity;
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

    public boolean hasFiles() {
        for (ConcurrentHashMap.Entry<String, RequestParamInterface> entry : params.entrySet()) {
            if (entry.getValue() instanceof FileParam) {
                return true;
            }
        }
        return false;
    }

    public boolean hasStreams() {
        for (ConcurrentHashMap.Entry<String, RequestParamInterface> entry : params.entrySet()) {
            if (entry.getValue() instanceof StreamParam) {
                return true;
            }
        }
        return false;
    }

    public Set<Map.Entry<String, RequestParamInterface>> getParams() {
        return params.entrySet();
    }

    /**
     * @deprecated
     */
    public void put(String key, File value) {
        putParam(new FileParam(key, value));
    }

    /**
     * @deprecated
     */
    public void setHttpEntityIsRepeatable(boolean isRepeatable) {
        AsyncHttpClient.log.w(LOG_TAG, "setHttpEntityIsRepeatable method does nothing");
    }

    /**
     * @deprecated
     */
    public void put(String key, String value) {
        putParam(new StringParam(key, value, ContentType.TEXT_PLAIN));
    }

    /**
     * @deprecated
     */
    public void put(String key, String[] value) {
        putParam(new StringListParam(key, Arrays.asList(value), ContentType.TEXT_PLAIN));
    }

    /**
     * @deprecated
     */
    public void put(String key, Set<String> value) {
        putParam(new StringListParam(key, Arrays.asList(value.toArray(new String[0])), ContentType.TEXT_PLAIN));
    }

    /**
     * @deprecated
     */
    public void put(String key, Map<String, String> value) {
        putParam(new StringMapParam(key, value, ContentType.TEXT_PLAIN));
    }

    /**
     * @deprecated
     */
    public void put(String key, File file, String contentType, String customFileName) throws FileNotFoundException {
        putParam(new FileParam(key, file, ContentType.create(contentType), customFileName));
    }


    /**
     * @deprecated
     */
    public void put(String key, File file, String contentType) throws FileNotFoundException {
        putParam(new FileParam(key, file, ContentType.create(contentType)));
    }

    /**
     * @deprecated
     */
    public void put(String key, List<String> value) {
        putParam(new StringListParam(key, value, ContentType.TEXT_PLAIN));
    }

    /**
     * @deprecated
     */
    public HttpEntity getEntity(ResponseHandlerInterface handlerInterface) {
        if (useJsonStreamer) {

        } else if (!hasFiles() && !hasStreams()) {
            return HttpEntityFactory.getFormEntity(this);
        }
        return null;
    }

    /**
     * @deprecated
     */
    private boolean useJsonStreamer = false;

    /**
     * @deprecated
     */
    public void setUseJsonStreamer(boolean flag) {
        this.useJsonStreamer = flag;
    }
}
