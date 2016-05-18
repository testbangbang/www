package com.onyx.android.sdk.data.cms;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.onyx.android.sdk.data.util.CursorUtil;

/**
 * Created by joy on 1/26/15.
 */
public class OnyxBookConfig {

    private static final String TAG = "OnyxBookConfig";

    public static final String DB_TABLE_NAME = "library_book_config";
    public static final Uri CONTENT_URI = Uri.parse("content://" + OnyxCmsCenter.PROVIDER_AUTHORITY + "/" + DB_TABLE_NAME);

    public static class Columns implements BaseColumns {
        public static String MD5 = "MD5";
        public static String APPLICATION = "Application";
        public static String CONFIG = "Config";

        // need read at runtime
        private static boolean sColumnIndexesInitialized = false;
        private static int sColumnID = -1;
        private static int sColumnMD5 = -1;
        private static int sColumnApplication = -1;
        private static int sColumnConfig = -1;

        public static ContentValues createColumnData(OnyxBookConfig config) {
            ContentValues values = new ContentValues();
            values.put(MD5, config.mMD5);
            values.put(APPLICATION, config.mApplication);
            values.put(CONFIG, config.mConfig);
            return values;
        }

        public static OnyxBookConfig readColumnData(Cursor c) {
            OnyxBookConfig config = new OnyxBookConfig();
            readColumnData(c, config);
            return config;
        }

        public static void readColumnData(Cursor c, OnyxBookConfig config) {
            if (!sColumnIndexesInitialized) {
                sColumnID = c.getColumnIndex(_ID);
                sColumnMD5 = c.getColumnIndex(MD5);
                sColumnApplication = c.getColumnIndex(APPLICATION);
                sColumnConfig = c.getColumnIndex(CONFIG);
            }

            long id = CursorUtil.getLong(c, sColumnID);
            String md5 = CursorUtil.getString(c, sColumnMD5);
            String app = CursorUtil.getString(c, sColumnApplication);
            String conf = CursorUtil.getString(c, sColumnConfig);

            config.setId(id);
            config.setMd5(md5);
            config.setApplication(app);
            config.setConfig(conf);
        }

    }

    private long mId = 0;
    private String mMD5;
    private String mApplication;
    private String mConfig;

    public OnyxBookConfig() {

    }

    public OnyxBookConfig(String md5, String application, String config) {
        mMD5 = md5;
        mApplication = application;
        mConfig = config;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getMd5() {
        return mMD5;
    }

    public void setMd5(String md5) {
        mMD5 = md5;
    }

    public String getApplication() {
        return mApplication;
    }

    public void setApplication(String application) {
        mApplication = application;
    }

    public String getConfig() {
        return mConfig;
    }

    public void setConfig(String config) {
        mConfig = config;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof OnyxBookConfig)) {
            return false;
        }
        OnyxBookConfig config = (OnyxBookConfig)o;
        return config.mId == mId && config.mMD5.equals(mMD5) && config.mApplication.equals(mApplication) &&
                config.mConfig.equals(mConfig);
    }
}
