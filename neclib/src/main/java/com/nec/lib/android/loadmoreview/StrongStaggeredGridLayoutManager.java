package com.nec.lib.android.loadmoreview;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.AttributeSet;
import android.util.Log;

/**
 * 增强类，忽略IndexOutOfBoundsException错误
 */
public class StrongStaggeredGridLayoutManager extends StaggeredGridLayoutManager {
    public StrongStaggeredGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr,
                                      int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public StrongStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e(this.getClass().getName(), "meet a IOOBE in RecyclerView");
        }
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}
