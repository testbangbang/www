package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.android.sdk.data.ReaderConstants;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.utils.StringUtils;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class RestoreRequest extends BaseReaderRequest {

    private BaseOptions baseOptions;
    private String position;

    public RestoreRequest(final BaseOptions options) {
        baseOptions = options;
    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        restoreLayoutType(reader);
        restorePagePosition(reader);
        restoreScale(reader);
        restoreViewport(reader);
        restoreOthers(reader);
        reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap(), createReaderViewInfo());
    }

    private void restoreLayoutType(final Reader reader) throws Exception {
        if (StringUtils.isNotBlank(baseOptions.getLayoutType())) {
            reader.getReaderLayoutManager().setCurrentLayout(baseOptions.getLayoutType(), null);
        }
    }

    private void restorePagePosition(final Reader reader) throws Exception {
        position = baseOptions.getCurrentPage();
        if (StringUtils.isNullOrEmpty(position)) {
            position = reader.getNavigator().getInitPosition();
        }
        reader.getReaderLayoutManager().gotoPosition(position);
    }

    private void restoreScale(final Reader reader) throws Exception {
        if (ReaderConstants.isSpecialScale(baseOptions.getSpecialScale())) {
            setSpecialScale(reader, baseOptions, position);
        } else {
            setActualScale(reader, baseOptions, position);
        }
    }

    private void restoreViewport(final Reader reader) throws Exception {
        if (baseOptions.getViewport() != null) {
            reader.getReaderLayoutManager().getPageManager().setViewportRect(baseOptions.getViewport());
        }
    }

    private void restoreOthers(final Reader reader) {
        reader.getDocumentOptions().setGamma(baseOptions.getGammaLevel());
        reader.getDocumentOptions().setEmboldenLevel(baseOptions.getEmboldenLevel());
    }

    private void setSpecialScale(final Reader reader, final BaseOptions baseOptions, final String position) throws Exception {
        if (ReaderConstants.isScaleToPage(baseOptions.getSpecialScale())) {
            reader.getReaderLayoutManager().scaleToPage(position);
        } else if (ReaderConstants.isScaleToWidth(baseOptions.getSpecialScale())) {
            reader.getReaderLayoutManager().scaleToWidth(position);
        } else if (ReaderConstants.isScaleToHeight(baseOptions.getSpecialScale())) {
            reader.getReaderLayoutManager().scaleToHeight(position);
        } else if (ReaderConstants.isScaleToPageContent(baseOptions.getSpecialScale())) {
            reader.getReaderLayoutManager().scaleToPageContent(position);
        }
    }

    private void setActualScale(final Reader reader, final BaseOptions baseOptions, final String position) throws Exception {
        reader.getReaderLayoutManager().setScale(position, baseOptions.getActualScale(), 0, 0);
    }


}
