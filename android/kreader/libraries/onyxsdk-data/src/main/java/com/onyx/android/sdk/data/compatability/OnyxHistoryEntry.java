package com.onyx.android.sdk.data.compatability;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import java.io.Serializable;
import java.util.Date;

public class OnyxHistoryEntry implements Serializable, Parcelable
{
	private static final String TAG = "OnyxHistoryEntry";
	
    public static final String DB_TABLE_NAME = "library_history";
    private static final long serialVersionUID = 1L;
    public static final Uri CONTENT_URI = Uri.parse("content://" + OnyxCmsCenter.PROVIDER_AUTHORITY + "/" + DB_TABLE_NAME);
    
    /**
     * only store reading history longer than the threshold 5 * 60s.
     * considering being configured by user if necessary 
     */
    public static int HISTORY_THRESHOLD = 300;
    
    public static class Columns implements BaseColumns 
    {
        public static String MD5 = "MD5";
        public static final String START_TIME = "StartTime";
        public static final String END_TIME = "EndTime";
        public static final String PROGRESS = "Progress";
        public static final String APPLICATION = "Application";
        public static String EXTRA_ATTRIBUTES = "ExtraAttributes";

        // need read at runtime
        private static boolean sColumnIndexesInitialized = false;
        private static int sColumnID = -1;
        private static int sColumnMD5 = -1;
        private static int sColumnStartTime = -1;
        private static int sColumnEndTime = -1;
        private static int sColumnProgress = -1;
        private static int sColumnApplication = -1;
        private static int sColumnExtraAttributes = -1;
        
        public static ContentValues createColumnData(OnyxHistoryEntry entry)
        {
            ContentValues values = new ContentValues();
            values.put(MD5, entry.getMD5());
            values.put(START_TIME, entry.getStartTime() == null ? 0 : entry.getStartTime().getTime());
            values.put(END_TIME, entry.getEndTime() == null ? 0 : entry.getEndTime().getTime());
            values.put(PROGRESS, entry.getProgress() == null ? "" : entry.getProgress().toString());
            values.put(APPLICATION, entry.getApplication());

            return values;
        }
        
        public static OnyxHistoryEntry readColumnsData(Cursor c)
        {
            if (!sColumnIndexesInitialized) {
                sColumnID = c.getColumnIndex(_ID);
                sColumnMD5 = c.getColumnIndex(MD5);
                sColumnStartTime = c.getColumnIndex(START_TIME);
                sColumnEndTime = c.getColumnIndex(END_TIME);
                sColumnProgress = c.getColumnIndex(PROGRESS);
                sColumnApplication = c.getColumnIndex(APPLICATION);
                sColumnExtraAttributes = c.getColumnIndex(EXTRA_ATTRIBUTES);
                
                sColumnIndexesInitialized = true;
            }

            long id = c.getLong(sColumnID);
            String md5 = CursorUtil.getString(c, sColumnMD5);
            Long start_time = CursorUtil.getLong(c, sColumnStartTime);
            Long end_time = CursorUtil.getLong(c, sColumnEndTime);
            OnyxBookProgress progress = OnyxBookProgress.fromString(CursorUtil.getString(c, sColumnProgress));
            String application = CursorUtil.getString(c, sColumnApplication);
            String extra_attributes = CursorUtil.getString(c, sColumnExtraAttributes);
            
            OnyxHistoryEntry entry = new OnyxHistoryEntry();
            entry.setId(id);
            entry.setMD5(md5);
            entry.setStartTime(new Date(start_time == null ? 0 : start_time));
            entry.setEndTime(new Date(end_time == null ? 0 : end_time));
            entry.setProgress(progress);
            entry.setApplication(application);
            entry.setExtraAttributes(extra_attributes);

            return entry;
        }
    }
    
    private long mId = -1;
    private String mMD5 = null;
    private Date mStartTime = null;
    private Date mEndTime = null;
    private OnyxBookProgress mProgress = null;
    private String mApplication = null;
    
    public OnyxHistoryEntry()
    {
    	
    }
    
    public OnyxHistoryEntry(Parcel source)
    {
    	readFromParcel(source);
    }
    
    /**
     * Additional attributes for flexibility
     */
    private String mExtraAttributes = null; 
    
    public long getId()
    {
        return mId;
    }
    public void setId(long id)
    {
        mId = id;
    }
    public String getMD5()
    {
        return mMD5;
    }
    public void setMD5(String md5)
    {
        this.mMD5 = md5;
    }
    public Date getStartTime()
    {
        return mStartTime;
    }
    public void setStartTime(Date time)
    {
        this.mStartTime = time;
    }
    public Date getEndTime()
    {
        return mEndTime;
    }
    public void setEndTime(Date time)
    {
        this.mEndTime = time;
    }
    /**
     * may return null
     * 
     * @return
     */
    public OnyxBookProgress getProgress()
    {
        return mProgress;
    }
    public void setProgress(OnyxBookProgress progress)
    {
        this.mProgress = progress;
    }
    
    public String getApplication()
    {
    	return mApplication;
    }
    
    public void setApplication(String application)
    {
    	mApplication = application;
    }
    
    /**
     * may return null
     * 
     * @return
     */
    public String getExtraAttributes()
    {
        return mExtraAttributes;
    }
    public void setExtraAttributes(String extraAttributes)
    {
        this.mExtraAttributes = extraAttributes;
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
	    dest.writeString(SerializationUtil.dateToString(mStartTime));
	    dest.writeString(SerializationUtil.dateToString(mEndTime));
	    dest.writeString(mApplication);
	}
	
	public void readFromParcel(Parcel source)
	{
    	mId = source.readLong();
    	mMD5 = source.readString();
    	mStartTime = SerializationUtil.dateFromString(source.readString());
    	mEndTime = SerializationUtil.dateFromString(source.readString());
    	mApplication = source.readString();
	}
	
	public static final Creator<OnyxHistoryEntry> CREATOR
		= new Creator<OnyxHistoryEntry>()
	{
		
		@Override
		public OnyxHistoryEntry createFromParcel(Parcel source)
		{
			return new OnyxHistoryEntry(source);
		}
		
		@Override
		public OnyxHistoryEntry[] newArray(int size) 
		{
			return new OnyxHistoryEntry[size];
		}

	};


}
