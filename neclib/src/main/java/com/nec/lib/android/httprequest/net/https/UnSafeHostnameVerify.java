package com.nec.lib.android.httprequest.net.https;

import android.annotation.SuppressLint;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class UnSafeHostnameVerify implements HostnameVerifier {
    @SuppressLint("BadHostnameVerifier")
    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
