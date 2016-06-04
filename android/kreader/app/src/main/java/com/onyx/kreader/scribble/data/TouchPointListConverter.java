package com.onyx.kreader.scribble.data;

import com.raizlabs.android.dbflow.converter.TypeConverter;
import com.raizlabs.android.dbflow.data.Blob;
import org.nustaq.serialization.FSTConfiguration;

import java.util.List;

/**
 * Created by zhuzeng on 6/4/16.
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class TouchPointListConverter extends TypeConverter<Blob, TouchPointList> {

    static FSTConfiguration singletonConf = FSTConfiguration.createDefaultConfiguration();

    @Override
    public TouchPointList getModelValue(final Blob blob) {
        return (TouchPointList)singletonConf.asObject(blob.getBlob());
    }

    @Override
    public Blob getDBValue(final TouchPointList list) {
        return new Blob(singletonConf.asByteArray(list));
    }
}
