/**
 * 
 */
package com.onyx.android.sdk.ui.data;


/**
 * @author qingyue
 *
 */
public class FirstLetterItemData
{
    private boolean mIsEnabled = false;
    private String mText = null;

    public FirstLetterItemData(String letter, boolean enabled)
    {
        mText = letter;
        mIsEnabled = enabled;
    }

    public FirstLetterItemData(String letter)
    {
        this(letter, false);        
    }

    public boolean getIsEnabled()
    {
        return mIsEnabled;
    }

    public void setEnabled(boolean enabled)
    {
        mIsEnabled = enabled;
    }

    public String getText()
    {
        return mText;
    }
}
