/**
 * 
 */
package com.onyx.android.sdk.reader;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * @author joy
 *
 */
public class RenderParameter implements Cloneable, Parcelable
{
    private ReadingMode mReadingMode = ReadingMode.Page;

    private double mPagePosition = 0;
    private String mDocLocation = null;

    private ZoomSetting mZoomSetting = ZoomSetting.To_Width;
    /**
     * selection rectangle of doc page in original size
     */
    private Rect mZoomSelection = new Rect();
    private double mZoomFactor = 1.0;

    private double mFontSize = 1.0;
    private boolean mIsGlyphEmbolden = false;

    private Size mViewportSize = new Size();
    /**
     * Scroll after zooming, indicates page position relative to the viewport
     */
    private Point mPageScroll = new Point();

    
    public RenderParameter()
    {
    }
    
    public RenderParameter(Parcel p)
    {
        this.readFromParcel(p);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }

        if (!(o instanceof RenderParameter)) {
            return false;
        }
        
        RenderParameter p = (RenderParameter)o;
        return mReadingMode == p.mReadingMode &&
                mPagePosition == p.mPagePosition &&
                (mDocLocation == null ? p.mDocLocation == null : mDocLocation.equals(p.mDocLocation)) &&
                mZoomSetting == p.mZoomSetting &&
                mZoomFactor == p.mZoomFactor &&
                mFontSize == p.mFontSize &&
                mIsGlyphEmbolden == p.mIsGlyphEmbolden &&
                mViewportSize.equals(p.mViewportSize) &&
                mPageScroll.equals(p.mPageScroll);
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("ReadingMode: ").append(String.valueOf(mReadingMode));
        sb.append(", PagePosition: ").append(String.valueOf(mPagePosition));
        sb.append(", DocLoation: ").append(String.valueOf(mDocLocation));
        sb.append(", ZoomSetting: ").append(String.valueOf(mZoomSetting));
        sb.append(", ZoomSelection: ").append(String.valueOf(mZoomSelection));
        sb.append(", ZoomFactor: ").append(String.valueOf(mZoomFactor));
        sb.append(", FontSize: ").append(String.valueOf(mFontSize));
        sb.append(", IsGlyphEmbolden: ").append(String.valueOf(mIsGlyphEmbolden));
        sb.append(", ViewportSize: ").append(String.valueOf(mViewportSize));
        sb.append(", PageScroll: ").append(String.valueOf(mPageScroll));
        sb.append("]");

        return sb.toString();
    }
    
    @Override
    public Object clone()
    {
        RenderParameter p = new RenderParameter();
        p.mReadingMode = mReadingMode;
        p.mPagePosition = mPagePosition;
        p.mDocLocation = mDocLocation;
        p.mZoomSetting = mZoomSetting;
        p.mZoomSelection.set(mZoomSelection);
        p.mZoomFactor = mZoomFactor;
        p.mFontSize = mFontSize;
        p.mIsGlyphEmbolden = mIsGlyphEmbolden;
        p.mViewportSize.set(mViewportSize);
        p.mPageScroll.set(mPageScroll.x, mPageScroll.y);
        
        return p;
    }
    
    public void copyFrom(RenderParameter p)
    {
        mReadingMode = p.mReadingMode;
        mPagePosition = p.mPagePosition;
        mDocLocation = p.mDocLocation;
        mZoomSetting = p.mZoomSetting;
        mZoomSelection.set(p.mZoomSelection);
        mZoomFactor = p.mZoomFactor;
        mFontSize = p.mFontSize;
        mIsGlyphEmbolden = p.mIsGlyphEmbolden;
        mViewportSize.set(p.mViewportSize);
        mPageScroll.set(p.mPageScroll.x, p.mPageScroll.y);
    }
    
    public ReadingMode getReadingMode()
    {
        return mReadingMode;
    }
    public void setReadingMode(ReadingMode mode)
    {
        mReadingMode = mode;
    }
    
    public double getPagePosition()
    {
        return mPagePosition;
    }
    public void setPagePosition(double pos)
    {
        mPagePosition = pos;
    }
    
    public String getDocLocation()
    {
        return mDocLocation;
    }
    public void setDocLocation(String location)
    {
        mDocLocation = location;
    }
    
    public ZoomSetting getZoomSetting()
    {
        return mZoomSetting;
    }
    
    public void setZoomSetting(ZoomSetting zoomSetting)
    {
        mZoomSetting = zoomSetting;
    }
    
    public Rect getZoomSelection()
    {
        return mZoomSelection;
    }
    
    public void setZoomSelection(Rect rect)
    {
        mZoomSelection = rect;
    }
    
    public double getZoomFactor()
    {
        return mZoomFactor;
    }
    
    public void setZoomFactor(double value)
    {
        mZoomFactor = value;
    }
    
    /**
     * Zoom factor is tightly coupled with zoom setting
     * 
     * @param zoomSetting
     * @param zoomValue
     */
    public void setZoomFactor(ZoomSetting zoomSetting, double zoomValue)
    {
        mZoomSetting = zoomSetting;
        mZoomFactor = zoomValue;
        if (Double.isInfinite(zoomValue) || Double.isNaN(zoomValue)) {
            Log.d("FFFFFF", "invalid zoom value");
        }
    }
    
    public double getFontSize()
    {
        return mFontSize;
    }
    public void setFontSize(double size)
    {
        mFontSize = size;
    }
    
    public boolean isGlyphEmbolden()
    {
        return mIsGlyphEmbolden;
    }
    public void setIsGlyphEmbolden(boolean isGlyphEmbolden)
    {
        mIsGlyphEmbolden = isGlyphEmbolden;
    }
    
    public Point getPageScroll()
    {
        return mPageScroll;
    }
    public void setPageScroll(int x, int y)
    {
        mPageScroll.set(x, y);
    }
    public void setPageScrollX(int x)
    {
        mPageScroll.x = x;
    }
    public void setPageScrollY(int y)
    {
        mPageScroll.y = y;
    }
    
    public Size getViewpotSize()
    {
        return mViewportSize;
    }
    public void setViewportSize(int w, int h)
    {
        mViewportSize.set(w, h);
    }
    
    public static final Parcelable.Creator<RenderParameter> CREATOR = new Parcelable.Creator<RenderParameter>() {

        @Override
        public RenderParameter createFromParcel(Parcel source)
        {
            return new RenderParameter(source);
        }

        @Override
        public RenderParameter[] newArray(int size)
        {
            return new RenderParameter[size];
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
        dest.writeParcelable(mReadingMode, 0);
        dest.writeDouble(mPagePosition);
        dest.writeString(mDocLocation);
        dest.writeParcelable(mZoomSetting, 0);
        dest.writeParcelable(mZoomSelection, 0);
        dest.writeDouble(mZoomFactor);
        dest.writeDouble(mFontSize);
        dest.writeInt(mIsGlyphEmbolden ? 1 : 0);
        dest.writeParcelable(mViewportSize, 0);
        dest.writeInt(mPageScroll.x);
        dest.writeInt(mPageScroll.y);
    }
    
    public void readFromParcel(Parcel p)
    {
        mReadingMode = p.readParcelable(ReadingMode.class.getClassLoader());
        mPagePosition = p.readDouble();
        mDocLocation = p.readString();
        mZoomSetting = p.readParcelable(ZoomSetting.class.getClassLoader());
        mZoomSelection = p.readParcelable(Rect.class.getClassLoader());
        mZoomFactor = p.readDouble();
        mFontSize = p.readDouble();
        
        int is_embolden = p.readInt();
        mIsGlyphEmbolden = is_embolden != 0 ? true : false;

        mViewportSize = p.readParcelable(Size.class.getClassLoader());

        int scroll_x = p.readInt();
        int scroll_y = p.readInt();
        mPageScroll = new Point(scroll_x, scroll_y);
    }
    
}
