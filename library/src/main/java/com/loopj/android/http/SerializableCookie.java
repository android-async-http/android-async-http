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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

/**
 * A wrapper class around {@link Cookie} and/or {@link BasicClientCookie} designed for use in {@link
 * PersistentCookieStore}.
 */
public class SerializableCookie implements Serializable {
    private static final long serialVersionUID = 6374381828722046732L;

    private transient final Cookie cookie;
    private transient BasicClientCookie clientCookie;

    public SerializableCookie(Cookie cookie) {
        this.cookie = cookie;
    }

    public Cookie getCookie() {
        Cookie bestCookie = cookie;
        if (clientCookie != null) {
            bestCookie = clientCookie;
        }
        return bestCookie;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(cookie.getName());
        out.writeObject(cookie.getValue());
        out.writeObject(cookie.getComment());
        out.writeObject(cookie.getDomain());
        out.writeObject(cookie.getExpiryDate());
        out.writeObject(cookie.getPath());
        out.writeInt(cookie.getVersion());
        out.writeBoolean(cookie.isSecure());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        String key = (String) in.readObject();
        String value = (String) in.readObject();
        clientCookie = new BasicClientCookie(key, value);
        clientCookie.setComment((String) in.readObject());
        clientCookie.setDomain((String) in.readObject());
        clientCookie.setExpiryDate((Date) in.readObject());
        clientCookie.setPath((String) in.readObject());
        clientCookie.setVersion(in.readInt());
        clientCookie.setSecure(in.readBoolean());
    }
}