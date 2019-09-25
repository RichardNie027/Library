package com.nec.lib.android.base;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.nec.lib.android.utils.AndroidUtil;

import java.util.Arrays;

@Deprecated
public abstract class BaseAppCompatActivity extends AppCompatActivity {

    /**全屏，隐藏系统顶部状态栏*/
    protected boolean mFullScreen = false;

    //自己的弱引用
    protected BaseAppCompatActivity _this;

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

    /**
     *   隐藏输入法
     */
    @Deprecated
    protected void hideKeyboard(View view) {
        InputMethodManager im = (InputMethodManager) _this.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     *   设置输入法隐藏
     */
    protected void hideKeyboard(boolean hide) {
        String[] specialSystemModels = {"95S Series"};
        if(Arrays.binarySearch(specialSystemModels, AndroidUtil.SystemInfo.getSystemModel()) < 0)
            AndroidUtil.hideKeyboard(_this, hide);
    }

}
