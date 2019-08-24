package com.nec.lib.android.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;

import android.text.TextUtils;
import android.view.WindowManager;

import com.nec.lib.android.httprequest.utils.ApiConfig;
import com.nec.lib.android.application.MyApplication;
import com.nec.lib.android.httprequest.use.BaseObserver;
import com.nec.lib.android.utils.AndroidUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;

public class BaseRxAppCompatActivity extends RxAppCompatActivity {

    /**Disposable生命周期容器*/
    protected CompositeDisposable mCompositeDisposable = null;
    /**接收（网络令牌非法的）广播*/
    private QuitAppReceiver mQuitAppReceiver;

    /**全屏，隐藏系统顶部状态栏*/
    protected boolean mFullScreen = false;

    /**自己的弱引用*/
    protected BaseRxAppCompatActivity _this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        if(mFullScreen) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        _this = this;
        //构造生命周期容器
        mCompositeDisposable = new CompositeDisposable();
        //初始化广播接收器
        initReceiver();
    }

    @Override
    protected void onStop() {
        try {
            getApplicationContext().unregisterReceiver(mQuitAppReceiver);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                // Ignore this exception.
            } else {
                throw e;
            }
        }
        super.onStop();
    }

    /**
     *   设置输入法隐藏
     */
    public void hideKeyboard(boolean hide) {
        AndroidUtil.hideKeyboard(_this, hide);
    }

    private void initReceiver() {
        mQuitAppReceiver = new QuitAppReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ApiConfig.getQuitBroadcastReceiverFilter());
        getApplicationContext().registerReceiver(mQuitAppReceiver, filter);
    }

    private class QuitAppReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ApiConfig.getQuitBroadcastReceiverFilter().equals(intent.getAction())) {
                String msg = intent.getStringExtra(BaseObserver.TOKEN_INVALID_TAG);
                if (!TextUtils.isEmpty(msg)) {
                    new AlertDialog.Builder(MyApplication.getInstance())
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("严重")
                            .setMessage(msg + "，应用即将关闭")
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);
                                }
                            })
                            .show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
        }
    }

}
