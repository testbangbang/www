package com.onyx.android.sdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;
import android.util.Base64;

/**
 * Created by lxm on 2017/11/18.
 */
public class Base64ImageParser implements ImageGetter {

    Context context;

    public Base64ImageParser(Context context) {
        this.context = context;
    }

    public Drawable getDrawable(String source) {
        String base_64_source = source.replaceAll("data:image.*base64", "");
        byte[] data = Base64.decode(base_64_source, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Drawable image = new BitmapDrawable(context.getResources(), bitmap);
        image.setBounds(0, 0, 0 + image.getIntrinsicWidth(), 0 + image.getIntrinsicHeight());
        return image;
    }

}