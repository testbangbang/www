package com.onyx.jdread.shop.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.ui.view.DisableScrollLinearManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentBookVipReadBinding;
import com.onyx.jdread.library.event.HideAllDialogEvent;
import com.onyx.jdread.library.event.LoadingDialogEvent;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.personal.common.LoginHelper;
import com.onyx.jdread.shop.adapter.VipReadAdapter;
import com.onyx.jdread.shop.event.BookItemClickEvent;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.event.ViewAllClickEvent;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.SubjectViewModel;
import com.onyx.jdread.shop.model.VipReadViewModel;
import com.onyx.jdread.shop.model.VipUserInfoViewModel;
import com.onyx.jdread.shop.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by jackdeng on 2018/1/16.
 */

public class BookVIPReadFragment extends BaseFragment {
    private FragmentBookVipReadBinding bookVipReadBinding;
    private int space = JDReadApplication.getInstance().getResources().getInteger(R.integer.vip_read_recycle_view_space);
    private DividerItemDecoration itemDecoration;
    private PageRecyclerView recyclerView;
    private GPaginator paginator;
    private VipReadAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bookVipReadBinding = FragmentBookVipReadBinding.inflate(inflater, container, false);
        initView();
        initLibrary();
        initData();
        return bookVipReadBinding.getRoot();
    }

    private void initLibrary() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
    }

    private void initData() {
        List<SubjectViewModel> rankItems = getShopDataBundle().getRankViewModel().getRankItems();
        VipUserInfoViewModel vipUserInfoViewModel = new VipUserInfoViewModel(getEventBus());
        String imgUrl = LoginHelper.getImgUrl();
        String userName = LoginHelper.getUserName();
        vipUserInfoViewModel.name.set(userName);
        vipUserInfoViewModel.vipStatus.set("");
        vipUserInfoViewModel.imageUrl.set(imgUrl);
        if (adapter != null) {
            adapter.setInfoViewModel(vipUserInfoViewModel);
        }
        getVipReadViewModel().setSubjectModels(rankItems);
        if (rankItems != null) {
            initPageIndicator(rankItems.size());
        }
    }

    private void initPageIndicator(int size) {
        paginator.resize(adapter.getRowCount(), adapter.getColumnCount(), size + Constants.PAGE_STEP);
        int pages = paginator.pages();
        bookVipReadBinding.scrollBar.setTotal(pages);
    }

    private void initView() {
        initDividerItemDecoration();
        setRecycleView();
        bookVipReadBinding.setViewModel(getVipReadViewModel());
        getVipReadViewModel().getTitleBarViewModel().leftText = getString(R.string.vip);
    }

    private void setRecycleView() {
        adapter = new VipReadAdapter();
        recyclerView = bookVipReadBinding.recyclerViewVipRead;
        recyclerView.setLayoutManager(new DisableScrollLinearManager(JDReadApplication.getInstance()));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
        paginator = recyclerView.getPaginator();
        recyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                int currentPage = paginator.getCurrentPage();
                bookVipReadBinding.scrollBar.setFocusPosition(currentPage);
            }
        });
    }

    private void initDividerItemDecoration() {
        itemDecoration = new DividerItemDecoration(JDReadApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        itemDecoration.setDrawLine(false);
        itemDecoration.setSpace(space);
    }

    @Override
    public void onResume() {
        super.onResume();
        initLibrary();
    }

    @Override
    public void onStop() {
        super.onStop();
        getEventBus().unregister(this);
    }

    private EventBus getEventBus() {
        return getShopDataBundle().getEventBus();
    }

    public ShopDataBundle getShopDataBundle() {
        return ShopDataBundle.getInstance();
    }

    private VipReadViewModel getVipReadViewModel() {
        return getShopDataBundle().getVipReadViewModel();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadingDialogEvent(LoadingDialogEvent event) {
        if (isAdded()) {
            showLoadingDialog(getString(event.getResId()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideAllDialogEvent(HideAllDialogEvent event) {
        hideLoadingDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopBackEvent(TopBackEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().viewBack();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onViewAllClickEvent(ViewAllClickEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(ViewAllBooksFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookItemClickEvent(BookItemClickEvent event) {
        JDPreferenceManager.setLongValue(Constants.SP_KEY_BOOK_ID, event.getBookBean().ebook_id);
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(BookDetailFragment.class.getName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideLoadingDialog();
    }
}
