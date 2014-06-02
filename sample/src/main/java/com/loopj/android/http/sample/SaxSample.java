/*
    Android Asynchronous Http Client Sample
    Copyright (c) 2014 Marek Sebera <marek.sebera@gmail.com>
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

package com.loopj.android.http.sample;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SaxAsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class SaxSample extends SampleParentActivity {

    private static final String LOG_TAG = "SaxSample";

    @Override
    public ResponseHandlerInterface getResponseHandler() {
        return saxAsyncHttpResponseHandler;
    }

    private SaxAsyncHttpResponseHandler saxAsyncHttpResponseHandler = new SaxAsyncHttpResponseHandler<SAXTreeStructure>(new SAXTreeStructure()) {
        @Override
        public void onStart() {
            clearOutputs();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, SAXTreeStructure saxTreeStructure) {
            debugStatusCode(LOG_TAG, statusCode);
            debugHeaders(LOG_TAG, headers);
            debugHandler(saxTreeStructure);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, SAXTreeStructure saxTreeStructure) {
            debugStatusCode(LOG_TAG, statusCode);
            debugHeaders(LOG_TAG, headers);
            debugHandler(saxTreeStructure);
        }

        private void debugHandler(SAXTreeStructure handler) {
            for (Tuple t : handler.responseViews) {
                addView(getColoredView(t.color, t.text));
            }
        }
    };

    @Override
    public String getDefaultURL() {
        return "http://bin-iin.com/sitemap.xml";
    }

    @Override
    public boolean isRequestHeadersAllowed() {
        return false;
    }

    @Override
    public boolean isRequestBodyAllowed() {
        return false;
    }

    @Override
    public int getSampleTitle() {
        return R.string.title_sax_example;
    }

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        return client.get(this, URL, headers, null, responseHandler);
    }

    private class Tuple {
        public Integer color;
        public String text;

        public Tuple(int _color, String _text) {
            this.color = _color;
            this.text = _text;
        }
    }

    private class SAXTreeStructure extends DefaultHandler {

        public List<Tuple> responseViews = new ArrayList<Tuple>();

        public void startElement(String namespaceURI, String localName,
                                 String rawName, Attributes atts) {
            responseViews.add(new Tuple(LIGHTBLUE, "Start Element: " + rawName));
        }

        public void endElement(String namespaceURI, String localName,
                               String rawName) {
            responseViews.add(new Tuple(LIGHTBLUE, "End Element  : " + rawName));
        }

        public void characters(char[] data, int off, int length) {
            if (length > 0 && data[0] != '\n') {
                responseViews.add(new Tuple(LIGHTGREEN, "Characters  :  " + new String(data,
                        off, length)));
            }
        }
    }
}
