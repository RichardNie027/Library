package com.nec.lib.android.utils;

import android.transition.TransitionManager;

import androidx.annotation.IdRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

/* 示例

public void onApplyClick(View view) {
    constraintUtil = new ConstraintUtil(constraintLayout);
    ConstraintUtil.ConstraintBegin begin = constraintUtil.beginWithAnim();

    begin.clear(R.id.button1,R.id.button2,R.id.button3);

    begin.Left_toLeftOf(R.id.button1,R.id.main);
    begin.Left_toRightOf(R.id.button2,R.id.button1);
    begin.Right_toLeftOf(R.id.button2,R.id.button3);
    begin.Right_toRightOf(R.id.button3,R.id.main);

    begin.setWidth(R.id.button1, ConstraintSet.WRAP_CONTENT);
    begin.setWidth(R.id.button2, ConstraintSet.WRAP_CONTENT);
    begin.setWidth(R.id.button3, ConstraintSet.WRAP_CONTENT);
    begin.setHeight(R.id.button1, ConstraintSet.WRAP_CONTENT);
    begin.setHeight(R.id.button2, ConstraintSet.WRAP_CONTENT);
    begin.setHeight(R.id.button3, ConstraintSet.WRAP_CONTENT);

    begin.commit();
}

*/

/**
 * 代码动态修改 ConstraintLayout 内控件布局的辅助类
 */
public class ConstraintUtil {
    private ConstraintLayout constraintLayout;
    private ConstraintBegin begin;
    private ConstraintSet applyConstraintSet = new ConstraintSet();
    private ConstraintSet resetConstraintSet = new ConstraintSet();

    public ConstraintUtil(ConstraintLayout constraintLayout)
    {
        this.constraintLayout = constraintLayout;
        resetConstraintSet.clone(constraintLayout);
    }

    /**
     * 开始修改
     * @return
     */
    public ConstraintBegin begin()
    {
        synchronized (ConstraintBegin.class)
        {
            if (begin == null)
            {
                begin = new ConstraintBegin();
            }
        }
        applyConstraintSet.clone(constraintLayout);
        return begin;
    }

    /**
     * 带动画的修改
     * @return
     */
    public ConstraintBegin beginWithAnim()
    {
        TransitionManager.beginDelayedTransition(constraintLayout);
        return begin();
    }

    /**
     * 重置
     */
    public void reSet()
    {
        resetConstraintSet.applyTo(constraintLayout);
    }

    /**
     * 带动画的重置
     */
    public void reSetWidthAnim()
    {
        TransitionManager.beginDelayedTransition(constraintLayout);
        resetConstraintSet.applyTo(constraintLayout);
    }

    public class ConstraintBegin
    {
        /**
         * 清除关系<br/>
         * 注意：这里不仅仅会清除关系，还会清除对应控件的宽高为 w:0,h:0
         * @param viewIds
         * @return
         */
        public ConstraintBegin clear(@IdRes int... viewIds)
        {
            for (int viewId : viewIds)
            {
                applyConstraintSet.clear(viewId);
            }
            return this;
        }

        /**
         * 清除某个控件的，某个关系
         * @param viewId
         * @param anchor
         * @return
         */
        public ConstraintBegin clear(int viewId,int anchor)
        {
            applyConstraintSet.clear(viewId,anchor);
            return this;
        }
        /**
         * 为某个控件设置 margin
         * @param viewId 某个控件ID
         * @param left marginLeft
         * @param top   marginTop
         * @param right marginRight
         * @param bottom marginBottom
         * @return
         */
        public ConstraintBegin setMargin(@IdRes int viewId, int left, int top, int right, int bottom)
        {
            setMarginLeft(viewId,left);
            setMarginTop(viewId,top);
            setMarginRight(viewId,right);
            setMarginBottom(viewId,bottom);
            return this;
        }

        /**
         * 为某个控件设置 marginLeft
         * @param viewId 某个控件ID
         * @param left marginLeft
         * @return
         */
        public ConstraintBegin setMarginLeft(@IdRes int viewId, int left)
        {
            applyConstraintSet.setMargin(viewId, ConstraintSet.LEFT, left);
            return this;
        }

        /**
         * 为某个控件设置 marginRight
         * @param viewId 某个控件ID
         * @param right marginRight
         * @return
         */
        public ConstraintBegin setMarginRight(@IdRes int viewId, int right)
        {
            applyConstraintSet.setMargin(viewId, ConstraintSet.RIGHT, right);
            return this;
        }

        /**
         * 为某个控件设置 marginTop
         * @param viewId 某个控件ID
         * @param top marginTop
         * @return
         */
        public ConstraintBegin setMarginTop(@IdRes int viewId, int top)
        {
            applyConstraintSet.setMargin(viewId, ConstraintSet.TOP, top);
            return this;
        }

        /**
         * 为某个控件设置marginBottom
         * @param viewId 某个控件ID
         * @param bottom marginBottom
         * @return
         */
        public ConstraintBegin setMarginBottom(@IdRes int viewId, int bottom)
        {
            applyConstraintSet.setMargin(viewId, ConstraintSet.BOTTOM, bottom);
            return this;
        }

        /**
         * 为某个控件设置关联关系 left_to_left_of
         * @param startId
         * @param endId
         * @return
         */
        public ConstraintBegin Left_toLeftOf(@IdRes int startId, @IdRes int endId)
        {
            applyConstraintSet.connect(startId, ConstraintSet.LEFT, endId, ConstraintSet.LEFT);
            return this;
        }

        /**
         * 为某个控件设置关联关系 left_to_right_of
         * @param startId
         * @param endId
         * @return
         */
        public ConstraintBegin Left_toRightOf(@IdRes int startId, @IdRes int endId)
        {
            applyConstraintSet.connect(startId, ConstraintSet.LEFT, endId, ConstraintSet.RIGHT);
            return this;
        }

        /**
         * 为某个控件设置关联关系 top_to_top_of
         * @param startId
         * @param endId
         * @return
         */
        public ConstraintBegin Top_toTopOf(@IdRes int startId, @IdRes int endId)
        {
            applyConstraintSet.connect(startId, ConstraintSet.TOP, endId, ConstraintSet.TOP);
            return this;
        }

        /**
         * 为某个控件设置关联关系 top_to_bottom_of
         * @param startId
         * @param endId
         * @return
         */
        public ConstraintBegin Top_toBottomOf(@IdRes int startId, @IdRes int endId)
        {
            applyConstraintSet.connect(startId, ConstraintSet.TOP, endId, ConstraintSet.BOTTOM);
            return this;
        }
        /**
         * 为某个控件设置关联关系 right_to_left_of
         * @param startId
         * @param endId
         * @return
         */
        public ConstraintBegin Right_toLeftOf(@IdRes int startId, @IdRes int endId)
        {
            applyConstraintSet.connect(startId, ConstraintSet.RIGHT, endId, ConstraintSet.LEFT);
            return this;
        }
        /**
         * 为某个控件设置关联关系 right_to_right_of
         * @param startId
         * @param endId
         * @return
         */
        public ConstraintBegin Right_toRightOf(@IdRes int startId, @IdRes int endId)
        {
            applyConstraintSet.connect(startId, ConstraintSet.RIGHT, endId, ConstraintSet.RIGHT);
            return this;
        }
        /**
         * 为某个控件设置关联关系 bottom_to_bottom_of
         * @param startId
         * @param endId
         * @return
         */
        public ConstraintBegin Bottom_toBottomOf(@IdRes int startId, @IdRes int endId)
        {
            applyConstraintSet.connect(startId, ConstraintSet.BOTTOM, endId, ConstraintSet.BOTTOM);
            return this;
        }

        /**
         * 为某个控件设置关联关系 bottom_to_top_of
         * @param startId
         * @param endId
         * @return
         */
        public ConstraintBegin Bottom_toTopOf(@IdRes int startId, @IdRes int endId)
        {
            applyConstraintSet.connect(startId, ConstraintSet.BOTTOM, endId, ConstraintSet.TOP);
            return this;
        }

        /**
         * 为某个控件设置宽度
         * @param viewId
         * @param width
         * @return
         */
        public ConstraintBegin setWidth(@IdRes int viewId, int width)
        {
            applyConstraintSet.constrainWidth(viewId, width);
            return this;
        }

        /**
         * 某个控件设置高度
         * @param viewId
         * @param height
         * @return
         */
        public ConstraintBegin setHeight(@IdRes int viewId, int height)
        {
            applyConstraintSet.constrainHeight(viewId, height);
            return this;
        }

        /**
         * 提交应用生效
         */
        public void commit()
        {
            applyConstraintSet.applyTo(constraintLayout);
        }
    }
}
