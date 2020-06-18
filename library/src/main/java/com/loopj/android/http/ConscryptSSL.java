package com.loopj.android.http;

import android.util.Log;

import org.conscrypt.Conscrypt;

import java.security.Security;

public class ConscryptSSL {

    public void install(){
        try {
            Security.insertProviderAt(Conscrypt.newProvider(),1);
        }catch (NoClassDefFoundError ex){
            Log.e(AsyncHttpClient.LOG_TAG, "java.lang.NoClassDefFoundError: org.conscrypt.Conscrypt, Please add org.conscrypt.Conscrypt to your dependency");
        }

    }
}
