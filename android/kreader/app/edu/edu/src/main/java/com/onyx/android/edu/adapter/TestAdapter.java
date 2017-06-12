package com.onyx.android.edu.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.EntityConfig;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/6/24.
 */
public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {

    private static final String TAG = "TestAdapter";

    private List<String> mPaperList;
    private int mLayoutId;
    private ViewGroup mParent;
    private int mRowCount;
    private int mColumn;
    private int mLayoutType;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener{
        void OnClick();
    }

    public TestAdapter(List<String> paperList, int layoutId,int layoutType, int rowCount, int column) {
        this.mPaperList = paperList;
        mLayoutId = layoutId;
        mRowCount = rowCount;
        mColumn = column;
        mLayoutType = layoutType;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        mParent = parent;
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int paddingBottom = mParent.getPaddingBottom();
        int paddingTop = mParent.getPaddingTop();
        int gridHeight = (mParent.getMeasuredHeight() - paddingBottom - paddingTop) / mRowCount;
        holder.mTime.setText(position + "");
        holder.mItemTestContainer.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, gridHeight));
        holder.mItemTestContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null){
                    mOnItemClickListener.OnClick();
                }
                Log.e(TAG, "onClick: setOnClickListener");
            }
        });

        if (position >= mPaperList.size()){
            holder.mTime.setVisibility(View.GONE);
            holder.mContentLayout.setVisibility(View.GONE);
            holder.mLine.setVisibility(View.GONE);
        }else {
            holder.mTime.setVisibility(View.VISIBLE);
            holder.mContentLayout.setVisibility(View.VISIBLE);
            holder.mLine.setVisibility(View.VISIBLE);
        }
        if (mLayoutType == EntityConfig.GRID_LAYOUT){
            holder.mLine.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        int countCountOfPage = mRowCount * mColumn;
        if (mPaperList != null){
            int size = mPaperList.size();
            int remainder = size % countCountOfPage;
            if (remainder > 0){
                int blankCount =  countCountOfPage - remainder;
                return size + blankCount;
            }else {
                return size;
            }
        }
        return 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.item_test_container)
        LinearLayout mItemTestContainer;
        @Bind(R.id.content_layout)
        ViewGroup mContentLayout;
        @Bind(R.id.line)
        View mLine;

        public ViewHolder(View view) {
            super(view);
//            R.layout.item_grid_test
            ButterKnife.bind(this, view);
        }
    }
}
