package com.loopj.android.http;

import android.util.Log;

import org.conscrypt.Conscrypt;

import java.security.Security;

public class ConscryptSSLProvider {
    public static void install(){
        try {
            Security.insertProviderAt(Conscrypt.newProviderBuilder().build(),1);
        }catch (NoClassDefFoundError ex){
            Log.e(AsyncHttpClient.LOG_TAG, "java.lang.NoClassDefFoundError: org.conscrypt.Conscrypt, Please add org.conscrypt.Conscrypt to your dependency");
        }

    }
}
