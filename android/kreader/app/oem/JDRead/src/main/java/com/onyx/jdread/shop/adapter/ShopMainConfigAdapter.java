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
import com.onyx.jdread.databinding.BannerSubjectBinding;
import com.onyx.jdread.databinding.ItemVipReadUserInfoBinding;
import com.onyx.jdread.databinding.LayoutBackTopViewBinding;
import com.onyx.jdread.databinding.LayoutBookShopTopFunctionBinding;
import com.onyx.jdread.databinding.SubjectCommonBinding;
import com.onyx.jdread.databinding.SubjectTitleBinding;
import com.onyx.jdread.shop.model.BannerViewModel;
import com.onyx.jdread.shop.model.BaseSubjectViewModel;
import com.onyx.jdread.shop.model.MainConfigEndViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.SubjectType;
import com.onyx.jdread.shop.model.SubjectViewModel;
import com.onyx.jdread.shop.model.TitleSubjectViewModel;
import com.onyx.jdread.shop.model.TopFunctionViewModel;
import com.onyx.jdread.shop.model.VipUserInfoViewModel;

/**
 * Created by jackdeng on 2018/1/31.
 */

public class ShopMainConfigAdapter extends SubjectCommonAdapter {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case SubjectType.TYPE_TITLE:
                return new TitleSubjectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_title, parent, false));
            case SubjectType.TYPE_COVER:
                return new CommonSubjectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_common, parent, false));
            case SubjectType.TYPE_BANNER:
                return new BannerSubjectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_banner, parent, false));
            case SubjectType.TYPE_TOP_FUNCTION:
                return new TopFunctionSubjectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_book_shop_top_function, parent, false));
            case SubjectType.TYPE_END:
                return new EndSubjectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_back_top_view, parent, false));
            case SubjectType.TYPE_VIP_USER:
                return new VipUserInfoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vip_read_user_info, parent, false));
            default:
                return new CommonSubjectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_common, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final BaseSubjectViewModel viewModel = getDatas().get(position);
        if (holder instanceof TitleSubjectViewHolder) {
            TitleSubjectViewHolder viewHolder = (TitleSubjectViewHolder) holder;
            viewHolder.bindTo((TitleSubjectViewModel) viewModel);
        } else if (holder instanceof CommonSubjectViewHolder) {
            CommonSubjectViewHolder viewHolder = (CommonSubjectViewHolder) holder;
            viewHolder.bindTo((SubjectViewModel) viewModel);
        } else if (holder instanceof BannerSubjectViewHolder) {
            BannerSubjectViewHolder viewHolder = (BannerSubjectViewHolder) holder;
            viewHolder.bindTo((BannerViewModel) viewModel);
        } else if (holder instanceof TopFunctionSubjectViewHolder) {
            TopFunctionSubjectViewHolder viewHolder = (TopFunctionSubjectViewHolder) holder;
            viewHolder.bindTo((TopFunctionViewModel) viewModel);
        } else if (holder instanceof EndSubjectViewHolder) {
            EndSubjectViewHolder viewHolder = (EndSubjectViewHolder) holder;
            viewHolder.bindTo((MainConfigEndViewModel) viewModel);
        } else if (holder instanceof VipUserInfoHolder) {
            VipUserInfoHolder viewHolder = (VipUserInfoHolder) holder;
            viewHolder.bindTo((VipUserInfoViewModel) viewModel);
        }
    }

    static class TitleSubjectViewHolder extends PageRecyclerView.ViewHolder {

        private final SubjectTitleBinding bind;

        public TitleSubjectViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
            TitleSubjectAdapter adapter = new TitleSubjectAdapter(ShopDataBundle.getInstance().getEventBus());
            PageRecyclerView recyclerViewSuject = bind.recyclerViewTitleSubject;
            recyclerViewSuject.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
            recyclerViewSuject.setAdapter(adapter);
        }

        public SubjectTitleBinding getBind() {
            return bind;
        }

        public void bindTo(TitleSubjectViewModel viewModel) {
            bind.setViewModel(viewModel);
            bind.executePendingBindings();
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

    static class BannerSubjectViewHolder extends PageRecyclerView.ViewHolder {

        private final BannerSubjectBinding bind;

        public BannerSubjectViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
            BannerSubjectAdapter adapter = new BannerSubjectAdapter(ShopDataBundle.getInstance().getEventBus());
            PageRecyclerView recyclerViewSuject = bind.recyclerViewBanner;
            recyclerViewSuject.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
            recyclerViewSuject.setAdapter(adapter);
        }

        public BannerSubjectBinding getBind() {
            return bind;
        }

        public void bindTo(BannerViewModel viewModel) {
            bind.setViewModel(viewModel);
            bind.executePendingBindings();
        }
    }

    static class TopFunctionSubjectViewHolder extends PageRecyclerView.ViewHolder {

        private final LayoutBookShopTopFunctionBinding bind;

        public TopFunctionSubjectViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
        }

        public LayoutBookShopTopFunctionBinding getBind() {
            return bind;
        }

        public void bindTo(TopFunctionViewModel viewModel) {
            bind.setViewModel(viewModel);
            bind.executePendingBindings();
        }
    }

    static class EndSubjectViewHolder extends PageRecyclerView.ViewHolder {

        private final LayoutBackTopViewBinding bind;

        public EndSubjectViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
        }

        public LayoutBackTopViewBinding getBind() {
            return bind;
        }

        public void bindTo(MainConfigEndViewModel viewModel) {
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