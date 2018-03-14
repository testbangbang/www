package com.onyx.jdread.shop.adapter;

import android.view.View;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.event.CategoryItemClickEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/12.
 */

public class AllCategoryAdapter extends CategoryBookListAdapter {

    public AllCategoryAdapter(EventBus eventBus) {
        super(eventBus);
    }

    @Override
    public void onPageBindViewHolder(PageRecyclerView.ViewHolder holder, int position) {
        final CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo catListBean = getItemVMList().get(position);
        AllCategoryNormalViewHolder viewHolder = (AllCategoryNormalViewHolder) holder;
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.bindTo(catListBean);
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }
        int position = (int) tag;
        List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> levelTwoList = getItemVMList();
        if (eventBus != null && levelTwoList != null) {
            eventBus.post(new CategoryItemClickEvent(levelTwoList.get(position), position));
        }
    }
}