package com.onyx.jdread.personal.adapter;

import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemReadPreferenceBinding;
import com.onyx.jdread.main.activity.StartActivity;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2018/1/3.
 */

public class ReadPreferenceAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> data;
    private List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> list = new ArrayList<>();

    @Override
    public int getRowCount() {
        return ResManager.getInteger(R.integer.read_preference_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getInteger(R.integer.read_preference_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_read_preference_layout, parent, false);
        return new ReadPreferenceViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReadPreferenceViewHolder viewHolder = (ReadPreferenceViewHolder) holder;
        CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo catListBean = data.get(position);
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.getBinding().itemPreferenceType.setSelected(catListBean.isSelect);
        viewHolder.BindTo(catListBean.name);
    }

    public void setData(List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> getData() {
        return data;
    }

    @Override
    public void onClick(View v) {
        ConnectivityManager connManager = (ConnectivityManager) JDReadApplication.getInstance().getSystemService(JDReadApplication.getInstance().CONNECTIVITY_SERVICE);
        NetworkInfo.State state1 = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (NetworkInfo.State.CONNECTED != state1) {
            ToastUtil.showToast(ResManager.getString(R.string.wifi_no_connected));
            return;
        }
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }
        int position = (int) tag;
        CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo catListBean = data.get(position);
        catListBean.isSelect = !catListBean.isSelect;
        notifyItemChanged(position);
    }

    public List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> getSelectedBean() {
        if (list.size() > 0) {
            list.clear();
        }
        if (data == null) {
            return list;
        }
        for (int i = 0; i < data.size(); i++) {
            CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo catListBean = data.get(i);
            if (catListBean.isSelect) {
                list.add(catListBean);
            }
        }
        return list;
    }

    static class ReadPreferenceViewHolder extends RecyclerView.ViewHolder {
        private ItemReadPreferenceBinding binding;

        public ReadPreferenceViewHolder(View itemView) {
            super(itemView);
            binding = (ItemReadPreferenceBinding) DataBindingUtil.bind(itemView);
        }

        public ItemReadPreferenceBinding getBinding() {
            return binding;
        }

        public void BindTo(String type) {
            binding.setType(type);
            binding.executePendingBindings();
        }
    }
}
