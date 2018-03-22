package com.onyx.jdread.shop.cloud.entity.jdbean;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;

/**
 * Created by hehai on 17-3-31.
 */

public class ResultBookBean extends BaseObservable{
    public final ObservableField<CloseableReference<Bitmap>> coverBitmap = new ObservableField<>();
    public final ObservableInt coverDefault = new ObservableInt();
    public int ebook_id;
    public String name;
    public String author;
    public String image_url;
    public String large_image_url;
    public String info;
    public String bookType;
    public String format;
    public float file_size;
}
