package com.onyx.android.sdk.reader.host.layout;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.onyx.android.sdk.reader.BuildConfig;
import com.onyx.android.sdk.reader.api.ReaderRenderer;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.cache.ReaderBitmapReferenceImpl;
import com.onyx.android.sdk.reader.host.math.PageManager;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.reader.utils.ImageUtils;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class PageCropper extends PageManager.PageCropProvider {

    private ReaderRenderer readerRenderer;
    private boolean debugCrop = false;

    public PageCropper(final ReaderRenderer r) {
        readerRenderer = r;
    }

    private ReaderRenderer getReaderRenderer() {
        return readerRenderer;
    }

    public RectF cropPage(final PageInfo pageInfo) {
        return cropPage(pageInfo, null);
    }

    public RectF cropPage(final PageInfo pageInfo, final RectF targetRatioRegion) {
        // step1: render in a small bitmap.
        final RectF viewport = getCropDisplay(pageInfo.getOriginWidth(), pageInfo.getOriginHeight());
        final ReaderBitmapReferenceImpl bitmapWrapper = ReaderBitmapReferenceImpl.create((int) viewport.width(), (int) viewport.height(), ReaderBitmapReferenceImpl.DEFAULT_CONFIG);
        try {
            final Bitmap bitmap = bitmapWrapper.getBitmap();

            // step2: reuse page manager to calculate positions.
            LayoutProviderUtils.drawPageWithScaleToPage(pageInfo, bitmapWrapper, getReaderRenderer());
            if (debugCrop && BuildConfig.DEBUG) {
                BitmapUtils.saveBitmap(bitmap, "/mnt/sdcard/before-crop.png");
            }

            final int left = targetRatioRegion == null ? 0 : (int) (bitmap.getWidth() * targetRatioRegion.left);
            final int top = targetRatioRegion == null ? 0 : (int) (bitmap.getHeight() * targetRatioRegion.top);
            final int right = targetRatioRegion == null ? bitmap.getWidth() : (int) (bitmap.getWidth() * targetRatioRegion.right);
            final int bottom = targetRatioRegion == null ? bitmap.getHeight() : (int) (bitmap.getHeight() * targetRatioRegion.bottom);

            // step3: crop the image.
            RectF cropRegion = ImageUtils.cropPage(bitmap, left, top, right, bottom, PageConstants.DEFAULT_AUTO_CROP_VALUE);
            if (debugCrop && BuildConfig.DEBUG) {
                BitmapUtils.drawRectOnBitmap(bitmap, cropRegion);
                BitmapUtils.saveBitmap(bitmap, "/mnt/sdcard/with-crop.png");
            }

            // step4: calculate region for page with origin size.
            // for caller, PageUtils.scaleRect(cropRegion, viewport);
            float delta = PageUtils.scaleByRect(viewport, new RectF(0, 0, pageInfo.getOriginWidth(), pageInfo.getOriginHeight()));
            PageUtils.scaleRect(cropRegion, delta);
            if (targetRatioRegion == null || (targetRatioRegion.width() >= 1 &&
                    targetRatioRegion.height() >= 1)) {
                pageInfo.setAutoCropContentRegion(cropRegion);
            }
            return cropRegion;
        } finally {
            bitmapWrapper.close();
        }
    }

    public RectF cropWidth(final PageInfo pageInfo) {
        return cropPage(pageInfo);
    }

    public static RectF getCropDisplay(final float pw, final float ph) {
        // when calculate crop region, use specified display size instead of view size
        // to reduce rendering time. cropDisplay should be smaller than view size.
        final RectF cropDisplay;

        // if crop display is too small, we may getById incorrect result, so we limit the size of scaled page
        final int TARGET_CROP_PAGE_WIDTH = 300;
        final int TARGET_CROP_PAGE_HEIGHT = 400;

        if (pw <= TARGET_CROP_PAGE_WIDTH && ph <= TARGET_CROP_PAGE_HEIGHT) {
            cropDisplay = new RectF(0, 0, pw, ph);
        } else {
            float scale = Math.min(pw / TARGET_CROP_PAGE_WIDTH, ph / TARGET_CROP_PAGE_HEIGHT);
            cropDisplay = new RectF(0, 0, pw / scale, ph / scale);
        }
        return cropDisplay;
    }


}
