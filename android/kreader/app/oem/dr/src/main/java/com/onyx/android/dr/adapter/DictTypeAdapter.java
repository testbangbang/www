package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.DictTypeBean;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public class DictTypeAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private List<DictTypeBean> dictDatas;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClick(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public void setMenuDatas(List<DictTypeBean> dictDatas) {
        this.dictDatas = dictDatas;
    }

    @Override
    public int getRowCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.main_tab_row);
    }

    @Override
    public int getColumnCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.dict_tab_menu_column);
    }

    @Override
    public int getDataCount() {
        return dictDatas == null ? 0 : dictDatas.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_dict_query, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        DictTypeBean dictData = dictDatas.get(position);
        viewHolder.tabMenuTitle.setText(dictData.getTabName());
        viewHolder.rootView.setTag(position);
        viewHolder.rootView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        int position = (int) v.getTag();
        if (onRecyclerViewItemClickListener != null) {
            onRecyclerViewItemClickListener.onItemClick(v, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tab_dict_title)
        TextView tabMenuTitle;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
