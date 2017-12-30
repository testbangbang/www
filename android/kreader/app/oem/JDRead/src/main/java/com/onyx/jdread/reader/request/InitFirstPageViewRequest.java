package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 2017/12/22.
 */

public class InitFirstPageViewRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;
    private int width;
    private int height;

    public InitFirstPageViewRequest(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public InitFirstPageViewRequest call() throws Exception {
        updateView();
        initPosition();
        readerDataHolder.getReaderViewHelper().updatePageView(readerDataHolder);
        return this;
    }

    private void restoreScale(final Reader reader,String position) throws Exception {
        BaseOptions baseOptions = reader.getReaderHelper().getDocumentOptions();
        if (PageConstants.isSpecialScale(baseOptions.getSpecialScale())) {
            setSpecialScale(reader, baseOptions, position);
        } else {
            setActualScale(reader, baseOptions, position);
        }
    }

    private void setSpecialScale(final Reader reader, final BaseOptions baseOptions, final String position) throws Exception {
        if (PageConstants.isScaleToPage(baseOptions.getSpecialScale())) {
            reader.getReaderHelper().getReaderLayoutManager().scaleToPage(position);
        } else if (PageConstants.isScaleToWidth(baseOptions.getSpecialScale())) {
            reader.getReaderHelper().getReaderLayoutManager().scaleToWidth(position);
        } else if (PageConstants.isScaleToHeight(baseOptions.getSpecialScale())) {
            reader.getReaderHelper().getReaderLayoutManager().scaleToHeight(position);
        } else if (PageConstants.isScaleToPageContent(baseOptions.getSpecialScale())) {
            reader.getReaderHelper().getReaderLayoutManager().scaleToPageContent(position);
        } else if (PageConstants.isWidthCrop(baseOptions.getSpecialScale())) {
            reader.getReaderHelper().getReaderLayoutManager().scaleToWidthContent(position);
        } else {
            reader.getReaderHelper().getReaderLayoutManager().scaleToPage(position);
        }
    }

    private void setActualScale(final Reader reader, final BaseOptions baseOptions, final String position) throws Exception {
        float scale = baseOptions.getActualScale();
        if (scale > PageConstants.SCALE_INVALID && baseOptions.getActualScale() < PageConstants.MAX_SCALE) {
            reader.getReaderHelper().getReaderLayoutManager().setScale(position, baseOptions.getActualScale(), 0, 0);
        } else {
            reader.getReaderHelper().getReaderLayoutManager().scaleToPage(position);
        }
    }

    private void updateView() throws Exception{
        width = readerDataHolder.getReaderViewHelper().getPageViewWidth();
        height = readerDataHolder.getReaderViewHelper().getPageViewHeight();
        readerDataHolder.getReader().getReaderHelper().updateViewportSize(width, height);
    }

    private void initPosition() throws Exception{
        String bookPath = readerDataHolder.getReader().getDocumentInfo().getBookPath();
        String position = PreferenceManager.getStringValue(JDReadApplication.getInstance(),bookPath,"0");
        readerDataHolder.getReader().getReaderHelper().gotoPosition(position);
        restoreScale(readerDataHolder.getReader(),position);
    }
}
