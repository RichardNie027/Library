package com.nec.lib.httprequest.utils;

import android.content.Context;
import android.util.ArrayMap;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * 网络请求配置文件
 */
public class ApiConfig implements Serializable {

    private static int mInvalidToken;
    private static String mBaseUrl;
    private static int mDefaultTimeout = 2000;
    private static int mSucceedCode;
    private static String mQuitBroadcastReceiverFilter;
    private static ArrayMap<String, String> mHeaders;
    private static String mToken = "";
    private static boolean mOpenHttps;
    private static SslSocketConfigure mSslSocketConfigure;

    private ApiConfig(Builder builder) {
        mInvalidToken = builder.invalidToken;
        mBaseUrl = builder.baseUrl;
        mDefaultTimeout = builder.defaultTimeout;
        mSucceedCode = builder.succeedCode;
        mQuitBroadcastReceiverFilter = builder.broadcastFilter;
        mHeaders = builder.headers;
        mOpenHttps = builder.openHttps;
        mSslSocketConfigure = builder.sslSocketConfigure;
    }

    public void init(Context appContext) {
        AppContextUtils.init(appContext);
    }

    public static int getInvalidToken() {
        return mInvalidToken;
    }

    public static String getBaseUrl() {
        return mBaseUrl;
    }

    public static int getDefaultTimeout() {
        return mDefaultTimeout;
    }

    public static int getSucceedCode() {
        return mSucceedCode;
    }

    public static String getQuitBroadcastReceiverFilter() {
        return mQuitBroadcastReceiverFilter;
    }

    public static ArrayMap<String, String> getHeads() {
        return mHeaders;
    }

    public static void setHeaders(ArrayMap<String, String> mHeaders) {
        ApiConfig.mHeaders = mHeaders;
    }


    public static String getToken() {
        return mToken;
    }

    public static void setToken(String mToken) {
        ApiConfig.mToken = mToken;
    }

    public static boolean getOpenHttps() {
        return mOpenHttps;
    }


    public static SslSocketConfigure getSslSocketConfigure() {
        return mSslSocketConfigure;
    }

    public static final class Builder  {

        private int invalidToken;

        private String baseUrl;

        private int defaultTimeout;

        private int succeedCode;

        private String broadcastFilter;

        private ArrayMap<String, String> headers;

        private boolean openHttps = false;

        private SslSocketConfigure sslSocketConfigure;

        public Builder setHeaders(ArrayMap<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder setFilter(@NonNull String filter) {
            this.broadcastFilter = filter;
            return this;
        }


        public Builder setSucceedCode(int succeedCode) {
            this.succeedCode = succeedCode;
            return this;
        }

        public Builder setBaseUrl(String mBaseUrl) {
            this.baseUrl = mBaseUrl;
            return this;
        }

        public Builder setInvalidToken(int invalidToken) {
            this.invalidToken = invalidToken;
            return this;
        }

        public Builder setDefaultTimeout(int defaultTimeout) {
            this.defaultTimeout = defaultTimeout;
            return this;
        }

        public Builder setOpenHttps(boolean open) {
            this.openHttps = open;
            return this;
        }

        public Builder setSslSocketConfigure(SslSocketConfigure sslSocketConfigure) {
            this.sslSocketConfigure = sslSocketConfigure;
            return this;
        }

        public ApiConfig build() {
            return new ApiConfig(this);
        }
    }
}
