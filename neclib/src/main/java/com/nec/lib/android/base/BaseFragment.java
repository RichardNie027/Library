package com.nec.lib.android.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.nec.lib.android.utils.AndroidUtil;

public class BaseFragment extends Fragment {

    //自己的弱引用
    protected BaseFragment _this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _this = this;
    }

    /**
     *   设置输入法隐藏
     */
    public void hideKeyboard(boolean hide) {
        AndroidUtil.hideKeyboard(_this.getActivity(), hide);
    }

}
