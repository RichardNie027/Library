package com.nec.lib.android.utils;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class AndroidUtil {

    /**
     *   设置输入法隐藏
     */
    public static void hideKeyboard(Activity activity, boolean hide) {
        if(hide) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                }
            });
        }
    }

    /**
     *   设置输入法隐藏，指定Window
     */
    public static void hideKeyboard(Activity activity, Window window, boolean hide) {
        if(hide) {
            window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                }
            });
        }
    }

}
