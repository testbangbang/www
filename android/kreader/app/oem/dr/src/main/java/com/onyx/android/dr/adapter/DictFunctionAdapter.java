package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.data.DictFunctionData;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 17-6-28.
 */

public class DictFunctionAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private List<DictFunctionData> menuDatas;

    public void setMenuDatas(List<DictFunctionData> menuDatas) {
        this.menuDatas = menuDatas;
    }

    @Override
    public int getRowCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.main_tab_row);
    }

    @Override
    public int getColumnCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.main_tab_column);
    }

    @Override
    public int getDataCount() {
        return menuDatas == null ? 0 : menuDatas.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_main_menu_tab, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        DictFunctionData menuData = menuDatas.get(position);
        viewHolder.tabMenuIcon.setImageResource(menuData.getImageResources());
        viewHolder.tabMenuTitle.setText(menuData.getTabName());
        viewHolder.rootView.setTag(position);
        viewHolder.rootView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        int position = (int) v.getTag();
        EventBus.getDefault().post(menuDatas.get(position).getEventBean());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tab_menu_icon)
        ImageView tabMenuIcon;
        @Bind(R.id.tab_menu_title)
        TextView tabMenuTitle;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
