package com.onyx.android.sdk.ui.dialog.data;

import com.onyx.android.sdk.ui.data.DirectoryItem;

public class AnnotationItem extends DirectoryItem
{
    public enum TitleType{note, quote}
    private TitleType mType = null;

    public AnnotationItem(String title, String page,Object tag, TitleType type)
    {
        super(title, page, tag);
        mType = type;
    }

    public AnnotationItem(String title, int page,Object tag, TitleType type)
    {
        super(title, page, tag);
        mType = type;
    }

    public TitleType getType()
    {
        return mType;
    }
}
