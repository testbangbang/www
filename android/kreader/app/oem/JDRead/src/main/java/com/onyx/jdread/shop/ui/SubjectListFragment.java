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
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.action.BookCategoryLevel2BooksAction;
import com.onyx.jdread.shop.adapter.CategorySubjectAdapter;
import com.onyx.jdread.shop.adapter.SubjectListAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.event.BookItemClickEvent;
import com.onyx.jdread.shop.event.CategoryItemClickEvent;
import com.onyx.jdread.shop.event.SubjectListSortTypeChangeEvent;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.event.TopRightTitle2Event;
import com.onyx.jdread.shop.event.TopRightTitle3Event;
import com.onyx.jdread.shop.model.AllCategoryViewModel;
import com.onyx.jdread.shop.model.BookShopViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.SubjectListViewModel;
import com.onyx.jdread.shop.model.TitleBarViewModel;
import com.onyx.jdread.shop.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/30.
 */

public class SubjectListFragment extends BaseFragment {

    private FragmentSubjectListBinding subjectListBinding;
    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.subject_list_recycle_viw_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.subject_list_recycle_viw_col);
    private int catRow = JDReadApplication.getInstance().getResources().getInteger(R.integer.subject_list_category_recycle_viw_row);
    private int catCol = JDReadApplication.getInstance().getResources().getInteger(R.integer.subject_list_category_recycle_viw_col);
    private PageRecyclerView recyclerView;
    private GPaginator paginator;
    private int currentPage = 1;
    private String currentCatName;
    private int catid;
    private int sortType = CloudApiContext.CategoryLevel2BookList.SORT_TYPE_HOT;
    private boolean typeFree;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        subjectListBinding = FragmentSubjectListBinding.inflate(inflater, container, false);
        initView();
        initLibrary();
        initData();
        return subjectListBinding.getRoot();
    }

    private void initLibrary() {
        getEventBus().register(this);
    }

    private void initData() {
        catid = PreferenceManager.getIntValue(getContextJD(), Constants.SP_KEY_CATEGORY_ID, 0);
        currentCatName = PreferenceManager.getStringValue(getContextJD(), Constants.SP_KEY_CATEGORY_NAME, "");
        typeFree = PreferenceManager.getBooleanValue(getContextJD(), Constants.SP_KEY_CATEGORY_ISFREE, false);
        getSubjectListViewModel().getTitleBarViewModel().leftText = currentCatName;
        getSubjectListViewModel().getTitleBarViewModel().showRightText2 = true;
        getSubjectListViewModel().getTitleBarViewModel().showRightText3 = true;
        getSubjectListViewModel().getTitleBarViewModel().rightText2 = getString(R.string.subject_list_all);
        getSubjectListViewModel().getTitleBarViewModel().rightText3 = getString(R.string.subject_list_sort_type_hot);
        setSortButtonIsOpen(false);
        setAllCatIsOpen(false);
        setRightText2Icon();
        setRightText3Icon();
        getBooksData(catid, currentPage, sortType);
        setCategoryV2Data();
    }

    private void getBooksData(int catid, int currentPage, int sortType) {
        BookCategoryLevel2BooksAction booksAction = new BookCategoryLevel2BooksAction(catid, currentPage, sortType);
        booksAction.execute(getShopDataBundle(), new RxCallback<BookCategoryLevel2BooksAction>() {
            @Override
            public void onNext(BookCategoryLevel2BooksAction categoryBooksAction) {
                setBooksData();
                updateContentView();
            }
        });
    }

    private void setCategoryV2Data() {
        List<CategoryListResultBean.CatListBean> allCategoryItems = getAllCategoryViewModel().getAllCategoryItems();
        getSubjectListViewModel().setCategoryItems(allCategoryItems);
        getSubjectListViewModel().isFree.set(typeFree);
    }

    private void setBooksData() {
        recyclerView.gotoPage(0);
    }

    private void initView() {
        SubjectListAdapter adapter = new SubjectListAdapter(getEventBus());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContextJD(), DividerItemDecoration.VERTICAL_LIST);
        itemDecoration.setDrawLine(false);
        recyclerView = subjectListBinding.recyclerViewSubjectList;
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
        subjectListBinding.setViewModel(getSubjectListViewModel());
        CategorySubjectAdapter categorySubjectAdapter = new CategorySubjectAdapter(getEventBus(), true);
        categorySubjectAdapter.setRowAndCol(catRow,catCol);
        PageRecyclerView recyclerViewCategoryList = subjectListBinding.recyclerViewCategoryList;
        recyclerViewCategoryList.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewCategoryList.setAdapter(categorySubjectAdapter);
        itemDecoration.setDrawLine(true);
        recyclerViewCategoryList.addItemDecoration(itemDecoration);
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

    private TitleBarViewModel getTitleBarViewModel() {
        return getSubjectListViewModel().getTitleBarViewModel();
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
        PreferenceManager.setLongValue(JDReadApplication.getInstance(), Constants.SP_KEY_BOOK_ID, event.getBookBean().ebookId);
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(BookDetailFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoryItemClickEvent(CategoryItemClickEvent event) {
        CategoryListResultBean.CatListBean categoryBean = event.getCategoryBean();
        this.catid = categoryBean.catId;
        this.currentCatName = categoryBean.catName;
        this.currentPage = 1;
        this.sortType = CloudApiContext.CategoryLevel2BookList.SORT_TYPE_HOT;
        getSubjectListViewModel().getTitleBarViewModel().leftText = currentCatName;
        getBooksData(catid, currentPage, sortType);
        showOrCloseAllCatButton();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopRight2Event(TopRightTitle2Event event) {
        showOrCloseAllCatButton();
    }

    private void showOrCloseAllCatButton() {
        if (getSubjectListViewModel().sortButtonIsOpen.get()) {
            showOrCloseSortButton();
        }
        boolean allCatIsOpen = getSubjectListViewModel().allCatIsOpen.get();
        setAllCatIsOpen(!allCatIsOpen);
        setRightText2Icon();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopRight3Event(TopRightTitle3Event event) {
        showOrCloseSortButton();
    }

    private void showOrCloseSortButton() {
        if (getSubjectListViewModel().allCatIsOpen.get()) {
            showOrCloseAllCatButton();
        }
        boolean sortButtonIsOpen = getSubjectListViewModel().sortButtonIsOpen.get();
        setSortButtonIsOpen(!sortButtonIsOpen);
        setRightText3Icon();
    }

    private void setAllCatIsOpen(boolean allCatIsOpen) {
        getSubjectListViewModel().allCatIsOpen.set(allCatIsOpen);
    }

    private void setSortButtonIsOpen(boolean sortButtonIsOpen) {
        getSubjectListViewModel().sortButtonIsOpen.set(sortButtonIsOpen);
    }

    private void setRightText2Icon() {
        getTitleBarViewModel().rightText2IconId.set(getSubjectListViewModel().allCatIsOpen.get() ? R.mipmap.ic_up : R.mipmap.ic_down);
    }

    private void setRightText3Icon() {
        getTitleBarViewModel().rightText3IconId.set(getSubjectListViewModel().sortButtonIsOpen.get() ? R.mipmap.ic_up : R.mipmap.ic_down);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubjectListSortTypeChangeEvent(SubjectListSortTypeChangeEvent event) {
        if (sortType != event.type) {
            sortType = event.type;
            getBooksData(catid, currentPage, sortType);
        }
        showOrCloseSortButton();
    }

    @Subscribe
    public void onLoadingDialogEvent(LoadingDialogEvent event) {
        showLoadingDialog(getString(event.getResId()));
    }

    @Subscribe
    public void onHideAllDialogEvent(HideAllDialogEvent event) {
        hideLoadingDialog();
    }
}
