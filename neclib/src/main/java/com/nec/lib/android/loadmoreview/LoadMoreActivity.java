package com.nec.lib.android.loadmoreview;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nec.lib.android.R;
import com.nec.lib.android.base.BaseFragment;
import com.nec.lib.android.base.BaseRxAppCompatActivity;
import com.nec.lib.android.utils.AndroidUtil;
import com.nec.lib.android.utils.ImageUtil;
import com.nec.lib.android.utils.ResUtil;

import java.lang.reflect.Constructor;

/* activity_default_load_more_list.xml 默认配置
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_marginLeft="0dp"
    android:layout_marginRight="0dp"
    android:orientation="vertical">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">

        <TextView
            android:id="@+id/mode_switch_button"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="显示模式"
            android:textSize="14dp" />
    </LinearLayout>

    <androidx.swiperefreshlayout.widget.SwipeRefreshLayout
        android:id="@+id/refresh_layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <com.nec.lib.android.loadmoreview.LoadMoreRecyclerView
            android:id="@+id/recycler_list"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:layoutManager="LinearLayoutManager"
            tools:listitem="@layout/fragment_default_load_more_list_item_linear" />

    </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
</LinearLayout>
 */

/**
 * 无限加载Activity
 * 定制布局文件，对应更新子类的sLayoutOfFragmentItemList
 *
 * 调用示例：
 * RecordFragment.newInstance(RecordFragment.class, RecordRecyclerViewItemAdapter.class, new RecordListDataRequest(), dataBundle, LoadMoreFragment.DisplayMode.LINEAR, 3);
 *
 * @param <TAdapter extends RecyclerViewItemAdapter>
 */
public abstract class LoadMoreActivity<TAdapter extends RecyclerViewItemAdapter> extends BaseRxAppCompatActivity {

    ////////////////资源（Layout/ID）前提 START////////////////
    //布局xml文件的内部资源ID
    /**布局文件中SwipeRefreshLayout的ID，必要*/
    protected String mIdOfSwipeRefreshLayout = "refresh_layout";
    /**布局文件中LoadMoreRecyclerView的ID，必要*/
    protected String mIdOfRecyclerView = "recycler_list";
    /**切换模式的视图控件，类型为TextView或Button，非必要*/
    protected String mIdOfModeSwitchButton = "mode_switch_button";
    ////////////////资源（Layout/ID）前提 END////////////////


    ////////////////Fragment参数静态资源名 START////////////////
    /**内部使用的列数关键字名称，Staggered模式下的列数*/
    private static String ARG_COLUMN_COUNT = "column-count";
    /**内部使用的显示模式关键字名称*/
    private static String ARG_DISPLAY_MODE = "display-mode";
    /**内部使用的异步数据请求接口AsynDataRequest关键字名称*/
    private static String ASYN_DATA_REQUEST = "asyn-data-request";
    ////////////////Fragment参数静态资源名 END////////////////


    ////////////////异步数据 START////////////////
    /**异步数据请求对象*/
    protected AsynDataRequest mAsynDataRequest;
    /**当前页码,zero-base*/
    protected int mPage = 0;
    /**
     * 数据条件 Bundle mDataBundle
     * 在三处使用：
     * 1、进入页面初始装载数据onCreate -> initView -> doDataBundle4AsynDataRequest
     * 2、刷新数据SwipeRefreshLayout.OnRefreshListener：下拉刷新
     * 3、加载更多LoadMoreRecyclerView.LoadMoreListener：上拉加载（初始加载的首批数据不足一屏时，自动触发）*/
    protected Bundle mDataBundle = new Bundle();
    ////////////////异步数据 END////////////////


    ////////////////可循环视图 START////////////////
    /**支持加载更多的RecyclerView*/
    protected LoadMoreRecyclerView mRecyclerView;
    /**行项目适配器*/
    protected TAdapter myRecyclerViewItemAdapter;
    /**行项目适配器子类（RecyclerViewItemAdapter），用于反射构造*/
    protected Class<TAdapter> mAdapterClass;
    ////////////////可循环视图 END////////////////


    ////////////////显示模式 START////////////////
    /**切换模式的控件,TEXTVIEW或BUTTON*/
    private View mSwitchModeView = null;
    /**显示模式*/
    protected DisplayMode mDisplayMode = DisplayMode.LINEAR;
    /**Staggered模式列数*/
    protected int mColumnCount = 1;
    ////////////////显示模式 END////////////////


    ////////////////类的其它私有属性////////////////
    /**被嵌套的SwipeRefreshLayout布局对象*/
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    public LoadMoreActivity() {
    }

    /**
     * 必须调用此函数，传必要参数
     * @param itemAdapterClass 行适配器的类
     * @param asynDataRequest 异步数据请求
     * displayMode 显示模式 Linear
     */
    public void setArguments(Class<TAdapter> itemAdapterClass, AsynDataRequest asynDataRequest) {
        setArguments(itemAdapterClass, asynDataRequest, DisplayMode.LINEAR);
    }

    /**
     * 必须调用此函数，传必要参数
     * @param itemAdapterClass 行适配器的类
     * @param asynDataRequest 异步数据请求
     * @param displayMode 显示模式 Linear/Staggered
     * Staggered显示2列
     */
    public void setArguments(Class<TAdapter> itemAdapterClass, AsynDataRequest asynDataRequest, DisplayMode displayMode) {
        setArguments(itemAdapterClass, asynDataRequest, displayMode, 2);
    }

    /**
     * 必须调用此函数，传必要参数
     * @param itemAdapterClass 行适配器的类
     * @param asynDataRequest 异步数据请求
     * @param displayMode 显示模式 Linear/Staggered
     * @param columns Staggered显示列数
     */
    public void setArguments(Class<TAdapter> itemAdapterClass, AsynDataRequest asynDataRequest, DisplayMode displayMode, int columns) {
        mColumnCount = columns;
        mDisplayMode = displayMode;
        mAsynDataRequest = asynDataRequest;
        mAdapterClass = itemAdapterClass;
    }

    /**
     * ##子类需要重写方法##
     * 调用时机：onCreate的最后 -> initView，initView的开始 -> initViewBefore
     * 作用：1、调用setArguments设置RecyclerView参数；2、组装mDataBundle（数据获取AsynDataRequest需要）
     * mDataBundle.putLong/putInt/putFloat/putString/putBoolean/putSerializable
     */
    protected abstract void initViewBegin(View rootView);

    /**
     * 初始化视图
     * 调用时机：onCreate的最后 -> initView，initView中加载初始数据前 -> initViewEnd
     * 作用：初始化控件
     */
    protected abstract void initViewEnd(View rootView);

    @Override
    @Deprecated
    protected void initView() {
        View rootView = this.getWindow().getDecorView().findViewById(android.R.id.content);
        initViewBegin(rootView);

        // find view
        mRecyclerView = findViewById(ResUtil.getId(mIdOfRecyclerView, _this));
        int switchControlResId = ResUtil.getId(mIdOfModeSwitchButton, _this);
        if(mSwipeRefreshLayout != null)
            mSwipeRefreshLayout = findViewById(ResUtil.getId(mIdOfSwipeRefreshLayout, _this));

        // control view
        mRecyclerView.setHasFixedSize(true);
        if(switchControlResId != 0) {
            mSwitchModeView = findViewById(switchControlResId);
            if(mSwitchModeView != null) {
                mSwitchModeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        modeAdaptation(view, mDisplayMode == DisplayMode.LINEAR ? DisplayMode.STAGGERED : DisplayMode.LINEAR);
                    }
                });
            }
        }
        if(mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    doRefreshOnRecyclerView();
                }
            });
        if (mDisplayMode == DisplayMode.STAGGERED) {
            mRecyclerView.setLayoutManager(new StrongStaggeredGridLayoutManager(mColumnCount, StrongStaggeredGridLayoutManager.VERTICAL));
        } else {
            mRecyclerView.setLayoutManager(new StrongLinearLayoutManager(_this));
        }

        try {
            //myRecyclerViewItemAdapter = mAdapterClass.newInstance();
            Constructor constructor = mAdapterClass.getDeclaredConstructor();
            myRecyclerViewItemAdapter = (TAdapter) constructor.newInstance();
        } catch (Throwable t) {
            Log.e(this.getClass().getName(), t.getMessage(), t);
            AndroidUtil.showToast("初始化出错");
        }
        modeAdaptation(mSwitchModeView, mDisplayMode);  //适配显示模式

        initViewEnd(rootView);
        //自动加载初始数据
        mAsynDataRequest.fetchData(mPage, 1, mHandler, mDataBundle, _this);     //发起数据异步请求
        mRecyclerView.setAdapter(myRecyclerViewItemAdapter);
        mRecyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout != null)
                            mSwipeRefreshLayout.setRefreshing(false);
                        mAsynDataRequest.fetchData(++mPage, 3, mHandler, mDataBundle, _this);   //发起数据异步请求
                    }
                }, 200);
            }
        });
    }

    /**刷新RecyclerView*/
    public void doRefreshOnRecyclerView() {
//        if(mSwipeRefreshLayout != null)
//        mSwipeRefreshLayout.setRefreshing(false);
//        myRecyclerViewItemAdapter.clearData();
//        mPage = 0;
        mRecyclerView.scrollToPosition(0);
        mAsynDataRequest.fetchData(0, 2, mHandler, mDataBundle, _this); //发起数据异步请求
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            PageContent<String> pageContent = (PageContent) message.getData().getSerializable(AsynDataRequest.PAGE_CONTENT);
            boolean hasMore = pageContent.hasMore && !myRecyclerViewItemAdapter.layoutMissing();
            switch (message.what){
                case 1: //init
                    mRecyclerView.notifyDataReset();
                    myRecyclerViewItemAdapter.setData(pageContent.datas);
                    mRecyclerView.notifyMoreFinish(pageContent.hasMore);
                    //myRecyclerViewItemAdapter.notifyDataSetChanged();
                     break;
                case 2: //SwipeRefreshLayout.OnRefreshListener
                    if(mSwipeRefreshLayout != null)
                        mSwipeRefreshLayout.setRefreshing(false);
                    myRecyclerViewItemAdapter.clearData();
                    mPage = 0;
                    mRecyclerView.notifyDataReset();
                    myRecyclerViewItemAdapter.setData(pageContent.datas);
                    mRecyclerView.notifyMoreFinish(hasMore);
                    //myRecyclerViewItemAdapter.notifyDataSetChanged();
                    break;
                case 3: //LoadMoreRecyclerView.LoadMoreListener
                    myRecyclerViewItemAdapter.addDatas(pageContent.datas);
                    mRecyclerView.notifyMoreFinish(hasMore);
                    break;
                default:
                    break;
            }
            if(pageContent.hasMore)
                loadMoreDelayIfPossible();
            //空白页效果
            if(myRecyclerViewItemAdapter.getDataSize() == 0) {
                BitmapDrawable bitmapDrawable = new BitmapDrawable(ImageUtil.getBitmapFormResources(_this, R.drawable.empty_page));
                bitmapDrawable.setGravity(Gravity.CENTER);
                mRecyclerView.setBackground(bitmapDrawable);
            } else
                mRecyclerView.setBackground(null);
        }
    };

    private void loadMoreDelayIfPossible() {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.loadMoreIfPossible();     //尝试加载更多
            }
        }, 200);
    }

    private void modeAdaptation(View view, DisplayMode displayMode) {
        mDisplayMode = displayMode;
        if (mDisplayMode == DisplayMode.STAGGERED) {
            mColumnCount = (mColumnCount<1 || mColumnCount>9) ? 1 : mColumnCount;
            if(view != null) {
                if(TextView.class.isAssignableFrom(view.getClass()))
                    ((TextView) view).setText("瀑布模式");
                else if(Button.class.isAssignableFrom(view.getClass()))
                    ((Button) view).setText("瀑布模式");
            }
            myRecyclerViewItemAdapter.switchMode(mDisplayMode, mColumnCount);
            mRecyclerView.switchLayoutManager(new StrongStaggeredGridLayoutManager(mColumnCount, StrongStaggeredGridLayoutManager.VERTICAL));
        } else {
            if(view != null) {
                if(TextView.class.isAssignableFrom(view.getClass()))
                    ((TextView) view).setText("列表模式");
                else if(Button.class.isAssignableFrom(view.getClass()))
                    ((Button) view).setText("列表模式");
            }
            myRecyclerViewItemAdapter.switchMode(mDisplayMode);
            mRecyclerView.switchLayoutManager(new StrongLinearLayoutManager(_this));
        }
    }

}
