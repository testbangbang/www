package com.onyx.android.eschool.fragment;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.ViewTarget;
import com.facebook.common.references.CloseableReference;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.onyx.android.eschool.R;
import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.eschool.action.CloudContentLoadAction;
import com.onyx.android.eschool.action.CloudContentRefreshAction;
import com.onyx.android.eschool.action.DownloadAction;
import com.onyx.android.eschool.action.LibraryGotoPageAction;
import com.onyx.android.eschool.custom.PageIndicator;
import com.onyx.android.eschool.device.DeviceConfig;
import com.onyx.android.eschool.events.AccountTokenErrorEvent;
import com.onyx.android.eschool.events.BookLibraryEvent;
import com.onyx.android.eschool.events.DownloadingEvent;
import com.onyx.android.eschool.events.HardwareErrorEvent;
import com.onyx.android.eschool.events.TabSwitchEvent;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.manager.CacheManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudThumbnailLoadRequest;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.data.utils.MetadataUtils;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.dialog.DialogProgressHolder;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.RawResourceUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by suicheng on 2017/4/28.
 */
public class BookTextFragment extends Fragment {
    public static final String LIBRARY_ID_ARGS = "libraryIdArgs";
    public static final String FRAGMENT_NAME = "fragmentName";

    @Bind(R.id.content_pageView)
    SinglePageRecyclerView contentPageView;
    @Bind(R.id.parent_library_ref)
    LinearLayout parentLibraryRefLayout;
    @Bind(R.id.root_library_view)
    TextView rootLibraryView;

    PageIndicator pageIndicator;

    LibraryDataHolder dataHolder;
    private LibraryDataModel pageDataModel = new LibraryDataModel();

    private boolean newPage = false;
    private int noThumbnailPosition = 0;

    private int row = 2;
    private int col = 3;

    private String fragmentName;
    private boolean isVisibleToUser = false;
    private boolean isColorDevice = true;
    private int[] imageReqWidthHeight;

    private DialogProgressHolder progressDialogHolder = new DialogProgressHolder();

    public static BookTextFragment newInstance(String fragmentName, String libraryId) {
        BookTextFragment fragment = new BookTextFragment();
        Bundle bundle = new Bundle();
        resetArgumentsBundle(bundle, fragmentName, libraryId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static void resetArgumentsBundle(Bundle bundle, String fragmentName, String libraryId) {
        if (bundle == null) {
            return;
        }
        if (StringUtils.isNotBlank(libraryId)) {
            bundle.putString(LIBRARY_ID_ARGS, libraryId);
        }
        if (StringUtils.isNotBlank(fragmentName)) {
            bundle.putString(FRAGMENT_NAME, fragmentName);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConfig();
    }

    private void initConfig() {
        isColorDevice = DeviceConfig.sharedInstance(getContext()).isDeviceSupportColor();
        if (isColorDevice) {
            imageReqWidthHeight = new int[]{ViewTarget.SIZE_ORIGINAL, ViewTarget.SIZE_ORIGINAL};
        } else {
            imageReqWidthHeight = new int[]{getResources().getDimensionPixelSize(R.dimen.book_item_cover_image_width),
                    getResources().getDimensionPixelSize(R.dimen.book_item_cover_image_height)};
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_text, container, false);
        ButterKnife.bind(this, view);
        initView((ViewGroup) view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return processBackKey();
                    }
                }
                return false;
            }
        });
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private String getRootLibraryId() {
        if (getArguments() == null) {
            return null;
        }
        return getArguments().getString(LIBRARY_ID_ARGS);
    }

    private void loadData() {
        String uniqueId = null;
        if (getArguments() != null) {
            uniqueId = getRootLibraryId();
            fragmentName = getArguments().getString(FRAGMENT_NAME);
        }
        if (StringUtils.isNullOrEmpty(uniqueId)) {
            return;
        }
        updateRootLibraryView(fragmentName, uniqueId);
        loadData(uniqueId);
    }

    private void loadData(String libraryId) {
        LibraryViewInfo viewInfo = getLibraryViewInfo();
        viewInfo.updateSortBy(SortBy.CreationTime, SortOrder.Desc);
        viewInfo.getQueryPagination().setCurrentPage(0);
        QueryArgs args = viewInfo.libraryQuery(libraryId);
        args.useCloudMemDbPolicy();
        if (NetworkUtil.isWiFiConnected(getContext())) {
            refreshDataFormCloud();
        } else {
            loadData(args);
        }
    }

    private void loadData(final QueryArgs args) {
        final CloudContentLoadAction contentLoadAction = new CloudContentLoadAction(args);
        contentLoadAction.execute(getDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                updateContentView(contentLoadAction.getDataModel());
                preloadNext();
            }
        });
    }

    private void nextPage() {
        if (!getPagination().nextPage()) {
            postNextTab();
            return;
        }
        final CloudContentLoadAction loadAction = new CloudContentLoadAction(getLibraryViewInfo().nextPage());
        loadAction.execute(getDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                updateContentView(loadAction.getDataModel());
                preloadNext();
            }
        });
    }

    private LibraryDataModel getLibraryDataModel(QueryResult<Metadata> result, Map<String, CloseableReference<Bitmap>> map) {
        return LibraryViewInfo.buildLibraryDataModel(result, map);
    }

    private QueryPagination getPagination() {
        return getLibraryViewInfo().getQueryPagination();
    }

    private void initPagination() {
        QueryPagination pagination = getPagination();
        pagination.resize(row, col, 0);
        pagination.setCurrentPage(0);
    }

    private void preloadNext() {
        int preLoadPage = getPagination().getCurrentPage() + 1;
        if (preLoadPage >= getPagination().pages()) {
            return;
        }
        QueryArgs queryArgs = getLibraryViewInfo().pageQueryArgs(preLoadPage);
        final CloudContentLoadAction contentLoadAction = new CloudContentLoadAction(queryArgs);
        contentLoadAction.execute(getDataHolder(), null);
    }

    protected void initView(ViewGroup parentView) {
        initContentPageView();
        initPageIndicator(parentView);
    }

    private void initContentPageView() {
        contentPageView.setNestedScrollingEnabled(true);
        contentPageView.setLayoutManager(new DisableScrollGridManager(getContext()));
        contentPageView.setOnChangePageListener(new SinglePageRecyclerView.OnChangePageListener() {
            @Override
            public void prev() {
                prevPage();
            }

            @Override
            public void next() {
                nextPage();
            }
        });
        contentPageView.setAdapter(new PageRecyclerView.PageAdapter<BookItemHolder>() {
            @Override
            public int getRowCount() {
                return row;
            }

            @Override
            public int getColumnCount() {
                return col;
            }

            @Override
            public int getDataCount() {
                return getLibraryListSize() + getBookListSize();
            }

            @Override
            public BookItemHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new BookItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.book_reading_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(final BookItemHolder viewHolder, int position) {
                viewHolder.itemView.setTag(position);

                if (position < getLibraryListSize()) {
                    Library library = getLibraryList().get(position);
                    viewHolder.coverImage.setImageResource(R.drawable.library_sub_cover);
                    viewHolder.getWidgetImage.setVisibility(View.GONE);
                    viewHolder.titleView.setText(String.valueOf(library.getName()));
                } else {
                    updateThumbnailView(viewHolder, getBookItemPosition(position));
                }
            }
        });
    }

    private int getImageResource(Metadata book) {
        String type = book.getType();
        if (StringUtils.isNullOrEmpty(type)) {
            return R.drawable.cloud_default_cover;
        }
        Map<String, String> map = DeviceConfig.sharedInstance(getContext()).getCustomizedProductCovers();
        if (map.containsKey(type)) {
            return RawResourceUtil.getDrawableIdByName(getContext(), map.get(type));
        }
        return R.drawable.cloud_default_cover;
    }

    public int getLibraryListSize() {
        return CollectionUtils.getSize(getPageDataModel().visibleLibraryList);
    }

    public int getBookListSize() {
        return CollectionUtils.getSize(getEBookList());
    }

    private Bitmap getBitmap(Metadata metadata) {
        LibraryDataModel libraryDataModel = getPageDataModel();
        if (libraryDataModel == null || libraryDataModel.thumbnailMap == null) {
            return null;
        }
        Bitmap bitmap = null;
        CloseableReference<Bitmap> refBitmap = libraryDataModel.thumbnailMap.get(getThumbnailMapKey(metadata));
        if (refBitmap != null && refBitmap.isValid()) {
            bitmap = refBitmap.get();
        }
        return bitmap;
    }

    private OnyxThumbnail.ThumbnailKind getThumbnailKind() {
        if (!isColorDevice) {
            return OnyxThumbnail.ThumbnailKind.Original;
        }
        return OnyxThumbnail.ThumbnailKind.Large;
    }

    private String getCoverKey() {
        return getThumbnailKind().toString().toLowerCase();
    }

    private String getCoverUrl(final Metadata metadata) {
        String url = metadata.getCoverUrl(getCoverKey());
        if (StringUtils.isNullOrEmpty(url)) {
            url = metadata.getCoverUrl();
        }
        return getRealUrl(url);
    }

    private String getThumbnailMapKey(Metadata metadata) {
        return CacheManager.generateCloudThumbnailKey(metadata.getAssociationId(), getCoverUrl(metadata),
                getCoverKey());
    }

    private int[] getReqImageWidthHeight() {
        return imageReqWidthHeight;
    }

    private void loadThumbnailRequest(final int position, final Metadata metadata) {
        final CloudThumbnailLoadRequest loadRequest = new CloudThumbnailLoadRequest(
                getCoverUrl(metadata), metadata.getAssociationId(), getThumbnailKind());
        loadRequest.setReqWidthHeight(getReqImageWidthHeight());
        if (isVisibleToUser && noThumbnailPosition == position) {
            loadRequest.setAbortPendingTasks(true);
        }
        getCloudStore().submitRequestToSingle(getContext().getApplicationContext(), loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (!isContentValid(request, e)) {
                    return;
                }
                CloseableReference<Bitmap> closeableRef = loadRequest.getRefBitmap();
                if (closeableRef != null && closeableRef.isValid()) {
                    getPageDataModel().thumbnailMap.put(getThumbnailMapKey(metadata), closeableRef);
                    updateContentView();
                }
            }
        });
    }

    private boolean isContentValid(BaseRequest request, Throwable e) {
        if (e != null || request.isAbort() || isContentViewInvalid()) {
            return false;
        }
        return true;
    }

    private boolean isContentViewInvalid() {
        return contentPageView == null || pageIndicator == null;
    }

    private String getRealUrl(String url) {
        if (StringUtils.isNotBlank(url) && !url.startsWith(Constant.HTTP_TAG)) {
            url = getCloudStore().getCloudConf().getHostBase() + url;
        }
        return url;
    }

    private void updateDownloadPanel(ImageView getWidgetImage, Metadata eBook) {
        if (isFileExists(eBook)) {
            getWidgetImage.setVisibility(View.VISIBLE);
            getWidgetImage.setImageResource(R.drawable.book_item_get_widget);
            return;
        }

        boolean showProgress = true;
        BaseDownloadTask task = getDownLoaderManager().getTask(eBook.getGuid());
        if (task == null) {
            showProgress = false;
        } else {
            switch (task.getStatus()) {
                case FileDownloadStatus.started:
                case FileDownloadStatus.pending:
                case FileDownloadStatus.progress:
                    showProgress = true;
                    break;
                case FileDownloadStatus.completed:
                case FileDownloadStatus.error:
                    showProgress = false;
                    getDownLoaderManager().removeTask(eBook.getGuid());
                    break;
            }
        }
        getWidgetImage.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        if (showProgress) {
            getWidgetImage.setImageResource(R.drawable.book_item_download_widget);
        }
    }

    private void initPageIndicator(ViewGroup parentView) {
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

    private void postNextTab() {
        EventBus.getDefault().post(TabSwitchEvent.createNextTabSwitch());
    }

    private void postPrevTab() {
        EventBus.getDefault().post(TabSwitchEvent.createPrevTabSwitch());
    }

    private void prevPage() {
        if (!getPagination().prevPage()) {
            postPrevTab();
            return;
        }

        final CloudContentLoadAction contentLoadAction = new CloudContentLoadAction(getLibraryViewInfo().prevPage());
        contentLoadAction.execute(getDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                updateContentView(contentLoadAction.getDataModel());
                preLoadPrev();
            }
        });
    }

    private void preLoadPrev() {
        int preLoadPage = getPagination().getCurrentPage() - 1;
        if (preLoadPage < 0) {
            return;
        }
        final CloudContentLoadAction contentLoadAction = new CloudContentLoadAction(getLibraryViewInfo().pageQueryArgs(preLoadPage));
        contentLoadAction.execute(getDataHolder(), null);
    }

    private void showGotoPageAction(int currentPage) {
        final LibraryGotoPageAction gotoPageAction = new LibraryGotoPageAction(getActivity(), currentPage,
                getPagination().pages());
        gotoPageAction.execute(getDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                int newPage = gotoPageAction.getSelectPage();
                gotoPageImpl(newPage);
            }
        });
    }

    private void gotoPageImpl(int page) {
        final int originPage = getPagination().getCurrentPage();
        final CloudContentLoadAction contentLoadAction = new CloudContentLoadAction(getLibraryViewInfo().gotoPage(page));
        contentLoadAction.execute(getDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    getPagination().setCurrentPage(originPage);
                    return;
                }
                updateContentView(contentLoadAction.getDataModel());
                preLoadPrev();
                preloadNext();
            }
        });
    }

    private LibraryDataModel getPageDataModel() {
        return pageDataModel;
    }

    private void updateRootLibraryView(String libraryName, String libraryId) {
        rootLibraryView.setVisibility(View.VISIBLE);
        rootLibraryView.setText(libraryName);
        rootLibraryView.setTag(libraryId);
    }

    private void updateContentView(LibraryDataModel libraryDataModel) {
        pageDataModel = getLibraryViewInfo().getPageLibraryDataModel(libraryDataModel);
        updateContentView();
    }

    private void updateContentView() {
        if (isContentViewInvalid()) {
            return;
        }
        newPage = true;
        contentPageView.getAdapter().notifyDataSetChanged();
        updatePageIndicator();
    }

    private void updateOnlyContentView() {
        if (isContentViewInvalid()) {
            return;
        }
        contentPageView.getAdapter().notifyDataSetChanged();
    }

    private void updatePageIndicator() {
        int totalCount = getTotalCount();
        getPagination().resize(row, col, totalCount);
        pageIndicator.resetGPaginator(getPagination());
        pageIndicator.updateTotal(totalCount);
        pageIndicator.updateCurrentPage(totalCount);
    }

    private void updateThumbnailView(BookItemHolder viewHolder, int position) {
        Metadata eBook = getEBookList().get(position);
        updateCommonView(viewHolder.titleView, viewHolder.getWidgetImage, eBook);
        loadThumbnailCover(viewHolder.coverImage, eBook, position);
    }

    private void updateCommonView(TextView titleView, ImageView getWidgetImage, Metadata eBook) {
        updateDownloadPanel(getWidgetImage, eBook);
        titleView.setText(String.valueOf(eBook.getName()));
    }

    private void loadThumbnailCover(ImageView imageView, Metadata eBook, int position) {
        Bitmap bitmap = getBitmap(eBook);
        if (bitmap == null) {
            imageView.setImageResource(getImageResource(eBook));
            if (newPage) {
                newPage = false;
                noThumbnailPosition = position;
            }
            loadThumbnailRequest(position, eBook);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    private void fullUpdateView() {
        if (getActivity() == null || getActivity().getWindow() == null) {
            return;
        }
        EpdController.postInvalidate(getActivity().getWindow().getDecorView().getRootView(), UpdateMode.GC);
    }

    private int getTotalCount() {
        LibraryDataModel dataModel = getLibraryViewInfo().getLibraryDataModel();
        return dataModel.bookCount + dataModel.libraryCount;
    }

    private List<Metadata> getEBookList() {
        LibraryDataModel libraryDataModel = getPageDataModel();
        if (libraryDataModel == null || CollectionUtils.isNullOrEmpty(libraryDataModel.visibleBookList)) {
            return new ArrayList<>();
        }
        return libraryDataModel.visibleBookList;
    }

    private List<Library> getLibraryList() {
        LibraryDataModel libraryDataModel = getPageDataModel();
        if (libraryDataModel == null || CollectionUtils.isNullOrEmpty(libraryDataModel.visibleLibraryList)) {
            return new ArrayList<>();
        }
        return libraryDataModel.visibleLibraryList;
    }

    private String getDataSaveFilePath(Metadata book) {
        if (checkBookMetadataPathValid(book)) {
            return book.getNativeAbsolutePath();
        }
        String fileName = FileUtils.fixNotAllowFileName(book.getName() + "." + book.getType());
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        return new File(CloudUtils.dataCacheDirectory(getContext(), book.getGuid()), fileName)
                .getAbsolutePath();
    }

    private void openCloudFile(final Metadata book) {
        String path = getDataSaveFilePath(book);
        if (StringUtils.isNullOrEmpty(path)) {
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        Intent intent = MetadataUtils.putIntentExtraDataMetadata(ViewDocumentUtils.viewActionIntentWithMimeType(file), book);
        ResolveInfo info = ViewDocumentUtils.getDefaultActivityInfo(getContext(), intent,
                ViewDocumentUtils.getEduReaderComponentName().getPackageName());
        if (info == null) {
            return;
        }
        ActivityUtil.startActivitySafely(getContext(), intent, info.activityInfo);
    }

    private boolean checkBookMetadataPathValid(Metadata book) {
        if (StringUtils.isNotBlank(book.getNativeAbsolutePath()) && new File(book.getNativeAbsolutePath()).exists()) {
            return true;
        }
        return false;
    }

    private boolean isFileExists(Metadata book) {
        if (checkBookMetadataPathValid(book)) {
            return true;
        }
        if (StringUtils.isNullOrEmpty(book.getGuid())) {
            return false;
        }
        File dir = CloudUtils.dataCacheDirectory(getContext(), book.getGuid());
        if (dir.list() == null || dir.list().length <= 0) {
            return false;
        }
        String path = getDataSaveFilePath(book);
        if (StringUtils.isNullOrEmpty(path)) {
            return false;
        }
        File file = new File(path);
        if (!file.exists() && file.length() <= 0) {
            return false;
        }
        return true;
    }

    private BaseCallback downloadCallback = new BaseCallback() {

        @Override
        public void start(BaseRequest request) {
            updateOnlyContentView();
        }

        @Override
        public void progress(BaseRequest request, ProgressInfo info) {
            updateOnlyContentView();
        }

        @Override
        public void done(BaseRequest request, Throwable e) {
            updateOnlyContentView();
        }
    };

    private void startDownload(final Metadata eBook) {
        String filePath = getDataSaveFilePath(eBook);
        DownloadAction downloadAction = new DownloadAction(getRealUrl(eBook.getLocation()), filePath, eBook.getGuid());
        downloadAction.execute(getDataHolder(), null);
    }

    private void processProductItemClick(final int position) {
        Metadata book = getEBookList().get(position);
        if (isFileExists(book)) {
            openCloudFile(book);
            return;
        }
        if (enableWifiOpenAndDetect()) {
            ToastUtils.showToast(getContext(), R.string.open_wifi);
            return;
        }
        startDownload(book);
    }

    private void processLibraryItemClick(final Library library) {
        QueryArgs args = getLibraryViewInfo().libraryQuery(library.getIdString());
        final CloudContentLoadAction loadAction = new CloudContentLoadAction(args);
        loadAction.execute(getDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dismissProgressDialog(loadAction);
                if (e != null) {
                    return;
                }
                updateContentView(loadAction.getDataModel());
                addLibraryToParentRefList(library);
            }
        });
        showProgressDialog(loadAction, R.string.loading, null);
    }

    private int getBookItemPosition(int originPosition) {
        return originPosition - getLibraryListSize();
    }

    private void processNormalModeItemClick(int position) {
        if (position < getLibraryListSize()) {
            processLibraryItemClick(getPageDataModel().visibleLibraryList.get(position));
            return;
        }
        processProductItemClick(getBookItemPosition(position));
    }

    private boolean enableWifiOpenAndDetect() {
        if (!NetworkUtil.isWiFiConnected(getContext())) {
            Device.currentDevice().enableWifiDetect(getContext());
            NetworkUtil.enableWiFi(getContext(), true);
            return true;
        }
        return false;
    }

    class BookItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image_cover)
        ImageView coverImage;
        @Bind(R.id.image_get_widget)
        ImageView getWidgetImage;
        @Bind(R.id.textView_title)
        TextView titleView;

        public BookItemHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processNormalModeItemClick((Integer) v.getTag());
                }
            });
            ButterKnife.bind(this, itemView);
        }
    }

    private CloudStore getCloudStore() {
        return SchoolApp.getSchoolCloudStore();
    }

    private OnyxDownloadManager getDownLoaderManager() {
        return OnyxDownloadManager.getInstance();
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(getContext());
            dataHolder.setCloudManager(getCloudStore().getCloudManager());
        }
        return dataHolder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        dismissAllProgressDialog();
    }

    private void showProgressDialog(final Object object, int resId, DialogProgressHolder.DialogCancelListener listener) {
        if (progressDialogHolder == null) {
            return;
        }
        progressDialogHolder.showProgressDialog(getActivity(), object, resId, listener);
    }

    private void dismissProgressDialog(final Object object) {
        if (progressDialogHolder != null) {
            progressDialogHolder.dismissProgressDialog(object);
        }
    }

    private void dismissAllProgressDialog() {
        if (progressDialogHolder != null) {
            progressDialogHolder.dismissAllProgressDialog();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onLibraryEvent(BookLibraryEvent event) {
        if (Library.isValid(event.library)) {
            if (event.library.getName().equals(fragmentName)) {
                if (isSameRootLibrary(event.library)) {
                    return;
                }
                resetArgumentsBundle(getArguments(), fragmentName, event.library.getIdString());
                onRootLibraryClick();
            }
        }
    }

    private boolean isSameRootLibrary(Library library) {
        return library.getIdString().equals(getRootLibraryId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onKeyEvent(KeyEvent keyEvent) {
        if (contentPageView != null && isVisibleToUser) {
            contentPageView.dispatchKeyEvent(keyEvent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAccountTokenErrorEvent(AccountTokenErrorEvent event) {
        if (pageIndicator != null) {
            pageIndicator.setTotalText(getString(R.string.token_exception_contact_admin));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHardwareErrorEvent(HardwareErrorEvent event) {
        if (pageIndicator != null) {
            pageIndicator.setTotalText(getString(R.string.hardware_error));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadingEvent(DownloadingEvent event) {
        updateOnlyContentView();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            if (contentPageView != null) {
                updateContentView();
            }
        }
    }

    private void refreshDataFormCloud() {
        if (enableWifiOpenAndDetect()) {
            ToastUtils.showToast(getContext(), R.string.open_wifi);
            return;
        }
        final CloudContentRefreshAction refreshAction = new CloudContentRefreshAction();
        refreshAction.execute(getDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dismissProgressDialog(refreshAction);
                if (e != null) {
                    return;
                }
                getPagination().setCurrentPage(0);
                updateContentView(refreshAction.getLibraryDataModel());
                preloadNext();
            }
        });
        showProgressDialog(refreshAction, R.string.refreshing, null);
    }

    @OnClick(R.id.root_library_view)
    public void onRootLibraryClick() {
        remoteAllParentLibrary();
        loadData();
    }

    private LibraryViewInfo getLibraryViewInfo() {
        return getDataHolder().getCloudViewInfo();
    }

    private void remoteAllParentLibrary() {
        parentLibraryRefLayout.removeAllViews();
        getLibraryViewInfo().getLibraryPathList().clear();
    }

    private String removeLastParentLibrary() {
        parentLibraryRefLayout.removeViewAt(parentLibraryRefLayout.getChildCount() - 1);
        return getLibraryViewInfo().getLibraryPathList().remove(
                getLibraryViewInfo().getLibraryPathList().size() - 1).getIdString();
    }

    private void processLibraryRefViewClick(View v) {
        int index = parentLibraryRefLayout.indexOfChild(v);
        if (index == parentLibraryRefLayout.getChildCount() - 1) {
            return;
        }
        int removeCount = parentLibraryRefLayout.getChildCount() - 1 - index;
        for (int i = 0; i < removeCount; i++) {
            removeLastParentLibrary();
        }
        getPagination().setCurrentPage(0);
        loadData(getLibraryViewInfo().libraryQuery());
    }

    private TextView getLibraryTextView(Library library) {
        TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.parent_library_ref_item, null);
        tv.setText(library.getName());
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processLibraryRefViewClick(v);
            }
        });
        return tv;
    }

    private void addLibraryToParentRefList(Library library) {
        getLibraryViewInfo().getLibraryPathList().add(library);
        parentLibraryRefLayout.addView(getLibraryTextView(library));
    }

    private boolean processBackKey() {
        if (!isVisibleToUser) {
            return false;
        }
        String lastLibraryId = getLibraryViewInfo().getLibraryIdString();
        if (StringUtils.isNullOrEmpty(lastLibraryId)) {
            return false;
        }
        removeLastParentLibrary();
        if (CollectionUtils.isNullOrEmpty(getLibraryViewInfo().getLibraryPathList())) {
            onRootLibraryClick();
        } else {
            loadData(getLibraryViewInfo().libraryQuery());
        }
        return true;
    }
}
