/**
 * 
 */
package com.onyx.android.sdk.data.cms;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.onyx.android.sdk.data.sys.OnyxSysCenter;
import com.onyx.android.sdk.data.util.ProfileUtil;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * @author Simon
 *
 */
public class OnyxSyncCenter {

	private static final String TAG = "OnyxSyncCenter";
	public static final String PROVIDER_AUTHORITY = "com.onyx.android.sdk.OnyxSyncProvider";
	
    public static final Uri METADATA_CONTENT_URI = Uri.parse("content://" + PROVIDER_AUTHORITY + "/" + OnyxMetadata.DB_TABLE_NAME);
    public static final Uri POSITION_CONTENT_URI = Uri.parse("content://" + PROVIDER_AUTHORITY + "/" + OnyxPosition.DB_TABLE_NAME);
    public static final Uri BOOKMARK_CONTENT_URI = Uri.parse("content://" + PROVIDER_AUTHORITY + "/" + OnyxBookmark.DB_TABLE_NAME);
    public static final Uri ANNOTATION_CONTENT_URI = Uri.parse("content://" + PROVIDER_AUTHORITY + "/" + OnyxAnnotation.DB_TABLE_NAME);
	public static final Uri HISTORY_ENTRY_CONTENT_URI = Uri.parse("content://" + PROVIDER_AUTHORITY + "/" + OnyxHistoryEntry.DB_TABLE_NAME);

    public static final String TAG_LAST_UPDATE_DATE = "OnyxSyncCenter.last_update_date";
    
	public static boolean getBookmarks(Context context, String md5, List<OnyxBookmark> result)
	{
        Cursor c = null;
        try {
            ProfileUtil.start(TAG, "query bookmarks");
            c = context.getContentResolver().query(BOOKMARK_CONTENT_URI,
                    null,
                    OnyxBookmark.Columns.MD5 + "='" + md5 + "'", 
                    null, null);
            ProfileUtil.end(TAG, "query bookmarks");

            if (c == null) {
                Log.d(TAG, "query database failed");
                return false;
            }

            ProfileUtil.start(TAG, "read db result");
            readBookmarkCursor(c, result);
            ProfileUtil.end(TAG, "read db result");
            
            Log.d(TAG, "items loaded, count: " + result.size());
            
            return true;
        } finally {
            if (c != null) {
                c.close();
            }
        }
	}
	
    public static boolean insertBookmark(Context context, OnyxBookmark bookmark)
    {
        Uri result = context.getContentResolver().insert(
                BOOKMARK_CONTENT_URI,
                OnyxBookmark.Columns.createColumnData(bookmark));
        if (result == null) {
            return false;
        }

        String id = result.getLastPathSegment();
        if (id == null) {
            return false;
        }

        bookmark.setId(Long.parseLong(id));

        return true;
    }
    
    private static void readBookmarkCursor(Cursor c,
            Collection<OnyxBookmark> result)
    {
        if (c.moveToFirst()) {
            result.add(OnyxBookmark.Columns.readColumnData(c));

            while (c.moveToNext()) {
                if (Thread.interrupted()) {
                    return;
                }

                result.add(OnyxBookmark.Columns.readColumnData(c));
            }
        }
    }
    
	public static boolean getAnnotations(Context context, String md5, List<OnyxAnnotation> result)
	{
        Cursor c = null;
        try {
            ProfileUtil.start(TAG, "query annotations");
            c = context.getContentResolver().query(ANNOTATION_CONTENT_URI,
                    null, 
                    OnyxAnnotation.Columns.MD5 + "='" + md5 + "'", 
                    null, null);
            ProfileUtil.end(TAG, "query annotations");

            if (c == null) {
                Log.d(TAG, "query database failed");
                return false;
            }

            ProfileUtil.start(TAG, "read db result");
            readAnnotationCursor(c, result);
            ProfileUtil.end(TAG, "read db result");
            
            Log.d(TAG, "items loaded, count: " + result.size());
            
            return true;
        } finally {
            if (c != null) {
                c.close();
            }
        }
	}
	
    public static boolean insertAnnotation(Context context, OnyxAnnotation annotation)
    {
        Uri result = context.getContentResolver().insert(
                ANNOTATION_CONTENT_URI,
                OnyxAnnotation.Columns.createColumnData(annotation));
        if (result == null) {
            return false;
        }

        String id = result.getLastPathSegment();
        if (id == null) {
            return false;
        }

        annotation.setId(Long.parseLong(id));

        return true;
    }

    private static void readAnnotationCursor(Cursor c,
            Collection<OnyxAnnotation> result)
    {
        if (c.moveToFirst()) {
            result.add(OnyxAnnotation.Columns.readColumnData(c));

            while (c.moveToNext()) {
                if (Thread.interrupted()) {
                    return;
                }

                result.add(OnyxAnnotation.Columns.readColumnData(c));
            }
        }
    }
    
	public static boolean getPosition(Context context, String md5, OnyxPosition result)
	{
        Cursor c = null;
        try {
            ProfileUtil.start(TAG, "query annotations");
            c = context.getContentResolver().query(POSITION_CONTENT_URI, 
                    null, 
                    OnyxPosition.Columns.MD5 + "='" + md5 + "'", 
                    null, null);
            ProfileUtil.end(TAG, "query annotations");

            if (c == null) {
                Log.d(TAG, "query database failed");
                return false;
            }

            if (c.moveToFirst()) {
                OnyxPosition.Columns.readColumnData(c, result);
                return true;
            } else {
            	return false;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
	}

    private static void readPositionCursor(Cursor c,
            Collection<OnyxPosition> result)
    {
        if (c.moveToFirst()) {
            result.add(OnyxPosition.Columns.readColumnData(c));

            while (c.moveToNext()) {
                if (Thread.interrupted()) {
                    return;
                }

                result.add(OnyxPosition.Columns.readColumnData(c));
            }
        }
    }
	
	public static boolean insertPosition(Context context, OnyxPosition location)
	{
        Uri result = context.getContentResolver().insert(
                POSITION_CONTENT_URI,
                OnyxPosition.Columns.createColumnData(location));
        if (result == null) {
            return false;
        }

        String id = result.getLastPathSegment();
        if (id == null) {
            return false;
        }

        location.setId(Long.parseLong(id));

        return true;
	}

	/**
     * reading data from DB to metadata, old data in metadata will be overwritten 
     * 
     * @param context
     * @param data
     * @return
     */
    public static boolean getMetadata(Context context, OnyxMetadata data)
    {
        Cursor c = null;
        try {
        	if (data.getISBN() != null) {
                c = context.getContentResolver().query(METADATA_CONTENT_URI,
                        null,
                        OnyxMetadata.Columns.ISBN + "=?",
                        new String[] { data.getISBN() }, null);
        	} else {
                c = context.getContentResolver().query(METADATA_CONTENT_URI,
                        null,
                        OnyxMetadata.Columns.NATIVE_ABSOLUTE_PATH + "=?" + " AND " +
                                OnyxMetadata.Columns.SIZE + "=" + data.getSize() + " AND " +
                                OnyxMetadata.Columns.LAST_MODIFIED + "=" + data.getLastModified().getTime(),
                        new String[] { data.getNativeAbsolutePath() }, null);
        	}
            if (c == null) {
                Log.d(TAG, "query database failed");
                return false;
            }
            if (c.moveToFirst()) {
                OnyxMetadata.Columns.readColumnData(c, data);
                return true;
            }

            return false;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
    
    public static boolean insertMetadata(Context context, OnyxMetadata data)
    {
        Log.d(TAG, "insert metadata: " + data.getNativeAbsolutePath());
        
        int n = context.getContentResolver().delete(METADATA_CONTENT_URI,
                OnyxMetadata.Columns.NATIVE_ABSOLUTE_PATH + "=?",
                new String[] { data.getNativeAbsolutePath() });
        if (n > 0) {
            Log.w(TAG, "delete obsolete metadata: " + n);
        }
        
        Uri result = context.getContentResolver().insert(
                METADATA_CONTENT_URI,
                OnyxMetadata.Columns.createColumnData(data));
        if (result == null) {
            return false;
        }

        String id = result.getLastPathSegment();
        if (id == null) {
            return false;
        }

        data.setId(Long.parseLong(id));

        return true;
    }
    
	public static boolean getHistoryEntries(Context context, String md5, List<OnyxHistoryEntry> result)
	{
        Cursor c = null;
        try {
            ProfileUtil.start(TAG, "query historyEntries");
            c = context.getContentResolver().query(HISTORY_ENTRY_CONTENT_URI,
                    null,
                    OnyxHistoryEntry.Columns.MD5 + "='" + md5 + "'", 
                    null, null);
            ProfileUtil.end(TAG, "query historyEntries");

            if (c == null) {
                Log.d(TAG, "query database failed");
                return false;
            }

            ProfileUtil.start(TAG, "read db result");
            readHistoryEntryCursor(c, result);
            ProfileUtil.end(TAG, "read db result");
            
            Log.d(TAG, "items loaded, count: " + result.size());
            
            return true;
        } finally {
            if (c != null) {
                c.close();
            }
        }
	}
	
    public static boolean insertHistoryEntry(Context context, OnyxHistoryEntry historyEntry)
    {
        Uri result = context.getContentResolver().insert(
                HISTORY_ENTRY_CONTENT_URI,
                OnyxHistoryEntry.Columns.createColumnData(historyEntry));
        if (result == null) {
            return false;
        }

        String id = result.getLastPathSegment();
        if (id == null) {
            return false;
        }

        historyEntry.setId(Long.parseLong(id));

        return true;
    }
    
    private static void readHistoryEntryCursor(Cursor c,
            Collection<OnyxHistoryEntry> result)
    {
        if (c.moveToFirst()) {
            result.add(OnyxHistoryEntry.Columns.readColumnsData(c));

            while (c.moveToNext()) {
                if (Thread.interrupted()) {
                    return;
                }

                result.add(OnyxHistoryEntry.Columns.readColumnsData(c));
            }
        }
    }
    
	public static boolean getAggregatedData(Context context, String isbn, OnyxCmsAggregatedData data)
	{
		if (isbn == null || "".equals(isbn)) {
			return false;
		}
		
		OnyxMetadata metadata = new OnyxMetadata();
		metadata.setISBN(isbn);
		if (!OnyxSyncCenter.getMetadata(context, metadata))
		{
			return false;
		}
		
		data.setBook(metadata);
		
		String md5 = metadata.getMD5();
		List<OnyxAnnotation> annotations = new LinkedList<OnyxAnnotation>();
		if (OnyxSyncCenter.getAnnotations(context, md5, annotations)) {
			data.setAnnotations(annotations);
		} else {
			data.setAnnotations(null);
		}
		
		List<OnyxBookmark> bookmarks = new LinkedList<OnyxBookmark>();
		if (OnyxSyncCenter.getBookmarks(context, md5, bookmarks)) {
			data.setBookmark(bookmarks);
		} else {
			data.setBookmark(null);
		}
		
		OnyxPosition position = new OnyxPosition();
		if (getPosition(context, md5, position)) {
			data.setPosition(position);
		}
		
		List<OnyxHistoryEntry> history = new LinkedList<OnyxHistoryEntry>();
		long readTime = 0;
		if (OnyxSyncCenter.getHistoryEntries(context, md5, history)) {
			for (OnyxHistoryEntry entry : history) {
				readTime += ((entry.getEndTime().getTime() - entry.getStartTime().getTime()) / 1000);
			}
		}
		
		data.setReadTime(readTime);

		return true;
	}
	
	public static boolean clear(Context context)
	{
        int count = -1;
        
        count = context.getContentResolver().delete(METADATA_CONTENT_URI, null, null);
        if (count < 0) {
            return false;
        }
        
        count = context.getContentResolver().delete(BOOKMARK_CONTENT_URI, null, null);
        if (count < 0) {
            return false;
        }
        
        count = context.getContentResolver().delete(POSITION_CONTENT_URI, null, null);
        if (count < 0) {
            return false;
        }
        
        count = context.getContentResolver().delete(ANNOTATION_CONTENT_URI, null, null);
        if (count < 0) {
            return false;
        }

        count = context.getContentResolver().delete(HISTORY_ENTRY_CONTENT_URI, null, null);
        if (count < 0) {
            return false;
        }
        
        return true;
	}
	
	public static class CmsSync
	{
		private static boolean mergePosition(Context context, String application, OnyxPosition position)
		{
			if (position.getId() == -1) {
				Log.i(TAG, "Insert position");
				return OnyxCmsCenter.insertPosition(context, application, position);
			} else {
				Log.i(TAG, "Update position");
				return OnyxCmsCenter.updatePosition(context, application, position);
			}
		}
		
		private static boolean mergeBookmarks(Context context, String application, List<OnyxBookmark> bookmarks)
		{
			for (OnyxBookmark bookmark : bookmarks) {
				if (bookmark.getId() == -1) {
					Log.i(TAG, "Insert bookmark");
					OnyxCmsCenter.insertBookmark(context, application, bookmark);
				} else {
					Log.i(TAG, "Update bookmark");
					OnyxCmsCenter.updateBookmark(context, application, bookmark);
				}
			}
			
			return true;
		}
		
		private static boolean mergeAnnotations(Context context, String application, List<OnyxAnnotation> annotations)
		{
			for (OnyxAnnotation annotation : annotations) {
				Log.i(TAG, annotation.getQuote());
				
				if (annotation.getId() == -1) {
					Log.i(TAG, "Insert annotation");
					OnyxCmsCenter.insertAnnotation(context, application, annotation);
				} else {
					Log.i(TAG, "Update annotation");
					OnyxCmsCenter.updateAnnotation(context, application, annotation);
				}
			}
			
			return true;
		}
		
		private static boolean deleteBookmarks(Context context, List<OnyxBookmark> bookmarks)
		{
			for (OnyxBookmark bookmark : bookmarks) {
				Log.i(TAG, "Delete bookmark");
				OnyxCmsCenter.deleteBookmark(context, bookmark);
			}
			
			return true;
		}
		
		private static boolean deleteAnnotations(Context context, List<OnyxAnnotation> annotations)
		{
			for (OnyxAnnotation annotation : annotations) {
				Log.i(TAG, "Delete annotation");
				OnyxCmsCenter.deleteAnnotation(context, annotation);
			}
			
			return true;
		}
		
		private static boolean updateTime(Context context, OnyxMetadata metadata)
		{
			String updateTime = metadata.getExtraAttributes().getString(TAG_LAST_UPDATE_DATE);
			
			if (OnyxCmsCenter.getMetadata(context, metadata)) {
				metadata.putExtraAttribute(TAG_LAST_UPDATE_DATE, updateTime);
				return OnyxCmsCenter.updateMetadata(context, metadata);
			}
			
			return false;
		}
		
		private static boolean syncHistoryEnties(Context context, String application, String md5)
		{
			return OnyxCmsCenter.deleteHistoryByMD5(context, application, md5);
		}
		
		public static boolean mergeDiff(Context context, OnyxCmsAggregatedData updates, OnyxCmsAggregatedData removes)
		{
			return mergeDiff(context, context.getPackageName(), updates, removes);
		}
		
		public static boolean mergeDiff(Context context, String application, OnyxCmsAggregatedData updates, OnyxCmsAggregatedData removes)
		{
			boolean result = true;
			
			if (updates.getPosition() != null) {
				if (!mergePosition(context, application, updates.getPosition())) {
					result = false;
				}
			}
			
			if (updates.getBookmarks() != null && updates.getBookmarks().size() > 0) {
				if (!mergeBookmarks(context, application, updates.getBookmarks())) {
					result = false;
				}
			}
			
			if (updates.getAnnotations() != null && updates.getAnnotations().size() > 0) {
				if (!mergeAnnotations(context, application, updates.getAnnotations())) {
					result = false;
				}
			}
			
			if (removes.getBookmarks() != null && removes.getBookmarks().size() > 0) {
				if (!deleteBookmarks(context, removes.getBookmarks())) {
					result = false;
				}
			}
			
			if (removes.getAnnotations() != null && removes.getAnnotations().size() > 0) {
				if (!deleteAnnotations(context, removes.getAnnotations())) {
					result = false;
				}
			}
			
			//syncHistoryEnties(context, application, updates.getBook().getMD5());
			updateTime(context, updates.getBook());
			
			return result;
		}
		
	}
}
