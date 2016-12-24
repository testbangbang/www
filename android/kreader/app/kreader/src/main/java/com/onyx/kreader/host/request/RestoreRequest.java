package com.onyx.kreader.host.request;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.utils.LocaleUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.reflow.ImageReflowSettings;
import com.onyx.kreader.utils.DeviceConfig;
import com.onyx.kreader.utils.ImageUtils;

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
        restoreContrast(reader);
        restoreReaderTextStyle(reader);
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
        int viewWidth = reader.getViewOptions().getViewWidth();
        int viewHeight = reader.getViewOptions().getViewHeight();
        if (baseOptions.getViewport() != null &&
            baseOptions.getViewport().width() > 0 &&
            baseOptions.getViewport().height() > 0) {
            if (viewWidth != baseOptions.getViewport().width() || viewHeight != baseOptions.getViewport().height()) {
                Debug.e(this.getClass(),
                    "Restore with" +
                    " width: " + baseOptions.getViewport().width() +
                    " height: " + baseOptions.getViewport().height() +
                    " view width: " + viewWidth +
                    " view height: " + viewHeight);
            }
            reader.getReaderLayoutManager().getPageManager().setViewportRect(baseOptions.getViewport());
        }
    }

    private void restoreReflowSettings(final Reader reader) {
        if (StringUtils.isNotBlank(baseOptions.getReflowSettings())) {
            reader.getImageReflowManager().updateSettings(ImageReflowSettings.fromJsonString(baseOptions.getReflowSettings()));
        }
    }

    private void restoreContrast(final Reader reader) {
        float value = baseOptions.getGammaLevel();
        if (value > ImageUtils.NO_GAMMA && value <= ImageUtils.MAX_GAMMA) {
            reader.getDocumentOptions().setGamma(value);
        }
        reader.getDocumentOptions().setEmboldenLevel(baseOptions.getEmboldenLevel());
    }

    private void restoreReaderTextStyle(final Reader reader) throws ReaderException {
        String fontface = baseOptions.getFontFace();
        if (StringUtils.isNullOrEmpty(fontface) && LocaleUtils.isChinese()) {
            fontface = DeviceConfig.sharedInstance(getContext()).getDefaultFontFileForChinese();
        }
        float fontSize = baseOptions.getFontSize();
        int lineSpacing = baseOptions.getLineSpacing();
        int leftMargin = baseOptions.getLeftMargin();
        int topMargin = baseOptions.getTopMargin();
        int rightMargin = baseOptions.getRightMargin();
        int bottomMargin = baseOptions.getBottomMargin();
        ReaderTextStyle style = ReaderTextStyle.create(fontface,
                ReaderTextStyle.SPUnit.create(fontSize),
                ReaderTextStyle.Percentage.create(lineSpacing),
                ReaderTextStyle.Percentage.create(leftMargin),
                ReaderTextStyle.Percentage.create(topMargin),
                ReaderTextStyle.Percentage.create(rightMargin),
                ReaderTextStyle.Percentage.create(bottomMargin));
        reader.getReaderLayoutManager().setStyle(style);
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
