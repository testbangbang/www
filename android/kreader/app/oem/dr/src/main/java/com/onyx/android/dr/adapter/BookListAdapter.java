package com.onyx.android.dr.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudThumbnailLoadRequest;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.data.utils.MetadataUtils;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehai on 2017/7/5.
 */
public class BookListAdapter extends PageRecyclerView.PageAdapter<BookListAdapter.LibraryItemViewHolder> implements View.OnClickListener {
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.common_books_fragment_row);
    private int col = DRApplication.getInstance().getResources().getInteger(R.integer.common_books_fragment_col);
    private Activity context;
    private boolean newPage = false;
    private int noThumbnailPosition = 0;
    private boolean isVisibleToUser = false;
    private LibraryDataHolder dataHolder;

    public BookListAdapter(Activity context, LibraryDataHolder dataHolder) {
        this.context = context;
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
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_item, parent, false);
        inflate.setPadding(context.getResources().getInteger(R.integer.library_item_padding_left),
                context.getResources().getInteger(R.integer.library_item_padding_top),
                context.getResources().getInteger(R.integer.library_item_padding_right),
                context.getResources().getInteger(R.integer.library_item_padding_bottom));
        return new LibraryItemViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(LibraryItemViewHolder viewHolder, int position) {
        viewHolder.itemView.setTag(position);

        Metadata eBook = getEBookList().get(position);
        viewHolder.getWidgetImage.setVisibility(isFileExists(eBook) ? View.VISIBLE : View.GONE);
        viewHolder.titleView.setVisibility(View.VISIBLE);
        viewHolder.titleView.setText(String.valueOf(eBook.getName()));

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
        viewHolder.rootView.setOnClickListener(this);
        viewHolder.rootView.setTag(position);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        int position = (int) v.getTag();
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

    private void startDownload(final Metadata eBook) {
        String filePath = getDataSaveFilePath(eBook);
        // TODO: 17-7-3 download book
    }

    static class LibraryItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.library_item_image_cover)
        ImageView coverImage;
        @Bind(R.id.library_item_image_get_widget)
        ImageView getWidgetImage;
        @Bind(R.id.library_item_textView_title)
        TextView titleView;
        View rootView;

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
        File dir = CloudUtils.dataCacheDirectory(context, book.getGuid());
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
        DRApplication.getCloudStore().submitRequestToSingle(context.getApplicationContext(), loadRequest, new BaseCallback() {
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
        ActivityUtil.startActivitySafely(context,
                MetadataUtils.putIntentExtraDataMetadata(ViewDocumentUtils.viewActionIntentWithMimeType(file), book),
                ViewDocumentUtils.getEduReaderComponentName(context));
    }

    private String getDataSaveFilePath(Metadata book) {
        if (checkBookMetadataPathValid(book)) {
            return book.getNativeAbsolutePath();
        }
        String fileName = FileUtils.fixNotAllowFileName(book.getName() + "." + book.getType());
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        return new File(CloudUtils.dataCacheDirectory(context, book.getGuid()), fileName)
                .getAbsolutePath();
    }

    private boolean checkBookMetadataPathValid(Metadata book) {
        if (StringUtils.isNotBlank(book.getNativeAbsolutePath()) && new File(book.getNativeAbsolutePath()).exists()) {
            return true;
        }
        return false;
    }

    private boolean enableWifiOpenAndDetect() {
        if (!NetworkUtil.isWiFiConnected(context)) {
            Device.currentDevice().enableWifiDetect(context);
            NetworkUtil.enableWiFi(context, true);
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


