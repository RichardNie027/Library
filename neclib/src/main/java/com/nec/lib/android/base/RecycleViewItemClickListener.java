package com.nec.lib.android.base;

import android.view.View;

/**
 * RecycleView的Item的Click事件监听器接口
 */
public interface RecycleViewItemClickListener {
    public void onItemClick(View view, int position);
    public boolean onItemLongClick(View view, int position);
}
