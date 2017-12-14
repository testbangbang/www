package com.onyx.jdread.shop.common;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.onyx.jdread.JDReadApplication;

/**
 * Created by huxiaomao on 2017/1/14.
 */

public class ManageImageCache {
    public static void loadUrl(final String url, final ImageView imageView, final int defaultImage) {
        Glide.with(JDReadApplication.getInstance().getApplicationContext()).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defaultImage).into(imageView);
    }

    public static void loadUrl(final int resourceID, final ImageView imageView, final int defaultImage) {
        Glide.with(JDReadApplication.getInstance().getApplicationContext()).load(resourceID).asBitmap().fitCenter().into(imageView);
    }

    public static void loadUrl(final String url, final View view, final int defaultImage) {
        Glide.with(JDReadApplication.getInstance().getApplicationContext()).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defaultImage).centerCrop().into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                view.setBackgroundDrawable(resource);
            }
        });
    }

    public static void loadUrlNotCrop(final String url, final ImageView imageView, final int defaultImage) {
        Glide.with(JDReadApplication.getInstance().getApplicationContext()).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defaultImage).into(imageView);
    }
}
