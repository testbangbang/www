/**
 * 
 */
package com.onyx.android.sdk.data.util;

import java.util.Date;

import android.util.Log;

/**
 * @author joy
 *
 */
public class ProfileUtil
{
    public static class ProfileTimer {
        private Date mTimer = null;
        
        public ProfileTimer()
        {
        }
        
        public void start(String tag, String msg)
        {
            mTimer = new Date();
            Log.v(tag, "TIMING: >>>>>> " + msg + ", " + mTimer.getTime() + "ms");
        }
        
        public void end(String tag, String msg)
        {
            Date t = new Date();
            Log.v(tag, "TIMING: <<<<<< " + msg + ", " + t.getTime() + "ms, " + (t.getTime() - mTimer.getTime()) + "ms");
        }
    }
    
    public static class ProfileAccumulationTimer
    {
        private Date mTimer = null;
        private long mAccumulation = 0;

        public ProfileAccumulationTimer()
        {
        }
        
        public void recordStart()
        {
            mTimer = new Date();
        }
        public void recordEnd()
        {
            Date t = new Date();
            if (mTimer == null) {
                assert(false);
                mTimer = t;
            }
            
            mAccumulation += t.getTime() - mTimer.getTime();
            mTimer = t;
        }
        
        public void logSummary(String tag, String msg)
        {
            Date t = new Date();
            Log.v(tag, "TIMING: <<<<<< " + msg + ", " + t.getTime() + "ms, " + mAccumulation + "ms");
        }
    }
    
    
    private static Date sTimer = null;
    
    public static void log(String tag, String msg)
    {
        Date t = new Date();
        
        StringBuffer sb = new StringBuffer(msg);
        sb.append(", ").append(t.getTime()).append("ms");
        sb.append(", ").append(sTimer == null ? 0 : (t.getTime() - sTimer.getTime())).append("ms");
        
        sTimer = t;
        
        Log.v(tag, sb.toString());
    }
    
    public static void start(String tag, String msg)
    {
        log(tag, "TIMING: >>>>>> " + msg);
    }
    
    public static void end(String tag, String msg)
    {
        log(tag, "TIMING: <<<<<< " + msg);
    }
}
