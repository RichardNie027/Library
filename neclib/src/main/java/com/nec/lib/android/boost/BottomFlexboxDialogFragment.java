package com.nec.lib.android.boost;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.nec.lib.android.R;
import com.nec.lib.android.utils.MapUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 底部弹出的对话框，显示简单文本，FlexBox样式，点击事件监听
 * LinkedHashMap, KEY=TAG, VALUE=TEXT
 */
public abstract class BottomFlexboxDialogFragment extends BottomDialogFragment<String, String> {

    private RecyclerView mRecyclerView;

    public void setTextViewOnClickListener(View.OnClickListener textViewOnClickListener) {
        this.mTextViewOnClickListener = textViewOnClickListener;
    }

    private View.OnClickListener mTextViewOnClickListener;

    protected abstract int setRecyclerViewResourceID();

    protected int setRecyclerViewItemLayoutResourceID() {
        return R.layout.bottom_flexbox_dialog_fragment_default_recyclerview_item;
    }

    protected int setIdOfRecyclerViewItemLayout() {
        return R.id.layoutRecylerviewItem;
    }

    protected int setIdOfTextViewInRecyclerView() {
        return R.id.tvItem;
    }

    @Override
    protected void initView() {
        //设置RecyclerView
        mRecyclerView = mRootView.findViewById(setRecyclerViewResourceID());
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this.getContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP);           //设置是否换行
        layoutManager.setFlexDirection(FlexDirection.ROW);  //设置主轴排列方式
        layoutManager.setAlignItems(AlignItems.STRETCH);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        mRecyclerView.setLayoutManager(layoutManager);

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this.getContext(), mDatas);
        mRecyclerView.setAdapter(recyclerViewAdapter);
    }

    ///自定义类继承RecycleView.Adapter类作为数据适配器
    class RecyclerViewAdapter extends RecyclerView.Adapter {

        private Context mContext;
        private LinkedHashMap<String, String> mDatas = new LinkedHashMap<>();

        public RecyclerViewAdapter(Context context, LinkedHashMap<String, String> datas) {
            this.mContext = context;
            this.mDatas.putAll(datas);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        ///对控件赋值
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
            Map.Entry<String, String> entry = MapUtil.getByIndex(mDatas, position);
            if(entry != null) {
                recyclerViewHolder.tvItem.setTag(entry.getKey());
                recyclerViewHolder.tvItem.setText(entry.getValue());
                recyclerViewHolder.tvItem.setOnClickListener(mTextViewOnClickListener);
            } else {
                recyclerViewHolder.tvItem.setTag("");
                recyclerViewHolder.tvItem.setText("");
                recyclerViewHolder.tvItem.setOnClickListener(null);
            }
            //定义Flexbox布局的元素特征
            ViewGroup.LayoutParams lp = recyclerViewHolder.layoutRecylerviewItem.getLayoutParams();
            if (lp instanceof FlexboxLayoutManager.LayoutParams) {
                FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) lp;
                flexboxLp.setFlexGrow(1.0f);
                flexboxLp.setAlignSelf(AlignItems.FLEX_START);
            }
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(setRecyclerViewItemLayoutResourceID(), parent, false);
            RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
            return recyclerViewHolder;
        }

        ///适配器中的自定义内部类，其中的子对象用于呈现数据
        class RecyclerViewHolder extends RecyclerView.ViewHolder {
            TextView tvItem;
            ConstraintLayout layoutRecylerviewItem;

            public RecyclerViewHolder(View view) {
                super(view);
                //实例化自定义对象
                tvItem = view.findViewById(setIdOfTextViewInRecyclerView());
                layoutRecylerviewItem = view.findViewById(setIdOfRecyclerViewItemLayout());
            }
        }
    }

}
