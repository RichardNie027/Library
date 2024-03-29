package com.nec.lib.android.loadmoreview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nec.lib.android.R;
import com.nec.lib.android.application.MyApplication;
import com.nec.lib.android.base.BaseFragment;
import com.nec.lib.android.utils.AndroidUtil;
import com.nec.lib.android.utils.ImageUtil;
import com.nec.lib.android.utils.ResUtil;
import com.nec.lib.android.utils.TextDrawable;

import java.lang.reflect.Constructor;

/* fragment_default_load_more_list.xml 默认配置
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
 * 无限加载Fragment
 *
 * 调用示例：
 * 子类中，
 * 1、实现setLayoutResourceID()
 * 2、beforeCreate()中设置mIdOfSwipeRefreshLayout和mIdOfRecyclerView（如果修改了默认命名）
 * 3、实现doParamBundle()，解析Bundle参数：A、组装mDataBundle（数据获取AsynDataRequest需要）；B、为页面传参，需要在LoadMoreFragment子类定义新的属性。
 * 4、实现initView()，查找控件、设置控件；mAutoload = false;//是否加载初始数据
 *
 * RecordFragment.newInstance(RecordFragment.class, RecordRecyclerViewItemAdapter.class, new RecordListDataRequest(), dataBundle, LoadMoreFragment.DisplayMode.LINEAR, 3);
 *
 *
 * 调用doRefreshOnRecyclerView，刷新RecyclerView，重新加载控件数据显示（如果修改了数据条件，需要先修改mDataBundle，参照doParamBundle()）
 *
 * @param <TAdapter extends RecyclerViewItemAdapter>
 */
public abstract class LoadMoreFragment<TAdapter extends RecyclerViewItemAdapter> extends BaseFragment {

    ////////////////资源（Layout/ID）前提 START////////////////
    //布局xml文件的内部资源ID
    /**布局文件中SwipeRefreshLayout的ID（布局中可以没有SwipeRefreshLayout），必要*/
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
     * 1、进入页面，装载初始数据onCreateView，子类中在onCreate -> doParamBundle传入数据
     * 2、刷新数据SwipeRefreshLayout.OnRefreshListener -> doRefreshOnRecyclerView：下拉刷新
     * 3、加载更多LoadMoreRecyclerView.LoadMoreListener：上拉加载（初始加载的首批数据不足一屏时，自动触发）*/
    protected Bundle mDataBundle = new Bundle();
    /**是否加载初始数据*/
    protected boolean mAutoload = true;
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
    //private OnListFragmentInteractionListener mListener;  //在子类中实现

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LoadMoreFragment() {
    }

    /**
     * 默认地创建Linear显示模式的实例
     * @param fragmentClass 子类
     * @param itemAdapterClass 行适配器的类
     * @param asynDataRequest 异步数据请求
     * @param paramBundle 传递参数（数据请求条件、页面参数）
     * @return
     */
    public static <T extends LoadMoreFragment> T newInstance(Class<? extends T> fragmentClass,
                                                             Class<? extends RecyclerViewItemAdapter> itemAdapterClass,
                                                             AsynDataRequest asynDataRequest,
                                                             Bundle paramBundle) {
        return newInstance(fragmentClass, itemAdapterClass, asynDataRequest, paramBundle, DisplayMode.LINEAR, 2);
    }

    /**
     * 创建Linear/Staggered(默认2列)显示模式的实例
     * @param fragmentClass 子类
     * @param itemAdapterClass 行适配器的类
     * @param asynDataRequest 异步数据请求
     * @param paramBundle 传递参数（数据请求条件、页面参数）
     * @param displayMode 显示模式
     * @return
     */
    public static <T extends LoadMoreFragment> T newInstance(Class<? extends T> fragmentClass,
                                                             Class<? extends RecyclerViewItemAdapter> itemAdapterClass,
                                                             AsynDataRequest asynDataRequest,
                                                             Bundle paramBundle,
                                                             DisplayMode displayMode) {
        return newInstance(fragmentClass, itemAdapterClass, asynDataRequest, paramBundle, displayMode, 2);
    }

    /**
     * 创建Linear/Staggered显示模式的实例
     * @param fragmentClass 子类
     * @param itemAdapterClass 行适配器的类
     * @param asynDataRequest 异步数据请求
     * @param paramBundle 传递参数（数据请求条件、页面参数）
     * @param displayMode 显示模式
     * @param columns Staggered显示列数
     * @return
     */
    public static <T extends LoadMoreFragment> T newInstance(Class<? extends T> fragmentClass,
                                                             Class<? extends RecyclerViewItemAdapter> itemAdapterClass,
                                                             AsynDataRequest asynDataRequest,
                                                             Bundle paramBundle,
                                                             DisplayMode displayMode,
                                                             int columns) {
        T fragment = null;
        try {
            Class clazz = fragmentClass.asSubclass(LoadMoreFragment.class);
            fragment = (T) clazz.newInstance();
        } catch (ClassCastException | java.lang.InstantiationException | IllegalAccessException e) {
            return null;
        }
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columns);
        args.putInt(ARG_DISPLAY_MODE, displayMode.value);
        args.putSerializable(ASYN_DATA_REQUEST, asynDataRequest);
        if(paramBundle != null && !paramBundle.isEmpty())
            args.putAll(paramBundle);    //包含数据请求条件、页面参数
        fragment.setArguments(args);
        fragment.mAdapterClass = itemAdapterClass;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null && !bundle.isEmpty()) {
            mColumnCount = bundle.getInt(ARG_COLUMN_COUNT);
            mDisplayMode = DisplayMode.fromValue(bundle.getInt(ARG_DISPLAY_MODE));
            mAsynDataRequest = (AsynDataRequest) bundle.getSerializable(ASYN_DATA_REQUEST);
            doParamBundle(bundle);
        }
    }

    /**
     * ##子类需要重写方法##
     * 解析Bundle参数：
     * 1、组装mDataBundle（数据获取AsynDataRequest需要）；
     * 2、为页面传参，需要在LoadMoreFragment子类定义新的属性。
     * @param bundle 通过newInstance方法传入的参数集
     */
    /*
        //1、组装mDataBundle
        mDataBundle.putLong("dataParam1", bundle.getLong("dataParam1"));
        mDataBundle.putInt("dataParam1", bundle.getInt("dataParam1"));
        mDataBundle.putFloat("dataParam1", bundle.getFloat("dataParam1"));
        mDataBundle.putString("dataParam1", bundle.getString("dataParam1"));
        mDataBundle.putBoolean("dataParam1", bundle.getBoolean("dataParam1"));
        mDataBundle.putSerializable("dataParam1", bundle.getSerializable("dataParam1"));

        //2、为页面传参
        mParam1 = bundle.getString("dataParam1"); //需要定义类属性mParam1
     */
    protected abstract void doParamBundle(Bundle bundle);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // find view
        View view = inflater.inflate(setLayoutResourceID(), container, false);
        mRecyclerView = (LoadMoreRecyclerView) view.findViewById(ResUtil.getId(mIdOfRecyclerView, getContext()));
        int switchControlResId = ResUtil.getId(mIdOfModeSwitchButton, getContext());
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(ResUtil.getId(mIdOfSwipeRefreshLayout, getContext()));

        // control view
        mRecyclerView.setHasFixedSize(true);
        if(switchControlResId != 0) {
            mSwitchModeView = view.findViewById(switchControlResId);
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
            mRecyclerView.setLayoutManager(new StrongLinearLayoutManager(getActivity()));
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

        initView(view, getArguments());
        //自动加载初始数据
        if(mAutoload) {
            mAsynDataRequest.fetchData(mPage, 1, mHandler, mDataBundle, _this.getActivity());     //发起数据异步请求
        } else {
            mRecyclerView.setAutoLoadMoreEnable(false);
        }
        mRecyclerView.setAdapter(myRecyclerViewItemAdapter);
        mRecyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout != null)
                            mSwipeRefreshLayout.setRefreshing(false);
                        mAsynDataRequest.fetchData(++mPage, 3, mHandler, mDataBundle, _this.getActivity());   //发起数据异步请求
                    }
                }, 200);
            }
        });
        return view;
    }

    /**
     * 刷新RecyclerView，重新加载控件数据显示
     * 如果修改了数据条件，需要先修改mDataBundle，参照doParamBundle()
     */
    public void doRefreshOnRecyclerView() {
//        if(mSwipeRefreshLayout != null)
//        mSwipeRefreshLayout.setRefreshing(false);
//        myRecyclerViewItemAdapter.clearData();
//        mPage = 0;
        mRecyclerView.setAutoLoadMoreEnable(true);
        mRecyclerView.scrollToPosition(0);
        mAsynDataRequest.fetchData(0, 2, mHandler, mDataBundle, _this.getActivity()); //发起数据异步请求
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
                BitmapDrawable bitmapDrawable = new BitmapDrawable(ImageUtil.getBitmapFormResources(_this.getContext(), R.drawable.empty_page));
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
            mRecyclerView.switchLayoutManager(new StrongLinearLayoutManager(getActivity()));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnListFragmentInteractionListener {
//        public void onListFragmentInteraction();
//    }
}
