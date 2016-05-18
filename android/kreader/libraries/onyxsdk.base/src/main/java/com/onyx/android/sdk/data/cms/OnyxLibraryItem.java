/**
 * 
 */
package com.onyx.android.sdk.data.cms;

import java.io.File;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.onyx.android.sdk.data.util.CursorUtil;
import com.onyx.android.sdk.data.util.FileUtil;
import com.onyx.android.sdk.data.util.NotImplementedException;

/**
 * @author joy
 *
 */
public class OnyxLibraryItem
{
    private final static String TAG = "OnyxLibraryItem";

    public static final String DB_TABLE_NAME = "library_item";
    public static final Uri CONTENT_URI = Uri.parse("content://" + OnyxCmsCenter.PROVIDER_AUTHORITY + "/" + DB_TABLE_NAME);
    
    public static class Columns implements BaseColumns {
        public static final String PATH = "Path";
        public static final String NAME = "Name";
        public static final String SIZE = "Size";
        public static final String TYPE = "Type";
        public static final String LAST_MODIFIED = "LastChange";
        
        // need read at runtime
        private static boolean sColumnIndexesInitialized = false; 
        private static int sColumnID = -1;
        private static int sColumnPath = -1;
        private static int sColumnName = -1;
        private static int sColumnSize = -1;
        private static int sColumnType = -1;
        private static int sColumLastChange = -1;
        
        // TODO: default sort by file name, which may conflict with file title
        public static final String DEFAULT_ORDER_BY = Columns.NAME;
        
        public static ContentValues createColumnData(File file)
        {
            ContentValues values = new ContentValues();
            values.put(PATH, file.getAbsolutePath());
            values.put(NAME, file.getName());
            values.put(SIZE, file.length());
            values.put(TYPE, FileUtil.getFileExtension(file.getName()));
            values.put(LAST_MODIFIED, FileUtil.getLastChangeTime(file));
            
            return values;
        }
        
        public static OnyxLibraryItem readColumnData(ContentValues columnData)
        {
            throw new NotImplementedException();
        }
        
        public static void readColumnData(Cursor c, OnyxLibraryItem item)
        {
            if (!sColumnIndexesInitialized) {
                sColumnID = c.getColumnIndex(_ID);
                sColumnPath = c.getColumnIndex(PATH);
                sColumnName = c.getColumnIndex(NAME);
                sColumnSize = c.getColumnIndex(SIZE);
                sColumnType = c.getColumnIndex(TYPE);
                sColumLastChange = c.getColumnIndex(LAST_MODIFIED);
                
                sColumnIndexesInitialized = true;
            }
            
            long id = CursorUtil.getLong(c, sColumnID);
            String path = CursorUtil.getString(c, sColumnPath);
            String name = CursorUtil.getString(c, sColumnName);
            Long size = CursorUtil.getLong(c, sColumnSize);
            String type = CursorUtil.getString(c, sColumnType);
            Long last_change = CursorUtil.getLong(c, sColumLastChange);
            
            item.setId(id);
            item.setPath(path);
            item.setName(name);
            item.setSize(size == null ? 0 : size);
            item.setType(type);
            item.setLastChange(last_change == null ? 0 : last_change);
        }
        
        public static OnyxLibraryItem readColumnData(Cursor c)
        {
            OnyxLibraryItem item = new OnyxLibraryItem();
            readColumnData(c, item); 
            return item;
        }
    }
    
    private long mId = 0;
    private String mPath = null;
    private String mName = null;
    private long mSize = 0;
    private String mType = null;
    /**
     * time of last status change of the file, corresponding to st_ctime.
     * There may be mass number of OnyxLibraryItem, so use primitive long directly instead of Date Object to save memory 
     */
    private long mLastChange = 0;
    
    public OnyxLibraryItem()
    {
    }
  
	public OnyxLibraryItem(File file) 
    {
        mPath = file.getAbsolutePath();
        mName = file.getName();
        mSize = file.length();
        mType = FileUtil.getFileExtension(file.getName());
        mLastChange = FileUtil.getLastChangeTime(file);
    }
    
    public long getId()
    {
        return mId;
    }
    public void setId(long id)
    {
        mId = id;
    }
    
    public String getPath()
    {
        return mPath;
    }
    public void setPath(String path)
    {
        mPath = path;
    }
    
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        this.mName = name;
    }
    
  	public void setType(String type)
  	{
  		this.mType = type;
  	}
  	public String getType()
  	{
  		return mType;
  	}
  	
  	public void setSize(long size) 
  	{
  		this.mSize = size;
  	}
  	public long getSize()
  	{
  		return mSize;
  	}
    
    public void setLastChange(long lastChange) 
    {
        this.mLastChange = lastChange;
    }
    public long getLastChange()
    {
        return mLastChange;
    }
    
}
