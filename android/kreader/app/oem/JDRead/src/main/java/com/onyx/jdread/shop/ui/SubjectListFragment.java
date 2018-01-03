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
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentSubjectListBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.action.BookCategoryV2BooksAction;
import com.onyx.jdread.shop.adapter.SubjectListAdapter;
import com.onyx.jdread.shop.event.OnBookItemClickEvent;
import com.onyx.jdread.shop.event.OnTopBackEvent;
import com.onyx.jdread.shop.model.AllCategoryViewModel;
import com.onyx.jdread.shop.model.BookShopViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.SubjectListViewModel;
import com.onyx.jdread.shop.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by jackdeng on 2017/12/30.
 */

public class SubjectListFragment extends BaseFragment {

    private FragmentSubjectListBinding subjectListBinding;
    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.subject_list_recycle_viw_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.subject_list_recycle_viw_col);
    private PageRecyclerView recyclerView;
    private GPaginator paginator;
    private int currentPage = 1;
    private String currentCatName;
    private int catid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        subjectListBinding = FragmentSubjectListBinding.inflate(inflater, container, false);
        initView();
        initData();
        return subjectListBinding.getRoot();
    }

    private void initData() {
        catid = PreferenceManager.getIntValue(getContextJD(), Constants.SP_KEY_CATEGORY_ID, 0);
        currentCatName = PreferenceManager.getStringValue(getContextJD(), Constants.SP_KEY_CATEGORY_NAME, "");
        getSubjectListViewModel().getTitleBarViewModel().leftText = currentCatName;
        getCategoryData(catid, currentPage);
    }

    private void getCategoryData(int catid, int currentPage) {
        BookCategoryV2BooksAction booksAction = new BookCategoryV2BooksAction(catid, currentPage);
        booksAction.execute(getShopDataBundle(), new RxCallback<BookCategoryV2BooksAction>() {
            @Override
            public void onNext(BookCategoryV2BooksAction categoryV2BooksAction) {
                setBooksData();
                updateContentView();
            }
        });
    }

    private void setBooksData() {
        recyclerView.gotoPage(0);
    }

    private void initView() {
        SubjectListAdapter adapter = new SubjectListAdapter(getEventBus());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContextJD(), DividerItemDecoration.HORIZONTAL_LIST);
        itemDecoration.setDrawLine(false);
        recyclerView = subjectListBinding.recyclerViewSubjectList;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(itemDecoration);
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
        subjectListBinding.setSubjectListViewModel(getSubjectListViewModel());
    }

    private void initPageIndicator() {
        int size = 0;
        if (getSubjectListViewModel().getBookList() != null) {
            size = getSubjectListViewModel().getBookList().size();
        }
        paginator.resize(row, col, size);
        getSubjectListViewModel().setTotalPage(paginator.pages());
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
        getSubjectListViewModel().setCurrentPage(currentPage + Constants.PAGE_STEP);
    }

    private ShopDataBundle getShopDataBundle() {
        return ShopDataBundle.getInstance();
    }

    private BookShopViewModel getShopViewModel() {
        return getShopDataBundle().getShopViewModel();
    }

    private AllCategoryViewModel getAllCategoryViewModel() {
        return getShopViewModel().getAllCategoryViewModel();
    }

    private SubjectListViewModel getSubjectListViewModel() {
        return getAllCategoryViewModel().getSubjectListViewModel();
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
        getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getEventBus().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopBackEvent(OnTopBackEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().viewBack();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookItemClickEvent(OnBookItemClickEvent event) {
        PreferenceManager.setLongValue(JDReadApplication.getInstance(), Constants.SP_KEY_BOOK_ID, event.getBookBean().ebookId);
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(BookDetailFragment.class.getName());
        }
    }
}