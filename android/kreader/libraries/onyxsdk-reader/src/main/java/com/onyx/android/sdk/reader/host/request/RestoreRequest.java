package com.onyx.android.sdk.reader.host.request;

import android.graphics.RectF;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.android.sdk.reader.utils.ImageUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class RestoreRequest extends BaseReaderRequest {

    private BaseOptions baseOptions;
    private String position;
    private boolean alwaysScaleToPage = false;

    public RestoreRequest(final BaseOptions options) {
        baseOptions = options;
    }

    public void setAlwaysScaleToPage(boolean alwaysScaleToPage) {
        this.alwaysScaleToPage = alwaysScaleToPage;
    }

    public void execute(final Reader reader) throws Exception {
        try {
            // overrides with doc built-in options
            reader.getDocument().readBuiltinOptions(baseOptions);

            restoreDocumentCategory(reader);
            restoreLayoutType(reader);
            restorePagePosition(reader);
            restoreScale(reader);
            restoreOrientation(reader);
            restoreViewport(reader);
            restoreReflowSettings(reader);
            restoreContrast(reader);
            restoreReaderTextStyle(reader);
        } catch (Throwable tr) {
            Debug.e(getClass(), tr);
        }

        if (alwaysScaleToPage) {
            scaleToPage(reader);
        }
        drawVisiblePages(reader);
    }

    private void scaleToPage(final Reader reader) throws Exception {
        position = baseOptions.getCurrentPage();
        if (StringUtils.isNullOrEmpty(position)) {
            position = reader.getNavigator().getInitPosition();
        }
        reader.getReaderLayoutManager().scaleToPage(position);
    }

    private void restoreDocumentCategory(final Reader reader) {
        reader.getDocumentOptions().setDocumentCategory(baseOptions.getDocumentCategory());
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
            reader.getDocumentOptions().setTextGamma(value);
            if (value > ImageUtils.NO_GAMMA && value <= ImageUtils.MAX_GAMMA) {
                float textGamma = ImageUtils.getGammaCorrectionBySelection(value);
                reader.getRenderer().setTextGamma(textGamma);
            }
        }

        reader.getDocumentOptions().setEmboldenLevel(baseOptions.getEmboldenLevel());
    }

    private void restoreReaderTextStyle(final Reader reader) throws ReaderException {
        ReaderTextStyle style = ReaderTextStyle.create(baseOptions.getFontFace(),
                ReaderTextStyle.SPUnit.create(baseOptions.getFontSize()),
                ReaderTextStyle.Percentage.create(baseOptions.getLineSpacing()),
                ReaderTextStyle.CharacterIndent.create((int)baseOptions.getParagraphIndent()),
                ReaderTextStyle.Percentage.create(baseOptions.getLeftMargin()),
                ReaderTextStyle.Percentage.create(baseOptions.getTopMargin()),
                ReaderTextStyle.Percentage.create(baseOptions.getRightMargin()),
                ReaderTextStyle.Percentage.create(baseOptions.getBottomMargin()));
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
