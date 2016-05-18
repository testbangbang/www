package com.onyx.android.sdk.data.cms;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.data.util.CursorUtil;
import com.onyx.android.sdk.data.util.FileUtil;
import com.onyx.android.sdk.data.util.NotImplementedException;
import com.onyx.android.sdk.data.util.SerializationUtil;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class OnyxMetadata implements Parcelable
{
    private final static String TAG = "OnyxMetadata";
    
    public static final String DB_TABLE_NAME = "library_metadata";
    public static final Uri CONTENT_URI = Uri.parse("content://" + OnyxCmsCenter.PROVIDER_AUTHORITY + "/" + DB_TABLE_NAME);

    public static class Columns implements BaseColumns
    {
        public static String MD5 = "MD5";
        public static String NAME = "Name";
        public static String TITLE = "Title";
        public static String AUTHORS = "Authors";
        public static String PUBLISHER = "Publisher";
        public static String LANGUAGE = "Language";
        public static String ISBN = "ISBN";
        public static String DESCRIPTION = "Description";
        public static String LOCATION = "Location";
        public static String NATIVE_ABSOLUTE_PATH = "NativeAbsolutePath";
        public static String SIZE = "Size";
        public static String ENCODING = "Encoding";
        public static String LAST_ACCESS = "LastAccess";
        public static String LAST_MODIFIED = "LastModified";
        public static String PROGRESS = "Progress";
        public static String FAVORITE = "Favorite";
        public static String RATING = "Rating";
        public static String TAGS = "Tags";
        public static String EXTRA_ATTRIBUTES = "ExtraAttributes";
        public static String TYPE = "Type";
        public static String CLOUD_REFERENCE = "CloudReference";
        public static String SERIES = "Series";

        
        // need read at runtime
        private static boolean sColumnIndexesInitialized = false; 
        private static int sColumnID = -1;
        private static int sColumnMD5 = -1;
        private static int sColumnName = -1;
        private static int sColumnTitle = -1;
        private static int sColumnAuthors = -1;
        private static int sColumnPublisher = -1;
        private static int sColumnLanguage = -1;
        private static int sColumnISBN = -1;
        private static int sColumnDescription = -1;
        private static int sColumnLocation = -1;
        private static int sColumnNativeAbsolutePath = -1;
        private static int sColumnSize = -1;
        private static int sColumnEncoding = -1;
        private static int sColumnLastAccess = -1;
        private static int sColumnLastModified = -1;
        private static int sColumnProgress = -1;
        private static int sColumnFavorite = -1;
        private static int sColumnRating = -1;
        private static int sColumnTags = -1;
        private static int sColumnExtraAttributes = -1;
        private static int sColumnType = -1;
        private static int sColumnCloudReference = -1;
        private static int sColumnSeries = -1;
        
        public static ContentValues createColumnData(OnyxMetadata data)
        {
            ContentValues values = new ContentValues();
            values.put(MD5, data.getMD5());
            values.put(NAME, data.getName());
            values.put(TITLE, data.getTitle());
            values.put(AUTHORS, SerializationUtil.authorsToString(data.getAuthors()));
            values.put(PUBLISHER, data.getPublisher());
            values.put(LANGUAGE, data.getLanguage());
            values.put(ISBN, data.getISBN());
            values.put(DESCRIPTION, data.getDescription());
            values.put(LOCATION, data.getLocation());
            values.put(NATIVE_ABSOLUTE_PATH, data.getNativeAbsolutePath());
            values.put(SIZE, data.getSize());
            values.put(ENCODING, data.getEncoding());
            values.put(LAST_ACCESS, data.getLastAccess() == null ? null : data.getLastAccess().getTime());
            values.put(LAST_MODIFIED, data.getLastModified() == null ? null : data.getLastModified().getTime());
            values.put(PROGRESS, data.getProgress() == null ? null : data.getProgress().toString());
            values.put(FAVORITE, data.getFavorite());
            values.put(RATING, data.getRating());
            values.put(TAGS, SerializationUtil.tagsToString(data.getTags()));
            values.put(EXTRA_ATTRIBUTES, JSON.toJSONString(data.getExtraAttributes()));
            values.put(TYPE, data.getType());
            values.put(CLOUD_REFERENCE, data.getCloudReference());
            values.put(SERIES, SerializationUtil.setToString(data.getSeries()));
            return values;
        }
        
        public static OnyxMetadata readColumnData(ContentValues values) {
            throw new NotImplementedException();
        }
        
        public static void readColumnData(Cursor c, OnyxMetadata data)
        {
            if (!sColumnIndexesInitialized) {
                sColumnID = c.getColumnIndex(_ID);
                sColumnMD5 = c.getColumnIndex(MD5);
                sColumnName = c.getColumnIndex(NAME);
                sColumnTitle = c.getColumnIndex(TITLE);
                sColumnAuthors = c.getColumnIndex(AUTHORS);
                sColumnPublisher = c.getColumnIndex(PUBLISHER);
                sColumnLanguage = c.getColumnIndex(LANGUAGE);
                sColumnISBN = c.getColumnIndex(ISBN);
                sColumnDescription = c.getColumnIndex(DESCRIPTION);
                sColumnLocation = c.getColumnIndex(LOCATION);
                sColumnNativeAbsolutePath = c.getColumnIndex(NATIVE_ABSOLUTE_PATH);
                sColumnSize = c.getColumnIndex(SIZE);
                sColumnEncoding = c.getColumnIndex(ENCODING);
                sColumnLastAccess = c.getColumnIndex(LAST_ACCESS);
                sColumnLastModified = c.getColumnIndex(LAST_MODIFIED);
                sColumnProgress = c.getColumnIndex(PROGRESS);
                sColumnFavorite = c.getColumnIndex(FAVORITE);
                sColumnRating = c.getColumnIndex(RATING);
                sColumnTags = c.getColumnIndex(TAGS);
                sColumnExtraAttributes = c.getColumnIndex(EXTRA_ATTRIBUTES);
                sColumnType = c.getColumnIndex(TYPE);
                sColumnCloudReference = c.getColumnIndex(CLOUD_REFERENCE);
                sColumnSeries = c.getColumnIndex(SERIES);
                sColumnIndexesInitialized = true;
            }
            
            long id = CursorUtil.getLong(c, sColumnID);
            String md5 = CursorUtil.getString(c, sColumnMD5);
            String name = CursorUtil.getString(c, sColumnName);
            String title = CursorUtil.getString(c, sColumnTitle);
            String authors = CursorUtil.getString(c, sColumnAuthors);
            String publisher = CursorUtil.getString(c, sColumnPublisher);
            String language = CursorUtil.getString(c, sColumnLanguage);
            String isbn = CursorUtil.getString(c, sColumnISBN);
            String description = CursorUtil.getString(c, sColumnDescription);
            String location = CursorUtil.getString(c, sColumnLocation);
            String native_absolute_path = CursorUtil.getString(c, sColumnNativeAbsolutePath);
            Long size = CursorUtil.getLong(c, sColumnSize);
            String encoding = CursorUtil.getString(c, sColumnEncoding);
            Long last_access = CursorUtil.getLong(c, sColumnLastAccess);
            Long last_modified = CursorUtil.getLong(c, sColumnLastModified);
            OnyxBookProgress progress = OnyxBookProgress.fromString(CursorUtil.getString(c, sColumnProgress));
            int favorite = c.getInt(sColumnFavorite);
            int rating = c.getInt(sColumnRating);
            String tags = CursorUtil.getString(c, sColumnTags);
            String cloudReference = CursorUtil.getString(c, sColumnCloudReference);
            String series = CursorUtil.getString(c, sColumnSeries);

            JSONObject extra_attrs;
            try {
                extra_attrs = JSON.parseObject(CursorUtil.getString(c, sColumnExtraAttributes), JSONObject.class);
            } catch (Throwable tr) {
                // for compatibility, pay the cost of throwing old attributes
                extra_attrs = new JSONObject();
            }
            String type = CursorUtil.getString(c, sColumnType);
            
            data.setId(id);
            data.setMD5(md5);
            data.setName(name);
            data.setTitle(title);
            data.setAuthors(SerializationUtil.authorsFromString(authors));
            data.setPublisher(publisher);
            data.setLanguage(language);
            data.setISBN(isbn);
            data.setDescription(description);
            data.setLocation(location);
            data.setNativeAbsolutePath(native_absolute_path);
            if (last_access != null && last_access > 0) {
                data.setLastAccess(new Date(last_access));
            }
            data.setlastModified(new Date(last_modified == null ? 0 : last_modified));
            data.setSize(size == null ? 0 : size);
            data.setEncoding(encoding);
            data.setProgress(progress);
            data.setFavorite(favorite);
            data.setRating(rating);
            data.setTags(SerializationUtil.tagsFromString(tags));
            data.putExtraAttributes(extra_attrs);
            data.setType(type);
            data.setCloudReference(cloudReference);
            data.setSeries(SerializationUtil.setFromString(series));
        }
        
        public static OnyxMetadata readColumnData(Cursor c)
        {
            OnyxMetadata data = new OnyxMetadata();
            readColumnData(c, data);
            return data;
        }
    }

    // -1 should never be valid DB value
    private static final int INVALID_ID = -1;
    
    // be careful to keep field definitions in order
    private long mId = INVALID_ID;
    private String mMD5 = null;
    private String mName = null;
    private String mTitle = null;
    private ArrayList<String> mAuthors = null;
    private String mPublisher = null;
    private String mLanguage = null;
    private String mISBN = null;
    private String mDescription = null;
    private String mLocation = null;
    private String mNativeAbsolutePath = null;
    private long mSize = 0;
    private String mEncoding = null;
    private Date mLastAccess = null;
    private Date mLastModified = null;
    private OnyxBookProgress mProgress = null;
    private int mFavorite = 0;
    private int mRating = 0;
    private ArrayList<String> mTags = null;
    private Set<String> series = null;

    /**
     * Additional attributes for flexibility
     */
    private JSONObject mExtraAttributes = new JSONObject();
    private String mType = null;
    private String cloudReference;

    
    public OnyxMetadata()
    {
    }
    
    public OnyxMetadata(Parcel source)
    {
    	readFromParcel(source);
    }
    
    /**
     * get basic metadata with data relating to file (including MD5), return null if failed
     * 
     * @param file
     * @return
     */
    public static OnyxMetadata createFromFile(File file) {
        return createFromFile(file, true);
    }
    
    public static OnyxMetadata createFromFile(String path)
    {
        return createFromFile(new File(path));
    }


    public static OnyxMetadata createFromFile(File file, boolean computeMd5)
    {
        try {
            OnyxMetadata data = new OnyxMetadata();
            if (computeMd5) {
                String md5 = FileUtil.computeMD5(file);
                data.setMD5(md5);
            }

            data.setName(file.getName());
            data.setLocation(file.getAbsolutePath());
            data.setNativeAbsolutePath(file.getAbsolutePath());
            data.setSize(file.length());
            data.setlastModified(new Date(FileUtil.getLastChangeTime(file)));
            data.setType(FileUtil.getFileExtension(file.getName()));

            return data;
        }
        catch (NoSuchAlgorithmException e) {
            Log.w(TAG, e);
        }
        catch (IOException e) {
            Log.w(TAG, e);
        }

        return null;
    }

    @Override
    public Object clone() {
        OnyxMetadata data = new OnyxMetadata();
        data.setId(mId);
        data.setMD5(mMD5);
        data.setName(mName);
        data.setTitle(mTitle);
        data.setAuthors(mAuthors);
        data.setPublisher(mPublisher);
        data.setLanguage(mLanguage);
        data.setISBN(mISBN);
        data.setDescription(mDescription);
        data.setLocation(mLocation);
        data.setNativeAbsolutePath(mNativeAbsolutePath);
        data.setSize(mSize);
        data.setEncoding(mEncoding);
        data.setLastAccess(mLastAccess);
        data.setlastModified(mLastModified);
        data.setProgress(mProgress);
        data.setFavorite(mFavorite);
        data.setRating(mRating);
        data.setTags(mTags);
        data.putExtraAttributes(mExtraAttributes);
        data.setType(mType);
        data.setCloudReference(cloudReference);
        data.setSeries(series);
        return data;
    }

    public boolean isDataFromDB()
    {
        return mId != INVALID_ID;
    }
    
    public long getId()
    {
        return mId;
    }
    public void setId(long id)
    {
        this.mId = id;
    }

    /**
     * may return null
     * 
     * @return
     */
    public String getMD5()
    {
        return mMD5;
    }
    public void setMD5(String md5)
    {
        this.mMD5 = md5;
    }
    
    /**
     * may return null
     * 
     * @return
     */
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        this.mName = name;
    }
    
    /**
     * may return null
     * 
     * @return
     */
    public String getTitle()
    {
        return mTitle;
    }
    public void setTitle(String title)
    {
        this.mTitle = title;
    }
    
    /**
     * may return null
     * 
     * @return
     */
    public ArrayList<String> getAuthors()
    {
        return mAuthors;
    }

    public String getAuthorsString() {
        if (mAuthors != null) {
            return StringUtils.join(mAuthors, ",");
        }
        return "";
    }

    /**
     * may return null
     *
     * @return
     */
    public String getAuthorsStringWithCustomSeparator(String separator) {
        if (mAuthors != null) {
            return StringUtils.join(mAuthors, separator);
        }
        return "";
    }

    public void setAuthors(ArrayList<String> authors)
    {
        this.mAuthors = authors;
    }

    /**
     * may return null
     * 
     * @return
     */
    public String getPublisher()
    {
        return mPublisher;
    }
    public void setPublisher(String publisher)
    {
        this.mPublisher = publisher;
    }
    
    /**
     * may return null
     * 
     * @return
     */
    public String getLanguage()
    {
        return mLanguage;
    }
    public void setLanguage(String language)
    {
        this.mLanguage = language;
    }
    
    /**
     * may return null
     * 
     * @return
     */
    public String getISBN()
    {
    	return mISBN;
    }
    
    public void setISBN(String isbn)
    {
    	mISBN = isbn;
    }
    
    /**
     * may return null
     * 
     * @return
     */
    public String getDescription()
    {
        return mDescription;
    }
    public void setDescription(String description)
    {
        this.mDescription = description;
    }
    
    /**
     * may return null
     * 
     * @return
     */
    public String getLocation()
    {
        return mLocation;
    }
    public void setLocation(String location)
    {
        this.mLocation = location;
    }
    
    /**
     * may return null
     * 
     * @return
     */
    public String getNativeAbsolutePath()
    {
        return mNativeAbsolutePath;
    }
    public void setNativeAbsolutePath(String nativeAbsolutePath)
    {
        this.mNativeAbsolutePath = nativeAbsolutePath;
    }
    
    public long getSize()
    {
        return mSize;
    }
    public void setSize(long size)
    {
        this.mSize = size;
    }

    public String getEncoding()
    {
        return mEncoding;
    }
    public void setEncoding(String encoding)
    {
        this.mEncoding = encoding;
    }
    
    /**
     * may return null
     * 
     * @return
     */
    public Date getLastAccess()
    {
        return mLastAccess;
    }
    public void setLastAccess(Date lastAccess)
    {
        this.mLastAccess = lastAccess;
    }
    public void updateLastAccess() {
        mLastAccess = new Date();
    }


    /**
     * may return null
     * 
     * @return
     */
    public Date getLastModified()
    {
        return mLastModified;
    }
    public void setlastModified(Date lastModified)
    {
        this.mLastModified = lastModified;
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

    public boolean isReaded() {
        return (mProgress != null && mProgress.isFinished());
    }

    public boolean isReading() {
        return (mProgress != null);
    }

    public boolean isNew() {
        return (mProgress == null);
    }

    public int getFavorite()
    {
        return mFavorite;
    }
    public void setFavorite(int favorite)
    {
        this.mFavorite = favorite;
    }
    
    public int getRating()
    {
        return mRating;
    }
    public void setRating(int rating)
    {
        this.mRating = rating;
    }
    
    /**
     * may return null
     * 
     * @return
     */
    public ArrayList<String> getTags()
    {
        return mTags;
    }
    public void setTags(ArrayList<String> tags)
    {
        this.mTags = tags;
    }

    public String getTagsString() {
        if (mTags != null) {
            return StringUtils.join(mTags, SerializationUtil.SEPERATOR);
        }
        return "";
    }

    /**
     * never return null
     *
     * @return
     */
    public JSONObject getExtraAttributes() {
        return mExtraAttributes;
    }
    public void putExtraAttribute(String key, Object value) {
        mExtraAttributes.put(key, value);
    }
    public void putExtraAttributes(Map<String, Object> value) {
        if (value != null) {
            mExtraAttributes.putAll(value);
        }
    }

    /**
     * may return null
     *
     * @return
     */
    public String getType() {
        return mType;
    }
    public void setType(String type) {
        this.mType = type;
    }

    public String getCloudReference() {
        return cloudReference;
    }

    public void setCloudReference(final String ref) {
        cloudReference = ref;
    }

    public final Set<String> getSeries() {
        return series;
    }

    public final String getSeriesString() {
        return SerializationUtil.setToString(series);
    }

    public void setSeries(final Set<String> s) {
        series = s;
    }

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mId);
		dest.writeString(mMD5);
		dest.writeString(mName);
		dest.writeString(mTitle);
		dest.writeStringList(mAuthors);
		dest.writeString(mPublisher);
		dest.writeString(mLanguage);
		dest.writeString(mISBN);
		dest.writeString(mDescription);
		dest.writeString(mLocation);
		dest.writeString(mNativeAbsolutePath);
		dest.writeLong(mSize);
		dest.writeString(mEncoding);
		dest.writeString(SerializationUtil.dateToString(mLastAccess));
		dest.writeString(SerializationUtil.dateToString(mLastModified));
		dest.writeInt(mFavorite);
		dest.writeInt(mRating);
		dest.writeStringList(mTags);
		dest.writeString(JSON.toJSONString(mExtraAttributes));
        dest.writeString(mType);
        dest.writeString(cloudReference);
        if (series != null && series.size() > 0) {
            dest.writeStringList(new ArrayList<String>(series));
        }
    }

    public void readFromParcel(Parcel source)
	{
		mId = source.readLong();
		mMD5 = source.readString();
		mName = source.readString();
		mTitle = source.readString();
		mAuthors = new ArrayList<String>();
		source.readStringList(mAuthors);
		mPublisher = source.readString();
		mLanguage = source.readString();
		mISBN = source.readString();
		mDescription = source.readString();
		mLocation = source.readString();
		mNativeAbsolutePath = source.readString();
		mSize = source.readLong();
		mEncoding = source.readString();
		mLastAccess = SerializationUtil.dateFromString(source.readString());
		mLastModified = SerializationUtil.dateFromString(source.readString());
		mFavorite = source.readInt();
		mRating = source.readInt();
		mTags = new ArrayList<String>();
		source.readStringList(mTags);
		mExtraAttributes = JSON.parseObject(source.readString());
        mType = source.readString();
        cloudReference = source.readString();
        List<String> list = new ArrayList<String>();
        source.readStringList(list);
        if (list.size() > 0) {
            series = new HashSet<String>(list);
        }
    }
	
	public static final Parcelable.Creator<OnyxMetadata> CREATOR 
								= new Parcelable.Creator<OnyxMetadata>() 
	{
		
		@Override
		public OnyxMetadata createFromParcel(Parcel source) 
		{
			Log.i(TAG, "Create metadata from parcel!");
			return new OnyxMetadata(source);
		}
		
		@Override
		public OnyxMetadata[] newArray(int size) 
		{
			return new OnyxMetadata[size];
		}
		
	};

}
