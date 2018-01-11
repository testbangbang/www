package com.onyx.jdread.reader.data;

import android.view.SurfaceView;

import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
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
