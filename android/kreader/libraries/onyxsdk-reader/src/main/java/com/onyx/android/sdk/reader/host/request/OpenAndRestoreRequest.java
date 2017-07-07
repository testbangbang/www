package com.onyx.android.sdk.reader.host.request;

import android.graphics.RectF;
import android.os.Build;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.android.sdk.reader.api.ReaderDrmCertificateFactory;
import com.onyx.android.sdk.reader.api.ReaderDrmManager;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.impl.ReaderDocumentOptionsImpl;
import com.onyx.android.sdk.reader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.android.sdk.reader.utils.ImageUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by zhuzeng on 10/6/15.
 */
public class OpenAndRestoreRequest extends BaseReaderRequest {

    private String documentPath;
    private BaseOptions baseOptions;
    private ReaderDrmCertificateFactory factory;
    private volatile boolean verifyDevice;
    private int newWidth, newHeight;
    private String position;
    private boolean restoreScaleToPage = false;

    public OpenAndRestoreRequest(final String path,
                                 final BaseOptions documentOptions,
                                 final ReaderDrmCertificateFactory f,
                                 boolean verify,
                                 int nw,
                                 int nh,
                                 boolean restoreScaleToPage) {
        documentPath = path;
        baseOptions = documentOptions;
        factory = f;
        verifyDevice = verify;
        newWidth = nw;
        newHeight = nh;
        this.restoreScaleToPage = restoreScaleToPage;
    }

    public void execute(final Reader reader) throws Exception {
        if (!checkDevice()) {
            throw new ReaderException(ReaderException.ACTIVATION_FAILED, "");
        }

        final ReaderDocumentOptionsImpl documentOptions = baseOptions.documentOptions();
        ReaderPluginOptions pluginOptions = baseOptions.pluginOptions();
        if (pluginOptions == null) {
            pluginOptions = ReaderPluginOptionsImpl.create(getContext());
        }

        if (!reader.getReaderHelper().selectPlugin(getContext(), documentPath, pluginOptions)) {
            return;
        }

        prepareDrmManager(reader);

        try {
            ReaderDocument document = reader.getPlugin().open(documentPath, documentOptions, pluginOptions);
            reader.getReaderHelper().onDocumentOpened(getContext(), documentPath, document, baseOptions, pluginOptions);
            // we should init data after document is opened
            reader.getReaderHelper().initData(getContext());
        } catch (ReaderException readerException) {
            throw readerException;
        }

        reader.getReaderHelper().updateViewportSize(newWidth, newHeight);

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
        if (restoreScaleToPage) {
            scaleToPage(reader);
        }
        drawVisiblePages(reader);
    }

    private boolean checkDevice() {
        if (!verifyDevice) {
            return true;
        }
        final String TAG = "onyx";
        if (Build.MANUFACTURER.toLowerCase().contains(TAG)) {
            return true;
        }
        if (Build.BRAND.toLowerCase().contains(TAG)) {
            return true;
        }
        if (Build.FINGERPRINT.toLowerCase().contains(TAG)) {
            return true;
        }
        return false;
    }

    private boolean prepareDrmManager(final Reader reader) {
        if (factory == null) {
            return false;
        }

        ReaderDrmManager manager = reader.getPlugin().createDrmManager();
        if (manager != null) {
            manager.activateDeviceDRM(factory.getDeviceId(), factory.getDrmCertificate());
        }
        return true;
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
