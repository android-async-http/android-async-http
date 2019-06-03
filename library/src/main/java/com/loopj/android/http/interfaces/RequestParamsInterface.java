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
package com.loopj.android.http.interfaces;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.entity.ContentType;

public interface RequestParamsInterface extends Serializable {

    /**
     * @deprecated
     */
    String APPLICATION_OCTET_STREAM = ContentType.APPLICATION_OCTET_STREAM.getMimeType();

    RequestParamsInterface putFile(String key, File file, ContentType contentType, String customFileName) throws FileNotFoundException;

    RequestParamsInterface putStream(String key, InputStream stream, String streamName, ContentType contentType, boolean autoClose);

    RequestParamsInterface putFileArray(String key, List<File> files, ContentType contentType, List<String> customFileNames);

    RequestParamsInterface putStreamArray(String key, List<InputStream> files, ContentType contentType, List<String> customStreamNames);

    RequestParamsInterface putParam(RequestParamInterface param);

    Set<Map.Entry<String, RequestParamInterface>> getParams();

}
