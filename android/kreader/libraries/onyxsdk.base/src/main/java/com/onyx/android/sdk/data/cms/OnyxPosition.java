/**
 * 
 */
package com.onyx.android.sdk.data.cms;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.util.Log;

import com.onyx.android.sdk.data.util.CursorUtil;
import com.onyx.android.sdk.data.util.SerializationUtil;

/**
 * @author joy
 *
 */
public class OnyxPosition implements Parcelable
{

	private static final String TAG = "OnyxPosition";
    
    public static final String DB_TABLE_NAME = "library_position";
    public static final Uri CONTENT_URI = Uri.parse("content://" + OnyxCmsCenter.PROVIDER_AUTHORITY + "/" + DB_TABLE_NAME);
    
    /**
     * TODO. It's better to use key-value based table instead of column based, 
     * since column based table is not sustainable to change
     * 
     * @author joy
     *
     */
    public static class Columns implements BaseColumns
    {
        public static String MD5 = "MD5";
        public static String LOCATION = "Location";
        public static String UPDATE_TIME = "UpdateTime";
        public static String APPLICATION = "Application";
        
        // need read at runtime
        private static boolean sColumnIndexesInitialized = false; 
        private static int sColumnID = -1;
        private static int sColumnMD5 = -1;
        private static int sColumnLocation = -1;
        private static int sColumnUpdateTime = -1;
        private static int sColumnApplication = -1;
        
        public static ContentValues createColumnData(OnyxPosition position)
        {
            ContentValues values = new ContentValues();
            values.put(MD5, position.getMD5());
            values.put(LOCATION, position.getLocation());
            values.put(UPDATE_TIME, SerializationUtil.dateToString(position.getUpdateTime()));
            values.put(APPLICATION, position.getApplication());
            
            return values;
        }
        
        public static void readColumnData(Cursor c, OnyxPosition position)
        {
            if (!sColumnIndexesInitialized) {
                sColumnID = c.getColumnIndex(_ID);
                sColumnMD5 = c.getColumnIndex(MD5);
                sColumnLocation = c.getColumnIndex(LOCATION);
                sColumnUpdateTime = c.getColumnIndex(UPDATE_TIME);
                sColumnApplication = c.getColumnIndex(APPLICATION);
                
                sColumnIndexesInitialized = true;
            }
            
            long id = CursorUtil.getLong(c, sColumnID);
            String md5 = CursorUtil.getString(c, sColumnMD5);
            String location = CursorUtil.getString(c, sColumnLocation);
            String update_time = CursorUtil.getString(c, sColumnUpdateTime);
            String application = CursorUtil.getString(c, sColumnApplication);
            
            position.setId(id);
            position.setMD5(md5);
            position.setLocation(location);
            position.setUpdateTime(SerializationUtil.dateFromString(update_time));
            position.setApplication(application);
        }
        
        public static OnyxPosition readColumnData(Cursor c)
        {
            OnyxPosition a = new OnyxPosition();
            readColumnData(c, a);
            return a;
        }
        
    }
    
    // -1 should never be valid DB value
    private static final int INVALID_ID = -1;
    
    private long mId = INVALID_ID;
    private String mMD5 = null;
    private String mLocation = null;
    private Date mUpdateTime = null;
    private String mApplication = null;
    
    public OnyxPosition()
    {
    }

    public OnyxPosition(Parcel source)
    {
    	readFromParcel(source);
    }
    
    public OnyxPosition(OnyxPosition position)
    {
    	mId = position.getId();
    	mMD5 = position.getMD5();
    	mLocation = position.getLocation();
    	mUpdateTime = position.getUpdateTime();
    	mApplication = position.getApplication();
    }
    
    @Override
	public boolean equals(Object o) {
		if (!(o instanceof OnyxPosition)) {
			return false;
		}
		
		OnyxPosition position = (OnyxPosition) o;
		
		try {
			return ((mMD5 == position.getMD5() || mMD5.equals(position.getMD5()))
					&& (mLocation == position.getLocation() || mLocation.equals(position.getLocation()))
					&& (mUpdateTime == position.getUpdateTime() || mUpdateTime.equals(position.getUpdateTime()))
//					&& (mApplication == position.getApplication() || mApplication.equals(position.getApplication()))
					);
		} catch (Exception e) {
			return false;
		}
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
    
    public String getLocation()
    {
        return mLocation;
    }
    
    public void setLocation(String location)
    {
        mLocation = location;
    }

    public Date getUpdateTime()
    {
        return mUpdateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.mUpdateTime = updateTime;
    }

    public String getApplication()
    {
    	return mApplication;
    }
    
    public void setApplication(String application)
    {
    	mApplication = application;
    }
    
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mId);
		dest.writeString(mMD5);
		dest.writeString(mLocation);
		dest.writeString(SerializationUtil.dateToString(mUpdateTime));
		dest.writeString(mApplication);
	}
	
	public void readFromParcel(Parcel source) {
    	mId = source.readLong();
    	mMD5 = source.readString();
    	mLocation = source.readString();
		mUpdateTime = SerializationUtil.dateFromString(source.readString());
		mApplication = source.readString();
	}
	
	public static final Parcelable.Creator<OnyxPosition> CREATOR 
			= new Parcelable.Creator<OnyxPosition>() 
	{
		
		@Override
		public OnyxPosition createFromParcel(Parcel source) 
		{
			Log.i(TAG, "Create position from parcel!");
			return new OnyxPosition(source);
		}
		
		@Override
		public OnyxPosition[] newArray(int size) 
		{
			return new OnyxPosition[size];
		}
	
	};

}
