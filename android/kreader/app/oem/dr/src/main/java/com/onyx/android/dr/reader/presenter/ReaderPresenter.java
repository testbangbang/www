package com.onyx.android.dr.reader.presenter;

import com.onyx.android.dr.reader.action.BookOperate;
import com.onyx.android.dr.reader.base.ReaderView;
import com.onyx.android.dr.reader.data.BookInfo;
import com.onyx.android.dr.reader.data.PageInformation;
import com.onyx.android.dr.reader.event.DocumentOpenEvent;
import com.onyx.android.dr.reader.handler.HandlerManger;
import com.onyx.android.dr.reader.highlight.ReaderSelectionManager;
import com.onyx.android.dr.reader.utils.ReaderUtil;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.DocumentInfo;
import com.onyx.android.sdk.reader.api.ReaderDocumentMetadata;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.common.PageAnnotation;
import com.onyx.android.sdk.reader.common.ReaderUserDataInfo;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.request.CloseRequest;
import com.onyx.android.sdk.reader.host.request.LoadDocumentOptionsRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by huxiaomao on 17/5/4.
 */

public class ReaderPresenter {
    private DataManager dataProvider;
    private boolean fluent;

    public void setFluent(boolean fluent) {
        this.fluent = fluent;
    }

    public boolean isFluent() {
        return fluent;
    }

    public enum DocumentOpenState {INIT, OPENING, OPENED}

    public DocumentOpenState documentOpenState = DocumentOpenState.INIT;
    private ReaderView readerView;
    private Reader reader;
    private PageInformation pageInformation;
    private BookInfo bookInfo;
    private BookOperate bookOperate;
    private ReaderViewInfo readerViewInfo;
    private ReaderUserDataInfo readerUserDataInfo;
    private ReaderSelectionManager readerSelectionManager;
    private HandlerManger handlerManger;
    private PageAnnotation pageAnnotation;

    public HandlerManger getHandlerManger() {
        if (handlerManger == null) {
            handlerManger = new HandlerManger(this);
        }
        return handlerManger;
    }

    public PageAnnotation getPageAnnotation() {
        return pageAnnotation;
    }

    public void setPageAnnotation(PageAnnotation pageAnnotation) {
        this.pageAnnotation = pageAnnotation;
    }

    public String getCurrentPagePosition() {
        return getReaderViewInfo().getFirstVisiblePage().getPositionSafely();
    }

    public ReaderSelectionManager getReaderSelectionManager() {
        if (readerSelectionManager == null) {
            readerSelectionManager = new ReaderSelectionManager();
        }
        return readerSelectionManager;
    }

    public ReaderUserDataInfo getReaderUserDataInfo() {
        return readerUserDataInfo;
    }

    public void saveReaderUserDataInfo(final BaseReaderRequest request) {
        readerUserDataInfo = request.getReaderUserDataInfo();
    }

    public ReaderViewInfo getReaderViewInfo() {
        return readerViewInfo;
    }

    public void setReaderViewInfo(ReaderViewInfo readerViewInfo) {
        this.readerViewInfo = readerViewInfo;
    }

    public BookOperate getBookOperate() {
        if (bookOperate == null) {
            bookOperate = new BookOperate(this);
        }
        return bookOperate;
    }

    public ReaderView getReaderView() {
        return readerView;
    }

    public Reader getReader() {
        if (reader == null) {
            reader = new Reader();
        }
        return reader;
    }

    public PageInformation getPageInformation() {
        if (pageInformation == null) {
            pageInformation = new PageInformation();
        }
        return pageInformation;
    }

    public BookInfo getBookInfo() {
        if (bookInfo == null) {
            bookInfo = new BookInfo();
        }
        return bookInfo;
    }

    public ReaderPresenter(ReaderView readerView) {
        this.readerView = readerView;
        this.dataProvider = new DataManager();
    }

    public boolean isDocumentOpened() {
        return documentOpenState == DocumentOpenState.OPENED && reader != null;
    }

    public void openDocument() {
        documentOpenState = DocumentOpenState.OPENING;
        checkFileType();
        openDocumentIml();
    }

    private void openDocumentIml() {
        final LoadDocumentOptionsRequest loadDocumentOptionsRequest = new LoadDocumentOptionsRequest(getBookInfo().getBookPath(),
                getReader().getDocumentMd5());
        dataProvider.submit(getReaderView().getViewContext(), loadDocumentOptionsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    BaseOptions options = loadDocumentOptionsRequest.getDocumentOptions();
                    getBookOperate().openDocument(options);
                }
            }
        });
    }

    private void checkFileType() {
        if (ReaderUtil.isJDPDF(getBookInfo().getBookPath())) {

            return;
        }

        if (ReaderUtil.isJEB(getBookInfo().getBookPath())) {

        }
    }

    public void gotoPage(final int page) {
        getPageInformation().setCurrentPage(page);
        getBookOperate().gotoPage(false);
    }

    public void nextScreen() {
        if (isDocumentOpened()) {
            getBookOperate().nextScreen();
        }
    }

    public void prevScreen() {
        if (isDocumentOpened()) {
            getBookOperate().prevScreen();
        }
    }

    public String getCurrentPageName(final BaseReaderRequest request) {
        return getBookOperate().getCurrentPageName(request);
    }

    public void onRenderRequestFinished(final BaseReaderRequest request,
                                        Throwable e,
                                        boolean applyGCIntervalUpdate,
                                        boolean renderShapeData) {
        if (e != null || request.isAbort()) {
            return;
        }
        setReaderViewInfo(request.getReaderViewInfo());
        saveReaderUserDataInfo(request);
        readerView.updatePage(getReader().getViewportBitmap().getBitmap());
        if (getReaderViewInfo() != null && getReaderViewInfo().layoutChanged) {
            //getEventBus().post(new LayoutChangeEvent());
        }
    }

    public boolean hasBookmark() {
        return getFirstVisiblePageWithBookmark() != null;
    }

    public PageInfo getFirstVisiblePageWithBookmark() {
        List<PageInfo> visiblePages = getReaderViewInfo().getVisiblePages();
        if (visiblePages != null && visiblePages.size() > 0) {
            for (PageInfo pageInfo : visiblePages) {
                if (getReaderUserDataInfo().hasBookmark(pageInfo)) {
                    return pageInfo;
                }
            }
        }

        return null;
    }

    public interface OnSearchContentCallBack {
        void OnNext(List<ReaderSelection> results, int page);

        void OnFinishedSearch(int endPage);
    }

    public boolean supportSearchByPage() {
        return isFixedPageDocument() && supportScalable();
    }

    public boolean isFixedPageDocument() {
        return getReaderViewInfo() != null && getReaderViewInfo().isFixedDocument;
    }

    public boolean supportScalable() {
        return getReaderViewInfo() != null && getReaderViewInfo().supportScalable;
    }

    public void closeRequest() {
        getReader().submitRequest(getReaderView().getViewContext(), new CloseRequest(), new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {

            }
        });
    }

    public void onDocumentOpened() {
        ReaderDocumentMetadata metadata = getReader().getDocumentMetadataSafely();
        DocumentInfo documentInfo = DocumentInfo.create(metadata.getAuthors(),
                getReader().getDocumentMd5(),
                getReader().getBookName(),
                getReader().getDocumentPath(),
                metadata.getTitle());
        EventBus.getDefault().post(new DocumentOpenEvent(getReaderView().getApplicationContext(), documentInfo));
    }
}
