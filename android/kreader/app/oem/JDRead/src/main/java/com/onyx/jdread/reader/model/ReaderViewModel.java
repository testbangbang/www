package com.onyx.jdread.reader.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.view.SurfaceView;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.data.RegionFunctionManager;
import com.onyx.jdread.reader.utils.ReaderViewUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2017/12/21.
 */

public class ReaderViewModel extends BaseObservable {
    private ReaderDataHolder readerDataHolder;
    private ObservableBoolean isShowTipMessage = new ObservableBoolean(false);
    private ObservableField<String> tipMessage = new ObservableField<>();
    private ObservableBoolean isShowLastPage = new ObservableBoolean(false);
    private ObservableField<String> page = new ObservableField<>();
    private ObservableField<String> time = new ObservableField<>();

    public EventBus getEventBus() {
        return getReaderDataHolder().getEventBus();
    }

    public ObservableField<String> getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page.set(page);
    }

    public ObservableField<String> getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public boolean setDocumentInfo(DocumentInfo documentInfo) {
        if (!checkOpenBookParameter(documentInfo)) {
            return false;
        }
        openDocument(documentInfo);
        return true;
    }

    private boolean checkOpenBookParameter(DocumentInfo documentInfo) {
        if (documentInfo.getMessageId() != Integer.MAX_VALUE) {
            setTipMessage(JDReadApplication.getInstance().getString(documentInfo.getMessageId()));
            return false;
        }
        return true;
    }

    private void openDocument(DocumentInfo documentInfo) {
        getReaderDataHolder().initReaderDataHolder(documentInfo);
    }

    public ReaderDataHolder getReaderDataHolder() {
        if (readerDataHolder == null) {
            readerDataHolder = new ReaderDataHolder(JDReadApplication.getInstance());
        }
        return readerDataHolder;
    }

    public ObservableBoolean getIsShowTipMessage() {
        return isShowTipMessage;
    }

    public void setIsShowTipMessage(boolean isShowTipMessage) {
        this.isShowTipMessage.set(isShowTipMessage);
    }

    public void setTipMessage(String tipMessage) {
        this.tipMessage.set(tipMessage);
        setIsShowTipMessage(true);
    }

    public ObservableField<String> getTipMessage() {
        return tipMessage;
    }

    public ObservableBoolean getIsShowLastPage() {
        return isShowLastPage;
    }

    public void setIsShowLastPage(boolean isShowLastPage) {
        this.isShowLastPage.set(isShowLastPage);
    }

    public void clearSurfaceView(SurfaceView surfaceView) {
        ReaderViewUtil.clearSurfaceView(surfaceView);
    }

    public void setReaderPageView(SurfaceView surfaceView) {
        readerDataHolder.setReadPageView(surfaceView);
        RegionFunctionManager.initRegionAction(readerDataHolder.getAppContext());
    }
}
