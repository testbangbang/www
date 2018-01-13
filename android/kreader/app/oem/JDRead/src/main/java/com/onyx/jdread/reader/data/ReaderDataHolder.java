package com.onyx.jdread.reader.data;

import android.view.SurfaceView;

import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.handler.HandlerManger;
import com.onyx.jdread.reader.highlight.ReaderSelectionManager;
import com.onyx.jdread.reader.menu.dialog.ReaderSettingMenuDialog;

/**
 * Created by huxiaomao on 2017/12/20.
 */

public class ReaderDataHolder {
    public enum DocumentOpenState {INIT, OPENING, OPENED}

    private DocumentOpenState documentOpenState = DocumentOpenState.INIT;
    private Reader reader;
    private ReaderTextStyle style;
    private ImageReflowSettings settings;
    private ReaderViewInfo readerViewInfo;
    private ReaderUserDataInfo readerUserDataInfo;
    private ReaderSelectionManager readerSelectionManager;
    private HandlerManger handlerManger;

    public ReaderSelectionManager getReaderSelectionManager() {
        if (readerSelectionManager == null) {
            readerSelectionManager = new ReaderSelectionManager();
        }
        return readerSelectionManager;
    }

    public HandlerManger getHandlerManger() {
        if (handlerManger == null) {
            handlerManger = new HandlerManger(this);
        }
        return handlerManger;
    }

    public ReaderDocument openDocument(final String path, final BaseOptions baseOptions, final ReaderPluginOptions pluginOptions) throws Exception{
        documentOpenState = DocumentOpenState.OPENING;
        return reader.getReaderHelper().openDocument(path,baseOptions,pluginOptions);
    }

    public void saveReaderDocument(ReaderDocument readerDocument, DocumentInfo documentInfo){
        reader.getReaderHelper().saveReaderDocument(readerDocument,documentInfo);
    }

    public ReaderViewInfo getReaderViewInfo() {
        return readerViewInfo;
    }

    public ReaderUserDataInfo getReaderUserDataInfo() {
        return readerUserDataInfo;
    }

    public ImageReflowSettings getSettings() {
        return settings;
    }

    public void setSettings(ImageReflowSettings settings){
        this.settings = settings;
    }

    public ReaderTextStyle getStyle() {
        return style;
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
        documentOpenState = DocumentOpenState.INIT;
        reader = ReaderManager.getReader(documentInfo);
        reader.init(this);
    }

    public boolean isDocumentOpened() {
        return documentOpenState == DocumentOpenState.OPENED && reader != null;
    }

    public void updateDocumentOpenStatePened(){
        documentOpenState = DocumentOpenState.OPENED;
    }

    public Reader getReader() {
        return reader;
    }

    public SurfaceView getReadPageView() {
        return reader.getReaderViewHelper().getReadPageView();
    }

    public void setReadPageView(SurfaceView readPageView) {
        reader.getReaderViewHelper().setReadPageView(readPageView);
        reader.getReaderTouchHelper().setReaderViewTouchListener(readPageView);
    }

    public ReaderViewHelper getReaderViewHelper() {
        return reader.getReaderViewHelper();
    }
}
