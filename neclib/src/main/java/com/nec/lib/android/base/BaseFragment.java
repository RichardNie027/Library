package com.nec.lib.android.base;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

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
