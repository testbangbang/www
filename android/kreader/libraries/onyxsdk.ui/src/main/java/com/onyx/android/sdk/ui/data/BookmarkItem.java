/**
 * 
 */
package com.onyx.android.sdk.ui.data;

/**
 * @author dxwts
 *
 */
public class BookmarkItem
{
    private String mTitle = null;
    private Object mTag = null;
    
    public BookmarkItem(String title, Object tag)
    {
        mTitle = title;
        mTag = tag;
    }
    
    public String getTitle()
    {
        return mTitle;
    }
    
    public Object getTag()
    {
        return mTag;
    }

}
