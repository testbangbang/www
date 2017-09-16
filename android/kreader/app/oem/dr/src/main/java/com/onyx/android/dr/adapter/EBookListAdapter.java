package com.onyx.android.dr.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.device.DeviceConfig;
import com.onyx.android.dr.event.AddToCartEvent;
import com.onyx.android.dr.event.BookDetailEvent;
import com.onyx.android.dr.event.DownloadSucceedEvent;
import com.onyx.android.dr.event.PayForEvent;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudThumbnailLoadRequest;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.data.v2.ContentService;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehai on 2017/7/5.
 */
public class EBookListAdapter extends PageRecyclerView.PageAdapter<EBookListAdapter.LibraryItemViewHolder> implements View.OnClickListener {
    private static final String TAG = EBookListAdapter.class.getSimpleName();
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.common_books_fragment_row);
    private int col = DRApplication.getInstance().getResources().getInteger(R.integer.common_books_fragment_col);
    private boolean newPage = false;
    private int noThumbnailPosition = 0;
    private boolean isVisibleToUser = false;
    private LibraryDataHolder dataHolder;

    public EBookListAdapter(LibraryDataHolder dataHolder) {
        this.dataHolder = dataHolder;
    }

    public void setRowAndCol(int row, int col) {
        this.row = row;
        this.col = col;
    }

    private LibraryDataModel pageDataModel = new LibraryDataModel();

    public void setNewPage(boolean newPage) {
        this.newPage = newPage;
    }

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
    public LibraryItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.ebook_list_item, parent, false);
        inflate.setPadding(DRApplication.getInstance().getResources().getInteger(R.integer.library_item_padding_left),
                DRApplication.getInstance().getResources().getInteger(R.integer.library_item_padding_top),
                DRApplication.getInstance().getResources().getInteger(R.integer.library_item_padding_right),
                DRApplication.getInstance().getResources().getInteger(R.integer.library_item_padding_bottom));
        return new LibraryItemViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(LibraryItemViewHolder viewHolder, int position) {
        viewHolder.itemView.setTag(position);

        final Metadata eBook = getEBookList().get(position);
        viewHolder.titleView.setVisibility(View.VISIBLE);
        viewHolder.titleView.setText(String.format(DRApplication.getInstance().getResources().getString(R.string.price_format), eBook.getPrice()));
        viewHolder.paid.setText(isFileExists(eBook) ? DRApplication.getInstance().getResources().getString(R.string.read) : DRApplication.getInstance().getResources().getString(R.string.download));
        Bitmap bitmap = getBitmap(eBook.getAssociationId());
        if (bitmap == null) {
            viewHolder.coverImage.setImageResource(R.drawable.book_cover);
            if (newPage) {
                newPage = false;
                noThumbnailPosition = position;
            }
            loadThumbnailRequest(position, eBook);
        } else {
            viewHolder.coverImage.setImageBitmap(bitmap);
        }
        viewHolder.coverImage.setOnClickListener(this);
        viewHolder.coverImage.setTag(position);
        viewHolder.addToShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new AddToCartEvent(eBook.getCloudId()));
            }
        });
        viewHolder.buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PayForEvent(eBook.getCloudId()));
            }
        });
        viewHolder.paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBookOrDownload(eBook);
            }
        });
        viewHolder.buyLayout.setVisibility(eBook.isPaid() ? View.GONE : View.VISIBLE);
        viewHolder.paid.setVisibility(eBook.isPaid() ? View.VISIBLE : View.GONE);
    }

    private void openBookOrDownload(Metadata eBook) {
        if (isFileExists(eBook)) {
            openCloudFile(eBook);
            return;
        }
        if (enableWifiOpenAndDetect()) {
            return;
        }
        startDownload(eBook);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        int position = (int) v.getTag();
        Metadata book = getEBookList().get(position);
        EventBus.getDefault().post(new BookDetailEvent(book.getCloudId()));
    }

    private void startDownload(final Metadata eBook) {
        final String filePath = getDataSaveFilePath(eBook);
        String bookDownloadUrl = DeviceConfig.sharedInstance(DRApplication.getInstance()).getBookDownloadUrl(eBook.getGuid());
        String token = DRApplication.getCloudStore().getCloudManager().getToken();
        Map<String, String> header = new HashMap<>();
        header.put(Constant.HEADER_AUTHORIZATION, ContentService.CONTENT_AUTH_PREFIX + token);
        OnyxDownloadManager downLoaderManager = getDownLoaderManager();
        BaseDownloadTask download = downLoaderManager.download(DRApplication.getInstance(), header, bookDownloadUrl, filePath, eBook.getGuid(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    setCloudMetadataNativeAbsolutePath(eBook, filePath);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void progress(BaseRequest request, ProgressInfo info) {

            }
        });
        getDownLoaderManager().startDownload(download);
    }

    private OnyxDownloadManager getDownLoaderManager() {
        return OnyxDownloadManager.getInstance();
    }

    static class LibraryItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ebook_library_image_cover)
        ImageView coverImage;
        @Bind(R.id.ebook_library_price)
        TextView titleView;
        View rootView;
        @Bind(R.id.ebook_library_add_to_shopping_cart)
        TextView addToShoppingCart;
        @Bind(R.id.ebook_library_item_buy)
        TextView buy;
        @Bind(R.id.ebook_library_item_paid)
        TextView paid;
        @Bind(R.id.ebook_library_item_buy_layout)
        LinearLayout buyLayout;

        public LibraryItemViewHolder(final View itemView) {
            super(itemView);
            rootView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    private List<Metadata> getEBookList() {
        LibraryDataModel libraryDataModel = getPageDataModel();
        if (libraryDataModel == null || CollectionUtils.isNullOrEmpty(libraryDataModel.visibleBookList)) {
            return new ArrayList<>();
        }
        return libraryDataModel.visibleBookList;
    }

    private LibraryDataModel getPageDataModel() {
        return pageDataModel;
    }

    public int getLibraryListSize() {
        return CollectionUtils.getSize(getPageDataModel().visibleLibraryList);
    }

    public int getBookListSize() {
        return CollectionUtils.getSize(getEBookList());
    }

    private boolean isFileExists(Metadata book) {
        if (checkBookMetadataPathValid(book)) {
            return true;
        }
        if (StringUtils.isNullOrEmpty(book.getGuid())) {
            return false;
        }
        File dir = CloudUtils.dataCacheDirectory(DRApplication.getInstance(), book.getGuid());
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
        if (StringUtils.isNullOrEmpty(book.getNativeAbsolutePath())) {
            setCloudMetadataNativeAbsolutePath(book, path);
        }
        return true;
    }

    private void setCloudMetadataNativeAbsolutePath(Metadata book, String path) {
        book.setNativeAbsolutePath(path);
        DownloadSucceedEvent event = new DownloadSucceedEvent(book);
        EventBus.getDefault().post(event);
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
        if (isVisibleToUser && noThumbnailPosition == position) {
            loadRequest.setAbortPendingTasks(true);
        }
        DRApplication.getCloudStore().submitRequestToSingle(DRApplication.getInstance().getApplicationContext(), loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (!isContentValid(request, e)) {
                    return;
                }
                CloseableReference<Bitmap> closeableRef = loadRequest.getRefBitmap();
                if (closeableRef != null && closeableRef.isValid()) {
                    getPageDataModel().thumbnailMap.put(metadata.getAssociationId(), closeableRef);
                    updateContentView();
                }
            }
        });
    }

    private boolean isContentValid(BaseRequest request, Throwable e) {
        if (e != null || request.isAbort()) {
            return false;
        }
        return true;
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
        ActivityManager.openBook(DRApplication.getInstance(), book, path);
    }

    private String getDataSaveFilePath(Metadata book) {
        if (checkBookMetadataPathValid(book)) {
            return book.getNativeAbsolutePath();
        }
        String fileName = FileUtils.fixNotAllowFileName(book.getName() + "." + book.getType());
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        return new File(CloudUtils.dataCacheDirectory(DRApplication.getInstance(), book.getGuid()), fileName)
                .getAbsolutePath();
    }

    private boolean checkBookMetadataPathValid(Metadata book) {
        if (StringUtils.isNotBlank(book.getNativeAbsolutePath()) && new File(book.getNativeAbsolutePath()).exists()) {
            return true;
        }
        return false;
    }

    private boolean enableWifiOpenAndDetect() {
        if (!NetworkUtil.isWiFiConnected(DRApplication.getInstance())) {
            Device.currentDevice().enableWifiDetect(DRApplication.getInstance());
            NetworkUtil.enableWiFi(DRApplication.getInstance(), true);
            return true;
        }
        return false;
    }

    private void updateContentView() {
        newPage = true;
        notifyDataSetChanged();
    }

    public void updateContentView(LibraryDataModel libraryDataModel) {
        pageDataModel = dataHolder.getCloudViewInfo().getPageLibraryDataModel(libraryDataModel);
        updateContentView();
    }
}


