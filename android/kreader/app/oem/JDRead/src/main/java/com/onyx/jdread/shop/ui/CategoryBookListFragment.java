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
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentCategoryBookListBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.action.SearchBookListAction;
import com.onyx.jdread.shop.adapter.CategoryBookListAdapter;
import com.onyx.jdread.shop.adapter.SubjectListAdapter;
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
    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.subject_list_recycle_viw_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.subject_list_recycle_viw_col);
    private int catRow = JDReadApplication.getInstance().getResources().getInteger(R.integer.subject_list_category_recycle_viw_row);
    private int catCol = JDReadApplication.getInstance().getResources().getInteger(R.integer.subject_list_category_recycle_viw_col);
    private PageRecyclerView recyclerView;
    private GPaginator paginator;
    private int currentPage = 1;
    private String currentCatName;
    private int sortkey = CloudApiContext.CategoryLevel2BookList.SORT_KEY_DEFAULT_VALUES;
    private int sortType = CloudApiContext.CategoryLevel2BookList.SORT_TYPE_DEFAULT_VALUES;
    private boolean typeFree;
    private int catLevel;
    private int catTwoId;

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
            catLevel = bundle.getInt(Constants.SP_KEY_CATEGORY_LEVEL_VALUE, 0);
            catTwoId = bundle.getInt(Constants.SP_KEY_CATEGORY_LEVEL_TWO_ID, 0);
            currentCatName = bundle.getString(Constants.SP_KEY_CATEGORY_NAME, "");
            typeFree = bundle.getBoolean(Constants.SP_KEY_CATEGORY_ISFREE, false);
            getCategoryBookListViewModel().getTitleBarViewModel().leftText = currentCatName;
            getCategoryBookListViewModel().getTitleBarViewModel().showRightText2 = true;
            getCategoryBookListViewModel().getTitleBarViewModel().showRightText3 = true;
            getCategoryBookListViewModel().getTitleBarViewModel().rightText2 = getString(R.string.subject_list_filter);
            getCategoryBookListViewModel().getTitleBarViewModel().rightText3 = getString(R.string.subject_list_sort_type_hot);
            initDefaultParams();
            setSortButtonIsOpen(false);
            setAllCatIsOpen(false);
            setRightText2Icon();
            setRightText3Icon();
            getBooksData(getFinalCatId(), currentPage, sortkey, sortType);
            setCategoryV2Data();
        }
    }

    private void initDefaultParams() {
        sortType = CloudApiContext.CategoryLevel2BookList.SORT_TYPE_DEFAULT_VALUES;
        getCategoryBookListViewModel().updateSortKeyInfo(getCategoryBookListViewModel().getSortKeySelected());
    }

    private String getFinalCatId() {
        return catTwoId + "_" + catLevel;
    }

    private void getBooksData(String catid, int currentPage, int sortKey, int sortType) {
        boolean justShowVip = categoryBookListBinding.subjectListShowVip.isChecked();
        int filter = justShowVip ? CloudApiContext.SearchBook.FILTER_VIP : CloudApiContext.SearchBook.FILTER_DEFAULT;
        final SearchBookListAction booksAction = new SearchBookListAction(catid, currentPage, sortKey, sortType, "", filter);
        booksAction.execute(getShopDataBundle(), new RxCallback<SearchBookListAction>() {
            @Override
            public void onNext(SearchBookListAction action) {
            }

            @Override
            public void onComplete() {
                updateContentView(booksAction.getBooksResultBean());
                gotoPage(getValidContentPage());
            }
        });
    }

    private void setCategoryV2Data() {
        List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> allCategoryItems = getAllCategoryViewModel().getAllCategoryItems();
        getCategoryBookListViewModel().setCategoryItems(allCategoryItems);
        getCategoryBookListViewModel().isFree.set(typeFree);
    }

    private void initView() {
        SubjectListAdapter adapter = new SubjectListAdapter(getEventBus());
        DashLineItemDivider itemDecoration = new DashLineItemDivider();
        recyclerView = categoryBookListBinding.recyclerViewSubjectList;
        recyclerView.setPageTurningCycled(true);
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.addItemDecoration(itemDecoration);
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
                getBooksData(getFinalCatId(), currentPage, sortkey, sortType);
                showOrCloseAllCatButton();
            }
        });
        checkWifi(currentCatName);
    }

    private void initPageIndicator() {
        int size = CollectionUtils.getSize(getCategoryBookListViewModel().getBookList());
        paginator.resize(row, col, size);
        getCategoryBookListViewModel().setTotalPage(paginator.pages());
        setCurrentPage(paginator.getCurrentPage());
    }

    private void updateContentView(BookModelBooksResultBean result) {
        List<ResultBookBean> list = result == null ? null : result.data.items;
        getCategoryBookListViewModel().addBookList(list, true);
        updateContentView();
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
        getCategoryBookListViewModel().setCurrentPage(currentPage + Constants.PAGE_STEP);
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
            getViewEventCallBack().viewBack();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookItemClickEvent(BookItemClickEvent event) {
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
        this.currentPage = 1;
        this.sortkey = CloudApiContext.CategoryLevel2BookList.SORT_KEY_DEFAULT_VALUES;
        getCategoryBookListViewModel().getTitleBarViewModel().leftText = currentCatName;
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.SP_KEY_CATEGORY_LEVEL_TWO_ID, catTwoId);
        bundle.putString(Constants.SP_KEY_CATEGORY_NAME, currentCatName);
        setBundle(bundle);
        getBooksData(getFinalCatId(), currentPage, sortkey, sortType);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopRight2Event(TopRightTitle2Event event) {
        showOrCloseAllCatButton();
    }

    private void showOrCloseAllCatButton() {
        if (getCategoryBookListViewModel().sortButtonIsOpen.get()) {
            showOrCloseSortButton();
        }
        boolean allCatIsOpen = getCategoryBookListViewModel().allCatIsOpen.get();
        setAllCatIsOpen(!allCatIsOpen);
        setRightText2Icon();
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
        setRightText3Icon();
    }

    private void setAllCatIsOpen(boolean allCatIsOpen) {
        getCategoryBookListViewModel().allCatIsOpen.set(allCatIsOpen);
    }

    private void setSortButtonIsOpen(boolean sortButtonIsOpen) {
        getCategoryBookListViewModel().sortButtonIsOpen.set(sortButtonIsOpen);
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
        unsetContentPage();
        getBooksData(getFinalCatId(), currentPage, sortkey, sortType);
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
}
