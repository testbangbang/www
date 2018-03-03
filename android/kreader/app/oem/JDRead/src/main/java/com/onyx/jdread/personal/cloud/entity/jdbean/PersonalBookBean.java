package com.onyx.jdread.personal.cloud.entity.jdbean;

import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.model.Metadata;

/**
 * Created by li on 2018/3/3.
 */

public class PersonalBookBean {
    public Metadata metadata;
    public CloseableReference<Bitmap> bitmap;
}
