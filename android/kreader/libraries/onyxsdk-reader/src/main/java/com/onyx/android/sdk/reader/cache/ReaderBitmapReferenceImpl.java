package com.onyx.android.sdk.reader.cache;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.memory.PoolConfig;
import com.facebook.imagepipeline.memory.PoolFactory;
import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by joy on 8/9/16.
 */
public class ReaderBitmapReferenceImpl implements ReaderBitmap, Closeable {
    private static PlatformBitmapFactory bitmapFactory = Fresco.getImagePipelineFactory().getPlatformBitmapFactory();

    public static Bitmap.Config DEFAULT_CONFIG = Bitmap.Config.ARGB_8888;

    private String key;
    private CloseableReference<Bitmap> bitmap;
    private float gammaCorrection = BaseOptions.getLowerGammaLimit();
    private float textGammaCorrection = BaseOptions.getLowerGammaLimit();
    private int emboldenLevel;

    public static ReaderBitmapReferenceImpl create(int width, int height, Bitmap.Config config) {
        ReaderBitmapReferenceImpl readerBitmap = new ReaderBitmapReferenceImpl(width, height, config);
        return readerBitmap;
    }

    public static ReaderBitmapReferenceImpl decodeStream(InputStream inputStream, Bitmap.Config config) throws IOException {
        PoolFactory poolFactory = new PoolFactory(PoolConfig.newBuilder().build());

        PooledByteBuffer pooledByteBuffer = null;
        EncodedImage image = null;
        try {
            pooledByteBuffer = poolFactory.getPooledByteBufferFactory().newByteBuffer(inputStream);
            image = new EncodedImage(CloseableReference.of(pooledByteBuffer));
            CloseableReference<Bitmap> bmp = Fresco.getImagePipelineFactory().getPlatformDecoder().decodeFromEncodedImage(image,
                    config);

            ReaderBitmapReferenceImpl result = new ReaderBitmapReferenceImpl();
            result.bitmap = bmp;
            return result;
        } finally {
            FileUtils.closeQuietly(image);
            FileUtils.closeQuietly(pooledByteBuffer);
        }
    }

    public ReaderBitmapReferenceImpl() {
        super();
    }

    public ReaderBitmapReferenceImpl(int width, int height, Bitmap.Config config) {
        super();
        bitmap = bitmapFactory.createBitmap(width, height, config);
    }

    public boolean isValid() {
        return bitmap != null && bitmap.isValid();
    }

    public void clear() {
        bitmap.get().eraseColor(Color.WHITE);
    }

    public void eraseColor(int white) {
        bitmap.get().eraseColor(white);
    }

    public CloseableReference<Bitmap> getBitmapReference() {
        return bitmap;
    }

    /**
     * add reference of internal bitmap
     * @return
     */
    public ReaderBitmapReferenceImpl clone() {
        ReaderBitmapReferenceImpl copy = new ReaderBitmapReferenceImpl();
        copy.key = key;
        copy.bitmap = bitmap.clone();
        copy.gammaCorrection = gammaCorrection;
        copy.textGammaCorrection = textGammaCorrection;
        copy.emboldenLevel = emboldenLevel;
        return copy;
    }

    /**
     * subtract reference of internal bitmap
     */
    public void close() {
        bitmap.close();
    }

    public String getKey() {
        return key;
    }

    public Bitmap getBitmap() {
        return bitmap.get();
    }

    public void setGammaCorrection(float correction) {
        this.gammaCorrection = correction;
    }

    public float gammaCorrection() {
        return gammaCorrection;
    }

    public boolean isGammaApplied(final float targetGammaCorrection) {
        return (Float.compare(gammaCorrection, targetGammaCorrection) == 0);
    }

    /**
     * when text gamma is used, we will ignore bitmap gamma correction
     * @return
     */
    public boolean isGammaIgnored() {
        return Float.compare(textGammaCorrection, BaseOptions.getLowerGammaLimit()) != 0;
    }

    public boolean isEmboldenApplied(final float targetEmboldenLevel) {
        return emboldenLevel == targetEmboldenLevel;
    }

    public void setTextGammaCorrection(float correction) {
        this.textGammaCorrection = correction;
    }

    public float textGammaCorrection() {
        return textGammaCorrection;
    }

    public boolean isTextGammaApplied(final float targetGammaCorrection) {
        return (Float.compare(textGammaCorrection, targetGammaCorrection) == 0);
    }

    public int getEmboldenLevel() {
        return emboldenLevel;
    }

    public void setEmboldenLevel(int emboldenLevel) {
        this.emboldenLevel = emboldenLevel;
    }

    public void attachWith(String key, final CloseableReference<Bitmap> src) {
        this.key = key;
        bitmap = src.clone();
    }

    public boolean match(int width, int height, final Bitmap.Config config) {
        return isValid() &&
                getBitmap().getWidth() == width &&
                getBitmap().getHeight() == height &&
                getBitmap().getConfig() == config;
    }
}
