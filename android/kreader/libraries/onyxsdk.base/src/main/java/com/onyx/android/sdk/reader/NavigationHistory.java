/**
 * 
 */
package com.onyx.android.sdk.reader;

import java.util.ArrayList;

/**
 * @author joy
 *
 */
public class NavigationHistory
{
    private ArrayList<String> mHistories = new ArrayList<String>();
    private int mCurrentIndex = -1;
    
    public NavigationHistory()
    {
    }
    
    public boolean isNewLocation(String loc)
    {
        if (mHistories.isEmpty()) {
            return true;
        }

        return loc.compareToIgnoreCase(mHistories.get(mCurrentIndex)) != 0;
    }
    
    public void insertNewLocation(String loc)
    {
        if (mHistories.isEmpty()) {
            mHistories.add(loc);
            mCurrentIndex = 0;
            return;
        }
        else {
            assert(mCurrentIndex >= 0);
            while (mHistories.size() > (mCurrentIndex + 1)) {
                mHistories.remove(mHistories.size() - 1);
            }
            mHistories.add(loc);
            mCurrentIndex++;
        }
    }
    
    public boolean canPreviousNavigation()
    {
        return mHistories.size() >= 2 && mCurrentIndex > 0;
    }
    /**
     * be sure canPreviousNavigation() first, or will throw exception
     * @return
     */
    public String previousNavigation()
    {
        if (!this.canPreviousNavigation()) {
            throw new IllegalAccessError();
        }

        mCurrentIndex--;
        return mHistories.get(mCurrentIndex);
    }
    
    public boolean canNextNavigation()
    {
        return mHistories.size() >= 2 && mCurrentIndex < (mHistories.size() - 1);
    }
    public String nextNavigation()
    {
        if (!this.canNextNavigation()) {
            throw new IllegalAccessError();
        }

        mCurrentIndex++;
        return mHistories.get(mCurrentIndex);
    }
}
