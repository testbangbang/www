package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.data.MenuBean;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehai on 17-6-28.
 */

public class TabMenuAdapter extends PageRecyclerView.PageAdapter<TabMenuAdapter.ViewHolder> implements View.OnClickListener {
    private List<MenuBean> menuDataList;

    public void setMenuDataList(List<MenuBean> menuDataList) {
        this.menuDataList = menuDataList;
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
        return menuDataList == null ? 0 : menuDataList.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_main_menu_tab, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {
        MenuBean menuData = menuDataList.get(position);
        setImage(menuData,holder.tabMenuIcon);
        holder.tabMenuTitle.setText(menuData.getTabName());
        holder.rootView.setTag(position);
        holder.rootView.setOnClickListener(this);
    }

    private void setImage(MenuBean menuData, ImageView tabMenuIcon) {
        if (menuData.getImageResources() != 0) {
            tabMenuIcon.setImageResource(menuData.getImageResources());
        }else if (StringUtils.isNotBlank(menuData.getImageUrl())){
            Glide.with(DRApplication.getInstance()).load(menuData.getImageUrl()).into(tabMenuIcon);
        }else {
            tabMenuIcon.setImageResource(R.drawable.ic_books);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        int position = (int) v.getTag();
        Object eventBean = menuDataList.get(position).getEventBean();
        if (eventBean != null) {
            EventBus.getDefault().post(eventBean);
        }
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
