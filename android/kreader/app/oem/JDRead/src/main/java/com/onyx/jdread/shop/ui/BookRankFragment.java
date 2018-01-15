package com.onyx.jdread.shop.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentBookRankBinding;
import com.onyx.jdread.library.event.HideAllDialogEvent;
import com.onyx.jdread.library.event.LoadingDialogEvent;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.action.BookRankAction;
import com.onyx.jdread.shop.adapter.BookRankAdapter;
import com.onyx.jdread.shop.event.BookItemClickEvent;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.event.ViewAllClickEvent;
import com.onyx.jdread.shop.model.RankViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by jackdeng on 2018/1/4.
 */

public class BookRankFragment extends BaseFragment {
    private static final int SCROLL_TOTAL = 3;
    private FragmentBookRankBinding bookRankBinding;
    private int bookDetailSpace = JDReadApplication.getInstance().getResources().getInteger(R.integer.book_detail_recycle_view_space);
    private DividerItemDecoration itemDecoration;
    private PageRecyclerView recyclerView;
    private GPaginator paginator;

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

            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });
    }

    private void initView() {
        initDividerItemDecoration();
        setRecycleView();
        bookRankBinding.scrollBar.setTotal(SCROLL_TOTAL);
        bookRankBinding.setRankViewModel(getRankViewModel());
        getRankViewModel().getTitleBarViewModel().leftText = getString(R.string.ranking);
    }

    private void setRecycleView() {
        BookRankAdapter adapter = new BookRankAdapter();
        recyclerView = bookRankBinding.recyclerViewRanks;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
        paginator = recyclerView.getPaginator();
        recyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                int currentPage = paginator.getCurrentPage();
                bookRankBinding.scrollBar.setFocusPosition(currentPage);
            }
        });
    }

    private void initDividerItemDecoration() {
        itemDecoration = new DividerItemDecoration(JDReadApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        itemDecoration.setDrawLine(false);
        itemDecoration.setSpace(bookDetailSpace);
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
        showLoadingDialog(getString(event.getResId()));
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
        PreferenceManager.setStringValue(JDReadApplication.getInstance(), Constants.SP_KEY_SUBJECT_NAME, event.subjectName);
        PreferenceManager.setIntValue(JDReadApplication.getInstance(), Constants.SP_KEY_SUBJECT_MODEL_ID, event.modelId);
        PreferenceManager.setIntValue(JDReadApplication.getInstance(), Constants.SP_KEY_SUBJECT_MODEL_TYPE, event.modelType);
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(ViewAllBooksFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookItemClickEvent(BookItemClickEvent event) {
        PreferenceManager.setLongValue(JDReadApplication.getInstance(), Constants.SP_KEY_BOOK_ID, event.getBookBean().ebook_id);
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
