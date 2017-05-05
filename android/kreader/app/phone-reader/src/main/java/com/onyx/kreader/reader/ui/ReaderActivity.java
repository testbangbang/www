package com.onyx.kreader.reader.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.action.NextScreenAction;
import com.onyx.kreader.action.OpenDocumentAction;
import com.onyx.kreader.action.PrevScreenAction;
import com.onyx.kreader.event.DocumentInitRenderedEvent;
import com.onyx.kreader.event.RenderRequestFinishedEvent;
import com.onyx.kreader.reader.ReaderRender;
import com.onyx.kreader.reader.data.ReaderDataHolder;
import com.onyx.kreader.reader.gesture.ReaderGestureListener;
import com.onyx.kreader.reader.opengl.CurlPage;
import com.onyx.kreader.reader.opengl.CurlView;

import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 2017/4/13.
 */

public class ReaderActivity extends AppCompatActivity {

    private static final String TAG = "ReaderActivity";

    @Bind(R.id.curl_view)
    CurlView curlView;

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
        curlView.setViewMode(CurlView.SHOW_ONE_PAGE);
        curlView.setSizeChangedObserver(new CurlView.SizeChangedObserver() {
            @Override
            public void onSizeChanged(int width, int height) {
                getReaderDataHolder().setDisplaySize(width, height);
            }
        });
        curlView.setViewChangedObserver(new CurlView.ViewChangedObserver() {
            @Override
            public void onViewChanged(int position, boolean next) {
                if (position == getReaderDataHolder().getCurrentPage()) {
                    return;
                }
                Log.d(TAG, "onViewChanged: " + position + "next:" + next);
                if (next) {
                    new NextScreenAction().execute(getReaderDataHolder(), null);
                }else {
                    new PrevScreenAction().execute(getReaderDataHolder(), null);
                }
            }
        });
        gestureDetector = new GestureDetector(this, new ReaderGestureListener(getReaderDataHolder()));
        curlView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getReaderDataHolder().getHandlerManager().setTouchStartEvent(event);
                if (getReaderDataHolder().inReadingProvider()) {
                    curlView.onTouchEvent(v, event);
                }
                gestureDetector.onTouchEvent(event);
                getReaderDataHolder().getHandlerManager().onTouchEvent(getReaderDataHolder(), event);
                return true;
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
        curlView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        curlView.onPause();
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
        ReaderRender.renderPage(this, getReaderDataHolder(), curlView);
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
