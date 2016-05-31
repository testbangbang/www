package com.onyx.kreader.host.layout;

import android.graphics.Bitmap;
import android.graphics.RectF;
import com.onyx.kreader.BuildConfig;
import com.onyx.kreader.api.ReaderPlugin;
import com.onyx.kreader.api.ReaderRenderer;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.math.PageManager;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.options.ReaderConstants;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.host.wrapper.ReaderHelper;
import com.onyx.kreader.ui.data.ReaderConfig;
import com.onyx.kreader.utils.BitmapUtils;
import com.onyx.kreader.utils.ImageUtils;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class PageCropper extends PageManager.PageCropProvider {

    // when calculate crop region, use specified display size instead of view size
    // to reduce rendering time. cropDisplay should be smaller than view size.
    private static RectF cropDisplay;
    private ReaderRenderer readerRenderer;
    private boolean debugCrop = true;

    public PageCropper(final ReaderRenderer r) {
        readerRenderer = r;
    }

    private ReaderRenderer getReaderRenderer() {
        return readerRenderer;
    }

    public float cropPage(final float displayWidth, final float displayHeight, final PageInfo pageInfo) {
        // step1: render in a small bitmap.
        final RectF viewport = getCropDisplay(pageInfo.getOriginWidth(), pageInfo.getOriginHeight());
        float scale = PageUtils.scaleToPage(pageInfo.getOriginWidth(), pageInfo.getOriginHeight(), viewport.width(), viewport.height());
        final ReaderBitmapImpl bitmapWrapper = ReaderBitmapImpl.create((int) viewport.width(), (int) viewport.height(), Bitmap.Config.ARGB_8888);
        final Bitmap bitmap = bitmapWrapper.getBitmap();

        // step2: reuse page manager to calculate positions.
        LayoutProviderUtils.drawPageWithScaleToPage(pageInfo, bitmapWrapper, getReaderRenderer());
        if (debugCrop && BuildConfig.DEBUG) {
            BitmapUtils.saveBitmap(bitmap, "/mnt/sdcard/before-crop.png");
        }

        // step3: crop the image.
        RectF cropRegion = ImageUtils.cropPage(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), ReaderConstants.DEFAULT_AUTO_CROP_VALUE);
        if (debugCrop && BuildConfig.DEBUG) {
            BitmapUtils.drawRectOnBitmap(bitmap, cropRegion);
            BitmapUtils.saveBitmap(bitmap, "/mnt/sdcard/with-crop.png");
        }
        bitmap.recycle();

        // step4: calculate region for page with origin size.
        // for caller, PageUtils.scaleRect(cropRegion, viewport);
        float delta = PageUtils.scaleByRect(viewport, new RectF(0, 0, pageInfo.getOriginWidth(), pageInfo.getOriginHeight()));
        PageUtils.scaleRect(cropRegion, delta);
        pageInfo.setAutoCropContentRegion(cropRegion);
        return scale * delta;
    }

    public static RectF getCropDisplay(final float pw, final float ph) {
        cropDisplay = new RectF(0, 0, pw / 2, ph / 2);
        return cropDisplay;
    }


}
