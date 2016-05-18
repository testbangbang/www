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
public enum ZoomSetting implements Parcelable
{
    To_Page, 
    To_Width,
    To_Height,
    To_ContentWidth,
    To_Selection, //To_Selection is actually zoom to the width of the selection
    By_Value;

    public static final Parcelable.Creator<ZoomSetting> CREATOR = new Parcelable.Creator<ZoomSetting>() {

        @Override
        public ZoomSetting createFromParcel(Parcel source)
        {
            return ZoomSetting.values()[source.readInt()];
        }

        @Override
        public ZoomSetting[] newArray(int size)
        {
            return new ZoomSetting[size];
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
