package com.onyx.jdread.shop.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentBookViewDirectoryBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.reader.ui.view.PageTextView;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.model.BookDetailViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by jackdeng on 2018/3/18.
 */

public class ViewDirectoryFragment extends BaseFragment {

    private FragmentBookViewDirectoryBinding viewDirectoryBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewDirectoryBinding = FragmentBookViewDirectoryBinding.inflate(inflater, container, false);
        initView();
        initData();
        return viewDirectoryBinding.getRoot();
    }

    private void initData() {
        viewDirectoryBinding.pageTextView.setOnPagingListener(new PageTextView.OnPagingListener() {
            @Override
            public void onPageChange(int currentPage, int totalPage) {
                getBookDetailViewModel().setCurrentPage(currentPage);
                getBookDetailViewModel().setTotalPage(totalPage);
            }
        });
    }

    private void initView() {
        BookDetailViewModel viewModel = getBookDetailViewModel();
        viewModel.getTitleBarViewModel().leftText = ResManager.getString(R.string.book_detail_view_catalog);
        if (viewModel.getBookDetailResultBean() != null && viewModel.getBookDetailResultBean().data != null) {
            String catalog = viewModel.getBookDetailResultBean().data.catalog;
            if (StringUtils.isNullOrEmpty(catalog)) {
                catalog = ResManager.getString(R.string.catalog_none);
            } else {
                catalog = catalog.replace("<br />", "\r\n");
            }
            viewModel.getBookDetailResultBean().data.catalog = catalog;
            viewDirectoryBinding.pageTextView.setText(catalog);
        }
        viewDirectoryBinding.setBookDetailViewModel(viewModel);
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.ensureRegister(getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(getEventBus(), this);
    }

    private BookDetailViewModel getBookDetailViewModel() {
        return getShopDataBundle().getBookDetailViewModel();
    }

    private EventBus getEventBus() {
        return getShopDataBundle().getEventBus();
    }

    public ShopDataBundle getShopDataBundle() {
        return ShopDataBundle.getInstance();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailTopBackEvent(TopBackEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().viewBack();
        }
    }
}
