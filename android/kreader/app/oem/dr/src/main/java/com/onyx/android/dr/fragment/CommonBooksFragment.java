package com.onyx.android.dr.fragment;

import android.graphics.Bitmap;
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
import com.onyx.android.dr.adapter.BookListAdapter;
import com.onyx.android.dr.event.BackToMainViewEvent;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.view.PageIndicator;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentRefreshRequest;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;
import com.onyx.android.sdk.utils.NetworkUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by hehai on 17-6-29.
 */

public class CommonBooksFragment extends BaseFragment {
    public static final String LIBRARY_ID_ARGS = "LIBRARY_ID_ARGS";
    public static final String FRAGMENT_NAME = "FRAGMENT_NAME";
    public static final String IMAGE_RESOURCE = "image_resource";
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView titleBarTitle;
    @Bind(R.id.title_bar_right_menu)
    TextView titleBarRightMenu;
    @Bind(R.id.pre_button)
    ImageView preButton;
    @Bind(R.id.next_button)
    ImageView nextButton;
    @Bind(R.id.page_recycler)
    SinglePageRecyclerView pageRecycler;
    @Bind(R.id.total_tv)
    TextView totalTv;
    @Bind(R.id.prev)
    ImageView prev;
    @Bind(R.id.next)
    ImageView next;
    @Bind(R.id.page_panel)
    LinearLayout pagePanel;
    @Bind(R.id.refresh)
    ImageView refresh;
    @Bind(R.id.page_indicator_layout)
    RelativeLayout pageIndicatorLayout;
    private LibraryDataHolder dataHolder;
    private BookListAdapter libraryAdapter;
    private LibraryViewInfo viewInfo;
    private QueryArgs args;
    private String idString = "";
    private PageIndicator pageIndicator;
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.common_books_fragment_row);
    private int col = DRApplication.getInstance().getResources().getInteger(R.integer.common_books_fragment_col);
    private String uniqueId;
    private String fragmentName;
    private int resourceId;

    @Override
    protected void initListener() {
        pageRecycler.setOnChangePageListener(new SinglePageRecyclerView.OnChangePageListener() {
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
    protected void initView(View rootView) {
        titleBarTitle.setText(getString(R.string.menu_real_time_articles));
        image.setImageResource(R.drawable.ic_real_time_books);
        libraryAdapter = new BookListAdapter(getActivity(), getDataHolder());
        libraryAdapter.setRowAndCol(row, col);
        pageRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        pageRecycler.addItemDecoration(dividerItemDecoration);
        pageRecycler.setAdapter(libraryAdapter);
        initPageIndicator(pageIndicatorLayout);
    }

    @Override
    protected void loadData() {
        titleBarTitle.setText(fragmentName);
        image.setImageResource(resourceId);
        viewInfo = getDataHolder().getCloudViewInfo();
        viewInfo.updateSortBy(SortBy.CreationTime, SortOrder.Desc);
        viewInfo.getQueryPagination().setCurrentPage(0);
        args = viewInfo.libraryQuery(idString);
        args.useCloudMemDbPolicy();
        if (NetworkUtil.isWiFiConnected(getActivity())) {
            refreshDataFormCloud();
        } else {
            loadData(args);
        }
    }

    public void setData(String idString, String fragmentName, int resourceId) {
        this.idString = idString;
        this.fragmentName = fragmentName;
        this.resourceId = resourceId;
        if (titleBarTitle != null && image != null) {
            loadData();
        }
    }

    private void loadData(final QueryArgs args) {
        final CloudContentListRequest listRequest = new CloudContentListRequest(args);
        DRApplication.getCloudStore().submitRequestToSingle(getActivity(), listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                QueryResult<Metadata> result = listRequest.getProductResult();
                updateContentView(getLibraryDataModel(result, listRequest.getThumbnailMap()));
            }
        });
    }

    private void updateContentView(LibraryDataModel libraryDataModel) {
        if (isContentViewInvalid()) {
            return;
        }
        libraryAdapter.updateContentView(libraryDataModel);
        updatePageIndicator();
    }

    private boolean isContentViewInvalid() {
        return pageRecycler == null || pageIndicator == null;
    }

    private LibraryDataModel getLibraryDataModel(QueryResult<Metadata> result, Map<String, CloseableReference<Bitmap>> map) {
        return LibraryViewInfo.buildLibraryDataModel(result, map);
    }

    @Override
    protected int getRootView() {
        return R.layout.fragment_common_books;
    }

    @Override
    public boolean onKeyBack() {
        EventBus.getDefault().post(new BackToMainViewEvent());
        return true;
    }

    @OnClick({R.id.menu_back, R.id.pre_button, R.id.next_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                EventBus.getDefault().post(new BackToMainViewEvent());
                break;
            case R.id.pre_button:
                break;
            case R.id.next_button:
                break;
        }
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(getActivity());
            dataHolder.setCloudManager(DRApplication.getCloudStore().getCloudManager());
        }
        return dataHolder;
    }

    private void refreshDataFormCloud() {
        if (enableWifiOpenAndDetect()) {
            ToastUtils.showToast(getActivity(), R.string.open_wifi);
            return;
        }
        final CloudContentRefreshRequest refreshRequest = new CloudContentRefreshRequest(args);
        DRApplication.getCloudStore().submitRequest(getActivity(), refreshRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                QueryResult<Metadata> result = refreshRequest.getProductResult();
                updateContentView(getLibraryDataModel(result, refreshRequest.getThumbnailMap()));
                getPagination().setCurrentPage(0);
                preloadNext();
            }
        });
    }

    private void updatePageIndicator() {
        int totalCount = getTotalCount();
        getPagination().resize(row, col, totalCount);
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
        pageIndicator = new PageIndicator(parentView.findViewById(R.id.page_indicator_layout), getPagination());
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
                refreshDataFormCloud();
            }
        });
    }

    private void initPagination() {
        QueryPagination pagination = getPagination();
        pagination.resize(row, col, 0);
        pagination.setCurrentPage(0);
    }

    private void prevPage() {
        if (!getPagination().prevPage()) {
            return;
        }
        final CloudContentListRequest listRequest = new CloudContentListRequest(dataHolder.getCloudViewInfo().prevPage());
        DRApplication.getCloudStore().submitRequestToSingle(getActivity(), listRequest, new BaseCallback() {
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
        DRApplication.getCloudStore().submitRequestToSingle(getActivity(), listRequest, null);
    }

    private void nextPage() {
        if (!getPagination().nextPage()) {
            return;
        }
        QueryArgs queryArgs = dataHolder.getCloudViewInfo().nextPage();
        final CloudContentListRequest listRequest = new CloudContentListRequest(queryArgs);
        DRApplication.getCloudStore().submitRequestToSingle(getActivity(), listRequest, new BaseCallback() {
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
        DRApplication.getCloudStore().submitRequestToSingle(getActivity(), listRequest, null);
    }

    private void showGotoPageAction(int currentPage) {
        final LibraryGotoPageAction gotoPageAction = new LibraryGotoPageAction(getActivity(), currentPage,
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
        DRApplication.getCloudStore().submitRequestToSingle(getActivity(), listRequest, callback);
    }

    private boolean enableWifiOpenAndDetect() {
        if (!NetworkUtil.isWiFiConnected(getActivity())) {
            Device.currentDevice().enableWifiDetect(getActivity());
            NetworkUtil.enableWiFi(getActivity(), true);
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyPageDown() {
        nextPage();
        return true;
    }

    @Override
    public boolean onKeyPageUp() {
        prevPage();
        return true;
    }
}
