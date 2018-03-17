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
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentViewAllBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.action.SearchBookListAction;
import com.onyx.jdread.shop.adapter.SubjectListAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelBooksResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.CloudApiContext.CategoryLevel2BookList;
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

public class SearchBookListFragment extends BaseFragment {

    private FragmentViewAllBinding viewAllBinding;
    private int row = ResManager.getInteger(R.integer.subject_list_recycle_viw_row);
    private int col = ResManager.getInteger(R.integer.subject_list_recycle_viw_col);
    private PageRecyclerView recyclerView;
    private GPaginator paginator;
    private int currentPage = 1;
    private String keyWord = "";
    private String catId = "";

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
        Utils.ensureRegister(getEventBus(), this);
    }

    private void initData() {
        keyWord = getBundle().getString(Constants.SP_KEY_KEYWORD, "");
        catId = getBundle().getString(Constants.SP_KEY_SEARCH_BOOK_CAT_ID, "");
        getTitleBarViewModel().leftText = keyWord;
        if (CollectionUtils.isNullOrEmpty(getViewAllViewModel().getBookList())) {
            String realKeyWord = "";
            if (StringUtils.isNullOrEmpty(catId)) {
                realKeyWord = keyWord;
            }
            getBooksData(catId, currentPage, CategoryLevel2BookList.SORT_KEY_DEFAULT_VALUES, CategoryLevel2BookList.SORT_TYPE_DEFAULT_VALUES, realKeyWord);
        } else {
            paginator.resize(row, col, CollectionUtils.getSize(getViewAllViewModel().getBookList()));
            paginator.setCurrentPage(getCurrentPage());
            gotoPage(getCurrentPage());
        }
        checkWifi(keyWord);
    }

    private void getBooksData(String catid, int currentPage, int sortKey, int sortType, String keyWord) {
        SearchBookListAction booksAction = new SearchBookListAction(catid, currentPage, sortKey, sortType, keyWord, CloudApiContext.SearchBook.FILTER_DEFAULT);
        booksAction.execute(getShopDataBundle(), new RxCallback<SearchBookListAction>() {
            @Override
            public void onNext(SearchBookListAction booksAction) {
                BookModelBooksResultBean booksResultBean = booksAction.getBooksResultBean();
                getViewAllViewModel().setBookList(booksResultBean.data.items);
                updateContentView();
            }
        });
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
                    int curPage = paginator.getCurrentPage();
                    setCurrentPage(curPage);
                }
            }
        });
        viewAllBinding.setViewModel(getViewAllViewModel());
    }

    private void initPageIndicator() {
        paginator.resize(row, col, CollectionUtils.getSize(getViewAllViewModel().getBookList()));
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

    private void gotoPage(int page) {
        if (recyclerView == null) {
            return;
        }
        recyclerView.gotoPage(page);
        initPageIndicator();
    }

    private void setCurrentPage(int currentPage) {
        getViewAllViewModel().setCurrentPage(currentPage + Constants.PAGE_STEP);
    }

    private int getCurrentPage() {
        return getViewAllViewModel().getCurrentPage() - Constants.PAGE_STEP;
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
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.SP_KEY_BOOK_ID, event.getBookBean().ebook_id);
        getViewEventCallBack().gotoView(BookDetailFragment.class.getName(), bundle);
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