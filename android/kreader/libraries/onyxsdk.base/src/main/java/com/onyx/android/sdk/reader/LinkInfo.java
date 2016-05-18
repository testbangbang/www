/**
 * 
 */
package com.onyx.android.sdk.reader;

/**
 * @author joy
 *
 */
public class LinkInfo
{
    private boolean mIsInternalLink = true;
    private String mTarget = null;
    private TextSelection mSelection = null;
    
    public LinkInfo(boolean isInternal, String target, TextSelection selection)
    {
        mIsInternalLink = isInternal;
        mTarget = target;
        mSelection = selection;
    }
    
    public boolean isInternalLink()
    {
        return mIsInternalLink;
    }
    public String getTarget()
    {
        return mTarget;
    }
    public TextSelection getSelection()
    {
        return mSelection;
    }
}
