package com.onyx.jdread.reader.data;

import android.content.Context;
import android.view.SurfaceView;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.handler.HandlerManger;
import com.onyx.jdread.reader.highlight.ReaderSelectionInfo;
import com.onyx.jdread.reader.model.SelectMenuModel;

/**
 * Created by huxiaomao on 2017/12/20.
 */

public class ReaderDataHolder {
    public enum DocumentState {INIT, OPENING, OPENED}

    private DocumentState documentState = DocumentState.INIT;
    private Reader reader;
    private ReaderTextStyle style;
    private ImageReflowSettings settings;
    private ReaderViewInfo readerViewInfo;
    private ReaderUserDataInfo readerUserDataInfo;
    private HandlerManger handlerManger;
    private Context appContext;
    private ReaderTouchHelper readerTouchHelper;
    private SelectMenuModel selectMenuModel;
    private DocumentInfo documentInfo;
    private ReaderSelectionInfo readerSelectionInfo;

    public ReaderDataHolder(final Context appContext) {
        this.readerTouchHelper = new ReaderTouchHelper();
        setAppContext(appContext);
    }

    public SelectMenuModel getSelectMenuModel() {
        return selectMenuModel;
    }

    public void setSelectMenuModel(SelectMenuModel selectMenuModel) {
        this.selectMenuModel = selectMenuModel;
    }

    public ReaderTouchHelper getReaderTouchHelper() {
        return readerTouchHelper;
    }

    public ReaderSelectionInfo getReaderSelectionInfo(){
        if (readerSelectionInfo == null) {
            readerSelectionInfo = new ReaderSelectionInfo();
        }
        return readerSelectionInfo;
    }

    public HandlerManger getHandlerManger() {
        if (handlerManger == null) {
            handlerManger = new HandlerManger(this);
        }
        return handlerManger;
    }

    public ReaderViewInfo getReaderViewInfo() {
        return readerViewInfo;
    }

    public ReaderUserDataInfo getReaderUserDataInfo() {
        return readerUserDataInfo;
    }

    public ImageReflowSettings getSettingsCopy() {
        return ImageReflowSettings.copy(settings);
    }

    public void setSettings(ImageReflowSettings settings){
        this.settings = settings;
    }

    public ReaderTextStyle getStyleCopy() {
        return ReaderTextStyle.copy(style);
    }

    public void setStyle(ReaderTextStyle style) {
        this.style = style;
    }

    public void setReaderViewInfo(ReaderViewInfo readerViewInfo) {
        this.readerViewInfo = readerViewInfo;
    }

    public void setReaderUserDataInfo(ReaderUserDataInfo readerUserDataInfo) {
        this.readerUserDataInfo = readerUserDataInfo;
    }

    public void initReaderDataHolder(final DocumentInfo documentInfo) {
        documentState = DocumentState.INIT;
        this.documentInfo = documentInfo;
        reader = ReaderManager.getReader(documentInfo,getAppContext());
        readerTouchHelper.setReaderDataHolder(this);
    }

    public boolean isDocumentOpened() {
        return documentState == DocumentState.OPENED && reader != null;
    }

    public void setDocumentOpenState(){
        documentState = DocumentState.OPENED;
    }

    public void setDocumentOpeningState(){
        documentState = DocumentState.OPENING;
    }

    public Reader getReader() {
        return reader;
    }

    public SurfaceView getReadPageView() {
        return reader.getReaderViewHelper().getReadPageView();
    }

    public void setReadPageView(SurfaceView readPageView) {
        reader.getReaderViewHelper().setReadPageView(readPageView);
        getReaderTouchHelper().setReaderViewTouchListener(readPageView);
    }

    public Context getAppContext() {
        return appContext;
    }

    public void setAppContext(Context appContext) {
        this.appContext = appContext;
    }

    public String getCurrentPagePosition() {
        return getReaderViewInfo().getFirstVisiblePage().getPositionSafely();
    }

    public String getBookName(){
        return getDocumentInfo().getBookName();
    }

    public DocumentInfo getDocumentInfo() {
        return documentInfo;
    }

    public String getCurrentPageName() {
        return getReaderViewInfo().getFirstVisiblePage().getName();
    }

    public int getCurrentPage() {
        return PagePositionUtils.getPageNumber(getCurrentPageName());
    }

    public int getPageCount() {
        return getReaderViewInfo().getTotalPage();
    }
}
