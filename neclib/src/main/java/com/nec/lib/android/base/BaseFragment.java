package com.nec.lib.android.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.nec.lib.android.utils.AndroidUtil;

import java.util.Arrays;

public abstract class BaseFragment extends Fragment {

    //自己的弱引用
    protected BaseFragment _this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _this = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(setLayoutResourceID(), container, false);
        initView(rootView, getArguments());
        return rootView;
    }

    protected abstract void initView(View rootView, Bundle bundle);

    protected abstract int setLayoutResourceID();

    /**
     *   设置输入法隐藏
     */
    public void hideKeyboard(boolean hide) {
        String[] specialSystemModels = {"95S Series"};
        if(Arrays.binarySearch(specialSystemModels, AndroidUtil.SystemInfo.getSystemModel()) < 0)
            AndroidUtil.hideKeyboard(_this.getActivity(), hide);
    }

}
