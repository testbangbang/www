package com.onyx.android.dr.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.action.LibraryGotoPageAction;
import com.onyx.android.dr.adapter.EBookLanguageGroupAdapter;
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
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

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
    SinglePageRecyclerView ebookStoreGroupsRecycler;
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
    private LibraryDataHolder dataHolder;
    private EBookListAdapter listAdapter;
    private EBookLanguageGroupAdapter eBookLanguageGroupAdapter;
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
        listAdapter = new EBookListAdapter(getDataHolder());
        ebookStoreGroupsRecycler.setAdapter(listAdapter);
        initPageIndicator(pageIndicatorLayout);
        eBookLanguageGroupAdapter = new EBookLanguageGroupAdapter();

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

        ebookStoreGroupsRecycler.setOnChangePageListener(new SinglePageRecyclerView.OnChangePageListener() {
            @Override
            public void prev() {
                prevPage();
            }

            @Override
            public void next() {
                nextPage();
            }
        });
    }

    @Override
    protected void initData() {
        eBookStorePresenter = new EBookStorePresenter(this);
        eBookStorePresenter.getRootLibraryList(getParentLibraryId());
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
        if (result != null) {
            QueryResult<Metadata> queryResult = new QueryResult<>();
            queryResult.list = result;
            queryResult.count = result.size();
            Map<String, CloseableReference<Bitmap>> bitmaps = DataManagerHelper.loadCloudThumbnailBitmapsWithCache(this, DRApplication.getCloudStore().getCloudManager(), queryResult.list);
            updateContentView(getLibraryDataModel(queryResult, bitmaps));
        }
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
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(this);
            dataHolder.setCloudManager(DRApplication.getCloudStore().getCloudManager());
        }
        return dataHolder;
    }

    private LibraryDataModel getLibraryDataModel(QueryResult<Metadata> result, Map<String, CloseableReference<Bitmap>> map) {
        return LibraryViewInfo.buildLibraryDataModel(result, map);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadBooks(ebookStoreTab.getTabAt(ebookStoreTab.getSelectedTabPosition()).getText().toString(), getDataHolder());
    }

    private void updateContentView(LibraryDataModel libraryDataModel) {
        if (isContentViewInvalid()) {
            return;
        }
        listAdapter.updateContentView(libraryDataModel);
        updatePageIndicator();
    }

    private boolean isContentViewInvalid() {
        return ebookStoreGroupsRecycler == null || pageIndicator == null;
    }

    private void updatePageIndicator() {
        int totalCount = getTotalCount();
        getPagination().resize(listAdapter.getRowCount(), listAdapter.getColumnCount(), totalCount);
        pageIndicator.resetGPaginator(getPagination());
        pageIndicator.updateTotal(totalCount);
        pageIndicator.updateCurrentPage(totalCount);
    }

    private int getTotalCount() {
        LibraryDataModel dataModel = dataHolder.getCloudViewInfo().getLibraryDataModel();
        return dataModel.bookCount + dataModel.libraryCount;
    }

    private QueryPagination getPagination() {
        return dataHolder.getCloudViewInfo().getQueryPagination();
    }

    private void initPageIndicator(ViewGroup parentView) {
        if (parentView == null) {
            return;
        }
        initPagination();
        pageIndicator = new PageIndicator(parentView.findViewById(R.id.page_indicator_layout), ebookStoreGroupsRecycler.getPaginator());
        pageIndicator.showRefresh(false);
        pageIndicator.setTotalFormat(getString(R.string.total_format));
        pageIndicator.setPageChangedListener(new PageIndicator.PageChangedListener() {
            @Override
            public void prev() {
                prevPage();
            }

            @Override
            public void next() {
                nextPage();
            }

            @Override
            public void gotoPage(int page) {
                showGotoPageAction(page);
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
    }

    private void prevPage() {
        if (!getPagination().prevPage()) {
            return;
        }
        final CloudContentListRequest listRequest = new CloudContentListRequest(dataHolder.getCloudViewInfo().prevPage());
        DRApplication.getCloudStore().submitRequestToSingle(DRApplication.getInstance(), listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                QueryResult<Metadata> result = listRequest.getProductResult();
                updateContentView(getLibraryDataModel(result, listRequest.getThumbnailMap()));
                preLoadPrev();
            }
        });
    }

    private void preLoadPrev() {
        int preLoadPage = getPagination().getCurrentPage() - 1;
        if (preLoadPage < 0) {
            return;
        }
        CloudContentListRequest listRequest = new CloudContentListRequest(dataHolder.getCloudViewInfo().pageQueryArgs(preLoadPage));
        DRApplication.getCloudStore().submitRequestToSingle(DRApplication.getInstance(), listRequest, null);
    }

    private void nextPage() {
        if (!getPagination().nextPage()) {
            return;
        }
        QueryArgs queryArgs = dataHolder.getCloudViewInfo().nextPage();
        final CloudContentListRequest listRequest = new CloudContentListRequest(queryArgs);
        DRApplication.getCloudStore().submitRequestToSingle(DRApplication.getInstance(), listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                QueryResult<Metadata> result = listRequest.getProductResult();
                updateContentView(getLibraryDataModel(result, listRequest.getThumbnailMap()));
                preloadNext();
            }
        });
    }

    private void preloadNext() {
        int preLoadPage = getPagination().getCurrentPage() + 1;
        if (preLoadPage >= getPagination().pages()) {
            return;
        }
        QueryArgs queryArgs = dataHolder.getCloudViewInfo().pageQueryArgs(preLoadPage);
        final CloudContentListRequest listRequest = new CloudContentListRequest(queryArgs);
        DRApplication.getCloudStore().submitRequestToSingle(DRApplication.getInstance(), listRequest, null);
    }

    private void showGotoPageAction(int currentPage) {
        final LibraryGotoPageAction gotoPageAction = new LibraryGotoPageAction(this, currentPage,
                getPagination().pages());
        gotoPageAction.execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                int newPage = gotoPageAction.getSelectPage();
                gotoPageImpl(newPage);
            }
        });
    }

    private void gotoPageImpl(int page) {
        final int originPage = getPagination().getCurrentPage();
        loadData(dataHolder.getCloudViewInfo().gotoPage(page), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    getPagination().setCurrentPage(originPage);
                    return;
                }
                CloudContentListRequest listRequest = (CloudContentListRequest) request;
                QueryResult<Metadata> result = listRequest.getProductResult();
                updateContentView(getLibraryDataModel(result, listRequest.getThumbnailMap()));
                preLoadPrev();
                preloadNext();
            }
        });
    }

    private void loadData(QueryArgs queryArgs, BaseCallback callback) {
        CloudContentListRequest listRequest = new CloudContentListRequest(queryArgs);
        DRApplication.getCloudStore().submitRequestToSingle(DRApplication.getInstance(), listRequest, callback);
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
