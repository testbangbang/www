package com.onyx.android.dr.fragment;

import android.graphics.Bitmap;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.action.LibraryGotoPageAction;
import com.onyx.android.dr.adapter.BookListAdapter;
import com.onyx.android.dr.event.BackToBookshelfEvent;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentRefreshRequest;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;
import com.onyx.android.sdk.utils.NetworkUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by hehai on 17-7-11.
 */

public class BookshelfV2Fragment extends BaseFragment {
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
    @Bind(R.id.bookshelf_book_search)
    ImageView bookshelfBookSearch;
    @Bind(R.id.bookshelf_author_search)
    ImageView bookshelfAuthorSearch;
    @Bind(R.id.bookshelf_v2_pre_button)
    ImageView bookshelfV2PreButton;
    @Bind(R.id.bookshelf_v2_next_button)
    ImageView bookshelfV2NextButton;
    @Bind(R.id.bookshelf_v2_recycler)
    SinglePageRecyclerView bookshelfV2Recycler;
    @Bind(R.id.enter_bookstore)
    TextView enterBookstore;
    private BookListAdapter adapter;
    private String title;
    private QueryArgs args;
    private LibraryDataHolder dataHolder;
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.book_list_recycler_row);
    private int col = DRApplication.getInstance().getResources().getInteger(R.integer.book_list_recycler_column);
    private LibraryDataModel libraryDataModel;
    private LibraryDataModel pageLibraryDataModel;
    private LibraryViewInfo viewInfo;

    @Override
    protected void initListener() {
        bookshelfV2Recycler.setOnChangePageListener(new SinglePageRecyclerView.OnChangePageListener() {
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
        adapter = new BookListAdapter(getActivity(), getDataHolder());
        adapter.setRowAndCol(row, col);
        bookshelfV2Recycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        bookshelfV2Recycler.addItemDecoration(dividerItemDecoration);
        bookshelfV2Recycler.setAdapter(adapter);
        initPagination();
    }

    @Override
    protected void loadData() {
        titleBarTitle.setText(title);
        args.fetchPolicy = FetchPolicy.MEM_CLOUD_DB;
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
        adapter.updateContentView(libraryDataModel);
        updatePageIndicator();
    }

    private boolean isContentViewInvalid() {
        return bookshelfV2Recycler == null;
    }

    private LibraryDataModel getLibraryDataModel(QueryResult<Metadata> result, Map<String, CloseableReference<Bitmap>> map) {
        return LibraryViewInfo.buildLibraryDataModel(result, map);
    }

    @Override
    protected int getRootView() {
        return R.layout.fragment_v2_bookshelf;
    }

    @Override
    public boolean onKeyBack() {
        back();
        return true;
    }

    private void back() {
        EventBus.getDefault().post(new BackToBookshelfEvent());
    }

    public void setData(String title, QueryArgs queryArgs) {
        this.title = title;
        this.args = queryArgs;
        loadData();
    }

    @OnClick({R.id.menu_back, R.id.bookshelf_book_search, R.id.bookshelf_author_search, R.id.bookshelf_v2_pre_button, R.id.bookshelf_v2_next_button, R.id.enter_bookstore})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                back();
                break;
            case R.id.bookshelf_book_search:
                break;
            case R.id.bookshelf_author_search:
                break;
            case R.id.bookshelf_v2_pre_button:
                break;
            case R.id.bookshelf_v2_next_button:
                break;
            case R.id.enter_bookstore:
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
}
