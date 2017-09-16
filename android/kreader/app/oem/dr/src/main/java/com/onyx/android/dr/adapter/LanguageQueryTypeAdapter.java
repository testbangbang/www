package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.DictTypeBean;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public class LanguageQueryTypeAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private List<DictTypeBean> dictDatas;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    public int selectedPosition = 0;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
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
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_language_query_type, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        DictTypeBean dictData = dictDatas.get(position);
        viewHolder.tabMenuTitle.setText(dictData.getTabName());
        viewHolder.rootView.setTag(position);
        if (selectedPosition == position) {
            viewHolder.tabMenuTitle.setBackgroundResource(R.drawable.rectangle_stroke_focused);
            viewHolder.tabMenuTitle.setTextColor(DRApplication.getInstance().getResources().getColor(R.color.white));
        } else {
            viewHolder.tabMenuTitle.setBackgroundResource(R.drawable.rectangle_stroke);
            viewHolder.tabMenuTitle.setTextColor(DRApplication.getInstance().getResources().getColor(R.color.black));
        }
        viewHolder.rootView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        int position = (int) v.getTag();
        selectedPosition = position;
        notifyDataSetChanged();
        EventBus.getDefault().post(dictDatas.get(position).getEventBean());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.language_query_type_item_title)
        TextView tabMenuTitle;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
