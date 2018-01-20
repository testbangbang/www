package com.onyx.jdread.personal.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemPersonalBinding;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.personal.model.PersonalData;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

/**
 * Created by li on 2017/12/28.
 */

public class PersonalAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private EventBus eventBus;
    private List<PersonalData> data;
    private Map<String, Object> events;

    public PersonalAdapter(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public int getRowCount() {
        return ResManager.getInteger(R.integer.personal_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getInteger(R.integer.personal_adapter_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_layout, parent, false);
        return new PersonalViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PersonalViewHolder viewHolder = (PersonalViewHolder) holder;
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.bindTo(data.get(position));
    }

    public void setData(List<PersonalData> data, Map<String, Object> events) {
        this.data = data;
        this.events = events;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }

        int position = (int) tag;
        PersonalData personalData = data.get(position);
        eventBus.post(events.get(personalData.getTitle()));
    }

    static class PersonalViewHolder extends RecyclerView.ViewHolder {
        private ItemPersonalBinding binding;

        public PersonalViewHolder(View itemView) {
            super(itemView);
            binding = (ItemPersonalBinding) DataBindingUtil.bind(itemView);
        }

        public ItemPersonalBinding getBinding() {
            return binding;
        }

        public void bindTo(PersonalData personalData) {
            binding.setData(personalData);
            binding.executePendingBindings();
        }
    }
}
