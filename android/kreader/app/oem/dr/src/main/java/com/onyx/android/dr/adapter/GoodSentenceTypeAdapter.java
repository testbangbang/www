package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.GoodSentenceTypeBean;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class GoodSentenceTypeAdapter extends PageRecyclerView.PageAdapter<GoodSentenceTypeAdapter.ViewHolder> implements View.OnClickListener {
    private List<GoodSentenceTypeBean> dataList;

    public void setMenuDataList(List<GoodSentenceTypeBean> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getRowCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.good_sentence_tab_row);
    }

    @Override
    public int getColumnCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.good_sentence_tab_column);
    }

    @Override
    public int getDataCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_good_sentence_type, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {
        GoodSentenceTypeBean goodSentenceTypeData = dataList.get(position);
        holder.tabMenuTitle.setText(goodSentenceTypeData.getTabName());
        holder.rootView.setTag(position);
        holder.rootView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        int position = (int) v.getTag();
        Object eventBean = dataList.get(position).getEventBean();
        if (eventBean != null) {
            EventBus.getDefault().post(eventBean);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tab_good_sentence_type)
        TextView tabMenuTitle;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
