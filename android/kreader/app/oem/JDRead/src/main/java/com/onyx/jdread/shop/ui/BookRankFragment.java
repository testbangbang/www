package com.onyx.jdread.shop.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentBookRankBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.action.BookRankAction;
import com.onyx.jdread.shop.adapter.ShopMainConfigAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;
import com.onyx.jdread.shop.event.BookItemClickEvent;
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.event.ViewAllClickEvent;
import com.onyx.jdread.shop.model.RankViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.view.CustomRecycleView;
import com.onyx.jdread.shop.view.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jackdeng on 2018/1/4.
 */

public class BookRankFragment extends BaseFragment {
    private FragmentBookRankBinding bookRankBinding;
    private int space = ResManager.getInteger(R.integer.custom_recycle_view_space);
    private CustomRecycleView recyclerView;

    private Map<Integer, Integer> pageIndexMap = new HashMap<>();
    private int pageIndex = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bookRankBinding = FragmentBookRankBinding.inflate(inflater, container, false);
        initView();
        initLibrary();
        initData();
        return bookRankBinding.getRoot();
    }

    private void initLibrary() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
    }

    private void initData() {
        BookRankAction rankAction = new BookRankAction();
        rankAction.execute(getShopDataBundle(), new RxCallback<BookRankAction>() {
            @Override
            public void onNext(BookRankAction rankAction) {
                scrollToCurrentPage();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });
    }

    private void scrollToCurrentPage() {
        if (recyclerView == null) {
            return;
        }
        int totalPage = getRankViewModel().getTotalPages();
        pageIndex = recyclerView.getCurPageIndex();
        pageIndexMap = recyclerView.getPageIndexMap();
        if (pageIndex >= totalPage || pageIndexMap.keySet().size() >= totalPage) {
            pageIndexMap.clear();
            pageIndex = 0;
        }
        recyclerView.gotoPage(pageIndex, getPagePosition(pageIndexMap, pageIndex));
        bookRankBinding.scrollBar.setTotal(totalPage);
        bookRankBinding.scrollBar.setFocusPosition(pageIndex);
    }

    private int getPagePosition(Map<Integer, Integer> pageIndexMap, int pageIndex) {
        return pageIndexMap.containsKey(pageIndex) ? pageIndexMap.get(pageIndex) : 0;
    }

    private void updateScrollbarPageIndex(int curIndex) {
        pageIndex = curIndex;
        if (bookRankBinding.scrollBar != null) {
            bookRankBinding.scrollBar.setFocusPosition(curIndex);
        }
    }

    private void scrollToTop() {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0);
        }
    }

    private void initView() {
        setRecycleView();
        bookRankBinding.setRankViewModel(getRankViewModel());
        getRankViewModel().getTitleBarViewModel().leftText = getString(R.string.ranking);
        checkWifi(getRankViewModel().getTitleBarViewModel().leftText);
    }

    private void setRecycleView() {
        ShopMainConfigAdapter adapter = new ShopMainConfigAdapter();
        recyclerView = bookRankBinding.recycleView;
        recyclerView.setPageTurningCycled(true);
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.addItemDecoration(new SpaceItemDecoration(space));
        recyclerView.setAdapter(adapter);
        recyclerView.updatePageIndexMap(pageIndexMap);
        recyclerView.updateCurPage(pageIndex);
        recyclerView.setOnPagingListener(new CustomRecycleView.OnPagingListener() {
            @Override
            public void onPageChange(int position) {
                updateScrollbarPageIndex(position);
            }
        });
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

    private RankViewModel getRankViewModel() {
        return getShopDataBundle().getRankViewModel();
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
        BookModelConfigResultBean.DataBean.ModulesBean modulesBean = event.modulesBean;
        if (modulesBean != null) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SP_KEY_SUBJECT_NAME, modulesBean.show_name);
            bundle.putInt(Constants.SP_KEY_BOOK_LIST_TYPE, Constants.BOOK_LIST_TYPE_BOOK_RANK);
            bundle.putInt(Constants.SP_KEY_SUBJECT_RANK_TYPE, modulesBean.module_type);
            if (getViewEventCallBack() != null) {
                getViewEventCallBack().gotoView(ViewAllBooksFragment.class.getName(), bundle);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookItemClickEvent(BookItemClickEvent event) {
        JDPreferenceManager.setLongValue(Constants.SP_KEY_BOOK_ID, event.getBookBean().ebook_id);
        if (getViewEventCallBack() != null) {
            Bundle bundle = new Bundle();
            bundle.putLong(Constants.SP_KEY_BOOK_ID, event.getBookBean().ebook_id);
            getViewEventCallBack().gotoView(BookDetailFragment.class.getName(), bundle);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideLoadingDialog();
    }
}
