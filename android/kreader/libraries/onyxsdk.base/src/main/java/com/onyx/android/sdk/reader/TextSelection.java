/**
 * 
 */
package com.onyx.android.sdk.reader;

import java.util.List;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author joy
 *
 */
public class TextSelection implements Parcelable
{
    private List<Rect> mBoxes = null;
    private String mText = null;
    private String mSelectionBegin = null;
    private String mSelectionEnd = null;

    public TextSelection(List<Rect> boxes, String text, String selectionBegin, String selectionEnd)
    {
        mBoxes = boxes;
        mText = text;
        mSelectionBegin = selectionBegin;
        mSelectionEnd = selectionEnd;
    }
    public TextSelection(Parcel p)
    {
        p.readList(mBoxes, null);
        mText = p.readString();
        mSelectionBegin = p.readString();
        mSelectionEnd = p.readString();
    }

    public List<Rect> getBoxes()
    {
        return mBoxes;
    }

    public String getText()
    {
        return mText;
    }

    public String getSelectionBegin()
    {
        return mSelectionBegin;
    }
    public void setSelectionBegin(String loc)
    {
        mSelectionBegin = loc;
    }

    public String getSelectionEnd()
    {
        return mSelectionEnd;
    }
    public void setSelectionEnd(String loc)
    {
        mSelectionEnd = loc;
    }
    
    public boolean hitTest(int x, int y)
    {
        if (mBoxes == null) {
            return false;
        }
        
        for (Rect r : mBoxes) {
            if (r.contains(x, y)) {
                return true;
            }
        }
        
        return false;
    }

    public static final Parcelable.Creator<TextSelection> CREATOR = new Parcelable.Creator<TextSelection>() {

        @Override
        public TextSelection createFromParcel(Parcel source)
        {
            return new TextSelection(source);
        }

        @Override
        public TextSelection[] newArray(int size)
        {
            return new TextSelection[size];
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
        dest.writeList(mBoxes);
        dest.writeString(mText);
        dest.writeString(mSelectionBegin);
        dest.writeString(mSelectionEnd);
    }
}
