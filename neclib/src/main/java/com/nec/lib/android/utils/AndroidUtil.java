package com.nec.lib.android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.nec.lib.android.application.MyApplication;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AndroidUtil {

    public static class SystemInfo {
        /**
         * 获取当前手机系统语言。
         *
         * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
         */
        public static String getSystemLanguage() {
            return Locale.getDefault().getLanguage();
        }
        /**
         * 获取当前系统上的语言列表(Locale列表)
         *
         * @return  语言列表
         */
        public static Locale[] getSystemLanguageList() {
            return Locale.getAvailableLocales();
        }
        /**
         * 获取当前手机系统版本号
         *
         * @return  系统版本号
         */
        public static String getSystemVersion() {
            return android.os.Build.VERSION.RELEASE;
        }
        /**
         * 获取手机型号
         *
         * @return  手机型号
         */
        public static String getSystemModel() {
            return android.os.Build.MODEL;
        }
        /**
         * 获取手机厂商
         *
         * @return  手机厂商
         */
        public static String getDeviceBrand() {
            return android.os.Build.BRAND;
        }
        /**
         * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
         *
         * @return  手机IMEI
         */
        @SuppressLint("MissingPermission")
        public static String getIMEI(Context ctx) {
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
            if (tm != null) {
                return tm.getDeviceId();
            }
            return null;
        }
    }

    /**
     * 判断当前应用是否是debug状态
     */
    public static boolean isApkInDebug() {
        try {
            ApplicationInfo info = MyApplication.getInstance().getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *   设置输入法隐藏
     */
    public static void hideKeyboard(final Activity activity, boolean hide) {
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

    public static void hideKeyboard(Activity activity, final EditText editText, boolean hide) {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Service.INPUT_METHOD_SERVICE);
            if(hide)
                inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            else
                inputManager.showSoftInput(editText,0);
        } else {
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, !hide);
            } catch (Exception e) {
            }

            try {
                method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, !hide);
            } catch (Exception e) {
            }
        }
    }

    public static void simulateKey(final int KeyCode) {
        new Thread () {
            public void run () {
                try {
                    Instrumentation inst=new Instrumentation();
                    inst.sendKeyDownUpSync(KeyCode);
                } catch(Exception e) {
                    System.out.println("Exception when sendKeyDownUpSync" + e.toString());
                }
            }
        }.start();
    }

    public static void showToast(String text) {
        Toast.makeText(MyApplication.getInstance(), text, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(String text) {
        Toast.makeText(MyApplication.getInstance(), text, Toast.LENGTH_LONG).show();
    }

    public static void showAlertDialog(String title, String msg) {
        new AlertDialog.Builder(MyApplication.getInstance())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("确定", null)
                .show();
    }

    public static void copyText(String text) {
        ClipboardManager cm = (ClipboardManager) MyApplication.getCurrentActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("text", text));
    }

    public static void copyUrl(String url) {
        ClipboardManager cm = (ClipboardManager) MyApplication.getCurrentActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newRawUri("url", Uri.parse(url)));
    }

    public static void copyIntent(Intent intent) {
        ClipboardManager cm = (ClipboardManager) MyApplication.getCurrentActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newIntent("intent", intent));
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    public static List<View> getAllViews(Activity activity, boolean recursion) {
        List<View> list = getAllChildViews(activity.getWindow().getDecorView(), recursion);
        return list;
    }

    private static List<View> getAllChildViews(View view, boolean recursion) {
        List<View> allchildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                allchildren.add(viewchild);
                if (recursion) {
                    //再次 调用本身（递归）
                    allchildren.addAll(getAllChildViews(viewchild, recursion));
                }
            }
        }
        return allchildren;
    }
}
