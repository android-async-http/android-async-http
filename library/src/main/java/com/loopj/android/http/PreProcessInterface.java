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

/**
 * This interface is used to define a pre-processing handler. The handler is
 * called once for a request and once for a response.
 *
 * @author Noor Dawod <github@fineswap.com>
 */
public interface PreProcessInterface {

    /**
     * This method is called once by the system when either a request or a response
     * needs pre-processing. The library makes sure that each request and each
     * response is pre-processed only once.
     */
    void onPreProcess();
}
