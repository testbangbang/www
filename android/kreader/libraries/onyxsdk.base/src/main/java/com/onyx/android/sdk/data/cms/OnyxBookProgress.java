/**
 * 
 */
package com.onyx.android.sdk.data.cms;

import android.util.Log;

/**
 * @author joy
 *
 */
public class OnyxBookProgress 
{
	private static final String TAG = "OnyxBookProgress";
	
	// -1 should never be valid DB value
    private static final int INVALID_ID = -1;
	
	private long mId = INVALID_ID;
    private String mMD5 = null;
	private int mCurrent = 0;
    private int mTotal = 1;
    
    private OnyxBookProgress()
    {
    }
    
    /**
     * current start from 1
     * 
     * @param current
     * @param total
     */
    public OnyxBookProgress(int current, int total)
    {
        if (current < 1) {
            //Log.w(TAG, "BookProgress: current must start from 1");
            current = 1;
        }
        mCurrent = current;
        mTotal = total;
    }
    
    public static OnyxBookProgress fromString(String str)
    {
        if (str == null) {
            return null;
        }
        
        String[] array = str.split("/");
        if (array.length != 2) {
            assert(false);
            return new OnyxBookProgress();
        }
        
        try {
            int current = Integer.parseInt(array[0]);
            int total = Integer.parseInt(array[1]);
            return new OnyxBookProgress(current, total);
        }
        catch (Exception e) {
            Log.e(TAG, "exception", e);
        }
        
        return new OnyxBookProgress();
    }
    
    public long getId()
    {
        return mId;
    }

    public void setId(long id)
    {
        this.mId = id;
    }

    public String getMD5()
    {
        return mMD5;
    }

    public void setMD5(String md5)
    {
        this.mMD5 = md5;
    }
    
    public int getCurrent()
    {
        return mCurrent;
    }
    
    public int getTotal()
    {
        return mTotal;
    }

    public boolean isFinished() {
        return mCurrent >= mTotal;
    }
    
    /**
     * in "xxx/yyy" form
     */
    @Override
    public String toString()
    {
        return mCurrent + "/" + mTotal;
    }
}
