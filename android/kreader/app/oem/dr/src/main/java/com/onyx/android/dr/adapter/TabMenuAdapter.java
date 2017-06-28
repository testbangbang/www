package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.data.MenuData;
import com.onyx.android.dr.data.RecyclerViewConfig;
import com.onyx.android.dr.device.DeviceConfig;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehai on 17-6-28.
 */

public class TabMenuAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private List<MenuData> menuDatas;

    public void setMenuDatas(List<MenuData> menuDatas) {
        this.menuDatas = menuDatas;
    }

    @Override
    public int getRowCount() {
        return DeviceConfig.sharedInstance(DRApplication.getInstance()).getRecyclerViewRow(RecyclerViewConfig.MAIN_TAB_MENU_TAG, RecyclerViewConfig.MAIN_TAB_MENU_ROW);
    }

    @Override
    public int getColumnCount() {
        return DeviceConfig.sharedInstance(DRApplication.getInstance()).getRecyclerViewColumn(RecyclerViewConfig.MAIN_TAB_MENU_TAG, RecyclerViewConfig.MAIN_TAB_MENU_COLUMN);
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
        MenuData menuData = menuDatas.get(position);
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
