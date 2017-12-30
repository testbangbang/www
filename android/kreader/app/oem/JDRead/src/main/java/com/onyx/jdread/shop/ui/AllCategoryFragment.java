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
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.FragmentBookAllCategoryBinding;
import com.onyx.jdread.shop.action.BookCategoryAction;
import com.onyx.jdread.shop.adapter.CategorySubjectAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.event.OnCategoryBoyClick;
import com.onyx.jdread.shop.event.OnCategoryGirlClick;
import com.onyx.jdread.shop.event.OnCategoryPublishClick;
import com.onyx.jdread.shop.event.OnTopBackEvent;
import com.onyx.jdread.shop.model.AllCategoryViewModel;
import com.onyx.jdread.shop.model.BookShopViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.onyx.jdread.common.Constants.PAGE_STEP;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        allCategoryBinding = FragmentBookAllCategoryBinding.inflate(inflater, container, false);
        initView();
        initData();
        return allCategoryBinding.getRoot();
    }

    private void initData() {
        List<CategoryListResultBean.CatListBean> categorySubjectItems = getShopViewModel().getCategorySubjectItems();
        if (categorySubjectItems == null) {
            getCategoryData();
        } else {
            getShopViewModel().setCategorySubjectItems(categorySubjectItems);
            initPageIndicator(categorySubjectItems);
        }
    }

    private void getCategoryData() {
        BookCategoryAction bookCategoryAction = new BookCategoryAction(JDReadApplication.getInstance());
        bookCategoryAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
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
        allCategoryBinding.setBookShopViewModel(getShopViewModel());
        getAllCategoryViewModel().getTitleBarViewModel().leftText = getString(R.string.all_category);
        recyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                if (paginator != null) {
                    setCurrentPage(paginator.getCurrentPage());
                }
            }
        });
    }

    private void initPageIndicator(List<CategoryListResultBean.CatListBean> resultBean) {
        if (resultBean != null) {
            int size = resultBean.size();
            recyclerView.resize(recyclerView.getPageAdapter().getRowCount(), recyclerView.getPageAdapter().getColumnCount(), size);
            getAllCategoryViewModel().setTotalPage(paginator.pages());
            setCurrentPage(paginator.getCurrentPage());
        }
    }

    private void setCurrentPage(int currentPage) {
        getAllCategoryViewModel().setCurrentPage(currentPage + PAGE_STEP);
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
    public void onTopBackEvent(OnTopBackEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().viewBack();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoryPublishClick(OnCategoryPublishClick event) {
        if (currentType != TYPE_PUBLISH) {
            currentType = TYPE_PUBLISH;
            getCategoryData();
            changeCategoryButtonState();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoryBoyClick(OnCategoryBoyClick event) {
        if (currentType != TYPE_BOY) {
            currentType = TYPE_BOY;
            getCategoryData();
            changeCategoryButtonState();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoryGirlClick(OnCategoryGirlClick event) {
        if (currentType != TYPE_GIRL) {
            currentType = TYPE_GIRL;
            getCategoryData();
            changeCategoryButtonState();
        }
    }

    public void changeCategoryButtonState() {
        allCategoryBinding.titleBoyShadow.setVisibility(currentType == TYPE_BOY ? View.VISIBLE : View.INVISIBLE);
        allCategoryBinding.titleGirlShadow.setVisibility(currentType == TYPE_GIRL ? View.VISIBLE : View.INVISIBLE);
        allCategoryBinding.titlePublisherShadow.setVisibility(currentType == TYPE_PUBLISH ? View.VISIBLE : View.INVISIBLE);
    }
}