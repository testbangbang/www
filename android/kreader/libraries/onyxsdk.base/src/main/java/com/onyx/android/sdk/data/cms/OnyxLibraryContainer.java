package com.onyx.android.sdk.data.cms;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import com.onyx.android.sdk.data.util.CursorUtil;
import com.onyx.android.sdk.data.util.NotImplementedException;

/**
 * Created by zengzhu on 12/29/15.
 */
public class OnyxLibraryContainer {

    private final static String TAG = OnyxLibraryContainer.class.getSimpleName();
    public static final String DB_TABLE_NAME = "library_container";
    public static final Uri CONTENT_URI = Uri.parse("content://" + OnyxCmsCenter.PROVIDER_AUTHORITY + "/" + DB_TABLE_NAME);

    public static class Columns implements BaseColumns {
        public static final String LIBRARY_UNIQUE_ID = "libUniqueId";
        public static final String ITEM_MD5 = "itemMd5";
        public static final String ITEM_PATH = "itemPath";

        private static boolean sColumnIndexesInitialized = false;
        private static int sColumnID = -1;
        private static int sColumnLibraryUniqueId = -1;
        private static int sColumnItemMd5 = -1;
        private static int sColumnItemPath = -1;

        public static ContentValues createColumnData(final String libId, final String md5, final String path) {
            ContentValues values = new ContentValues();
            values.put(LIBRARY_UNIQUE_ID, libId);
            values.put(ITEM_MD5, md5);
            values.put(ITEM_PATH, path);
            return values;
        }

        public static ContentValues createColumnData(final OnyxLibraryContainer container) {
            return createColumnData(container.getLibUniqueId(), container.getItemMd5(), container.getItemPath());
        }

        public static OnyxLibraryContainer readColumnData(ContentValues columnData) {
            throw new NotImplementedException();
        }

        public static void readColumnData(Cursor c, OnyxLibraryContainer item) {
            if (!sColumnIndexesInitialized) {
                sColumnID = c.getColumnIndex(_ID);
                sColumnLibraryUniqueId = c.getColumnIndex(LIBRARY_UNIQUE_ID);
                sColumnItemMd5 = c.getColumnIndex(ITEM_MD5);
                sColumnItemPath = c.getColumnIndex(ITEM_PATH);
                sColumnIndexesInitialized = true;
            }

            long id = CursorUtil.getLong(c, sColumnID);
            final String libUniqueId = CursorUtil.getString(c, sColumnLibraryUniqueId);
            final String md5 = CursorUtil.getString(c, sColumnItemMd5);
            final String path = CursorUtil.getString(c, sColumnItemPath);

            item.setId(id);
            item.setLibUniqueId(libUniqueId);
            item.setItemMd5(md5);
            item.setItemPath(path);
        }

        public static OnyxLibraryContainer readColumnData(Cursor c)  {
            OnyxLibraryContainer container = new OnyxLibraryContainer();
            readColumnData(c, container);
            return container;
        }
    }

    private long id = 0;
    private String libUniqueId = null;
    private String itemMd5 = null;
    private String path = null;

    public OnyxLibraryContainer() {
    }

    public OnyxLibraryContainer(final String uniqueId, final String md5) {
        setLibUniqueId(uniqueId);
        setItemMd5(md5);
    }

    public long getId() {
        return id;
    }

    public void setId(long value) {
        id = value;
    }

    public String getLibUniqueId() {
        return libUniqueId;
    }

    public void setLibUniqueId(final String value) {
        libUniqueId = value;
    }

    public String getItemMd5() {
        return itemMd5;
    }

    public void setItemMd5(final String value) {
        itemMd5 = value;
    }

    public String getItemPath() {
        return path;
    }

    public void setItemPath(final String p) {
        path = p;
    }

}
