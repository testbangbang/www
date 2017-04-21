package com.onyx.android.sdk.data.model;

import android.graphics.Bitmap;

import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail.ThumbnailKind;
import com.onyx.android.sdk.data.converter.ThumbKindConverter;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by suicheng on 2016/9/5.
 */
@Table(database = ContentDatabase.class)
public class Thumbnail extends BaseData {

    @Column(name = "_data")
    private String data;

    @Column
    private String path = null;
    @Column
    private String sourceMD5 = null;
    @Column(typeConverter = ThumbKindConverter.class)
    private ThumbnailKind thumbnailKind = ThumbnailKind.Original;

    /**
     * 512x512 at most, or original bmp' size, if it's smaller than 512x512
     *
     * @param bmp
     * @return
     */
    public static Bitmap createLargeThumbnail(Bitmap bmp) {
        return OnyxThumbnail.createLargeThumbnail(bmp);
    }

    /**
     * 256x256 at most, or original bmp' size, if it's smaller than 256x256
     *
     * @param bmp
     * @return
     */
    public static Bitmap createMiddleThumbnail(Bitmap bmp) {
        return OnyxThumbnail.createMiddleThumbnail(bmp);
    }

    /**
     * 128x128 at most, or original bmp' size, if it's smaller than 128x128
     *
     * @param bmp
     * @return
     */
    public static Bitmap createSmallThumbnail(Bitmap bmp) {
        return OnyxThumbnail.createSmallThumbnail(bmp);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSourceMD5() {
        return sourceMD5;
    }

    public void setSourceMD5(String md5) {
        this.sourceMD5 = md5;
    }

    public ThumbnailKind getThumbnailKind() {
        return thumbnailKind;
    }

    public void setThumbnailKind(ThumbnailKind tk) {
        this.thumbnailKind = tk;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
