package com.onyx.android.sdk.scribble.data;

import com.onyx.android.sdk.scribble.utils.SerializationUtils;
import com.raizlabs.android.dbflow.converter.TypeConverter;
import com.raizlabs.android.dbflow.data.Blob;

/**
 * Created by zhuzeng on 6/4/16.
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class ConverterTouchPointList extends TypeConverter<Blob, TouchPointList> {


    @Override
    public TouchPointList getModelValue(final Blob blob) {
        return SerializationUtils.pointsFromByteArray(blob.getBlob());
    }

    @Override
    public Blob getDBValue(final TouchPointList list) {
        return new Blob(SerializationUtils.pointsToByteArray(list));
    }


}
