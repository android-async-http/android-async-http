package com.loopj.android.http;

import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

public class SerializableCookie implements Serializable {
    private transient Cookie cookie;
    private transient BasicClientCookie clientCookie;

    public SerializableCookie(Cookie cookie) {
        this.cookie = cookie;
    }

    public Cookie getCookie() {
        Cookie bestCookie = cookie;
        if(clientCookie != null) {
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
        String name = (String)in.readObject();
        String value = (String)in.readObject();
        clientCookie = new BasicClientCookie(name, value);
        clientCookie.setComment((String)in.readObject());
        clientCookie.setDomain((String)in.readObject());
        clientCookie.setExpiryDate((Date)in.readObject());
        clientCookie.setPath((String)in.readObject());
        clientCookie.setVersion(in.readInt());
        clientCookie.setSecure(in.readBoolean());
    }
}