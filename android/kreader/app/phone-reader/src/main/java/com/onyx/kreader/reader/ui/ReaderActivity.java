package com.onyx.kreader.reader.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.action.OpenDocumentAction;
import com.onyx.kreader.event.DocumentInitRenderedEvent;
import com.onyx.kreader.event.RenderRequestFinishedEvent;
import com.onyx.kreader.reader.data.ReaderDataHolder;
import com.onyx.kreader.reader.opengl.CurlPage;
import com.onyx.kreader.reader.opengl.CurlView;

import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 2017/4/13.
 */

public class ReaderActivity extends AppCompatActivity {

    @Bind(R.id.curl_view)
    CurlView curlView;

    private ReaderDataHolder readerDataHolder;
    private PageProvider pageProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        ButterKnife.bind(this);
        initComponents();
        initCurlView();
        openFileFromIntent();
    }

    private void initCurlView() {
        curlView.setSizeChangedObserver(new CurlView.SizeChangedObserver() {
            @Override
            public void onSizeChanged(int width, int height) {
                getReaderDataHolder().setDisplaySize(width, height);
            }
        });
    }

    private class PageProvider implements CurlView.PageProvider {

        @Override
        public int getPageCount() {
            return getReaderDataHolder().getPageCount();
        }

        @Override
        public void updatePage(CurlPage page, int width, int height, int index) {
            Bitmap front = getReaderDataHolder().getReaderPageCache(index);
            page.setTexture(front, CurlPage.SIDE_BOTH);
            page.setColor(Color.WHITE, CurlPage.SIDE_BACK);
        }
    }

    private void initComponents() {
        initReaderDataHolder();
    }

    private void initReaderDataHolder() {
        readerDataHolder = new ReaderDataHolder(this);
        readerDataHolder.registerEventBus(this);
    }

    public ReaderDataHolder getReaderDataHolder() {
        return readerDataHolder;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        readerDataHolder.unRegisterEventBus(this);
        super.onDestroy();
    }

    @Subscribe
    public void onDocumentInitRendered(final DocumentInitRenderedEvent event) {
        pageProvider = new PageProvider();
        curlView.setPageProvider(pageProvider);
    }

    @Subscribe
    public void onRenderRequestFinished(final RenderRequestFinishedEvent event) {
        curlView.setCurrentIndex(getReaderDataHolder().getCurrentPage());
    }

    private void openFileFromIntent() {
        Uri uri = getIntent().getData();
        if (uri == null) {
            return;
        }

        final String path = FileUtils.getRealFilePathFromUri(ReaderActivity.this, uri);
        OpenDocumentAction openDocumentAction = new OpenDocumentAction(path);
        openDocumentAction.execute(getReaderDataHolder(), null);
    }
}
