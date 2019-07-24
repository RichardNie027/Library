package com.nec.lib.android.httprequest.utils;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * 上下文工具类 {@link ApiConfig init()方法中默认初始化}
 */
public final class AppContextUtil {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext = null;

    private AppContextUtil() {
        throw new UnsupportedOperationException("You can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        AppContextUtil.mContext = context.getApplicationContext();
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
