/**
 * 
 */
package com.onyx.android.sdk.reader;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author joy
 *
 */
public class TOCItem implements Parcelable
{
    private String mTitle = null;
    private String mPage = null;
    private Object mTag = null;
    
    public TOCItem(String title, int page, Object tag)
    {
        mTitle = title;
        mPage = String.valueOf(page);
        mTag = tag;
    }
    public TOCItem(String title, String page, Object tag)
    {
        mTitle = title;
        mPage = page;
        mTag = tag;
    }
    public TOCItem(Parcel p)
    {
        mTitle = p.readString();
        mPage = p.readString();
        mTag = p.readValue(null);
    }
    
    public String getTitle()
    {
        return mTitle;
    }
    
    public String getPage()
    {
        return mPage;
    }
    
    public Object getTag()
    {
        return mTag;
    }
    
    public static final Parcelable.Creator<TOCItem> CREATOR = new Parcelable.Creator<TOCItem>() {

        @Override
        public TOCItem createFromParcel(Parcel source)
        {
            return new TOCItem(source);
        }

        @Override
        public TOCItem[] newArray(int size)
        {
            return new TOCItem[size];
        }
    };
    
    @Override
    public int describeContents()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mTitle);
        dest.writeString(mPage);
        dest.writeValue(mTag);
    }
}
