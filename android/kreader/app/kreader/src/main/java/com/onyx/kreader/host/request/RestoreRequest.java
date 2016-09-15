package com.onyx.kreader.host.request;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.reflow.ImageReflowSettings;

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
        restoreLayoutType(reader);
        restorePagePosition(reader);
        restoreScale(reader);
        restoreOrientation(reader);
        restoreViewport(reader);
        restoreReflowSettings(reader);
        restoreOthers(reader);
        drawVisiblePages(reader);
    }

    private void restoreLayoutType(final Reader reader) throws Exception {
        if (StringUtils.isNotBlank(baseOptions.getLayoutType())) {
            reader.getReaderLayoutManager().setCurrentLayout(baseOptions.getLayoutType(), baseOptions.getNavigationArgs());
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
        if (PageConstants.isSpecialScale(baseOptions.getSpecialScale())) {
            setSpecialScale(reader, baseOptions, position);
        } else {
            setActualScale(reader, baseOptions, position);
        }
    }

    private void restoreOrientation(final Reader reader) {
        reader.getDocumentOptions().setOrientation(baseOptions.getOrientation());
    }

    private void restoreViewport(final Reader reader) throws Exception {
        if (baseOptions.getViewport() != null) {
            reader.getReaderLayoutManager().getPageManager().setViewportRect(baseOptions.getViewport());
        }
    }

    private void restoreReflowSettings(final Reader reader) {
        if (StringUtils.isNotBlank(baseOptions.getReflowSettings())) {
            reader.getImageReflowManager().updateSettings(ImageReflowSettings.fromJsonString(baseOptions.getReflowSettings()));
        }
    }

    private void restoreOthers(final Reader reader) {
        reader.getDocumentOptions().setGamma(baseOptions.getGammaLevel());
        reader.getDocumentOptions().setEmboldenLevel(baseOptions.getEmboldenLevel());
    }

    private void setSpecialScale(final Reader reader, final BaseOptions baseOptions, final String position) throws Exception {
        if (PageConstants.isScaleToPage(baseOptions.getSpecialScale())) {
            reader.getReaderLayoutManager().scaleToPage(position);
        } else if (PageConstants.isScaleToWidth(baseOptions.getSpecialScale())) {
            reader.getReaderLayoutManager().scaleToWidth(position);
        } else if (PageConstants.isScaleToHeight(baseOptions.getSpecialScale())) {
            reader.getReaderLayoutManager().scaleToHeight(position);
        } else if (PageConstants.isScaleToPageContent(baseOptions.getSpecialScale())) {
            reader.getReaderLayoutManager().scaleToPageContent(position);
        } else if (PageConstants.isWidthCrop(baseOptions.getSpecialScale())) {
            reader.getReaderLayoutManager().scaleToWidthContent(position);
        } else {
            reader.getReaderLayoutManager().scaleToPage(position);
        }
    }

    private void setActualScale(final Reader reader, final BaseOptions baseOptions, final String position) throws Exception {
        float scale = baseOptions.getActualScale();
        if (scale > PageConstants.SCALE_INVALID && baseOptions.getActualScale() < PageConstants.MAX_SCALE) {
            reader.getReaderLayoutManager().setScale(position, baseOptions.getActualScale(), 0, 0);
        } else {
            reader.getReaderLayoutManager().scaleToPage(position);
        }
    }


}
