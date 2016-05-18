/**
 * 
 */
package com.onyx.android.sdk.data.cms;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * @author Simon
 *
 */
public class OnyxCmsAggregatedData implements Parcelable 
{
	private static final String TAG = "OnyxCmsAggregatedData";
	
	private OnyxMetadata mBook = null;
	//private OnyxBookProgress mProgress = null;
	private OnyxPosition mPosition = null;
	//private List<OnyxHistoryEntry> mHistoryEntries = null;
	private long mReadTime = 0;
	private List<OnyxBookmark> mBookmarks = null;
	private List<OnyxAnnotation> mAnnotations = null;
	
	public OnyxCmsAggregatedData(OnyxMetadata book, OnyxPosition position, long readTime,
		List<OnyxBookmark> bookmarks, List<OnyxAnnotation> annotations)
	{
		mBook = book;
		mPosition = position;
		mReadTime = readTime;
		mBookmarks = bookmarks;
		mAnnotations = annotations;
	}
	
	public OnyxCmsAggregatedData(Parcel source)
	{
		readFromParcel(source);
	}
	
	public OnyxCmsAggregatedData()
	{
		
	}
	
	/**
	 * 
	 * @return data of the book
	 */
	public OnyxMetadata getBook()
	{
		return mBook;
	}
	
	public void setBook(OnyxMetadata metadata)
	{
		mBook = metadata;
	}
	
	/**
	 * 
	 * @return null standing for nothing
	 */
/*	public OnyxBookProgress getProgress()
	{
		return mProgress;
	}*/
	
	public OnyxPosition getPosition()
	{
		return mPosition;
	}
	
	public void setPosition(OnyxPosition position)
	{
		mPosition = position;
	}
	
	public long getReadTime()
	{
		return mReadTime;
	}
	
	public void setReadTime(long readTime)
	{
		mReadTime = readTime;
	}
	
	/**
	 * 
	 * @return null standing for nothing
	 */
	public List<OnyxBookmark> getBookmarks()
	{
		return mBookmarks;
	}
	
	public void setBookmark(List<OnyxBookmark> bookmarks)
	{
		mBookmarks = bookmarks;
	}
	
	/**
	 * 
	 * @return null standing for nothing
	 */
	public List<OnyxAnnotation> getAnnotations()
	{
		return mAnnotations;
	}
	
	public void setAnnotations(List<OnyxAnnotation> annotations)
	{
		mAnnotations = annotations;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeParcelable(mBook, flags);
		dest.writeParcelable(mPosition, flags);
		dest.writeLong(mReadTime);
		dest.writeTypedList(mBookmarks);
		dest.writeTypedList(mAnnotations);
	}
	
	public void readFromParcel(Parcel source)
	{
		mBook = source.readParcelable(OnyxMetadata.class.getClassLoader());
		mPosition = source.readParcelable(OnyxPosition.class.getClassLoader());
		mReadTime = source.readLong();
		mBookmarks = new LinkedList<OnyxBookmark>();
		source.readTypedList(mBookmarks, OnyxBookmark.CREATOR);
		mAnnotations = new LinkedList<OnyxAnnotation>();
		source.readTypedList(mAnnotations, OnyxAnnotation.CREATOR);
	}
	
	public static final Parcelable.Creator<OnyxCmsAggregatedData> CREATOR 
				= new Parcelable.Creator<OnyxCmsAggregatedData>() 
	{

		@Override
		public OnyxCmsAggregatedData createFromParcel(Parcel source) 
		{
			Log.i(TAG, "Create aggregated data from parcel!");
			return new OnyxCmsAggregatedData(source);
		}

		@Override
		public OnyxCmsAggregatedData[] newArray(int size) 
		{
			return new OnyxCmsAggregatedData[size];
		}
		
	};

	public boolean getAggregatedDataByISBN(Context context, String isbn)
	{
		return getAggregatedDataByISBN(context, context.getPackageName(), isbn);
	}
	
	public boolean getAggregatedDataByISBN(Context context, String application, String isbn)
	{
		OnyxMetadata metadata = new OnyxMetadata();
		metadata.setISBN(isbn);
		if (!OnyxCmsCenter.getMetadata(context, metadata)) {
			return false;
		}
		mBook = metadata;
		
		String md5 = mBook.getMD5();
		
		List<OnyxAnnotation> annotations = new LinkedList<OnyxAnnotation>();
		if (OnyxCmsCenter.getAnnotations(context, application, md5, annotations)) {
			mAnnotations = annotations;
		} else {
			mAnnotations = null;
		}
		
		List<OnyxBookmark> bookmarks = new LinkedList<OnyxBookmark>();
		if (OnyxCmsCenter.getBookmarks(context, application, md5, bookmarks)) {
			mBookmarks = bookmarks;
		} else {
			mBookmarks = null;
		}
		
		long lastUpdateTime = 0;

        String date = metadata.getExtraAttributes().getString(OnyxSyncCenter.TAG_LAST_UPDATE_DATE);
		Date lastUpdate = dateFromString(date);
		if (lastUpdate != null) {
			lastUpdateTime = lastUpdate.getTime();
		}
		
		List<OnyxHistoryEntry> history = OnyxCmsCenter.getHistorysByMD5(context, application, md5);
		if (history != null) {
			for (OnyxHistoryEntry entry : history) {
				long startTime = entry.getStartTime().getTime();
				long endTime = entry.getEndTime().getTime();
				
				if (lastUpdateTime <= startTime) {
					mReadTime += (endTime - startTime);
				} else if (lastUpdateTime < endTime) {
					mReadTime += (endTime - lastUpdateTime);
				}
			}
		}
		
		mReadTime /= 1000;
		
		OnyxPosition position = new OnyxPosition();
		if (OnyxCmsCenter.getPosition(context, application, md5, position)) {
			mPosition = position;
		} else {
			mPosition = null;
		}

		return true;
	}
	
    public static Date dateFromString(String str)
    {
    	if (str == null || "null".equals(str)) {
    		return null;
    	} else {
            try {
            	return new Date(Long.parseLong(str));
            }
            catch (NumberFormatException e) {
                Log.w(TAG, e);
            }
            return null;
    	}
    }


}
