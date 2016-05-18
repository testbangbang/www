/**
 * 
 */
package com.onyx.android.sdk.data.cms;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.onyx.android.sdk.data.util.CursorUtil;

/**
 * @author joy
 *
 */
public class OnyxThumbnail
{
    private static final String TAG = "OnyxThumbnail";
    
    public static final String DB_TABLE_NAME = "library_thumbnail";
    public static final Uri CONTENT_URI = Uri.parse("content://" + OnyxCmsCenter.PROVIDER_AUTHORITY + "/" + DB_TABLE_NAME);
    
    public static enum ThumbnailKind { Original, Large, Middle, Small }
    
    /**
     * 512x512 at most, or original bmp' size, if it's smaller than 512x512
     * 
     * @param bmp
     * @return
     */
    public static Bitmap createLargeThumbnail(Bitmap bmp)
    {
        return createThumbnail(bmp, 512);
    }
    
    /**
     * 256x256 at most, or original bmp' size, if it's smaller than 256x256
     * 
     * @param bmp
     * @return
     */
    public static Bitmap createMiddleThumbnail(Bitmap bmp)
    {
        return createThumbnail(bmp, 256);
    }
    
    /**
     * 128x128 at most, or original bmp' size, if it's smaller than 128x128
     * 
     * @param bmp
     * @return
     */
    public static Bitmap createSmallThumbnail(Bitmap bmp)
    {
        return createThumbnail(bmp, 128);
    } 
    
    private static Bitmap createThumbnail(Bitmap bmp, int limit)
    {
        if (bmp.getWidth() <= limit && bmp.getHeight() <= limit) {
            return bmp;
        }
        
        int w = limit;
        int h = limit;
        
        if (bmp.getWidth() >= bmp.getHeight()) {
            double z = (double)limit / bmp.getWidth(); 
            h = (int)(z * bmp.getHeight());
        }
        else {
            double z = (double)limit / bmp.getHeight();
            w = (int)(z * bmp.getWidth());
        }
        
        return Bitmap.createScaledBitmap(bmp, w, h, true);
    }
    
    public static class Columns implements BaseColumns
    {
        /**
         * _data is used by Android convention
         */
        public static String _DATA = "_data";
        public static String SOURCE_MD5 = "Source_MD5";
        public static String THUMBNAIL_KIND = "Thumbnail_Kind";
        
        // need read at runtime
        private static boolean sColumnIndexesInitialized = false; 
        private static int sColumnID = -1;
        private static int sColumnDATA = -1;
        private static int sColumnSourceMD5 = -1;
        private static int sColumnThumbnailKind = -1;
        
        /**
         * need know from outside the directory of application
         * @param sourceMD5
         * @param AppDir
         * @return
         */
        public static ContentValues createColumnData(String sourceMD5, ThumbnailKind thumbnailKind)
        {
            ContentValues values = new ContentValues();
            values.put(SOURCE_MD5, sourceMD5);
            values.put(THUMBNAIL_KIND, thumbnailKind.toString());
            
            return values;
        }
        
        public static OnyxThumbnail readColumnData(Cursor c)
        {
            if (!sColumnIndexesInitialized) {
                sColumnID = c.getColumnIndex(_ID);
                sColumnDATA = c.getColumnIndex(_DATA);
                sColumnSourceMD5 = c.getColumnIndex(SOURCE_MD5);
                sColumnThumbnailKind = c.getColumnIndex(THUMBNAIL_KIND);
                
                sColumnIndexesInitialized = true;
            }
            
            long id = CursorUtil.getLong(c, sColumnID);
            String data = CursorUtil.getString(c, sColumnDATA);
            String md5 = CursorUtil.getString(c, sColumnSourceMD5);
            
            ThumbnailKind tk = ThumbnailKind.Original;
            try {
                tk = Enum.valueOf(ThumbnailKind.class, CursorUtil.getString(c, sColumnThumbnailKind));
            }
            catch (Throwable tr) {
                Log.w(TAG, "exception", tr);
            }
            
            OnyxThumbnail thumbnail = new OnyxThumbnail();
            thumbnail.mId = id;
            thumbnail.mData = data;
            thumbnail.mSourceMD5 = md5;
            thumbnail.mThumbnailKind = tk;
            
            return thumbnail;
        }
    }
    
    // -1 should never be valid DB value
    private static final int INVALID_ID = -1;
    
    private long mId = INVALID_ID;
    private String mData = null;
    private String mSourceMD5 = null;
    private ThumbnailKind mThumbnailKind = ThumbnailKind.Original;
    
    public OnyxThumbnail()
    {
    }
    
    public long getId()
    {
        return mId;
    }
    public String getData()
    {
        return mData;
    }
    public String getSourceMD5()
    {
        return mSourceMD5;
    }
    public ThumbnailKind getThumbnailKind()
    {
        return mThumbnailKind;
    }
    
}
