package com.onyx.android.sdk.reader.host.impl;

import android.graphics.RectF;

import com.onyx.android.sdk.reader.api.ReaderRichMedia;

import java.util.List;

/**
 * Created by joy on 6/27/17.
 */

public class ReaderRichMediaImpl implements ReaderRichMedia {

    private MediaType type = MediaType.Audio;
    private String name;
    private RectF rect;
    private byte[] data;

    private ReaderRichMediaImpl(String name, float[] rect, byte[] data) {
        this.name = name;
        this.rect = new RectF(rect[0], rect[1], rect[2], rect[3]);
        this.data = data;
    }

    @Override
    public MediaType getMediaType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RectF getRectangle() {
        return rect;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @SuppressWarnings("unused")
    public static void addToList(List<ReaderRichMedia> list, String fileName, float[] rect, byte[] data) {
        list.add(new ReaderRichMediaImpl(fileName, rect, data));
    }
}
