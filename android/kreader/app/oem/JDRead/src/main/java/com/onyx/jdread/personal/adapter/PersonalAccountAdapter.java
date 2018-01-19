package com.onyx.jdread.personal.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemAccountBinding;
import com.onyx.jdread.main.common.ResManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/**
 * Created by li on 2017/12/29.
 */

public class PersonalAccountAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private EventBus eventBus;
    private String[] titles;
    private Map<String, Object> events;

    public PersonalAccountAdapter(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public int getRowCount() {
        return ResManager.getResManager().getInteger(R.integer.personal_account_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getResManager().getInteger(R.integer.personal_account_adapter_col);
    }

    @Override
    public int getDataCount() {
        return titles == null ? 0 : titles.length;
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_layout, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AccountViewHolder viewHolder = (AccountViewHolder) holder;
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.bindTo(titles[position]);
    }

    public void setData(String[] accountTitles, Map<String, Object> accountEvents) {
        this.titles = accountTitles;
        this.events = accountEvents;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }
        int position = (int) tag;
        eventBus.post(events.get(titles[position]));
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {
        private ItemAccountBinding bind;

        public AccountViewHolder(View itemView) {
            super(itemView);
            bind = (ItemAccountBinding) DataBindingUtil.bind(itemView);
        }

        public ItemAccountBinding getBind() {
            return bind;
        }

        public void bindTo(String title) {
            bind.setTitle(title);
            bind.executePendingBindings();
        }
    }
}
