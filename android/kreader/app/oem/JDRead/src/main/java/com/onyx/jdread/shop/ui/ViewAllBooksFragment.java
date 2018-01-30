package com.onyx.jdread.shop.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentViewAllBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.shop.action.BookModelAction;
import com.onyx.jdread.shop.action.BookRankListAction;
import com.onyx.jdread.shop.adapter.SubjectListAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelBooksResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.RecommendListResultBean;
import com.onyx.jdread.shop.event.BookItemClickEvent;
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.TitleBarViewModel;
import com.onyx.jdread.shop.model.ViewAllViewModel;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by jackdeng on 2017/12/30.
 */

public class ViewAllBooksFragment extends BaseFragment {

    private FragmentViewAllBinding viewAllBinding;
    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.subject_list_recycle_viw_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.subject_list_recycle_viw_col);
    private PageRecyclerView recyclerView;
    private GPaginator paginator;
    private int currentPage = 1;
    private int modelId;
    private int modelType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewAllBinding = FragmentViewAllBinding.inflate(inflater, container, false);
        initView();
        initLibrary();
        initData();
        return viewAllBinding.getRoot();
    }

    private void initLibrary() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
    }

    private void initData() {
        String title= JDPreferenceManager.getStringValue(Constants.SP_KEY_SUBJECT_NAME, "");
        getTitleBarViewModel().leftText = title;
        int bookListType= JDPreferenceManager.getIntValue(Constants.SP_KEY_BOOK_LIST_TYPE, -1);
        if (bookListType == Constants.BOOK_LIST_TYPE_BOOK_MODEL) {
            modelId = JDPreferenceManager.getIntValue(Constants.SP_KEY_SUBJECT_MODEL_ID, -1);
            modelType = JDPreferenceManager.getIntValue(Constants.SP_KEY_SUBJECT_MODEL_TYPE, -1);
            getBookModelData(currentPage);
        } else if (bookListType == Constants.BOOK_LIST_TYPE_BOOK_RANK) {
            int rankType = JDPreferenceManager.getIntValue(Constants.SP_KEY_SUBJECT_RANK_TYPE, -1);
            getBookRankData(rankType,currentPage);
        }
    }

    private void getBookModelData(int currentPage) {
        BookModelAction booksAction = new BookModelAction(modelId,modelType,currentPage);
        booksAction.execute(getShopDataBundle(), new RxCallback<BookModelAction>() {
            @Override
            public void onNext(BookModelAction booksAction) {
                BookModelBooksResultBean bookModelResultBean = booksAction.getBookModelResultBean();
                getViewAllViewModel().setBookList(bookModelResultBean.data.items);
                updateContentView();
            }
        });
    }

    private void getBookRankData(int rankId, int currentPage) {
        BookRankListAction booksAction = new BookRankListAction(rankId, currentPage);
        booksAction.execute(getShopDataBundle(), new RxCallback<BookRankListAction>() {
            @Override
            public void onNext(BookRankListAction booksAction) {
                RecommendListResultBean bookModelResultBean = booksAction.getBookModelResultBean();
                getViewAllViewModel().setBookList(bookModelResultBean.data);
                updateContentView();
            }
        });
    }

    private void initView() {
        SubjectListAdapter adapter = new SubjectListAdapter(getEventBus());
        DashLineItemDivider itemDecoration = new DashLineItemDivider();
        recyclerView = viewAllBinding.recyclerViewSubjectList;
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.setAdapter(adapter);
        paginator = recyclerView.getPaginator();
        recyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                if (paginator != null) {
                    int curPage = paginator.getCurrentPage();
                    setCurrentPage(curPage);
                }
            }
        });
        viewAllBinding.setViewModel(getViewAllViewModel());
    }

    private void initPageIndicator() {
        int size = 0;
        if (getViewAllViewModel().getBookList() != null) {
            size = getViewAllViewModel().getBookList().size();
        }
        paginator.resize(row, col, size);
        getViewAllViewModel().setTotalPage(paginator.pages());
        setCurrentPage(paginator.getCurrentPage());
    }

    private void updateContentView() {
        if (recyclerView == null) {
            return;
        }
        recyclerView.getAdapter().notifyDataSetChanged();
        initPageIndicator();
    }

    private void setCurrentPage(int currentPage) {
        getViewAllViewModel().setCurrentPage(currentPage + Constants.PAGE_STEP);
    }

    private ShopDataBundle getShopDataBundle() {
        return ShopDataBundle.getInstance();
    }

    private ViewAllViewModel getViewAllViewModel() {
        return getShopDataBundle().getViewAllViewModel();
    }

    private TitleBarViewModel getTitleBarViewModel() {
        return getViewAllViewModel().getTitleBarViewModel();
    }

    private EventBus getEventBus() {
        return getShopDataBundle().getEventBus();
    }

    private Context getContextJD() {
        return JDReadApplication.getInstance().getApplicationContext();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopBackEvent(TopBackEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().viewBack();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookItemClickEvent(BookItemClickEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        JDPreferenceManager.setLongValue(Constants.SP_KEY_BOOK_ID, event.getBookBean().ebook_id);
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(BookDetailFragment.class.getName());
        }
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

    @Override
    public void onDetach() {
        super.onDetach();
        hideLoadingDialog();
    }

    private boolean checkWfiDisConnected() {
        if (!Utils.isNetworkConnected(JDReadApplication.getInstance())) {
            ToastUtil.showToast(JDReadApplication.getInstance().getResources().getString(R.string.wifi_no_connected));
            return true;
        }
        return false;
    }
}