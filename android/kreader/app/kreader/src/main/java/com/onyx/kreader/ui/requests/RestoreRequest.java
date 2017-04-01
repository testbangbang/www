package com.onyx.kreader.ui.requests;

import android.content.Context;
import android.graphics.RectF;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.utils.LocaleUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.device.DeviceConfig;
import com.onyx.android.sdk.reader.utils.ImageUtils;

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
        if (!reader.getReaderHelper().getRendererFeatures().supportScale()) {
            // only scalable document need restore viewport
            return;
        }
        RectF viewport = baseOptions.getViewport();
        int viewWidth = reader.getViewOptions().getViewWidth();
        int viewHeight = reader.getViewOptions().getViewHeight();
        if (viewport != null && viewport.width() > 0 && viewport.height() > 0) {
            if (viewWidth != viewport.width() || viewHeight != viewport.height()) {
                normalizeViewport(viewport, viewWidth, viewHeight);
            }
            Debug.d(getClass(), "normalized viewport: " + viewport);
            reader.getReaderLayoutManager().getPageManager().setViewportRect(viewport);
        }
    }

    private void normalizeViewport(RectF viewport, int dstWidth, int dstHeight) {
        viewport.right = viewport.left + dstWidth;
        viewport.bottom = viewport.top + dstHeight;
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
        if (reader.getRenderer().getRendererFeatures().supportFontGammaAdjustment()) {
            value = baseOptions.getTextGammaLevel();
            if (value > ImageUtils.NO_GAMMA && value <= ImageUtils.MAX_GAMMA) {
                reader.getRenderer().setTextGamma(value);
            }
        }

        reader.getDocumentOptions().setEmboldenLevel(baseOptions.getEmboldenLevel());
    }

    private void restoreReaderTextStyle(final Reader reader) throws ReaderException {
        ReaderTextStyle.setDefaultFontSizes(DeviceConfig.sharedInstance(getContext()).getDefaultFontSizes());
        String fontface = baseOptions.getFontFace();
        if (StringUtils.isNullOrEmpty(fontface) && LocaleUtils.isChinese()) {
            fontface = DeviceConfig.sharedInstance(getContext()).getDefaultFontFileForChinese();
        }

        float fontSize = getFontSize();
        int lineSpacing = getLineSpacing();
        int leftMargin = getLeftMargin();
        int topMargin = getTopMargin();
        int rightMargin = getRightMargin();
        int bottomMargin = getBottomMargin();

        ReaderTextStyle style = ReaderTextStyle.create(fontface,
                ReaderTextStyle.SPUnit.create(fontSize),
                ReaderTextStyle.Percentage.create(lineSpacing),
                ReaderTextStyle.Percentage.create(leftMargin),
                ReaderTextStyle.Percentage.create(topMargin),
                ReaderTextStyle.Percentage.create(rightMargin),
                ReaderTextStyle.Percentage.create(bottomMargin));
        reader.getReaderLayoutManager().setStyle(style);
    }

    private float getFontSize() {
        float fontSize = baseOptions.getFontSize();
        if (fontSize == BaseOptions.INVALID_FLOAT_VALUE) {
            int index = DeviceConfig.sharedInstance(getContext()).getDefaultFontSizeIndex();
            fontSize = ReaderTextStyle.getFontSizeByIndex(index).getValue();
            fontSize = SingletonSharedPreference.getLastFontSize(fontSize);
        }
        return fontSize;
    }

    private int getLineSpacing() {
        int lineSpacing = baseOptions.getLineSpacing();
        if (lineSpacing == BaseOptions.INVALID_INT_VALUE) {
            int index = DeviceConfig.sharedInstance(getContext()).getDefaultLineSpacingIndex();
            lineSpacing = ReaderTextStyle.getLineSpacingByIndex(index).getPercent();
            lineSpacing = SingletonSharedPreference.getLastLineSpacing(lineSpacing);
        }
        return lineSpacing;
    }

    private int getLeftMargin() {
        int leftMargin = baseOptions.getLeftMargin();
        if (leftMargin == BaseOptions.INVALID_INT_VALUE) {
            leftMargin = getDefaultPageMargin(getContext()).getLeftMargin().getPercent();
            leftMargin = SingletonSharedPreference.getLastLeftMargin(leftMargin);
        }
        return leftMargin;
    }

    private int getTopMargin() {
        int topMargin = baseOptions.getTopMargin();
        if (topMargin == BaseOptions.INVALID_INT_VALUE) {
            topMargin = getDefaultPageMargin(getContext()).getTopMargin().getPercent();
            topMargin = SingletonSharedPreference.getLastTopMargin(topMargin);
        }
        return topMargin;
    }

    private int getRightMargin() {
        int rightMargin = baseOptions.getRightMargin();
        if (rightMargin == BaseOptions.INVALID_INT_VALUE) {
            rightMargin = getDefaultPageMargin(getContext()).getRightMargin().getPercent();
            rightMargin = SingletonSharedPreference.getLastRightMargin(rightMargin);
        }
        return rightMargin;
    }

    private int getBottomMargin() {
        int bottomMargin = baseOptions.getBottomMargin();
        if (bottomMargin == BaseOptions.INVALID_INT_VALUE) {
            bottomMargin = getDefaultPageMargin(getContext()).getBottomMargin().getPercent();
            bottomMargin = SingletonSharedPreference.getLastBottomMargin(bottomMargin);
        }
        return bottomMargin;
    }

    private ReaderTextStyle.PageMargin getDefaultPageMargin(Context context) {
        int index = DeviceConfig.sharedInstance(context).getDefaultPageMarginIndex();
        return ReaderTextStyle.getPageMarginByIndex(index);
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
