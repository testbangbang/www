package com.onyx.android.dr.adapter;

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
import com.onyx.android.dr.bean.ProductBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.event.BookDetailEvent;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudThumbnailLoadRequest;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehai on 2017/7/5.
 */
public class SearchResultListAdapter extends PageRecyclerView.PageAdapter<SearchResultListAdapter.LibraryItemViewHolder> implements View.OnClickListener {
    private static final String TAG = SearchResultListAdapter.class.getSimpleName();
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.search_result_row);
    private int col = DRApplication.getInstance().getResources().getInteger(R.integer.search_result_col);
    private List<ProductBean> list;

    public SearchResultListAdapter() {

    }

    public void setRowAndCol(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setList(List<ProductBean> list) {
        this.list = list;
        notifyDataSetChanged();
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
        return list == null ? 0 : list.size();
    }

    @Override
    public LibraryItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        inflate.setPadding(DRApplication.getInstance().getResources().getInteger(R.integer.library_item_padding_left),
                DRApplication.getInstance().getResources().getInteger(R.integer.library_item_padding_top),
                DRApplication.getInstance().getResources().getInteger(R.integer.library_item_padding_right),
                DRApplication.getInstance().getResources().getInteger(R.integer.library_item_padding_bottom));
        return new LibraryItemViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(LibraryItemViewHolder viewHolder, int position) {
        ProductBean productBean = list.get(position);
        viewHolder.nameView.setText(String.valueOf(productBean.getMetadata().getName()));
        viewHolder.titleView.setVisibility(productBean.isFirst() ? View.VISIBLE : View.GONE);
        viewHolder.titleView.setText(StringUtils.isNotBlank(productBean.getMetadata().getNativeAbsolutePath()) ? DRApplication.getInstance().getString(R.string.bookshelf) : DRApplication.getInstance().getString(R.string.bookstore));
        loadThumbnailRequest(viewHolder.coverImage, productBean.getMetadata());
        viewHolder.rootView.setOnClickListener(this);
        viewHolder.rootView.setTag(position);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        int position = (int) v.getTag();
        Metadata book = list.get(position).getMetadata();
        if (isFileExists(book)) {
            openCloudFile(book);
            return;
        }
        if (enableWifiOpenAndDetect()) {
            return;
        }
        EventBus.getDefault().post(new BookDetailEvent(book.getCloudId()));
    }

    static class LibraryItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_search_result_book_cover)
        ImageView coverImage;
        @Bind(R.id.item_search_result_title)
        TextView titleView;
        @Bind(R.id.item_search_result_book_name)
        TextView nameView;
        View rootView;

        public LibraryItemViewHolder(final View itemView) {
            super(itemView);
            rootView = itemView;
            ButterKnife.bind(this, itemView);
        }
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
        return true;
    }

    private void loadThumbnailRequest(final ImageView imageView, final Metadata metadata) {
        final CloudThumbnailLoadRequest loadRequest = new CloudThumbnailLoadRequest(
                metadata.getCoverUrl(),
                metadata.getAssociationId(), OnyxThumbnail.ThumbnailKind.Original);
        DRApplication.getCloudStore().submitRequestToSingle(DRApplication.getInstance(), loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (!isContentValid(request, e)) {
                    return;
                }
                CloseableReference<Bitmap> closeableRef = loadRequest.getRefBitmap();
                if (closeableRef != null && closeableRef.isValid()) {
                    imageView.setImageBitmap(closeableRef.get());
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
}


