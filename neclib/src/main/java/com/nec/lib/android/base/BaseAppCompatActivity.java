package com.nec.lib.android.base;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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

    protected abstract void beforeCreate(Bundle savedInstanceState);

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
        //初始化View
        initView();
    }

    protected abstract void initView();

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
