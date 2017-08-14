package com.onyx.android.dr.reader.action;

import android.graphics.RectF;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.common.ReadPageInfo;
import com.onyx.android.dr.reader.common.ReadSettingFontFaceConfig;
import com.onyx.android.dr.reader.common.ReadSettingFontSizeConfig;
import com.onyx.android.dr.reader.common.ReadSettingMarginConfig;
import com.onyx.android.dr.reader.common.ReadSettingSpaceConfig;
import com.onyx.android.dr.reader.common.ReaderDeviceManager;
import com.onyx.android.dr.reader.common.ToastManage;
import com.onyx.android.dr.reader.event.ChangeScreenEvent;
import com.onyx.android.dr.reader.event.DocumentInfoRequestResultEvent;
import com.onyx.android.dr.reader.event.GetSearchHistoryEvent;
import com.onyx.android.dr.reader.event.GotoPositionActionResultEvent;
import com.onyx.android.dr.reader.event.SentenceRequestResultEvent;
import com.onyx.android.dr.reader.event.StartTtsPlayEvent;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.dr.reader.requests.GotoSearchLocationRequest;
import com.onyx.android.dr.reader.requests.SearchRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.data.model.SearchHistory;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.request.AddAnnotationRequest;
import com.onyx.android.sdk.reader.host.request.AddBookmarkRequest;
import com.onyx.android.sdk.reader.host.request.AddSearchHistoryRequest;
import com.onyx.android.sdk.reader.host.request.ChangeStyleRequest;
import com.onyx.android.sdk.reader.host.request.CreateViewRequest;
import com.onyx.android.sdk.reader.host.request.DeleteAnnotationRequest;
import com.onyx.android.sdk.reader.host.request.DeleteBookmarkRequest;
import com.onyx.android.sdk.reader.host.request.DeleteSearchHistoryRequest;
import com.onyx.android.sdk.reader.host.request.GetDocumentInfoRequest;
import com.onyx.android.sdk.reader.host.request.GetSearchHistoryRequest;
import com.onyx.android.sdk.reader.host.request.GetSentenceRequest;
import com.onyx.android.sdk.reader.host.request.GetTableOfContentRequest;
import com.onyx.android.sdk.reader.host.request.GotoPageRequest;
import com.onyx.android.sdk.reader.host.request.GotoPositionRequest;
import com.onyx.android.sdk.reader.host.request.NextScreenRequest;
import com.onyx.android.sdk.reader.host.request.OpenRequest;
import com.onyx.android.sdk.reader.host.request.PreviousScreenRequest;
import com.onyx.android.sdk.reader.host.request.RenderRequest;
import com.onyx.android.sdk.reader.host.request.RestoreRequest;
import com.onyx.android.sdk.reader.host.request.ScaleToPageRequest;
import com.onyx.android.sdk.reader.host.request.UpdateAnnotationRequest;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.reader.utils.TocUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by huxiaomao on 17/5/5.
 */

public class BookOperate {
    private ReaderPresenter readerPresenter;
    private boolean stopSearch;
    private int currentCount;
    private String query;
    private int contentLength;
    private int searchCount;
    private ReaderPresenter.OnSearchContentCallBack searchContentCallBack;
    private List<Integer> tocChapterNodeList;

    public BookOperate(ReaderPresenter readerPresenter) {
        this.readerPresenter = readerPresenter;
    }

    public void openDocument(final BaseOptions options) {
        options.setPassword(readerPresenter.getBookInfo().getPassword());
        OpenRequest openRequest = new OpenRequest(readerPresenter.getBookInfo().getBookPath(), options, null, true);
        openRequest.setContext(readerPresenter.getReaderView().getViewContext());
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(), openRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    setDocumentViewRect(readerPresenter.getReaderView().getView().getWidth(), readerPresenter.getReaderView().getView().getHeight(), options);
                } else {
                    readerPresenter.getReaderView().showThrowable(throwable);
                }
            }
        });
    }

    private void setDocumentViewRect(int width, int height, final BaseOptions options) {
        readerPresenter.onDocumentOpened();
        readerPresenter.getPageInformation().setDisplayWidth(width);
        readerPresenter.getPageInformation().setDisplayHeight(height);
        final CreateViewRequest createViewRequest = new CreateViewRequest(width, height);
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(), createViewRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    String currentPage = options.getCurrentPage();
                    restoreWithOptions(options, currentPage == null || "0".equals(currentPage));
                } else {
                    readerPresenter.getReaderView().showThrowable(throwable);
                }
            }
        });
    }

    private void restoreWithOptions(final BaseOptions options, final boolean init) {
        final RestoreRequest restoreRequest = new RestoreRequest(options);
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(), restoreRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    readerPresenter.setReaderViewInfo(restoreRequest.getReaderViewInfo());
                    readerPresenter.saveReaderUserDataInfo(restoreRequest);
                    if (init && ReadPageInfo.supportTypefaceAdjustment(readerPresenter)) {
                        ReadSettingFontSizeConfig.setDefaultReadSettingFontSize(readerPresenter);
                        ReadSettingSpaceConfig.setDefaultReadSettingLineSpace(readerPresenter);
                        ReadSettingMarginConfig.setDefaultReadSettingMargin(readerPresenter);
                        ReadSettingFontFaceConfig.setDefaultReadSettingFontFace(readerPresenter);
                        updateReaderStyle();
                    } else {
                        readerPresenter.getReaderView().updatePage(readerPresenter.getReader().getViewportBitmap().getBitmap());
                    }
                } else {
                    readerPresenter.getReaderView().showThrowable(e);
                }
                readerPresenter.documentOpenState = ReaderPresenter.DocumentOpenState.OPENED;
            }
        });
    }

    public void gotoPage(final boolean init) {
        final GotoPageRequest gotoPageRequest = new GotoPageRequest(readerPresenter.getPageInformation().getCurrentPage());
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(), gotoPageRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    readerPresenter.setReaderViewInfo(gotoPageRequest.getReaderViewInfo());
                    readerPresenter.saveReaderUserDataInfo(gotoPageRequest);
                    if (init && ReadPageInfo.supportTypefaceAdjustment(readerPresenter)) {
                        ReadSettingFontSizeConfig.setDefaultReadSettingFontSize(readerPresenter);
                        ReadSettingSpaceConfig.setDefaultReadSettingLineSpace(readerPresenter);
                        ReadSettingMarginConfig.setDefaultReadSettingMargin(readerPresenter);
                        ReadSettingFontFaceConfig.setDefaultReadSettingFontFace(readerPresenter);
                        updateReaderStyle();
                    } else {
                        readerPresenter.getReaderView().updatePage(readerPresenter.getReader().getViewportBitmap().getBitmap());
                    }
                } else {
                    readerPresenter.getReaderView().showThrowable(throwable);
                }
            }
        });
    }

    public void nextScreen() {
        if (!readerPresenter.getReaderViewInfo().canNextScreen) {
            ToastManage.showMessage(readerPresenter.getReaderView().getViewContext(),
                    readerPresenter.getReaderView().getViewContext().getString(R.string.max_page_toast));
            return;
        }
        final NextScreenRequest nextScreenRequest = new NextScreenRequest();
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(), nextScreenRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    readerPresenter.getPageInformation().setNextPage();
                }
                onRenderRequestFinished(nextScreenRequest, throwable);
            }
        });
    }

    public void prevScreen() {
        if (!readerPresenter.getReaderViewInfo().canPrevScreen) {
            ToastManage.showMessage(readerPresenter.getReaderView().getViewContext(),
                    readerPresenter.getReaderView().getViewContext().getString(R.string.min_page_toast));
            return;
        }
        final PreviousScreenRequest prevScreenRequest = new PreviousScreenRequest();
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(), prevScreenRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    readerPresenter.getPageInformation().setPrevPage();
                }
                onRenderRequestFinished(prevScreenRequest, throwable);
            }
        });
    }

    public void prepareGotoChapter(final boolean back) {
        List<Integer> tocChapterNodeList = getTocChapterNodeList();
        if (tocChapterNodeList == null || tocChapterNodeList.size() <= 0) {
            final GetTableOfContentRequest request = new GetTableOfContentRequest();
            readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(), request, new BaseCallback() {
                @Override
                public void done(BaseRequest baseRequest, Throwable throwable) {
                    ReaderDocumentTableOfContent toc = request.getReaderUserDataInfo().getTableOfContent();
                    if (toc == null || toc.isEmpty()) {
                        ToastManage.showMessage(readerPresenter.getReaderView().getApplicationContext(), R.string.no_chapters);
                        return;
                    }

                    List<Integer> list = TocUtils.buildChapterNodeList(toc);
                    setTocChapterNodeList(list);
                    gotoChapter(list, back);
                }
            });
        } else {
            gotoChapter(tocChapterNodeList, back);
        }
    }

    private void gotoChapter(List<Integer> list, boolean back) {
        int currentPagePosition = Integer.parseInt(readerPresenter.getCurrentPagePosition());
        if (back && currentPagePosition < list.get(0)) {
            ToastManage.showMessage(readerPresenter.getReaderView().getApplicationContext(), R.string.min_page_toast);
            return;
        }

        if (!back && currentPagePosition > list.get(list.size() - 1)) {
            ToastManage.showMessage(readerPresenter.getReaderView().getApplicationContext(), R.string.max_page_toast);
            return;
        }

        int positionByPage = getPositionByPage(currentPagePosition, list, back);
        GotoPositionAction(String.valueOf(positionByPage), true);
    }

    private int getPositionByPage(int currentPagePosition, List<Integer> list, boolean back) {
        for (int i = 0; i < list.size(); i++) {
            if (currentPagePosition < list.get(i)) {
                if (back) {
                    int index = i - 1;
                    if (index < 0) {
                        return 0;
                    }

                    int position = list.get(Math.max(0, index));
                    if (position < currentPagePosition) {
                        return position;
                    } else {
                        return getPositionByPage(currentPagePosition - 1, list, back);
                    }
                } else {
                    int position = list.get(i);
                    if (position > currentPagePosition) {
                        return position;
                    } else {
                        return getPositionByPage(currentPagePosition + 1, list, back);
                    }
                }
            }
        }

        if (back) {
            return currentPagePosition - 1;
        } else {
            return currentPagePosition + 1;
        }
    }

    public String getCurrentPageName(final BaseReaderRequest request) {
        return request.getReaderViewInfo().getFirstVisiblePage().getName();
    }

    public void redrawPage() {
        final RenderRequest renderRequest = new RenderRequest();
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(), renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable throwable) {
                if (throwable == null) {
                    readerPresenter.getPageInformation().setPrevPage();
                }
                onRenderRequestFinished(renderRequest, throwable);
            }
        });
    }

    public void updateReaderStyle() {
        ReaderTextStyle style = readerPresenter.getReaderViewInfo().readerTextStyle;
        final ChangeStyleRequest changeStyleRequest = new ChangeStyleRequest(style);
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(), changeStyleRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    readerPresenter.getPageInformation().setPrevPage();
                }
                onRenderRequestFinished(changeStyleRequest, throwable);
            }
        });
    }

    public void getSentenceRequest(final String startPosition) {
        final GetSentenceRequest sentenceRequest = new GetSentenceRequest(readerPresenter.getReaderViewInfo().getFirstVisiblePage().getPositionSafely(),
                startPosition);
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(),
                sentenceRequest, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable throwable) {
                        onRenderRequestFinished(sentenceRequest, throwable);
                        if (throwable == null) {
                            EventBus.getDefault().post(new SentenceRequestResultEvent(sentenceRequest.getSentenceResult(), throwable));
                        }
                    }
                });
    }

    public void startTtsPlay() {
        final ScaleToPageRequest scaleToPageRequest = new ScaleToPageRequest(readerPresenter.getReaderViewInfo().getFirstVisiblePage().getName());
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getApplicationContext(),
                scaleToPageRequest, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable throwable) {
                        onRenderRequestFinished(scaleToPageRequest, throwable);
                        EventBus.getDefault().post(new StartTtsPlayEvent());
                    }
                });
    }

    public void ttsNextScreen() {
        if (!readerPresenter.getReaderViewInfo().canNextScreen) {
            ToastManage.showMessage(readerPresenter.getReaderView().getViewContext(),
                    readerPresenter.getReaderView().getViewContext().getString(R.string.max_page_toast));
            return;
        }
        final NextScreenRequest nextScreenRequest = new NextScreenRequest();
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(), nextScreenRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                onRenderRequestFinished(nextScreenRequest, throwable);
                if (throwable == null) {
                    EventBus.getDefault().post(new ChangeScreenEvent());
                }
            }
        });
    }

    public void getDocumentInfo() {
        final GetDocumentInfoRequest getDocumentInfoRequest = new GetDocumentInfoRequest();
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(),
                getDocumentInfoRequest, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable throwable) {
                        DocumentInfoRequestResultEvent event = new DocumentInfoRequestResultEvent();
                        if (throwable == null) {
                            event.setBookmarks(getDocumentInfoRequest.getReaderUserDataInfo().getBookmarks());
                            event.setReaderDocumentTableOfContent(getDocumentInfoRequest.getReaderUserDataInfo().getTableOfContent());
                            event.setAnntation(getDocumentInfoRequest.getReaderUserDataInfo().getAnnotations());
                        }
                        EventBus.getDefault().post(event);
                    }
                });
    }

    private void onRenderRequestFinished(BaseReaderRequest renderRequest, Throwable throwable) {
        if (throwable == null) {
            readerPresenter.setReaderViewInfo(renderRequest.getReaderViewInfo());
            readerPresenter.saveReaderUserDataInfo(renderRequest);
            if (readerPresenter.getReaderViewInfo() != null) {
                readerPresenter.getReaderView().updatePage(readerPresenter.getReader().getViewportBitmap().getBitmap());
            }
            ReaderDeviceManager.applyWithGCInterval(readerPresenter.getReaderView().getView(), true);
        } else {
            readerPresenter.getReaderView().showThrowable(throwable);
        }
    }

    public void GotoPositionAction(String pagePosition, boolean abortPendingTasks) {
        final GotoPositionRequest gotoPositionRequest = new GotoPositionRequest(pagePosition);
        gotoPositionRequest.setAbortPendingTasks(abortPendingTasks);
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(),
                gotoPositionRequest, new BaseCallback() {
                    @Override
                    public void done(BaseRequest baseRequest, Throwable throwable) {
                        onRenderRequestFinished(gotoPositionRequest, throwable);
                        EventBus.getDefault().post(new GotoPositionActionResultEvent());
                    }
                });
    }

    public void addBookmark() {
        final AddBookmarkRequest addBookmarkRequest = new AddBookmarkRequest(readerPresenter.getReaderViewInfo().getFirstVisiblePage());
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(),
                addBookmarkRequest, new BaseCallback() {
                    @Override
                    public void done(BaseRequest baseRequest, Throwable throwable) {
                        onRenderRequestFinished(addBookmarkRequest, throwable);
                    }
                });
    }

    public void removeBookmark(final Object resultNotifyEvent) {
        Bookmark bookmark = readerPresenter.getReaderUserDataInfo().getBookmark(readerPresenter.getReaderViewInfo().getFirstVisiblePage());
        final DeleteBookmarkRequest deleteBookmarkRequest = new DeleteBookmarkRequest(bookmark);
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(),
                deleteBookmarkRequest, new BaseCallback() {
                    @Override
                    public void done(BaseRequest baseRequest, Throwable throwable) {
                        onRenderRequestFinished(deleteBookmarkRequest, throwable);
                        if (resultNotifyEvent != null) {
                            EventBus.getDefault().post(resultNotifyEvent);
                        }
                    }
                });
    }

    public void updateAnnotation(final Annotation annotation, final String note, final Object resultNotifyEvent) {
        annotation.setNote(note);
        final UpdateAnnotationRequest updateAnnotationRequest = new UpdateAnnotationRequest(annotation);
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(),
                new UpdateAnnotationRequest(annotation), new BaseCallback() {
                    @Override
                    public void done(BaseRequest baseRequest, Throwable throwable) {
                        onRenderRequestFinished(updateAnnotationRequest, throwable);
                        if (resultNotifyEvent != null) {
                            EventBus.getDefault().post(resultNotifyEvent);
                        }
                    }
                });
    }

    public void deleteAnnotation(final Annotation annotation, final Object resultNotifyEvent) {
        final DeleteAnnotationRequest deleteAnnotationRequest = new DeleteAnnotationRequest(annotation);
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(),
                new DeleteAnnotationRequest(annotation), new BaseCallback() {
                    @Override
                    public void done(BaseRequest baseRequest, Throwable throwable) {
                        onRenderRequestFinished(deleteAnnotationRequest, throwable);
                        if (resultNotifyEvent != null) {
                            EventBus.getDefault().post(resultNotifyEvent);
                        }
                    }
                });
    }

    public void addAnnotationAction(PageInfo pageInfo, String locationBegin, String locationEnd,
                                    List<RectF> rect, String quote, String note) {
        List<RectF> rectList = translateToDocument(pageInfo, rect);
        final AddAnnotationRequest addAnnotationRequest = new AddAnnotationRequest(pageInfo, locationBegin, locationEnd, rectList, quote, note);
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(),
                addAnnotationRequest, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable throwable) {
                        onRenderRequestFinished(addAnnotationRequest, throwable);
                    }
                });
    }

    public String getSelectionText() {
        ReaderSelection readerSelection = readerPresenter.getReaderUserDataInfo().getHighlightResult();
        return readerSelection != null ? readerSelection.getText() : "";
    }

    private List<RectF> translateToDocument(PageInfo pageInfo, List<RectF> rects) {
        for (RectF rect : rects) {
            PageUtils.translateToDocument(pageInfo, rect);
        }
        return rects;
    }

    public void onToggleSearchHistory(String content, boolean add) {
        if (add) {
            AddSearchHistoryRequest addSearchHistoryRequest = new AddSearchHistoryRequest(content);
            readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(),
                    addSearchHistoryRequest, new BaseCallback() {
                        @Override
                        public void done(BaseRequest request, Throwable throwable) {

                        }
                    });
        } else {
            DeleteSearchHistoryRequest deleteSearchHistoryRequest = new DeleteSearchHistoryRequest();
            readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(), deleteSearchHistoryRequest,
                    new BaseCallback() {
                        @Override
                        public void done(BaseRequest baseRequest, Throwable throwable) {

                        }
                    });
        }
    }

    public void getSearchHistory(int count) {
        final GetSearchHistoryRequest getSearchHistoryRequest = new GetSearchHistoryRequest(count);
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(),
                getSearchHistoryRequest, new BaseCallback() {
                    @Override
                    public void done(BaseRequest baseRequest, Throwable throwable) {
                        if (throwable != null) {
                            return;
                        }
                        List<SearchHistory> searchHistoryList = getSearchHistoryRequest.getReaderUserDataInfo().getSearchHistoryList();
                        EventBus.getDefault().post(new GetSearchHistoryEvent(searchHistoryList));
                    }
                });
    }

    public void searchParam() {
        stopSearch = false;
    }

    public void searchContent(final String query, final int contentLength, final int startPage,
                              final int searchCount, final ReaderPresenter.OnSearchContentCallBack searchContentCallBack) {

        this.query = query;
        this.contentLength = contentLength;
        this.searchCount = searchCount;
        this.searchContentCallBack = searchContentCallBack;
        if (startPage >= ReadPageInfo.getTotalPage(readerPresenter) || stopSearch ||
                currentCount >= searchCount) {
            if (searchContentCallBack != null) {
                searchContentCallBack.OnFinishedSearch(startPage);
            }
            return;
        }

        SearchRequest request = new SearchRequest(PagePositionUtils.fromPageNumber(startPage), query, false, false, contentLength, readerPresenter);
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(),
                request, new BaseCallback() {
                    @Override
                    public void done(BaseRequest baseRequest, Throwable throwable) {
                        List<ReaderSelection> selections = readerPresenter.getReaderUserDataInfo().getSearchResults();
                        if (searchContentCallBack != null) {
                            searchContentCallBack.OnNext(selections, startPage);
                        }
                        if (selections != null) {
                            currentCount += selections.size();
                        }

                        int next = startPage + 1;
                        if (!readerPresenter.supportSearchByPage()) {
                            searchContentCallBack.OnFinishedSearch(startPage);
                        } else {
                            searchContent(query, contentLength, next, searchCount, searchContentCallBack);
                        }
                    }
                });
    }

    public void proceedSearch(final int startPage) {
        currentCount = 0;
        searchContent(query, contentLength, startPage, searchCount, searchContentCallBack);
    }

    public void stopSearch() {
        currentCount = 0;
        stopSearch = true;
    }

    public void gotoSearchPage(String pageName, List<ReaderSelection> searchResults, final BaseCallback callback) {
        final BaseReaderRequest gotoPosition = new GotoSearchLocationRequest(pageName, searchResults);
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(),
                gotoPosition, new BaseCallback() {
                    @Override
                    public void done(BaseRequest baseRequest, Throwable throwable) {
                        onRenderRequestFinished(gotoPosition, throwable);
                        if (callback != null) {
                            callback.done(null, null);
                        }
                    }
                });
    }

    public List<Integer> getTocChapterNodeList() {
        return tocChapterNodeList;
    }

    public void setTocChapterNodeList(List<Integer> tocChapterNodeList) {
        this.tocChapterNodeList = tocChapterNodeList;
    }
}
