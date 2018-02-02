package com.onyx.jdread.shop.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemVipReadUserInfoBinding;
import com.onyx.jdread.databinding.SubjectCommonBinding;
import com.onyx.jdread.shop.model.BaseSubjectViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.SubjectType;
import com.onyx.jdread.shop.model.SubjectViewModel;
import com.onyx.jdread.shop.model.VipUserInfoViewModel;

/**
 * Created by jackdeng on 2017/12/12.
 */

public class BookRankAdapter extends SubjectCommonAdapter {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (SubjectType.TYPE_COVER == viewType) {
            return new CommonSubjectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_common, parent, false));
        } else {
            return new VipUserInfoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vip_read_user_info, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final BaseSubjectViewModel viewModel = getDatas().get(position);
        if (holder instanceof CommonSubjectViewHolder) {
            CommonSubjectViewHolder viewHolder = (CommonSubjectViewHolder) holder;
            viewHolder.bindTo((SubjectViewModel) viewModel);
        } else if (holder instanceof VipUserInfoHolder) {
            VipUserInfoHolder viewHolder = (VipUserInfoHolder) holder;
            viewHolder.bindTo((VipUserInfoViewModel) viewModel);
        }
    }

    static class CommonSubjectViewHolder extends PageRecyclerView.ViewHolder {

        private final SubjectCommonBinding bind;

        public CommonSubjectViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
            SubjectAdapter recyclerViewOneAdapter = new SubjectAdapter(ShopDataBundle.getInstance().getEventBus());
            PageRecyclerView recyclerViewSuject = bind.recyclerViewSuject;
            recyclerViewSuject.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
            recyclerViewSuject.setAdapter(recyclerViewOneAdapter);
        }

        public SubjectCommonBinding getBind() {
            return bind;
        }

        public void bindTo(SubjectViewModel viewModel) {
            bind.setViewModel(viewModel);
            bind.executePendingBindings();
        }
    }

    static class VipUserInfoHolder extends PageRecyclerView.ViewHolder {

        private final ItemVipReadUserInfoBinding bind;

        public VipUserInfoHolder(View view) {
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