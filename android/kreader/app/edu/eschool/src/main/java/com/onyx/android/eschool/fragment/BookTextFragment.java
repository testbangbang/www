package com.onyx.android.eschool.fragment;

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
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.onyx.android.eschool.R;
import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.eschool.action.LibraryGotoPageAction;
import com.onyx.android.eschool.custom.PageIndicator;
import com.onyx.android.eschool.events.BookLibraryEvent;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.CloudContentListRequest;
import com.onyx.android.sdk.data.request.cloud.CloudThumbnailLoadRequest;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;
import com.onyx.android.sdk.ui.wifi.NetworkHelper;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
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

/**
 * Created by suicheng on 2017/4/28.
 */
public class BookTextFragment extends Fragment {
    public static final String LIBRARY_ID_ARGS = "libraryIdArgs";
    public static final String FRAGMENT_NAME = "fragmentName";

    @Bind(R.id.content_pageView)
    SinglePageRecyclerView contentPageView;

    PageIndicator pageIndicator;

    LibraryDataHolder dataHolder;
    QueryPagination pagination;
    private LibraryDataModel pageDataModel = new LibraryDataModel();

    private boolean newPage = false;
    private int hasNoThumbnail = 0;

    private int row = 2;
    private int col = 3;

    private String fragmentName;

    public static BookTextFragment newInstance(String fragmentName, String libraryId) {
        BookTextFragment fragment = new BookTextFragment();
        Bundle bundle = new Bundle();
        resetArgumentsBundle(bundle, fragmentName, libraryId);
        fragment.setArguments(bundle);
        return fragment;
    }

    private static void resetArgumentsBundle(Bundle bundle, String fragmentName, String libraryId) {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        String uniqueId = null;
        if (getArguments() != null) {
            uniqueId = getArguments().getString(LIBRARY_ID_ARGS);
            fragmentName = getArguments().getString(FRAGMENT_NAME);
        }
        if (StringUtils.isNullOrEmpty(uniqueId)) {
            return;
        }
        loadData(uniqueId);
    }

    private void loadData(String libraryId) {
        QueryArgs args = getDataHolder().getCloudViewInfo().libraryQuery(libraryId);
        final CloudContentListRequest listRequest = new CloudContentListRequest(args);
        getCloudStore().submitRequestToSingle(getContext(), listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    QueryResult<Metadata> result = listRequest.getProductResult();
                    updateContentView(getLibraryDataModel(result, listRequest.getThumbnailMap()));
                }
            }
        });
        nextLoad();
    }

    private void nextPage() {
        if (!getPagination().nextPage()) {
            return;
        }
        QueryArgs queryArgs = getDataHolder().getCloudViewInfo().nextPage();
        final CloudContentListRequest listRequest = new CloudContentListRequest(queryArgs);
        getCloudStore().submitRequestToSingle(getContext(), listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    QueryResult<Metadata> result = listRequest.getProductResult();
                    updateContentView(getLibraryDataModel(result, listRequest.getThumbnailMap()));
                }
            }
        });
        nextLoad();
    }

    private LibraryDataModel getLibraryDataModel(QueryResult<Metadata> result, Map<String, CloseableReference<Bitmap>> map) {
        LibraryDataModel libraryDataModel = new LibraryDataModel();
        libraryDataModel.visibleLibraryList = new ArrayList<>();
        libraryDataModel.visibleBookList = result.list;
        libraryDataModel.bookCount = (int) result.count;
        libraryDataModel.thumbnailMap = map;
        libraryDataModel.libraryCount = 0;
        return libraryDataModel;
    }

    private QueryPagination getPagination() {
        if (pagination == null) {
            initPagination();
        }
        return pagination;
    }

    private void initPagination() {
        pagination = getDataHolder().getCloudViewInfo().getQueryPagination();
        pagination.resize(row, col, 0);
    }

    private void nextLoad() {
        int preLoadPage = getPagination().getCurrentPage() + 1;
        if (preLoadPage >= getPagination().pages()) {
            return;
        }
        QueryArgs queryArgs = getDataHolder().getCloudViewInfo().pageQueryArgs(preLoadPage);
        final CloudContentListRequest listRequest = new CloudContentListRequest(queryArgs);
        getCloudStore().submitRequestToSingle(getContext(), listRequest, null);
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
            public void onPageBindViewHolder(BookItemHolder viewHolder, int position) {
                viewHolder.itemView.setTag(position);

                Metadata eBook = getEBookList().get(position);
                viewHolder.getWidgetImage.setVisibility(isFileExists(eBook) ? View.VISIBLE : View.GONE);
                //updateDownloadPanel(viewHolder, eBook);
                viewHolder.titleView.setVisibility(View.VISIBLE);
                viewHolder.titleView.setText(String.valueOf(eBook.getName()));

                Bitmap bitmap = getBitmap(eBook.getAssociationId());
                if (bitmap == null) {
                    viewHolder.coverImage.setImageResource(R.drawable.cloud_default_cover);
                    if (newPage) {
                        newPage = false;
                        hasNoThumbnail = position;
                    }
                    loadThumbnailRequest(position, eBook);
                } else {
                    viewHolder.coverImage.setImageBitmap(bitmap);
                }
            }
        });
    }

    public int getLibraryListSize() {
        return CollectionUtils.getSize(getPageDataModel().visibleLibraryList);
    }

    public int getBookListSize() {
        return CollectionUtils.getSize(getEBookList());
    }

    private Bitmap getBitmap(String associationId) {
        LibraryDataModel libraryDataModel = getPageDataModel();
        if (libraryDataModel == null || libraryDataModel.thumbnailMap == null) {
            return null;
        }
        Bitmap bitmap = null;
        CloseableReference<Bitmap> refBitmap = libraryDataModel.thumbnailMap.get(associationId);
        if (refBitmap != null && refBitmap.isValid()) {
            bitmap = refBitmap.get();
        }

        return bitmap;
    }

    private void loadThumbnailRequest(final int position, final Metadata metadata) {
        final CloudThumbnailLoadRequest loadRequest = new CloudThumbnailLoadRequest(
                metadata.getCoverUrl(),
                metadata.getAssociationId(), OnyxThumbnail.ThumbnailKind.Original);
        if (hasNoThumbnail == position) {
            loadRequest.setAbortPendingTasks(true);
        }
        getCloudStore().submitRequestToSingle(getContext().getApplicationContext(), loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    e.printStackTrace();
                }
                CloseableReference<Bitmap> closeableRef = loadRequest.getRefBitmap();
                if (closeableRef != null && closeableRef.isValid()) {
                    getPageDataModel().thumbnailMap.put(metadata.getAssociationId(), closeableRef);
                    updateContentView();
                }
            }
        });
    }

    private String getRealUrl(String url) {
        if (!url.startsWith(Constant.HTTP_TAG)) {
            url = getCloudStore().getCloudConf().getHostBase() + url;
        }
        return url;
    }

    private void updateDownloadPanel(BookItemHolder holder, Metadata eBook) {
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
    }

    private void initPageIndicator(ViewGroup parentView) {
        initPagination();
        pageIndicator = new PageIndicator(parentView.findViewById(R.id.page_indicator_layout), pagination);
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
    }

    private void prevPage() {
        if (!getPagination().prevPage()) {
            return;
        }

        final CloudContentListRequest listRequest = new CloudContentListRequest(getDataHolder().getCloudViewInfo().prevPage());
        getCloudStore().submitRequestToSingle(getContext(), listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    QueryResult<Metadata> result = listRequest.getProductResult();
                    updateContentView(getLibraryDataModel(result, listRequest.getThumbnailMap()));
                }
            }
        });
        prevLoad();
    }

    private void prevLoad() {
        int preLoadPage = getPagination().getCurrentPage() - 1;
        if (preLoadPage < 0) {
            return;
        }
        CloudContentListRequest listRequest = new CloudContentListRequest(getDataHolder().getCloudViewInfo().pageQueryArgs(preLoadPage));
        getCloudStore().submitRequestToSingle(getContext(), listRequest, null);
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
        loadData(getDataHolder().getCloudViewInfo().gotoPage(page), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    getPagination().setCurrentPage(originPage);
                    return;
                }
                prevLoad();
                nextLoad();
                CloudContentListRequest listRequest = (CloudContentListRequest) request;
                QueryResult<Metadata> result = listRequest.getProductResult();
                updateContentView(getLibraryDataModel(result, listRequest.getThumbnailMap()));
            }
        });
    }

    private void loadData(QueryArgs queryArgs, BaseCallback callback) {
        CloudContentListRequest listRequest = new CloudContentListRequest(queryArgs);
        getCloudStore().submitRequestToSingle(getContext(), listRequest, callback);
    }

    private LibraryDataModel getPageDataModel() {
        return pageDataModel;
    }

    private void updateContentView(LibraryDataModel libraryDataModel) {
        pageDataModel = getDataHolder().getCloudViewInfo().getPageLibraryDataModel(libraryDataModel);
        updateContentView();
    }

    private void updateContentView() {
        newPage = true;
        contentPageView.getAdapter().notifyDataSetChanged();
        updatePageIndicator();
    }

    private void updatePageIndicator() {
        int totalCount = getTotalCount();
        getPagination().resize(row, col, totalCount);
        pageIndicator.resetGPaginator(getPagination());
        pageIndicator.updateTotal(totalCount);
        pageIndicator.updateCurrentPage(totalCount);
    }

    private int getTotalCount() {
        LibraryDataModel dataModel = getDataHolder().getCloudViewInfo().getLibraryDataModel();
        return dataModel.bookCount + dataModel.libraryCount;
    }

    private int getCount() {
        LibraryDataModel libraryDataModel = getDataHolder().getCloudViewInfo().getLibraryDataModel();
        if (libraryDataModel == null) {
            return 0;
        }
        return libraryDataModel.bookCount + libraryDataModel.libraryCount;
    }

    private List<Metadata> getEBookList() {
        LibraryDataModel libraryDataModel = getPageDataModel();
        if (libraryDataModel == null || CollectionUtils.isNullOrEmpty(libraryDataModel.visibleBookList)) {
            return new ArrayList<>();
        }
        return libraryDataModel.visibleBookList;
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
        ActivityUtil.startActivitySafely(getContext(),
                ViewDocumentUtils.viewActionIntentWithMimeType(file),
                ViewDocumentUtils.getReaderComponentName(getContext()));
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
        boolean exist = false;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.getAbsolutePath().contains(book.getName())) {
                exist = !"temp".equalsIgnoreCase(FileUtils.getFileExtension(file));
                break;
            }
        }
        return exist;
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

    private void startDownload(Metadata eBook) {
        if (StringUtils.isNullOrEmpty(eBook.getLocation())) {
            ToastUtils.showToast(getContext(), R.string.download_link_invalid);
            return;
        }
        String filePath = getDataSaveFilePath(eBook);
        if (StringUtils.isBlank(filePath)) {
            return;
        }
        BaseDownloadTask task = getDownLoaderManager().download(getRealUrl(eBook.getLocation()),
                filePath, eBook.getGuid(), baseCallback);
        getDownLoaderManager().addTask(eBook.getGuid(), task);
        getDownLoaderManager().startDownload(task);
    }

    private void processProductItemClick(final int position) {
        Metadata book = getEBookList().get(position);
        if (isFileExists(book)) {
            openCloudFile(book);
            return;
        }
        if (!NetworkHelper.requestWifi(getContext())) {
            return;
        }
        startDownload(book);
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
                    processProductItemClick((Integer) v.getTag());
                }
            });
            ButterKnife.bind(this, itemView);
        }
    }

    private CloudStore getCloudStore() {
        return SchoolApp.getSchoolCloudStore();
    }

    private OnyxDownloadManager getDownLoaderManager() {
        return OnyxDownloadManager.getInstance(getActivity());
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(getContext());
        }
        return dataHolder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onLibraryEvent(BookLibraryEvent event) {
        if (event.library != null) {
            if (event.library.getName().equals(fragmentName)) {
                resetArgumentsBundle(getArguments(), fragmentName, event.library.getIdString());
                loadData();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onKeyEvent(KeyEvent keyEvent) {
        if (contentPageView != null) {
            contentPageView.dispatchKeyEvent(keyEvent);
        }
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
        if (isVisibleToUser) {
            if (contentPageView != null) {
                updateContentView();
            }
        }
    }
}
