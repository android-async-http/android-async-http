/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.loopj.android.http;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/**
 * This interface is used to define pre- and post-processing handlers for every
 * request, and response, passing through the library.
 *
 * @author Noor Dawod <github@fineswap.com>
 */
public interface PreProcessingInterface {

    /**
     * This method is called right before the passed request is processed by
     * the executor service. This is only called once regardless if the request is
     * successful or not.
     *
     * @param request The request that's about to be processed by the executor
     */
    void onPreProcessRequest(final HttpRequest request);

    /**
     * This method is called right before {@link ResponseHandlerInterface} methods
     * are called in order to process success or failure requests. This is only
     * called once for processing.
     *
     * @param response The response that's about to be processed by {@link ResponseHandlerInterface} handler
     */
    void onPreProcessResponse(final HttpResponse response);
}
