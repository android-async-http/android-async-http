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

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A collection of string request parameters or files to send along with
 * requests made from an {@link AsyncHttpClient} instance.
 * <p>&nbsp;</p>
 * For example:
 * <p>&nbsp;</p>
 * <pre>
 * RequestParams params = new RequestParams();
 * params.put("username", "james");
 * params.put("password", "123456");
 * params.put("email", "my&#064;email.com");
 * params.put("profile_picture", new File("pic.jpg")); // Upload a File
 * params.put("profile_picture2", someInputStream); // Upload an InputStream
 * params.put("profile_picture3", new ByteArrayInputStream(someBytes)); // Upload some bytes
 * 
 * Map<String, String> map = new HashMap<String, String>();
 * map.put("first_name", "James");
 * map.put("last_name", "Smith");
 * params.put("user", map); // url params: "user[first_name]=James&user[last_name]=Smith"
 *
 * Set<String> set = new HashSet<String>(); // unordered collection
 * set.add("music");
 * set.add("art");
 * params.put("like", set); // url params: "like=music&like=art"
 * 
 * List<String> list = new ArrayList<String>(); // Ordered collection
 * list.add("Java");
 * list.add("C");
 * params.put("languages", list); // url params: "languages[]=Java&languages[]=C"
 * 
 * String[] colors = { "blue", "yellow" }; // Ordered collection
 * params.put("colors", colors); // url params: "colors[]=blue&colors[]=yellow"
 * 
 * List<Map<String, String>> listOfMaps = new ArrayList<Map<String, String>>();
 * Map<String, String> user1 = new HashMap<String, String>();
 * user1.put("age", "30");
 * user1.put("gender", "male");
 * Map<String, String> user2 = new HashMap<String, String>();
 * user2.put("age", "25");
 * user2.put("gender", "female");
 * listOfMaps.add(user1);
 * listOfMaps.add(user2);
 * params.put("users", listOfMaps); // url params: "users[][age]=30&users[][gender]=male&users[][age]=25&users[][gender]=female"
 *
 * AsyncHttpClient client = new AsyncHttpClient();
 * client.post("http://myendpoint.com", params, responseHandler);
 * </pre>
 */
public class RequestParams {

    private static final String LOG_TAG = "RequestParams";

    protected ConcurrentHashMap<String, String> urlParams;
    protected ConcurrentHashMap<String, StreamWrapper> streamParams;
    protected ConcurrentHashMap<String, FileWrapper> fileParams;
    protected ConcurrentHashMap<String, Object> urlParamsWithObjects;

    /**
     * Constructs a new empty <code>RequestParams</code> instance.
     */
    public RequestParams() {
        init();
    }

    /**
     * Constructs a new RequestParams instance containing the key/value
     * string params from the specified map.
     *
     * @param source the source key/value string map to add.
     */
    public RequestParams(Map<String, String> source) {
        init();

        for (Map.Entry<String, String> entry : source.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Constructs a new RequestParams instance and populate it with a single
     * initial key/value string param.
     *
     * @param key   the key name for the intial param.
     * @param value the value string for the initial param.
     */
    public RequestParams(String key, String value) {
        init();

        put(key, value);
    }

    /**
     * Constructs a new RequestParams instance and populate it with multiple
     * initial key/value string param.
     *
     * @param keysAndValues a sequence of keys and values. Objects are
     *                      automatically converted to Strings (including the value {@code null}).
     * @throws IllegalArgumentException if the number of arguments isn't even.
     */
    public RequestParams(Object... keysAndValues) {
        init();
        int len = keysAndValues.length;
        if (len % 2 != 0)
            throw new IllegalArgumentException("Supplied arguments must be even");
        for (int i = 0; i < len; i += 2) {
            String key = String.valueOf(keysAndValues[i]);
            String val = String.valueOf(keysAndValues[i + 1]);
            put(key, val);
        }
    }

    /**
     * Adds a key/value string pair to the request.
     *
     * @param key   the key name for the new param.
     * @param value the value string for the new param.
     */
    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
    }

    /**
     * Adds a file to the request.
     *
     * @param key  the key name for the new param.
     * @param file the file to add.
     * @throws java.io.FileNotFoundException throws if wrong File argument was passed
     */
    public void put(String key, File file) throws FileNotFoundException {
        put(key, file, null);
    }

    /**
     * Adds a file to the request.
     *
     * @param key         the key name for the new param.
     * @param file        the file to add.
     * @param contentType the content type of the file, eg. application/json
     * @throws java.io.FileNotFoundException throws if wrong File argument was passed
     */
    public void put(String key, File file, String contentType) throws FileNotFoundException {
        if (key != null && file != null) {
            fileParams.put(key, new FileWrapper(file, contentType));
        }
    }

    /**
     * Adds an input stream to the request.
     *
     * @param key    the key name for the new param.
     * @param stream the input stream to add.
     */
    public void put(String key, InputStream stream) {
        put(key, stream, null);
    }

    /**
     * Adds an input stream to the request.
     *
     * @param key    the key name for the new param.
     * @param stream the input stream to add.
     * @param name   the name of the stream.
     */
    public void put(String key, InputStream stream, String name) {
        put(key, stream, name, null);
    }

    /**
     * Adds an input stream to the request.
     *
     * @param key         the key name for the new param.
     * @param stream      the input stream to add.
     * @param name        the name of the stream.
     * @param contentType the content type of the file, eg. application/json
     */
    public void put(String key, InputStream stream, String name, String contentType) {
        if (key != null && stream != null) {
            streamParams.put(key, new StreamWrapper(stream, name, contentType));
        }
    }

    /**
     * Adds param with non-string value (e.g. Map, List, Set).
     * @param key   the key name for the new param.
     * @param value the non-string value object for the new param.
     */
    public void put(String key, Object value)  {
        if (key != null && value != null) {
            urlParamsWithObjects.put(key, value);
        }
    }
            
    /**
     * Adds string value to param which can have more than one value.
     * @param key   the key name for the param, either existing or new.
     * @param value the value string for the new param.
     */
    public void add(String key, String value) {
        if (key != null && value != null) {
            Object params = urlParamsWithObjects.get(key);
            if (params == null) {
                // Backward compatible, which will result in "k=v1&k=v2&k=v3"
                params = new HashSet<String>();
                this.put(key, params);
            }
            if (params instanceof List) {
                ((List<Object>) params).add(value);
            } else if (params instanceof Set) {
                ((Set<Object>) params).add(value);
            }
        }
    }
    
    /**
     * Removes a parameter from the request.
     *
     * @param key the key name for the parameter to remove.
     */
    public void remove(String key) {
        urlParams.remove(key);
        streamParams.remove(key);
        fileParams.remove(key);
        urlParamsWithObjects.remove(key);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            if (result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }

        for (ConcurrentHashMap.Entry<String, StreamWrapper> entry : streamParams.entrySet()) {
            if (result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append("STREAM");
        }

        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
            if (result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append("FILE");
        }

        List<BasicNameValuePair> params = getParamsList(null, urlParamsWithObjects);
        for (BasicNameValuePair kv : params) {
            if (result.length() > 0)
                result.append("&");
            
            result.append(kv.getName());
            result.append("=");
            result.append(kv.getValue());
        }

        return result.toString();
    }

    /**
     * Returns an HttpEntity containing all request parameters
     *
     * @param progressHandler HttpResponseHandler for reporting progress on entity submit
     * @return HttpEntity resulting HttpEntity to be included along with {@link org.apache.http.client.methods.HttpEntityEnclosingRequestBase}
     * @throws IOException if one of the streams cannot be read
     */
    public HttpEntity getEntity(AsyncHttpResponseHandler progressHandler) throws IOException {
        if (streamParams.isEmpty() && fileParams.isEmpty()) {
            return createFormEntity();
        } else {
            return createMultipartEntity(progressHandler);
        }
    }

    private HttpEntity createFormEntity() {
        try {
            return new UrlEncodedFormEntity(getParamsList(), HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            return null; // Actually cannot happen when using utf-8
        }
    }

    private HttpEntity createMultipartEntity(AsyncHttpResponseHandler progressHandler) throws IOException {
        SimpleMultipartEntity entity = new SimpleMultipartEntity(progressHandler);

        // Add string params
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            entity.addPart(entry.getKey(), entry.getValue());
        }

        // Add non-string params
        List<BasicNameValuePair> params = getParamsList(null, urlParamsWithObjects);
        for (BasicNameValuePair kv : params) {
        	entity.addPart(kv.getName(), kv.getValue());
        }

        // Add stream params
        for (ConcurrentHashMap.Entry<String, StreamWrapper> entry : streamParams.entrySet()) {
            StreamWrapper stream = entry.getValue();
            if (stream.inputStream != null) {
                entity.addPart(entry.getKey(), stream.name, stream.inputStream,
                        stream.contentType);
            }
        }

        // Add file params
        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
            FileWrapper fileWrapper = entry.getValue();
            entity.addPart(entry.getKey(), fileWrapper.file, fileWrapper.contentType);
        }

        return entity;
    }

    private void init() {
        urlParams = new ConcurrentHashMap<String, String>();
        streamParams = new ConcurrentHashMap<String, StreamWrapper>();
        fileParams = new ConcurrentHashMap<String, FileWrapper>();
        urlParamsWithObjects = new ConcurrentHashMap<String, Object>();
    }

    protected List<BasicNameValuePair> getParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

        for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        lparams.addAll(getParamsList(null, urlParamsWithObjects));

        return lparams;
    }

    private List<BasicNameValuePair> getParamsList(String key, Object value) {
        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
        if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            List<String> list = new ArrayList<String>(map.keySet());
            // Ensure consistent ordering in query string
            Collections.sort(list);
            for (String nestedKey : list) {
                Object nestedValue = map.get(nestedKey);
                if (nestedValue != null) {
                    params.addAll(getParamsList(key == null ? nestedKey : String.format("%s[%s]", key, nestedKey),
                            nestedValue));
                }
            }
        } else if (value instanceof List) {
            List<Object> list = (List<Object>) value;
            for (Object nestedValue : list) {
                params.addAll(getParamsList(String.format("%s[]", key), nestedValue));
            }
        } else if (value instanceof Object[]) {
            Object[] array = (Object[]) value;
            for (Object nestedValue : array) {
                params.addAll(getParamsList(String.format("%s[]", key), nestedValue));
            }
        } else if (value instanceof Set) {
            Set<Object> set = (Set<Object>) value;
            for (Object nestedValue : set) {
                params.addAll(getParamsList(key, nestedValue));
            }
        } else if (value instanceof String) {
            params.add(new BasicNameValuePair(key, (String) value));
        }
        return params;
    }
    
    protected String getParamString() {
        return URLEncodedUtils.format(getParamsList(), HTTP.UTF_8);
    }

    private static class FileWrapper {
        public File file;
        public String contentType;

        public FileWrapper(File file, String contentType) {
            this.file = file;
            this.contentType = contentType;
        }
    }

    private static class StreamWrapper {
        public InputStream inputStream;
        public String name;
        public String contentType;

        public StreamWrapper(InputStream inputStream, String name, String contentType) {
            this.inputStream = inputStream;
            this.name = name;
            this.contentType = contentType;
        }
    }
}
