package com.onyx.jdread.shop.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemVipReadUserInfoBinding;
import com.onyx.jdread.databinding.SubjectWithVipBinding;
import com.onyx.jdread.shop.model.SubjectViewModel;
import com.onyx.jdread.shop.model.VipUserInfoViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/12.
 */

public class VipReadAdapter extends PageAdapter<PageRecyclerView.ViewHolder, SubjectViewModel, SubjectViewModel> {

    private static final int VIP_READ_ITEM_TYPE_USER_INFO = 1;
    private static final int VIP_READ_ITEM_TYPE_NORMAL = 2;
    private VipUserInfoViewModel infoViewModel;
    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.vip_read_recycle_view_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.vip_read_recycle_view_col);

    public void setRowAndCol(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setInfoViewModel(VipUserInfoViewModel infoViewModel) {
        this.infoViewModel = infoViewModel;
    }

    @Override
    public int getRowCount() {
        return row;
    }

    @Override
    public int getColumnCount() {
        return col;
    }

    @Override
    public int getDataCount() {
        return getItemVMList().size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIP_READ_ITEM_TYPE_USER_INFO;
        } else {
            return VIP_READ_ITEM_TYPE_NORMAL;
        }
    }

    @Override
    public PageRecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIP_READ_ITEM_TYPE_USER_INFO) {
            return new UserInfoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vip_read_user_info, parent, false));
        } else {
            return new ModelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_with_vip, parent, false));
        }
    }

    @Override
    public void onPageBindViewHolder(PageRecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            UserInfoHolder viewHolder = (UserInfoHolder) holder;
            viewHolder.bindTo(infoViewModel);
        } else {
            final SubjectViewModel bookBean = getItemVMList().get(position - 1);
            ModelViewHolder viewHolder = (ModelViewHolder) holder;
            viewHolder.bindTo(bookBean);
        }
    }

    @Override
    public void setRawData(List<SubjectViewModel> rawData, Context context) {
        super.setRawData(rawData, context);
        setItemVMList(rawData);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }

    static class ModelViewHolder extends PageRecyclerView.ViewHolder {

        private final SubjectWithVipBinding bind;

        public ModelViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
            SubjectWithVipAdapter recyclerViewAdapter = new SubjectWithVipAdapter(EventBus.getDefault());
            PageRecyclerView recyclerViewSuject = bind.recyclerViewSuject;
            recyclerViewSuject.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
            recyclerViewSuject.setAdapter(recyclerViewAdapter);
        }

        public SubjectWithVipBinding getBind() {
            return bind;
        }

        public void bindTo(SubjectViewModel viewModel) {
            bind.setViewModel(viewModel);
            bind.executePendingBindings();
        }
    }

    static class UserInfoHolder extends PageRecyclerView.ViewHolder {

        private final ItemVipReadUserInfoBinding bind;

        public UserInfoHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
        }

        public ItemVipReadUserInfoBinding getBind() {
            return bind;
        }

        public void bindTo(VipUserInfoViewModel viewModel) {
            bind.setViewModel(viewModel);
            bind.executePendingBindings();
        }
    }
}