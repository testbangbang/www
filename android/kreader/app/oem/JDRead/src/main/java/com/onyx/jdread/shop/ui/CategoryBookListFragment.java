package com.onyx.jdread.shop.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentCategoryBookListBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.action.SearchBookListAction;
import com.onyx.jdread.shop.adapter.CategoryBookListAdapter;
import com.onyx.jdread.shop.adapter.SubjectListAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.BaseResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelBooksResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.event.BookItemClickEvent;
import com.onyx.jdread.shop.event.CategoryItemClickEvent;
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;
import com.onyx.jdread.shop.event.SubjectListSortKeyChangeEvent;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.event.TopRightTitle2Event;
import com.onyx.jdread.shop.event.TopRightTitle3Event;
import com.onyx.jdread.shop.model.AllCategoryViewModel;
import com.onyx.jdread.shop.model.BookShopViewModel;
import com.onyx.jdread.shop.model.CategoryBookListViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.TitleBarViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/30.
 */

public class CategoryBookListFragment extends BaseFragment {

    private FragmentCategoryBookListBinding categoryBookListBinding;
    private int row = ResManager.getInteger(R.integer.subject_list_recycle_viw_row);
    private int col = ResManager.getInteger(R.integer.subject_list_recycle_viw_col);
    private int catRow = ResManager.getInteger(R.integer.subject_list_category_recycle_viw_row);
    private int catCol = ResManager.getInteger(R.integer.subject_list_category_recycle_viw_col);
    private PageRecyclerView recyclerView;
    private GPaginator paginator;
    private int currentRequestPage = 1;
    private String currentCatName;
    private int sortkey = CloudApiContext.CategoryLevel2BookList.SORT_KEY_DEFAULT_VALUES;
    private int sortType = CloudApiContext.CategoryLevel2BookList.SORT_TYPE_DEFAULT_VALUES;
    private int levelTwoPosition;
    private int catLevel;
    private int catTwoId;
    private int filter = CloudApiContext.SearchBook.FILTER_DEFAULT;
    private static int levelThreeDataSize;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        categoryBookListBinding = FragmentCategoryBookListBinding.inflate(inflater, container, false);
        initView();
        initLibrary();
        initData();
        return categoryBookListBinding.getRoot();
    }

    private void initLibrary() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
    }

    private void initData() {
        Bundle bundle = getBundle();
        if (bundle != null) {
            currentRequestPage = 1;
            catLevel = bundle.getInt(Constants.SP_KEY_CATEGORY_LEVEL_VALUE, 0);
            catTwoId = bundle.getInt(Constants.SP_KEY_CATEGORY_LEVEL_TWO_ID, 0);
            currentCatName = bundle.getString(Constants.SP_KEY_CATEGORY_NAME, "");
            levelTwoPosition = bundle.getInt(Constants.SP_KEY_CATEGORY_LEVEL_TWO_POSITION, 0);
            getCategoryBookListViewModel().getTitleBarViewModel().leftText = currentCatName;
            getCategoryBookListViewModel().getTitleBarViewModel().showRightText2 = true;
            getCategoryBookListViewModel().getTitleBarViewModel().showRightText3 = true;
            getCategoryBookListViewModel().getTitleBarViewModel().rightText2 = getString(R.string.subject_list_filter);
            getCategoryBookListViewModel().getTitleBarViewModel().rightText3 = getString(R.string.subject_list_sort_type_hot);
            initDefaultParams();
            hideOptionLayout();
            getBooksData(true,true);
            setCategoryV3Data();
        }
        checkWifi(currentCatName);
    }

    private void resetPageIndicator() {
        getCategoryBookListViewModel().setCurrentPage(1);
        getCategoryBookListViewModel().setTotalPage(1);
    }

    private void initDefaultParams() {
        restoreSortKeyAndType();
        getCategoryBookListViewModel().updateSortKeyInfo(sortkey);
    }

    private String getFinalCatId() {
        return catTwoId + "_" + catLevel;
    }

    private void getBooksData(boolean showLoading, final boolean shouldCleanData) {
        final SearchBookListAction booksAction = new SearchBookListAction(getFinalCatId(), currentRequestPage, sortkey, sortType, "", filter);
        booksAction.setShowLoadingDialog(showLoading);
        booksAction.execute(getShopDataBundle(), new RxCallback<SearchBookListAction>() {

            @Override
            public void onNext(SearchBookListAction action) {
                BookModelBooksResultBean booksResultBean = action.getBooksResultBean();
                if (booksResultBean != null && booksResultBean.data != null) {
                    BookModelBooksResultBean.DataBean data = booksResultBean.data;
                    checkContentEmpty(data.items);
                }
                if (shouldCleanData) {
                    resetPageIndicator();
                }
                updateContentView(booksResultBean, shouldCleanData);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (currentRequestPage > 1) {
                    currentRequestPage--;
                }
            }
        });
    }

    private void setCategoryV3Data() {
        List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> allCategoryItems = getAllCategoryViewModel().getAllCategoryItems();
        if (CollectionUtils.getSize(allCategoryItems) > levelTwoPosition) {
            List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> levelThreeDatas = allCategoryItems.get(levelTwoPosition).sub_category;
            getCategoryBookListViewModel().setCategoryItems(levelThreeDatas);
            levelThreeDataSize = levelThreeDatas.size();
            if (levelThreeDataSize == 0) {
                categoryBookListBinding.layoutTitleBar.titleBarRightTitle2.setTextColor(getResources().getColor(R.color.divider_color));
                getTitleBarViewModel().rightText2IconId.set(R.mipmap.ic_shelf_unfold_gray_down);
            }
        }
    }

    private void resetLevelThreeData(List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> levelThreeDatas) {
        for (CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo cateBean : levelThreeDatas) {
            cateBean.isSelect = false;
        }
    }

    private void initView() {
        SubjectListAdapter adapter = new SubjectListAdapter(getEventBus());
        DashLineItemDivider itemDecoration = new DashLineItemDivider();
        recyclerView = categoryBookListBinding.recyclerViewSubjectList;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
        paginator = recyclerView.getPaginator();
        recyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                if (paginator != null) {
                    int curPage = position / pageSize + Constants.PAGE_STEP;
                    loadMoreData(curPage ,itemCount / pageSize);
                    setCurrentPage(curPage);
                }
            }
        });
        recyclerView.setOnArrayEndPageListener(new PageRecyclerView.OnArrayEndPageListener() {
            @Override
            public void onArrayEndPage() {
                if (paginator != null && paginator.getVisibleCurrentPage() == getCategoryBookListViewModel().getTotalPage()) {
                    gotoPage(0);
                }
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                if (isOptionLayoutShowing()) {
                    if (ev.getAction() == MotionEvent.ACTION_UP) {
                        hideOptionLayout();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        categoryBookListBinding.setViewModel(getCategoryBookListViewModel());
        CategoryBookListAdapter categoryBookListAdapter = new CategoryBookListAdapter(getEventBus());
        categoryBookListAdapter.setRowAndCol(catRow, catCol);
        categoryBookListAdapter.setCanSelected(true);
        PageRecyclerView recyclerViewCategoryList = categoryBookListBinding.recyclerViewCategoryList;
        recyclerViewCategoryList.setPageTurningCycled(true);
        recyclerViewCategoryList.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewCategoryList.setAdapter(categoryBookListAdapter);
        recyclerViewCategoryList.addItemDecoration(itemDecoration);
        categoryBookListBinding.subjectListShowVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryBookListBinding.subjectListShowFree.setChecked(false);
                saveFilterValue(categoryBookListBinding.subjectListShowVip.isChecked() ? CloudApiContext.SearchBook.FILTER_VIP : CloudApiContext.SearchBook.FILTER_DEFAULT);
                getBooksData(true, true);
                showOrCloseAllCatButton();
            }
        });
        categoryBookListBinding.subjectListShowFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryBookListBinding.subjectListShowVip.setChecked(false);
                saveFilterValue(categoryBookListBinding.subjectListShowFree.isChecked() ? CloudApiContext.SearchBook.FILTER_FREE : CloudApiContext.SearchBook.FILTER_DEFAULT);
                getBooksData(true, true);
                showOrCloseAllCatButton();
            }
        });
    }

    private void loadMoreData(int curPage, int pages) {
        if (curPage < getCategoryBookListViewModel().getTotalPage() && curPage == pages) {
            currentRequestPage++;
            getBooksData(true, false);
        }
    }

    private void hideOptionLayout() {
        setAllCatIsOpen(false);
        setSortButtonIsOpen(false);
    }

    private boolean isOptionLayoutShowing() {
        return getCategoryBookListViewModel().allCatIsOpen.get() || getCategoryBookListViewModel().sortButtonIsOpen.get();
    }

    private void initPageIndicator() {
        int size = CollectionUtils.getSize(getCategoryBookListViewModel().getEnsureBookList());
        paginator.resize(row, col, size);
        setCurrentPage(paginator.getCurrentPage());
    }

    private void updateContentView(BookModelBooksResultBean result, boolean shouldCleanData) {
        List<ResultBookBean> list = result == null ? null : result.data.items;
        if (result != null && BaseResultBean.checkSuccess(result.result_code) && result.data != null) {
            BookModelBooksResultBean.DataBean data = result.data;
            getCategoryBookListViewModel().setTotalPage(pages(data.total));
        }
        getCategoryBookListViewModel().addBookList(list, shouldCleanData);

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
    }

    private void setCurrentPage(int currentPage) {
        getCategoryBookListViewModel().setCurrentPage(currentPage);
    }

    public int pages(int total) {
        int itemsPerPage = row * col;
        int pages = total / itemsPerPage;
        if (pages * itemsPerPage < total) {
            return pages + 1;
        }
        return pages;
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

    private CategoryBookListViewModel getCategoryBookListViewModel() {
        return getAllCategoryViewModel().getCategoryBookListViewModel();
    }

    private TitleBarViewModel getTitleBarViewModel() {
        return getCategoryBookListViewModel().getTitleBarViewModel();
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
        int intValue = JDPreferenceManager.getIntValue(Constants.SP_KEY_CATEGORY_LEVEL_ONE_ID, 0);
        if (intValue == Constants.SP_KEY_CATEGORY_ID) {
            categoryBookListBinding.filtrateFreeVip.setVisibility(View.VISIBLE);
            categoryBookListBinding.bookPrice.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getEventBus().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopBackEvent(TopBackEvent event) {
        if (getViewEventCallBack() != null) {
            unsetContentPage();
            resetLevelThreeData(getCategoryBookListViewModel().getCategoryItems());
            getViewEventCallBack().viewBack();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookItemClickEvent(BookItemClickEvent event) {
        if (isOptionLayoutShowing()) {
            hideOptionLayout();
            return;
        }

        ResultBookBean bookBean = event.getBookBean();
        if (bookBean != null) {
            saveContentPage();
            Bundle bundle = new Bundle();
            bundle.putLong(Constants.SP_KEY_BOOK_ID, bookBean.ebook_id);
            getViewEventCallBack().gotoView(BookDetailFragment.class.getName(), bundle);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoryItemClickEvent(CategoryItemClickEvent event) {
        showOrCloseAllCatButton();
        if (checkWifiDisconnected()) {
            return;
        }
        CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo categoryBean = event.getCategoryBean();
        if (categoryBean == null || currentCatName == null) {
            return;
        }
        unsetContentPage();
        this.catTwoId = categoryBean.id;
        this.currentCatName = categoryBean.name;
        this.currentRequestPage = 1;
        this.catLevel = categoryBean.level;
        getCategoryBookListViewModel().getTitleBarViewModel().leftText = currentCatName;
        Bundle bundle = getBundle();
        bundle.putInt(Constants.SP_KEY_CATEGORY_LEVEL_TWO_ID, catTwoId);
        bundle.putInt(Constants.SP_KEY_CATEGORY_LEVEL_VALUE, catLevel);
        bundle.putString(Constants.SP_KEY_CATEGORY_NAME, currentCatName);
        getBooksData(true, true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopRight2Event(TopRightTitle2Event event) {
        showOrCloseAllCatButton();
    }

    private void showOrCloseAllCatButton() {
        if (levelThreeDataSize == 0) {
            return;
        }
        if (getCategoryBookListViewModel().sortButtonIsOpen.get()) {
            showOrCloseSortButton();
        }
        boolean allCatIsOpen = getCategoryBookListViewModel().allCatIsOpen.get();
        setAllCatIsOpen(!allCatIsOpen);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopRight3Event(TopRightTitle3Event event) {
        showOrCloseSortButton();
    }

    private void showOrCloseSortButton() {
        if (getCategoryBookListViewModel().allCatIsOpen.get()) {
            showOrCloseAllCatButton();
        }
        boolean sortButtonIsOpen = getCategoryBookListViewModel().sortButtonIsOpen.get();
        setSortButtonIsOpen(!sortButtonIsOpen);
    }

    private void setAllCatIsOpen(boolean allCatIsOpen) {
        getCategoryBookListViewModel().allCatIsOpen.set(allCatIsOpen);
        setRightText2Icon();
    }

    private void setSortButtonIsOpen(boolean sortButtonIsOpen) {
        getCategoryBookListViewModel().sortButtonIsOpen.set(sortButtonIsOpen);
        setRightText3Icon();
    }

    private void setRightText2Icon() {
        getTitleBarViewModel().rightText2IconId.set(getCategoryBookListViewModel().allCatIsOpen.get() ? R.mipmap.ic_up : R.mipmap.ic_down);
    }

    private void setRightText3Icon() {
        getTitleBarViewModel().rightText3IconId.set(getCategoryBookListViewModel().sortButtonIsOpen.get() ? R.mipmap.ic_up : R.mipmap.ic_down);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubjectListSortKeyChangeEvent(SubjectListSortKeyChangeEvent event) {
        showOrCloseSortButton();
        if (checkWifiDisconnected()) {
            return;
        }
        if (sortkey == event.sortKey) {
            sortType = sortType == CloudApiContext.SearchBook.SORT_TYPE_ASC ? CloudApiContext.SearchBook.SORT_TYPE_DESC : CloudApiContext.SearchBook.SORT_TYPE_ASC;
        } else {
            sortType = CloudApiContext.CategoryLevel2BookList.SORT_TYPE_DEFAULT_VALUES;
            sortkey = event.sortKey;
        }
        saveSortKeyAndType(event.sortKey, sortType);
        unsetContentPage();
        this.currentRequestPage = 1;
        getBooksData(true, true);
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

    private void saveContentPage() {
        getCategoryBookListViewModel().setContentPage(paginator.getCurrentPage());
    }

    private int getValidContentPage() {
        if (getCategoryBookListViewModel().getContentPage() >= paginator.pages()) {
            getCategoryBookListViewModel().setContentPage(0);
        }
        return getCategoryBookListViewModel().getContentPage();
    }

    private void unsetContentPage() {
        getCategoryBookListViewModel().setContentPage(0);
    }

    private void saveSortKeyAndType(int sortKey, int sortType) {
        getBundle().putInt(CloudApiContext.SearchBook.SORT_KEY, sortKey);
        getBundle().putInt(CloudApiContext.SearchBook.SORT_TYPE, sortType);
    }

    private void saveFilterValue(int filter) {
        this.filter = filter;
        getBundle().putInt(CloudApiContext.SearchBook.FILTER, filter);
    }

    private void restoreSortKeyAndType() {
        sortkey = getBundle().getInt(CloudApiContext.SearchBook.SORT_KEY, sortkey);
        sortType = getBundle().getInt(CloudApiContext.SearchBook.SORT_TYPE, sortType);
        filter = getBundle().getInt(CloudApiContext.SearchBook.FILTER, CloudApiContext.SearchBook.FILTER_DEFAULT);
        categoryBookListBinding.subjectListShowVip.setChecked(filter == CloudApiContext.SearchBook.FILTER_VIP);
        categoryBookListBinding.subjectListShowFree.setChecked(filter == CloudApiContext.SearchBook.FILTER_FREE);
    }

    @Override
    public Bundle getBundle() {
        Bundle bundle = super.getBundle();
        if (bundle == null) {
            bundle = new Bundle();
            setBundle(bundle);
        }
        return bundle;
    }
}
