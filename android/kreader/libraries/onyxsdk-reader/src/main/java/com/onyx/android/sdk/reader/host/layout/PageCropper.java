package com.onyx.android.sdk.reader.host.layout;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.onyx.android.sdk.reader.BuildConfig;
import com.onyx.android.sdk.reader.api.ReaderRenderer;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.data.PageInfo;
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
        RectF cropRegion = ImageUtils.cropPage(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), PageConstants.DEFAULT_AUTO_CROP_VALUE);
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

    public float cropWidth(final float displayWidth, final float displayHeight, final PageInfo pageInfo) {
        float value = cropPage(displayWidth, displayHeight, pageInfo);
        return value;
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
