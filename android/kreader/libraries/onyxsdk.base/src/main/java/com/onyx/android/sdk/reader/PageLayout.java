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
public enum PageLayout implements Parcelable
{
    Paged, // hard paged document, like PDF, DJVU
    Flow,;  // reflowable document, like EPUB, TXT

    public static final Parcelable.Creator<PageLayout> CREATOR = new Parcelable.Creator<PageLayout>() {

        @Override
        public PageLayout createFromParcel(Parcel source)
        {
            return PageLayout.values()[source.readInt()];
        }

        @Override
        public PageLayout[] newArray(int size)
        {
            return new PageLayout[size];
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
        dest.writeInt(ordinal());
    }
}
