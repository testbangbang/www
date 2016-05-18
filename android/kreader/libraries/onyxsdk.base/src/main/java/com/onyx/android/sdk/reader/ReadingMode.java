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
public enum ReadingMode implements Parcelable
{
    Page,
    Scroll,
    Reflow,
    Z_Mode,
    W_Mode,
    Auto_Crop;

    public static final Parcelable.Creator<ReadingMode> CREATOR = new Parcelable.Creator<ReadingMode>() {

        @Override
        public ReadingMode createFromParcel(Parcel source)
        {
            return ReadingMode.values()[source.readInt()];
        }

        @Override
        public ReadingMode[] newArray(int size)
        {
            return new ReadingMode[size];
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
