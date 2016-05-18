/**
 * 
 */
package com.onyx.android.sdk.ui.data;

/**
 * @author qingyue
 *
 */
public class DirectoryItem  implements Comparable<DirectoryItem>
{
    private String mTitle = null;
    private String mPage = null;
    private Object mTag = null;
    
    public DirectoryItem(String title, int page, Object tag)
    {
        mTitle = title;
        mPage = String.valueOf(page);
        mTag = tag;
    }
    public DirectoryItem(String title, String page, Object tag)
    {
        mTitle = title;
        mPage = page;
        mTag = tag;
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

    public int compareTo(DirectoryItem arg0) {
        int v1 = Integer.parseInt(mPage);
        int v2 = Integer.parseInt(arg0.getPage());
        return (v1 - v2);
    }
}
