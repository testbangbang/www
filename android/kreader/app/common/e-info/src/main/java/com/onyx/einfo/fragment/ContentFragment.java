package com.onyx.einfo.fragment;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.ViewType;
import com.onyx.android.sdk.data.manager.CacheManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.ui.dialog.DialogLoading;
import com.onyx.android.sdk.ui.dialog.DialogProgressHolder;
import com.onyx.android.sdk.utils.RawResourceUtil;
import com.onyx.einfo.R;
import com.onyx.einfo.InfoApp;
import com.onyx.einfo.action.CloudContentLoadAction;
import com.onyx.einfo.action.CloudContentRefreshAction;
import com.onyx.einfo.action.DownloadAction;
import com.onyx.einfo.action.LibraryGotoPageAction;
import com.onyx.einfo.custom.PageIndicator;
import com.onyx.einfo.device.DeviceConfig;
import com.onyx.einfo.events.AccountTokenErrorEvent;
import com.onyx.einfo.events.BookLibraryEvent;
import com.onyx.einfo.events.DownloadingEvent;
import com.onyx.einfo.events.HardwareErrorEvent;
import com.onyx.einfo.events.SortByEvent;
import com.onyx.einfo.events.TabSwitchEvent;
import com.onyx.einfo.events.ViewTypeEvent;
import com.onyx.einfo.holder.LibraryDataHolder;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudThumbnailLoadRequest;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.data.utils.MetadataUtils;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.einfo.manager.ConfigPreferenceManager;

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
public class ContentFragment extends Fragment {
    public static final String LIBRARY_ARGS = "library";
    public static final String FRAGMENT_NAME = "fragmentName";

    public static final String SPECIFIED_SORT_BY = "sort_by";
    public static final String SPECIFIED_SORT_ORDER = "sort_order";

    @Bind(R.id.content_pageView)
    SinglePageRecyclerView contentPageView;
    @Bind(R.id.parent_library_ref)
    LinearLayout parentLibraryRefLayout;
    @Bind(R.id.root_library_view)
    TextView rootLibraryView;

    PageIndicator pageIndicator;

    LibraryDataHolder dataHolder;
    private LibraryDataModel pageDataModel = new LibraryDataModel();
    private DialogProgressHolder progressHolder = new DialogProgressHolder();

    private boolean newPage = false;
    private int noThumbnailPosition = 0;

    private int thumbnailItemRow = 2;
    private int thumbnailItemCol = 3;

    private int detailItemRow = 5;
    private int detailItemCol = 1;

    private String fragmentName;
    private boolean isVisibleToUser = false;
    private boolean isColorDevice = true;
    private int[] imageReqWidthHeight;

    public static Fragment newInstance(String fragmentName, Library library) {
        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        resetArgumentsBundle(bundle, fragmentName, library);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Fragment newInstance(String fragmentName, Library library, SortBy sortBy, SortOrder sortOrder) {
        Fragment fragment = newInstance(fragmentName, library);
        fragment.getArguments().putString(SPECIFIED_SORT_BY, sortBy.toString());
        fragment.getArguments().putString(SPECIFIED_SORT_ORDER, sortOrder.toString());
        return fragment;
    }

    private static void resetArgumentsBundle(Bundle bundle, String fragmentName, Library newLibrary) {
        if (bundle == null) {
            return;
        }
        bundle.putString(FRAGMENT_NAME, fragmentName);
        bundle.putString(LIBRARY_ARGS, JSONObjectParseUtils.toJson(newLibrary));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConfig();
    }

    private void initConfig() {
        isColorDevice = DeviceConfig.sharedInstance(getContext()).isDeviceSupportColor();
        imageReqWidthHeight = new int[]{getResources().getDimensionPixelSize(R.dimen.book_item_cover_image_width),
                getResources().getDimensionPixelSize(R.dimen.book_item_cover_image_height)};
    }

    private Library getLibrary() {
        if (getArguments() == null) {
            return null;
        }
        return JSONObjectParseUtils.parseObject(getArguments().getString(LIBRARY_ARGS), Library.class);
    }

    private String getLibraryId() {
        Library library = getLibrary();
        return library == null ? null : library.getIdString();
    }

    private String getFragmentName() {
        return fragmentName = getArguments().getString(FRAGMENT_NAME);
    }

    private SortBy getSpecifiedSortBy() {
        return ConfigPreferenceManager.getCloudSortBy(getContext());
    }

    private SortOrder getSpecifiedSortOrder() {
        return ConfigPreferenceManager.getCloudSortOrder(getContext());
    }

    private ViewType getSpecifiedViewType() {
        return ConfigPreferenceManager.getViewType(getContext());
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

    private void updateRootLibraryView(String libraryName, String libraryId) {
        rootLibraryView.setText(libraryName);
        rootLibraryView.setTag(libraryId);
    }

    private void loadData() {
        String uniqueId = null;
        if (getArguments() != null) {
            uniqueId = getLibraryId();
            fragmentName = getFragmentName();
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
        final CloudContentLoadAction contentLoadAction = new CloudContentLoadAction(args, true);
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

        final CloudContentLoadAction loadAction = new CloudContentLoadAction(getLibraryViewInfo().nextPage(), true);
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

    private QueryPagination getPagination() {
        return getLibraryViewInfo().getQueryPagination();
    }

    private void initPagination() {
        QueryPagination pagination = getPagination();
        pagination.resize(getRow(), getCol(), 0);
        pagination.setCurrentPage(0);
    }

    private void preloadNext() {
        int preLoadPage = getPagination().getCurrentPage() + 1;
        if (preLoadPage >= getPagination().pages()) {
            return;
        }
        QueryArgs queryArgs = getLibraryViewInfo().pageQueryArgs(preLoadPage);
        final CloudContentLoadAction contentLoadAction = new CloudContentLoadAction(queryArgs, false);
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
        contentPageView.setAdapter(new PageRecyclerView.PageAdapter<ThumbnailItemHolder>() {
            @Override
            public int getRowCount() {
                return getRow();
            }

            @Override
            public int getColumnCount() {
                return getCol();
            }

            @Override
            public int getDataCount() {
                return getLibraryListSize() + getBookListSize();
            }

            @Override
            public int getItemViewType(int position) {
                return getCurrentViewType().ordinal();
            }

            @Override
            public ThumbnailItemHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                if (viewType == ViewType.Thumbnail.ordinal()) {
                    return new ThumbnailItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.content_thumbnail_item, parent, false));
                } else {
                    return new DetailItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.content_details_item, parent, false));
                }
            }

            @Override
            public void onPageBindViewHolder(ThumbnailItemHolder viewHolder, int position) {
                viewHolder.itemView.setTag(position);

                if (position < getLibraryListSize()) {
                    Library library = getLibraryList().get(position);
                    viewHolder.coverImage.setImageResource(R.drawable.library_sub_cover);
                    viewHolder.getWidgetImage.setVisibility(View.GONE);
                    viewHolder.messageWidgetImage.setVisibility(View.GONE);
                    viewHolder.titleView.setText(String.valueOf(library.getName()));
                } else {
                    int viewType = getItemViewType(position);
                    if (viewType == ViewType.Thumbnail.ordinal()) {
                        updateThumbnailView(viewHolder, position);
                    } else {
                        updateListItemView((DetailItemHolder) viewHolder, position);
                    }
                }
            }
        });
    }

    private int getImageResource(Metadata book) {
        String viewType = getCurrentViewType().name().toLowerCase();
        if (!isColorDevice) {
            viewType = ViewType.Thumbnail.name().toLowerCase();
        }
        String type = book.getType();
        Map<String, String> map = DeviceConfig.sharedInstance(getContext()).getCustomizedProductCovers();
        if (StringUtils.isNullOrEmpty(type) || !map.containsKey(type)) {
            return getDefaultCover(viewType);
        }
        return RawResourceUtil.getDrawableIdByName(getContext(), map.get(type) + "_" + viewType);
    }

    private int getDefaultCover(String viewType) {
        Map<String, String> map = DeviceConfig.sharedInstance(getContext()).getCustomizedProductCovers();
        String coverResName = map.get("default");
        if (StringUtils.isNotBlank(viewType)) {
            coverResName += "_" + viewType;
        }
        return RawResourceUtil.getDrawableIdByName(getContext(), coverResName);
    }

    private void updateListItemView(DetailItemHolder viewHolder, int position) {
        Metadata eBook = getEBookList().get(position);
        updateThumbnailView(viewHolder, position);
        viewHolder.authorsView.setText(eBook.getAuthors());
    }

    private void updateThumbnailView(ThumbnailItemHolder viewHolder, int position) {
        Metadata eBook = getEBookList().get(position);
        updateCommonView(viewHolder.titleView, viewHolder.getWidgetImage, viewHolder.messageWidgetImage, eBook);
        loadThumbnailCover(viewHolder.coverImage, eBook, position);
    }

    private void updateCommonView(TextView titleView, ImageView getWidgetImage, ImageView messageWidgetImage,
                                  Metadata eBook) {
        updateDownloadPanel(getWidgetImage, eBook);
        messageWidgetImage.setVisibility(getPageDataModel().notificationMap.containsKey(eBook.getAssociationId()) ? View.VISIBLE : View.GONE);
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

    public int getLibraryListSize() {
        return CollectionUtils.getSize(getPageDataModel().visibleLibraryList);
    }

    public int getBookListSize() {
        return CollectionUtils.getSize(getEBookList());
    }

    private Bitmap getBitmap(Metadata eBook) {
        LibraryDataModel libraryDataModel = getPageDataModel();
        if (libraryDataModel == null || libraryDataModel.thumbnailMap == null) {
            return null;
        }
        Bitmap bitmap = null;
        CloseableReference<Bitmap> refBitmap = libraryDataModel.thumbnailMap.get(getThumbnailMapKey(eBook));
        if (refBitmap != null && refBitmap.isValid()) {
            bitmap = refBitmap.get();
        }

        return bitmap;
    }

    private OnyxThumbnail.ThumbnailKind getThumbnailKind() {
        if (!isColorDevice) {
            return OnyxThumbnail.ThumbnailKind.Original;
        }
        return isThumbnailViewType() ? OnyxThumbnail.ThumbnailKind.Large : OnyxThumbnail.ThumbnailKind.Middle;
    }

    private String getCoverKey() {
        OnyxThumbnail.ThumbnailKind kind = getThumbnailKind();
        return kind.toString().toLowerCase();
    }

    private String getCoverUrl(final Metadata metadata) {
        Library library = getLibrary();
        if (library != null && !CollectionUtils.isNullOrEmpty(library.getBookCovers())) {
            return getRealUrl(library.getBookCoverUrl(getCoverKey()));
        }
        return getRealUrl(metadata.getCoverUrl(getCoverKey()));
    }

    private String getThumbnailMapKey(Metadata metadata) {
        String id = metadata.getAssociationId();
        Library library = getLibrary();
        if (library != null && !CollectionUtils.isNullOrEmpty(library.getBookCovers())) {
            String forceCoverUrl = library.getBookCoverUrl(getCoverKey());
            if (StringUtils.isNotBlank(forceCoverUrl)) {
                id = library.getIdString();
            }
        }
        return CacheManager.generateCloudThumbnailKey(id, getCoverUrl(metadata), getCoverKey());
    }

    private int[] getReqImageWidthHeight() {
        if (isColorDevice) {
            return new int[]{ViewTarget.SIZE_ORIGINAL, ViewTarget.SIZE_ORIGINAL};
        }
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

    private void updateDownloadPanel(ImageView getWidgetImageView, Metadata eBook) {
        if (isFileExists(eBook)) {
            getWidgetImageView.setVisibility(View.VISIBLE);
            getWidgetImageView.setImageResource(R.drawable.book_item_get_widget);
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
        getWidgetImageView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        if (showProgress) {
            getWidgetImageView.setImageResource(R.drawable.book_item_download_widget);
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
        final CloudContentLoadAction contentLoadAction = new CloudContentLoadAction(getLibraryViewInfo().prevPage(), true);
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
        final CloudContentLoadAction contentLoadAction = new CloudContentLoadAction(
                getLibraryViewInfo().pageQueryArgs(preLoadPage), false);
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
        final CloudContentLoadAction contentLoadAction = new CloudContentLoadAction(
                getLibraryViewInfo().gotoPage(page), true);
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

    private int getRow() {
        return getCurrentViewType() == ViewType.Thumbnail ? thumbnailItemRow : detailItemRow;
    }

    private int getCol() {
        return getCurrentViewType() == ViewType.Thumbnail ? thumbnailItemCol : detailItemCol;
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
        getPagination().resize(getRow(), getCol(), totalCount);
        pageIndicator.resetGPaginator(getPagination());
        pageIndicator.updateTotal(totalCount);
        pageIndicator.updateCurrentPage(totalCount);
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

    private BaseCallback baseCallback = new BaseCallback() {

        @Override
        public void progress(BaseRequest request, ProgressInfo info) {
            contentPageView.notifyDataSetChanged();
        }

        @Override
        public void done(BaseRequest request, Throwable e) {
            contentPageView.notifyDataSetChanged();
        }
    };

    private void startDownload(final Metadata eBook) {
        String filePath = getDataSaveFilePath(eBook);
        DownloadAction downloadAction = new DownloadAction(getRealUrl(eBook.getLocation()), filePath, eBook.getGuid());
        downloadAction.execute(getDataHolder(), null);
    }

    private DialogLoading showProgressDialog(final Object object, int resId, DialogProgressHolder.DialogCancelListener listener) {
        return showProgressDialog(object, getString(resId), listener);
    }

    private DialogLoading showProgressDialog(final Object object, String msg, DialogProgressHolder.DialogCancelListener listener) {
        return progressHolder.showProgressDialog(getActivity(), object, msg, listener);
    }

    private void dismissProgressDialog(final Object object) {
        if (progressHolder != null) {
            progressHolder.dismissProgressDialog(object);
        }
    }

    private int getBookItemPosition(int originPosition) {
        return originPosition - getLibraryListSize();
    }

    private String removeLastParentLibrary() {
        parentLibraryRefLayout.removeViewAt(parentLibraryRefLayout.getChildCount() - 1);
        return getLibraryViewInfo().getLibraryPathList().remove(
                getLibraryViewInfo().getLibraryPathList().size() - 1).getIdString();
    }

    private void remoteAllParentLibrary() {
        parentLibraryRefLayout.removeAllViews();
        getLibraryViewInfo().getLibraryPathList().clear();
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

    private void processLibraryItemClick(final Library library) {
        QueryArgs args = getLibraryViewInfo().libraryQuery(library.getIdString());
        final CloudContentLoadAction loadAction = new CloudContentLoadAction(args, true);
        loadAction.execute(getDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dismissProgressDialog(loadAction);
                if (e != null) {
                    return;
                }
                addLibraryToParentRefList(library);
            }
        });
        showProgressDialog(loadAction, getString(R.string.loading), null);
    }

    private void processNormalModeItemClick(int position) {
        if (position < getLibraryListSize()) {
            processLibraryItemClick(getPageDataModel().visibleLibraryList.get(position));
            return;
        }
        processProductItemClick(getBookItemPosition(position));
    }

    private void processProductItemClick(final int position) {
        Metadata book = getEBookList().get(position);
        if (isFileExists(book)) {
            openCloudFile(book);
            return;
        }
        if (enableWifiOpenAndDetect()) {
            return;
        }
        startDownload(book);
    }

    private boolean enableWifiOpenAndDetect() {
        if (!NetworkUtil.isWiFiConnected(getContext())) {
            Device.currentDevice().enableWifiDetect(getContext());
            NetworkUtil.enableWiFi(getContext(), true);
            return true;
        }
        return false;
    }

    class ThumbnailItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image_cover)
        ImageView coverImage;
        @Bind(R.id.image_get_widget)
        ImageView getWidgetImage;
        @Bind(R.id.image_message_widget)
        ImageView messageWidgetImage;
        @Bind(R.id.textView_title)
        TextView titleView;

        public ThumbnailItemHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processProductItemClick((Integer) v.getTag());
                }
            });
            ButterKnife.bind(this, itemView);
        }
    }

    class DetailItemHolder extends ThumbnailItemHolder {
        @Bind(R.id.textView_authors)
        TextView authorsView;

        public DetailItemHolder(View itemView) {
            super(itemView);
        }
    }

    private CloudStore getCloudStore() {
        return InfoApp.getCloudStore();
    }

    private OnyxDownloadManager getDownLoaderManager() {
        return OnyxDownloadManager.getInstance();
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(getContext().getApplicationContext());
            dataHolder.setCloudManager(getCloudStore().getCloudManager());
            dataHolder.getCloudViewInfo().updateSortBy(ConfigPreferenceManager.getCloudSortBy(getContext()),
                    ConfigPreferenceManager.getCloudSortOrder(getContext()));
            dataHolder.getCloudViewInfo().setCurrentViewType(ConfigPreferenceManager.getViewType(getContext()));
        }
        return dataHolder;
    }

    private LibraryViewInfo getLibraryViewInfo() {
        return getDataHolder().getCloudViewInfo();
    }

    private ViewType getCurrentViewType() {
        return getLibraryViewInfo().getCurrentViewType();
    }

    private boolean isThumbnailViewType() {
        return getCurrentViewType() == ViewType.Thumbnail;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onLibraryEvent(BookLibraryEvent event) {
        if (event.library != null) {
            if (event.library.getName().equals(fragmentName)) {
                resetArgumentsBundle(getArguments(), fragmentName, event.library);
                onRootLibraryClick();
            }
        }
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
    public void onViewTypeEvent(ViewTypeEvent event) {
        processViewTypeSwitch(event.viewType);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSortEvent(SortByEvent event) {
        processSortSwitch(event.sortBy, event.sortOrder);
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
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        progressHolder.dismissAllProgressDialog();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            updateContentView();
        }
    }

    @OnClick(R.id.root_library_view)
    public void onRootLibraryClick() {
        remoteAllParentLibrary();
        loadData();
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
                progressHolder.dismissProgressDialog(refreshAction);
                if (e != null) {
                    return;
                }
                getPagination().setCurrentPage(0);
                updateContentView(refreshAction.getLibraryDataModel());
                preloadNext();
            }
        });
        progressHolder.showProgressDialog(getContext(), refreshAction, R.string.refreshing, null);
    }

    private void processViewTypeSwitch(ViewType viewType) {
        getLibraryViewInfo().setCurrentViewType(viewType);
        String libraryId = getLibraryId();
        if (StringUtils.isNullOrEmpty(libraryId)) {
            return;
        }
        contentPageView.setAdapter(contentPageView.getAdapter());
        int offset = getLibraryViewInfo().getOffset();
        int page = offset / (getCol() * getRow());
        LibraryViewInfo viewInfo = resetLibraryViewInfoParams();
        viewInfo.getQueryPagination().setCurrentPage(page);
        QueryArgs args = viewInfo.libraryQuery(libraryId);
        args.offset = page * (getCol() * getRow());
        loadData(args);
    }

    private LibraryViewInfo resetLibraryViewInfoParams() {
        LibraryViewInfo viewInfo = getLibraryViewInfo();
        viewInfo.clearThumbnailMap(getPageDataModel());
        viewInfo.setQueryLimit(getRow() * getCol());
        viewInfo.resizePagination(getRow(), getCol(), getTotalCount());
        viewInfo.updateSortBy(viewInfo.getCurrentSortBy(), viewInfo.getCurrentSortOrder(), 0);
        return viewInfo;
    }

    void processSortSwitch(SortBy sortBy, SortOrder sortOrder) {
        LibraryViewInfo viewInfo = getLibraryViewInfo();
        viewInfo.updateSortBy(sortBy, sortOrder, 0);
        String libraryId = getLibraryId();
        if (StringUtils.isNullOrEmpty(libraryId)) {
            return;
        }
        if (NetworkUtil.isWiFiConnected(getContext())) {
            refreshDataFormCloud();
        } else {
            QueryArgs args = getLibraryViewInfo().libraryQuery(libraryId);
            args.fetchPolicy = FetchPolicy.DB_ONLY;
            loadData(args);
        }
    }
}
