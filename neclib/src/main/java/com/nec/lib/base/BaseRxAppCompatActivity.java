package com.nec.lib.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.nec.lib.application.MyApplication;
import com.nec.lib.httprequest.use.BaseObserver;
import com.nec.lib.httprequest.utils.ApiConfig;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;

public class BaseRxAppCompatActivity extends RxAppCompatActivity implements View.OnFocusChangeListener, View.OnTouchListener {

    /**Disposable生命周期容器*/
    protected CompositeDisposable mCompositeDisposable = null;
    /**接收（网络令牌非法的）广播*/
    private QuitAppReceiver mQuitAppReceiver;

    /**全屏，隐藏系统顶部状态栏*/
    protected boolean mFullScreen = false;
    /**需要隐藏输入法的视图*/
    protected View[] mHideInputViews;

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

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        //用于控件失去焦点时隐藏输入法
        if (!hasFocus) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        //用于隐藏输入法
        if(EditText.class.isAssignableFrom(view.getClass())) {
            ((EditText) view).setInputType(InputType.TYPE_NULL);
            return false;
        } else
            return true;
    }

    /**
     *     设置控件的OnFocusChangeListener
     *     用于控件失去焦点时隐藏输入法。
     *
     *     在Activity顶层布局中，需要设置：
     *     android:clickable="true"
     *     android:focusableInTouchMode="true"
     */
    public void setOnFocusChangeListener(View... views) {
        for(View view: views) {
            if(view != null)
                view.setOnFocusChangeListener(this);
        }
    }

    /**
     *    设置控件的OnTouchListener
     *    用于隐藏输入法。
     */
    protected void setHideInputViews(View... views) {
        for(View view: views) {
            if(view != null)
                view.setOnTouchListener(this);
        }
        mHideInputViews = views;
    }

    /**
     *   隐藏输入法
     */
    public void hideKeyboard(View view) {
        InputMethodManager im = (InputMethodManager) _this.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
