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
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentViewAllBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.action.BookModelAction;
import com.onyx.jdread.shop.action.BookRankListAction;
import com.onyx.jdread.shop.adapter.SubjectListAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelBooksResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.event.BookItemClickEvent;
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.TitleBarViewModel;
import com.onyx.jdread.shop.model.ViewAllViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/30.
 */

public class ViewAllBooksFragment extends BaseFragment {

    private FragmentViewAllBinding viewAllBinding;
    private int row = ResManager.getInteger(R.integer.subject_list_recycle_viw_row);
    private int col = ResManager.getInteger(R.integer.subject_list_recycle_viw_col);
    private PageRecyclerView recyclerView;
    private GPaginator paginator;
    private int currentPage = 1;
    private int modelId;
    private int modelType;

    private ViewAllViewModel viewAllViewModel;

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
        Bundle bundle = getBundle();
        if (bundle != null) {
            getTitleBarViewModel().leftText = bundle.getString(Constants.SP_KEY_SUBJECT_NAME, "");
            int bookListType = bundle.getInt(Constants.SP_KEY_BOOK_LIST_TYPE, -1);
            if (bookListType == Constants.BOOK_LIST_TYPE_BOOK_MODEL) {
                modelId = bundle.getInt(Constants.SP_KEY_SUBJECT_MODEL_ID, -1);
                modelType = bundle.getInt(Constants.SP_KEY_SUBJECT_MODEL_TYPE, -1);
                getBookModelData(currentPage);
            } else if (bookListType == Constants.BOOK_LIST_TYPE_BOOK_RANK) {
                int modelType = bundle.getInt(Constants.SP_KEY_SUBJECT_MODEL_TYPE, -1);
                getBookRankData(modelType, currentPage);
            }
        }
        checkWifi(getTitleBarViewModel().leftText);
    }

    private void getBookModelData(int currentPage) {
        BookModelAction booksAction = new BookModelAction(modelId, modelType, currentPage);
        booksAction.execute(getShopDataBundle(), new RxCallback<BookModelAction>() {
            @Override
            public void onNext(BookModelAction booksAction) {
                BookModelBooksResultBean bookModelResultBean = booksAction.getBookModelResultBean();
                if (bookModelResultBean != null && bookModelResultBean.data != null) {
                    List<ResultBookBean> data = bookModelResultBean.data.items;
                    setResult(data);
                }
            }
        });
    }

    private void getBookRankData(int modelType, int currentPage) {
        BookRankListAction booksAction = new BookRankListAction(modelType, currentPage);
        booksAction.execute(getShopDataBundle(), new RxCallback<BookRankListAction>() {
            @Override
            public void onNext(BookRankListAction booksAction) {
                if (booksAction.getBookModelResultBean() != null) {
                    List<ResultBookBean> data = booksAction.getBookModelResultBean().data;
                    setResult(data);
                }
            }
        });
    }

    private void setResult(List<ResultBookBean> data) {
        checkContentEmpty(data);
        updateContentView(data);
    }

    private void initView() {
        SubjectListAdapter adapter = new SubjectListAdapter(getEventBus());
        DashLineItemDivider itemDecoration = new DashLineItemDivider();
        recyclerView = viewAllBinding.recyclerViewSubjectList;
        recyclerView.setPageTurningCycled(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.setAdapter(adapter);
        paginator = recyclerView.getPaginator();
        recyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                if (paginator != null) {
                    setCurrentPage(paginator.getCurrentPage());
                }
            }
        });
        viewAllBinding.setViewModel(getViewAllViewModel());
        initPageIndicator();
    }

    private void initPageIndicator() {
        paginator.resize(row, col, CollectionUtils.getSize(getViewAllViewModel().getBookList()));
        getViewAllViewModel().setTotalPage(paginator.pages());
        setCurrentPage(getValidContentPage());
    }

    private void updatePageIndicator(int itemSize, int currentPage) {
        paginator.resize(row, col, itemSize);
        getViewAllViewModel().setTotalPage(paginator.pages());
        setCurrentPage(currentPage);
    }

    private void gotoPage(int page) {
        if (recyclerView == null) {
            return;
        }
        recyclerView.gotoPage(page);
    }

    private void updateContentView(List<ResultBookBean> bookList) {
        if (recyclerView == null) {
            return;
        }
        getViewAllViewModel().setBookList(bookList);
        recyclerView.getAdapter().notifyDataSetChanged();
        updatePageIndicator(CollectionUtils.getSize(bookList), getValidContentPage());
        gotoPage(getValidContentPage());
    }

    private void setCurrentPage(int currentPage) {
        getViewAllViewModel().setCurrentPage(currentPage + Constants.PAGE_STEP);
    }

    private int getCurrentPage() {
        return getViewAllViewModel().getCurrentPage() - Constants.PAGE_STEP;
    }

    private int getValidContentPage() {
        int page = getCurrentPage();
        if (page >= paginator.pages()) {
            return 0;
        }
        return page;
    }

    private ShopDataBundle getShopDataBundle() {
        return ShopDataBundle.getInstance();
    }

    private ViewAllViewModel getViewAllViewModel() {
        if (viewAllViewModel == null) {
            viewAllViewModel = new ViewAllViewModel(getEventBus());
        }
        return viewAllViewModel;
    }

    private TitleBarViewModel getTitleBarViewModel() {
        return getViewAllViewModel().getTitleBarViewModel();
    }

    private EventBus getEventBus() {
        return getShopDataBundle().getEventBus();
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
        if (getViewEventCallBack() != null) {
            Bundle bundle = new Bundle();
            bundle.putLong(Constants.SP_KEY_BOOK_ID, event.getBookBean().ebook_id);
            getViewEventCallBack().gotoView(BookDetailFragment.class.getName(), bundle);
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
}