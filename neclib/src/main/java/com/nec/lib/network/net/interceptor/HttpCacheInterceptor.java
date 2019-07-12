package com.nec.lib.network.net.interceptor;

import android.util.Log;

import com.nec.lib.network.utils.AppContextUtils;
import com.nec.lib.network.utils.NetworkUtils;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.annotations.EverythingIsNonNull;

import java.io.IOException;

/**
 * 配置缓存的拦截器
  */
@SuppressWarnings("ALL")
public class HttpCacheInterceptor implements Interceptor {
    @Override
    @EverythingIsNonNull
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //没网强制从缓存读取
        if (!NetworkUtils.isConnected(AppContextUtils.getContext())) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            Log.e("-->", "no network");
        }

        Response originalResponse = chain.proceed(request);

        if (NetworkUtils.isConnected(AppContextUtils.getContext())) {
            //有网的时候读接口上的@Headers里的配置
            String cacheControl = request.cacheControl().toString();
            return originalResponse.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma")
                    .build();
        } else {
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
                    .removeHeader("Pragma")
                    .build();
        }
    }
}
