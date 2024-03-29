package com.nec.lib.android.httprequest.net.interceptor;

import android.os.Build;

import com.nec.lib.android.httprequest.utils.ApiConfig;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.annotations.EverythingIsNonNull;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 请求头拦截器
 */
@SuppressWarnings("ALL")
public class HttpHeaderInterceptor implements Interceptor {

    @Override
    @EverythingIsNonNull
    public Response intercept(Chain chain) throws IOException {

        Request originalRequest = chain.request();

        Map<String, String> headers = ApiConfig.getHeaders();

        String token = ApiConfig.getToken();

        Request.Builder authorization = originalRequest.newBuilder()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .addHeader("Connection", "close")
                .addHeader("Accept-Encoding", "identity");

        //动态添加Header
        if (null != headers) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                headers.forEach(new BiConsumer<String, String>() {
                    @Override
                    public void accept(String key, String value) {
                        authorization.addHeader(key, value);
                    }
                });
            } else {
                Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    authorization.addHeader(entry.getKey(), entry.getValue());
                }
            }

        }

        Request build = authorization.build();

        return chain.proceed(build);
    }
}
