package com.nec.lib.android.boost;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.nec.lib.android.R;

import java.util.LinkedHashMap;

/**
 * 底部弹出的对话框
 * @param <K,VALUE>
 */
public abstract class BottomDialogFragment<K, V> extends DialogFragment {
    protected View mRootView;
    protected LinkedHashMap<K, V> mDatas = new LinkedHashMap<>();

    public void setDataList(LinkedHashMap<K, V> datas) {
        this.mDatas.clear();
        this.mDatas.putAll(datas);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //设置Dialog窗口特征，去掉标题需要在setContentView前
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        setCancelable(true);

        mRootView = inflater.inflate(setLayoutResourceID(), container, false);
        return mRootView;
    }

    /** Fragment layout xml Resource Id */
    protected abstract int setLayoutResourceID();

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawable(new ColorDrawable(0x80000000));
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.windowAnimations = R.style.BottomDialogAnimation;
        window.setAttributes(params);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    /*
        //设置RecyclerView
        mRecyclerView = mRootView.findViewById(R.id.recyclerView);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this.getContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP); //设置是否换行
        layoutManager.setFlexDirection(FlexDirection.ROW); // 设置主轴排列方式
        layoutManager.setAlignItems(AlignItems.STRETCH);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        mRecyclerView.setLayoutManager(layoutManager);

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this.getContext(), mDatas);
        mRecyclerView.setAdapter(recyclerViewAdapter);
     */
    protected abstract void initView();

    public void showDialog(FragmentManager fragmentManager) {
        if(this == null)
            return;

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(getTag());
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        show(ft, getTag());
    }

}
