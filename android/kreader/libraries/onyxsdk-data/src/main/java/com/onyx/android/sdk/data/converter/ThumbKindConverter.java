package com.onyx.android.sdk.data.converter;

import com.onyx.android.sdk.data.compatability.OnyxThumbnail.ThumbnailKind;
import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by suicheng on 2016/9/5.
 */
public class ThumbKindConverter extends TypeConverter<String, ThumbnailKind> {
    @Override
    public String getDBValue(ThumbnailKind model) {
        return model.toString();
    }

    @Override
    public ThumbnailKind getModelValue(String data) {
        ThumbnailKind tk = ThumbnailKind.Original;
        try {
            tk = Enum.valueOf(ThumbnailKind.class, data);
        } catch (Throwable exception) {
            exception.printStackTrace();
        }
        return tk;
    }
}
