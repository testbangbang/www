package com.onyx.android.dr.fragment;

import android.graphics.Bitmap;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.BookListAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.MoreBooksEvent;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.interfaces.MainFragmentView;
import com.onyx.android.dr.presenter.MainViewFragmentPresenter;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudThumbnailLoadRequest;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by hehai on 17-6-29.
 */

public class MainViewFragment extends BaseFragment implements MainFragmentView {
    @Bind(R.id.now_reading_book_cover)
    ImageView nowReadingBookCover;
    @Bind(R.id.now_reading_text_progress)
    TextView nowReadingTextProgress;
    @Bind(R.id.now_reading_progressBar)
    ProgressBar nowReadingProgressBar;
    @Bind(R.id.now_reading_book_name)
    TextView nowReadingBookName;
    @Bind(R.id.image_next)
    ImageView imageNext;
    @Bind(R.id.page_count)
    TextView pageCount;
    @Bind(R.id.recent_reading_recycler)
    PageRecyclerView recentReadingRecycler;
    @Bind(R.id.now_reading_book)
    LinearLayout nowReadingBook;
    @Bind(R.id.search_book)
    ImageView searchBook;
    private BookListAdapter libraryAdapter;
    private LibraryDataHolder dataHolder;
    private MainViewFragmentPresenter presenter;
    private Metadata nowReadingMetadata;
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.recent_book_row);
    private int col = DRApplication.getInstance().getResources().getInteger(R.integer.recent_book_col);

    @Override
    protected void initListener() {

    }

    @Override
    protected void initView(View rootView) {
        libraryAdapter = new BookListAdapter(getActivity(), getDataHolder());
        libraryAdapter.setRowAndCol(row, col);
        libraryAdapter.setShowName(true);
        libraryAdapter.setShowTime(true);
        recentReadingRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        recentReadingRecycler.addItemDecoration(dividerItemDecoration);
        recentReadingRecycler.setAdapter(libraryAdapter);
    }

    @Override
    protected void loadData() {
        if (presenter == null) {
            presenter = new MainViewFragmentPresenter(this);
        }
        presenter.getNowReading();
        presenter.loadData("");
    }

    @Override
    protected int getRootView() {
        return R.layout.fragment_main_view;
    }

    @Override
    public boolean onKeyBack() {
        return false;
    }

    @OnClick({R.id.now_reading_book_cover, R.id.image_next, R.id.search_book})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.now_reading_book_cover:
                openCloudFile(nowReadingMetadata);
                break;
            case R.id.image_next:
                EventBus.getDefault().post(new MoreBooksEvent());
                break;
            case R.id.search_book:
                search();
                break;
        }
    }

    private void search() {
        ActivityManager.startSearchBookActivity(getActivity(), Constants.NAME_SEARCH);
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
        return new File(CloudUtils.dataCacheDirectory(getActivity(), book.getGuid()), fileName)
                .getAbsolutePath();
    }

    private boolean checkBookMetadataPathValid(Metadata book) {
        if (StringUtils.isNotBlank(book.getNativeAbsolutePath()) && new File(book.getNativeAbsolutePath()).exists()) {
            return true;
        }
        return false;
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(getActivity());
            dataHolder.setCloudManager(DRApplication.getCloudStore().getCloudManager());
        }
        return dataHolder;
    }

    private LibraryDataModel getLibraryDataModel(QueryResult<Metadata> result, Map<String, CloseableReference<Bitmap>> map) {
        return LibraryViewInfo.buildLibraryDataModel(result, map);
    }

    @Override
    public void setDatas(QueryResult<Metadata> queryResult) {
        if (queryResult.list != null && queryResult.list.size() > 0) {
            Map<String, CloseableReference<Bitmap>> bitmaps = DataManagerHelper.loadCloudThumbnailBitmapsWithCache(getActivity(), DRApplication.getCloudStore().getCloudManager(), queryResult.list);
            libraryAdapter.updateContentView(getLibraryDataModel(queryResult, bitmaps));
        }
    }

    @Override
    public void setNowReading(QueryResult<Metadata> queryResult) {
        if (queryResult.list != null && queryResult.list.size() > 0) {
            Metadata metadata = queryResult.list.get(0);
            setNowReading(metadata);
        }
    }

    private void setNowReading(final Metadata metadata) {
        if (metadata != null) {
            nowReadingMetadata = metadata;
            Bitmap bitmap = getBitmap(metadata.getAssociationId());
            if (bitmap == null) {
                nowReadingBookCover.setImageResource(R.drawable.book_cover);
                loadThumbnailRequest(metadata);
            } else {
                nowReadingBookCover.setImageBitmap(bitmap);
            }
            nowReadingBookName.setText(nowReadingMetadata.getName());
            nowReadingTextProgress.setText(nowReadingMetadata.getProgress());
            nowReadingProgressBar.setProgress(nowReadingMetadata.getProgressPercent());
            nowReadingBook.setVisibility(View.VISIBLE);
        }
    }

    private void loadThumbnailRequest(final Metadata metadata) {
        final CloudThumbnailLoadRequest loadRequest = new CloudThumbnailLoadRequest(
                metadata.getCoverUrl(),
                metadata.getAssociationId(), OnyxThumbnail.ThumbnailKind.Original);
        DRApplication.getCloudStore().submitRequestToSingle(getActivity(), loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                CloseableReference<Bitmap> closeableRef = loadRequest.getRefBitmap();
                if (closeableRef != null && closeableRef.isValid()) {
                    getDataHolder().getCloudViewInfo().getLibraryDataModel().thumbnailMap.put(metadata.getAssociationId(), closeableRef);
                    nowReadingBookCover.setImageBitmap(closeableRef.get());
                }
            }
        });
    }

    private Bitmap getBitmap(String associationId) {
        Bitmap bitmap = null;
        CloseableReference<Bitmap> refBitmap = getDataHolder().getCloudViewInfo().getLibraryDataModel().thumbnailMap.get(associationId);
        if (refBitmap != null && refBitmap.isValid()) {
            bitmap = refBitmap.get();
        }
        return bitmap;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
