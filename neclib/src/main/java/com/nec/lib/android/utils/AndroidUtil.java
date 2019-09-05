package com.nec.lib.android.utils;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.nec.lib.android.application.MyApplication;

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
}
