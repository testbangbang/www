package com.onyx.jdread.shop.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.BuyReadVipBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.ClientUtils;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.shop.adapter.BuyReadVipAdapter;
import com.onyx.jdread.shop.model.BuyReadVipModel;
import com.onyx.jdread.shop.model.ShopDataBundle;

/**
 * Created by li on 2018/1/10.
 */

public class BuyReadVIPFragment extends BaseFragment {
    private BuyReadVipBinding binding;
    private BuyReadVipAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (BuyReadVipBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_buy_read_vip, container, false);
        initView();
        initData();
        initListener();
        return binding.getRoot();
    }

    private void initView() {
        binding.buyVipRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        OnyxPageDividerItemDecoration decoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
        binding.buyVipRecycler.addItemDecoration(decoration);
        adapter = new BuyReadVipAdapter();
        binding.buyVipRecycler.setAdapter(adapter);
    }

    private void initData() {
        TitleBarModel titleBarModel = ShopDataBundle.getInstance().getTitleBarModel();
        titleBarModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.buy_read_vip));
        titleBarModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.buyVipTitle.setTitleModel(titleBarModel);

        BuyReadVipModel buyReadVipModel = ShopDataBundle.getInstance().getBuyReadVipModel();
        if (adapter != null) {
            adapter.setData(buyReadVipModel.getBuyReadVipData());
        }
    }

    private void initListener() {
        if (adapter == null) {
            return;
        }

        adapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // TODO: 2018/1/13 to order
                ToastUtil.showToast(position + "");
            }
        });

        binding.buyReadVipNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/1/13
            }
        });
    }
}
