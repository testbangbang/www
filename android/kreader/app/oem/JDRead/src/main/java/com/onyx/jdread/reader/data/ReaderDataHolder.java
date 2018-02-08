package com.onyx.jdread.reader.data;

import android.content.Context;
import android.view.SurfaceView;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.common.GammaInfo;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.common.ReaderViewConfig;
import com.onyx.jdread.reader.handler.HandlerManger;
import com.onyx.jdread.reader.highlight.ReaderSelectionInfo;
import com.onyx.jdread.reader.model.SelectMenuModel;

import org.greenrobot.eventbus.EventBus;

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
    private GammaInfo gammaInfo;
    private SettingInfo settingInfo;
    private EventBus eventBus = EventBus.getDefault();

    public ReaderDataHolder(final Context appContext) {
        this.readerTouchHelper = new ReaderTouchHelper();
        setAppContext(appContext);
        initView(appContext);
    }

    public SettingInfo getSettingInfo() {
        return settingInfo;
    }

    public void setSettingInfo(SettingInfo settingInfo) {
        this.settingInfo = settingInfo;
    }

    private void initView(Context context){
        float readerBottomStateBarHeight = context.getResources().getDimension(R.dimen.reader_content_view_bottom_state_bar_height);
        ReaderViewConfig.setReaderBottomStateBarHeight(0);

        float fontSize = context.getResources().getDimension(R.dimen.level_two_heading_font);
        ReaderViewConfig.setTimeFontSize(fontSize);
        ReaderViewConfig.setPageNumberFontSize(fontSize);

        float marginLeft = context.getResources().getDimension(R.dimen.reader_time_margin_left);
        ReaderViewConfig.setTimeMarginLeft(marginLeft);
        float marginBottom = context.getResources().getDimension(R.dimen.reader_time_margin_bottom);
        ReaderViewConfig.setTimeMarginBottom(marginBottom);

        float marginRight = context.getResources().getDimension(R.dimen.reader_page_number_margin_right);
        ReaderViewConfig.setPageNumberMarginRight(marginRight);
        marginBottom = context.getResources().getDimension(R.dimen.reader_page_number_margin_bottom);
        ReaderViewConfig.setPageNumberMarginBottom(marginBottom);
    }

    public EventBus getEventBus() {
        return eventBus;
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

    public void setDocumentInitState(){
        documentState = DocumentState.INIT;
    }

    public void setDocumentOpeningState(){
        documentState = DocumentState.OPENING;
    }

    public Reader getReader() {
        return reader;
    }

    public SurfaceView getReadPageView() {
        return reader.getReaderViewHelper().getContentView();
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

    public boolean supportSearchByPage() {
        return isFixedPageDocument() && supportScalable();
    }

    public boolean isFixedPageDocument() {
        return getReaderViewInfo() != null && getReaderViewInfo().isFixedDocument;
    }

    public boolean supportScalable() {
        return getReaderViewInfo() != null && getReaderViewInfo().supportScalable;
    }

    public GammaInfo getGammaInfo() {
        return gammaInfo;
    }

    public void setGammaInfo(GammaInfo gammaInfo) {
        this.gammaInfo = gammaInfo;
    }

    public boolean supportFontSizeAdjustment() {
        return getReaderViewInfo() != null && getReaderViewInfo().supportFontSizeAdjustment;
    }
}
