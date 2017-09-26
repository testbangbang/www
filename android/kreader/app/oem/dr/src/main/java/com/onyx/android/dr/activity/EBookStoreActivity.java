package com.onyx.android.dr.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.EBookListAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.AddToCartEvent;
import com.onyx.android.dr.event.BookDetailEvent;
import com.onyx.android.dr.event.DownloadSucceedEvent;
import com.onyx.android.dr.event.PayForEvent;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.interfaces.EBookStoreView;
import com.onyx.android.dr.presenter.EBookStorePresenter;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.dr.view.PageIndicator;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.model.Metadata;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by hehai on 17-8-2.
 */

public class EBookStoreActivity extends BaseActivity implements EBookStoreView {
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.title_bar_title)
    TextView titleBarTitle;
    @Bind(R.id.title_bar_right_icon_one)
    ImageView search;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.ebook_store_groups_recycler)
    PageRecyclerView ebookStoreGroupsRecycler;
    @Bind(R.id.title_bar_right_menu)
    TextView titleBarRightMenu;
    @Bind(R.id.title_bar_right_shopping_cart)
    TextView shoppingCart;
    @Bind(R.id.ebook_store_tab)
    TabLayout ebookStoreTab;
    @Bind(R.id.prev)
    ImageView prev;
    @Bind(R.id.next)
    ImageView next;
    @Bind(R.id.page_panel)
    LinearLayout pagePanel;
    @Bind(R.id.page_indicator_layout)
    RelativeLayout pageIndicatorLayout;
    private EBookStorePresenter eBookStorePresenter;
    private EBookListAdapter listAdapter;
    private PageIndicator pageIndicator;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_ebook_store;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {
        titleBarTitle.setText(getString(R.string.ebook_store));
        shoppingCart.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
        search.setImageResource(R.drawable.ic_search);
        displayCartCount();
        ebookStoreGroupsRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        ebookStoreGroupsRecycler.addItemDecoration(dividerItemDecoration);
        listAdapter = new EBookListAdapter();
        ebookStoreGroupsRecycler.setAdapter(listAdapter);
        initPageIndicator(pageIndicatorLayout);

        ebookStoreTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadBooks(tab.getText().toString(), getDataHolder());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ebookStoreGroupsRecycler.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPrevPage(int prevPosition, int itemCount, int pageSize) {
                getPagination().prevPage();
                updatePageIndicator();
            }

            @Override
            public void onNextPage(int nextPosition, int itemCount, int pageSize) {
                getPagination().nextPage();
                updatePageIndicator();
            }
        });
    }

    @Override
    protected void initData() {
        eBookStorePresenter = new EBookStorePresenter(this);
        eBookStorePresenter.getRootLibraryList(getParentLibraryId());
        showProgressDialog("", null);
        eBookStorePresenter.getCartCount();
    }

    @OnClick({R.id.menu_back, R.id.title_bar_right_shopping_cart, R.id.title_bar_right_icon_one})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                back();
                break;
            case R.id.title_bar_right_shopping_cart:
                ActivityManager.startShoppingCartActivity(EBookStoreActivity.this);
                break;
            case R.id.title_bar_right_icon_one:
                ActivityManager.startSearchBookActivity(EBookStoreActivity.this);
                break;
        }
    }

    private void back() {
        finish();
    }

    @Override
    public void setBooks(List<Metadata> result) {
        listAdapter.setEBookList(result);
        updatePageIndicator();
        dismissAllProgressDialog();
    }

    @Override
    public void setLanguageList(List<String> languageList) {
        ebookStoreTab.removeAllTabs();
        for (String language : languageList) {
            ebookStoreTab.addTab(ebookStoreTab.newTab().setText(language));
        }
    }

    @Override
    public void setOrderId(String id) {
        ActivityManager.startPayActivity(this, id);
    }

    @Override
    public void setCartCount(int count) {
        DRApplication.getInstance().setCartCount(count);
        displayCartCount();
    }

    private void loadBooks(String language, LibraryDataHolder holder) {
        eBookStorePresenter.getLanguageCategoryBooks(holder, language);
    }

    private String getParentLibraryId() {
        return DRPreferenceManager.loadLibraryParentId(this, Constants.EMPTY_STRING);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailEvent(BookDetailEvent event) {
        ActivityManager.startBookDetailActivity(this, event.getBookId());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onDownloadSucceedEvent(DownloadSucceedEvent event) {
        getDataHolder().getCloudManager().getCloudDataProvider().saveMetadata(DRApplication.getInstance(), event.getMetadata());
    }


    private LibraryDataHolder getDataHolder() {
        return DRApplication.getLibraryDataHolder();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadBooks(ebookStoreTab.getTabAt(ebookStoreTab.getSelectedTabPosition()).getText().toString(), getDataHolder());
    }

    private void updatePageIndicator() {
        int totalCount = getTotalCount();
        getPagination().resize(listAdapter.getRowCount(), listAdapter.getColumnCount(), totalCount);
        pageIndicator.resetGPaginator(getPagination());
        pageIndicator.updateTotal(totalCount);
        pageIndicator.updateCurrentPage(totalCount);
    }

    private int getTotalCount() {
        return listAdapter.getBookListSize();
    }

    private QueryPagination getPagination() {
        return getDataHolder().getCloudViewInfo().getQueryPagination();
    }

    private void initPageIndicator(ViewGroup parentView) {
        if (parentView == null) {
            return;
        }
        initPagination();
        pageIndicator = new PageIndicator(parentView.findViewById(R.id.page_indicator_layout), getPagination());
        pageIndicator.showRefresh(false);
        pageIndicator.setTotalFormat(getString(R.string.total_format));
        pageIndicator.setPageChangedListener(new PageIndicator.PageChangedListener() {
            @Override
            public void prev() {
                ebookStoreGroupsRecycler.prevPage();
            }

            @Override
            public void next() {
                ebookStoreGroupsRecycler.nextPage();
            }

            @Override
            public void gotoPage(int page) {

            }
        });
        pageIndicator.setDataRefreshListener(new PageIndicator.DataRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
    }

    private void initPagination() {
        QueryPagination pagination = getPagination();
        pagination.resize(listAdapter.getRowCount(), listAdapter.getColumnCount(), 0);
        pagination.setCurrentPage(0);
        ebookStoreGroupsRecycler.setCurrentPage(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPayForEvent(PayForEvent event) {
        eBookStorePresenter.createOrder(event.getBookId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddToCartEvent(AddToCartEvent event) {
        eBookStorePresenter.addToCart(event.getBookId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayCartCount();
    }

    private void displayCartCount() {
        shoppingCart.setText(String.format(getString(R.string.shopping_cart_count_format), DRApplication.getInstance().getCartCount()));
    }
}
