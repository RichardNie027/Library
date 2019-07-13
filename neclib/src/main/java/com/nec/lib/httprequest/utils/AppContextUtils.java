package com.nec.lib.httprequest.utils;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * 上下文工具类 {@link ApiConfig init()方法中默认初始化}
 */
public final class AppContextUtils {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    private AppContextUtils() {
        throw new UnsupportedOperationException("You can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        AppContextUtils.mContext = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (mContext != null) return mContext;
        throw new NullPointerException("You must init first");
    }
}
