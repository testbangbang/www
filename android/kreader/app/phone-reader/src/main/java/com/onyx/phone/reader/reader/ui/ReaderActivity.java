package com.onyx.phone.reader.reader.ui;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.phone.reader.action.NextScreenAction;
import com.onyx.phone.reader.action.OpenDocumentAction;
import com.onyx.phone.reader.action.PrevScreenAction;
import com.onyx.phone.reader.event.DocumentInitRenderedEvent;
import com.onyx.phone.reader.event.RenderRequestFinishedEvent;
import com.onyx.phone.reader.reader.ReaderRender;
import com.onyx.phone.reader.reader.data.ReaderDataHolder;
import com.onyx.phone.reader.reader.gesture.ReaderGestureListener;
import com.onyx.phone.reader.reader.opengl.PageRenderView;
import com.onyx.phone.reader.R;

import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 2017/4/13.
 */

public class ReaderActivity extends AppCompatActivity {

    private static final String TAG = "ReaderActivity";

    @Bind(R.id.page_view)
    PageRenderView pageRenderView;

    private ReaderDataHolder readerDataHolder;
    private PageProvider pageProvider;
    private GestureDetector gestureDetector;

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
        pageProvider = new PageProvider();
        pageRenderView.setPageProvider(pageProvider);
        pageRenderView.setSizeChangedListener(new PageRenderView.SizeChangedListener() {
            @Override
            public void onSizeChanged(int width, int height) {
                getReaderDataHolder().setDisplaySize(width, height);
            }
        });
        pageRenderView.setViewChangedOListener(new PageRenderView.ViewChangedOListener() {
            @Override
            public void onViewChanged(int position, boolean next) {
                if (getReaderDataHolder().getReaderViewInfo() == null) {
                    return;
                }
                if (position == getReaderDataHolder().getCurrentPage()) {
                    return;
                }
                if (next) {
                    new NextScreenAction().execute(getReaderDataHolder(), null);
                }else {
                    new PrevScreenAction().execute(getReaderDataHolder(), null);
                }
            }
        });
        gestureDetector = new GestureDetector(this, new ReaderGestureListener(getReaderDataHolder()));
        pageRenderView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getReaderDataHolder().getHandlerManager().setTouchStartEvent(event);
                if (getReaderDataHolder().inReadingProvider()) {
                    pageRenderView.onTouchEvent(v, event);
                }
                gestureDetector.onTouchEvent(event);
                getReaderDataHolder().getHandlerManager().onTouchEvent(getReaderDataHolder(), event);
                return true;
            }
        });
    }

    private class PageProvider implements PageRenderView.PageProvider {

        @Override
        public int getPageCount() {
            return getReaderDataHolder().getPageCount();
        }

        @Override
        public Bitmap getPageView(int position) {
            return getReaderDataHolder().getReaderPageCache(position);
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
        pageRenderView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pageRenderView.onPause();
    }

    @Override
    protected void onDestroy() {
        readerDataHolder.unRegisterEventBus(this);
        super.onDestroy();
    }

    @Subscribe
    public void onDocumentInitRendered(final DocumentInitRenderedEvent event) {

    }

    @Subscribe
    public void onRenderRequestFinished(final RenderRequestFinishedEvent event) {
        pageRenderView.setCurrentIndex(getReaderDataHolder().getCurrentPage());
        ReaderRender.renderPage(this, getReaderDataHolder(), pageRenderView);
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
