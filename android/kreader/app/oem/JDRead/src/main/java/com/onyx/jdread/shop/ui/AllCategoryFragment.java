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
import com.onyx.jdread.databinding.FragmentBookAllCategoryBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.action.BookCategoryAction;
import com.onyx.jdread.shop.adapter.AllCategoryTopAdapter;
import com.onyx.jdread.shop.adapter.CategorySubjectAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.event.CategoryAdapterRawDataChangeEvent;
import com.onyx.jdread.shop.event.CategoryBoyClick;
import com.onyx.jdread.shop.event.CategoryGirlClick;
import com.onyx.jdread.shop.event.CategoryItemClickEvent;
import com.onyx.jdread.shop.event.CategoryPublishClick;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.model.AllCategoryViewModel;
import com.onyx.jdread.shop.model.BookShopViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2017/12/30.
 */

public class AllCategoryFragment extends BaseFragment {

    private FragmentBookAllCategoryBinding allCategoryBinding;
    private int space = JDReadApplication.getInstance().getResources().getInteger(R.integer.all_category_recycle_view_space);
    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.all_category_recycle_view_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.all_category_recycle_view_col);
    private PageRecyclerView recyclerView;
    private GPaginator paginator;
    private static final int TYPE_PUBLISH = 10;
    private static final int TYPE_BOY = 11;
    private static final int TYPE_GIRL = 12;
    private int currentType = TYPE_PUBLISH;
    private BookCategoryAction bookCategoryAction;
    private PageRecyclerView topRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        allCategoryBinding = FragmentBookAllCategoryBinding.inflate(inflater, container, false);
        initView();
        initData();
        return allCategoryBinding.getRoot();
    }

    private void initData() {
        getCategoryData();
    }

    private void getCategoryData() {
        bookCategoryAction = new BookCategoryAction(JDReadApplication.getInstance(), true);
        bookCategoryAction.execute(getShopDataBundle(), new RxCallback<BookCategoryAction>() {
            @Override
            public void onNext(BookCategoryAction bookCategoryAction) {
                List<CategoryListResultBean.CatListBean> catList = bookCategoryAction.getCatList();
                setCategoryData(catList);
            }
        });
    }

    private void setCategoryData(List<CategoryListResultBean.CatListBean> categorySubjectItems) {
        if (categorySubjectItems != null) {
            getAllCategoryViewModel().setAllCategoryItems(categorySubjectItems);
            if (categorySubjectItems.size() <= col) {
                getAllCategoryViewModel().setTopCategoryItems(categorySubjectItems);
                getAllCategoryViewModel().setBottomCategoryItems(new ArrayList<CategoryListResultBean.CatListBean>());
            } else {
                getAllCategoryViewModel().setTopCategoryItems(categorySubjectItems.subList(0,col));
                getAllCategoryViewModel().setBottomCategoryItems(categorySubjectItems.subList(col,categorySubjectItems.size()));
            }
            updateContentView(getAllCategoryViewModel().getAllCategoryItems());
            recyclerView.gotoPage(0);
        }
    }

    private void initView() {
        CategorySubjectAdapter adapter = new CategorySubjectAdapter(getEventBus(), true);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContextJD(), DividerItemDecoration.HORIZONTAL_LIST);
        itemDecoration.setDrawLine(false);
        itemDecoration.setSpace(space);
        adapter.setRowAndCol(row, col);
        recyclerView = allCategoryBinding.recyclerViewAllCategorys;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(itemDecoration);
        paginator = recyclerView.getPaginator();
        getAllCategoryViewModel().getTitleBarViewModel().leftText = getString(R.string.all_category);
        recyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                if (paginator != null) {
                    setCurrentPage(paginator.getCurrentPage());
                }
            }
        });

        AllCategoryTopAdapter topAdapter = new AllCategoryTopAdapter(getEventBus());
        topRecyclerView = allCategoryBinding.recyclerViewCategoryTop;
        topRecyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        topRecyclerView.setAdapter(topAdapter);
        topRecyclerView.addItemDecoration(itemDecoration);
        allCategoryBinding.setCategoryViewModel(getAllCategoryViewModel());
    }

    private void initPageIndicator(List<CategoryListResultBean.CatListBean> resultBean) {
        int size = 0;
        if (resultBean != null) {
            size = resultBean.size();
        }
        paginator.resize(row, col, size);
        getAllCategoryViewModel().setTotalPage(paginator.pages());
        setCurrentPage(paginator.getCurrentPage());
    }

    private void updateContentView(List<CategoryListResultBean.CatListBean> resultBean) {
        if (recyclerView == null) {
            return;
        }
        recyclerView.getAdapter().notifyDataSetChanged();
        initPageIndicator(resultBean);
    }

    private void setCurrentPage(int currentPage) {
        getAllCategoryViewModel().setCurrentPage(currentPage + Constants.PAGE_STEP);
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
    public void onTopBackEvent(TopBackEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().viewBack();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoryPublishClick(CategoryPublishClick event) {
        if (currentType != TYPE_PUBLISH) {
            currentType = TYPE_PUBLISH;
            getCategoryData();
            changeCategoryButtonState();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoryBoyClick(CategoryBoyClick event) {
        if (currentType != TYPE_BOY) {
            currentType = TYPE_BOY;
            getCategoryData();
            changeCategoryButtonState();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoryGirlClick(CategoryGirlClick event) {
        if (currentType != TYPE_GIRL) {
            currentType = TYPE_GIRL;
            getCategoryData();
            changeCategoryButtonState();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoryItemClickEvent(CategoryItemClickEvent event) {
        CategoryListResultBean.CatListBean categoryBean = event.getCategoryBean();
        if (categoryBean != null){
            if (categoryBean.isLeaf == 0){
                List<CategoryListResultBean.CatListBean> catListBeen = bookCategoryAction.loadCategoryV2(getAllCategoryViewModel().getAllCategoryItems(), categoryBean.catId);
                setCategoryData(catListBeen);
            } else {
                PreferenceManager.setIntValue(getContextJD(),Constants.SP_KEY_CATEGORY_ID,categoryBean.catId);
                PreferenceManager.setStringValue(getContextJD(),Constants.SP_KEY_CATEGORY_NAME,categoryBean.catName);
                PreferenceManager.setBooleanValue(getContextJD(),Constants.SP_KEY_CATEGORY_ISFREE, Constants.CATEGORY_TYPE_FREE == categoryBean.catType);
                getViewEventCallBack().gotoView(SubjectListFragment.class.getName());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoryAdapterRawDataChangeEvent(CategoryAdapterRawDataChangeEvent event) {
        updateContentView(getAllCategoryViewModel().getAllCategoryItems());
    }

    public void changeCategoryButtonState() {
        allCategoryBinding.titleBoyShadow.setVisibility(currentType == TYPE_BOY ? View.VISIBLE : View.INVISIBLE);
        allCategoryBinding.titleGirlShadow.setVisibility(currentType == TYPE_GIRL ? View.VISIBLE : View.INVISIBLE);
        allCategoryBinding.titlePublisherShadow.setVisibility(currentType == TYPE_PUBLISH ? View.VISIBLE : View.INVISIBLE);
    }
}