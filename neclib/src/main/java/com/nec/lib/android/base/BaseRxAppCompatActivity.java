package com.nec.lib.android.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.WindowManager;

import com.nec.lib.android.httprequest.utils.ApiConfig;
import com.nec.lib.android.application.MyApplication;
import com.nec.lib.android.httprequest.use.BaseObserver;
import com.nec.lib.android.utils.AndroidUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.Arrays;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseRxAppCompatActivity extends RxAppCompatActivity {

    /**Disposable生命周期容器*/
    protected CompositeDisposable mCompositeDisposable = null;
    /**接收（网络令牌非法的）广播*/
    private QuitAppReceiver mQuitAppReceiver;

    /**全屏，隐藏系统顶部状态栏*/
    protected boolean mFullScreen = false;

    /**自己的弱引用*/
    protected BaseRxAppCompatActivity _this;

    /**onCreate方法中，首先调用的方法，用于子类中扩展onCreate*/
    protected abstract void beforeCreate(Bundle savedInstanceState);

    /**工具栏*/
    protected Toolbar mToolbar;
    /**工具栏资源ID；子类的默认实现代表无工具栏（即值为0）*/
    protected abstract int setToolbarResourceID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        beforeCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(setLayoutResourceID());
        //全屏
        if(mFullScreen) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        _this = this;
        //构造生命周期容器
        mCompositeDisposable = new CompositeDisposable();
        //初始化广播接收器
        initReceiver();

        if(setToolbarResourceID() != 0) {
            mToolbar = findViewById(setToolbarResourceID());
            setSupportActionBar(mToolbar);
        }
        //初始化View
        initView();
    }

    /**在onCreate方法中最后执行的方法，用于子类中扩展onCreate，与beforeCreate不同的是本方法在onCreate中最后执行*/
    protected abstract void initView();

    /**主布局资源*/
    protected abstract int setLayoutResourceID();

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
        String[] specialSystemModels = {"95S Series"};
        if(Arrays.binarySearch(specialSystemModels, AndroidUtil.SystemInfo.getSystemModel()) < 0)
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
