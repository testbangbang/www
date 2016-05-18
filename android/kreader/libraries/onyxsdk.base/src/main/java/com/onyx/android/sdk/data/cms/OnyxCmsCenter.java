/**
 * 
 */
package com.onyx.android.sdk.data.cms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.content.ContentValues;

import com.onyx.android.sdk.data.AscDescOrder;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.dict.OnyxVocabulary;
import com.onyx.android.sdk.data.util.FileUtil;
import com.onyx.android.sdk.data.util.RefValue;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @author joy
 * 
 */
public class OnyxCmsCenter
{
    private static final String TAG = "OnyxCMSCenter";
    private static final boolean VERBOSE_LOG = false;
    private static final boolean VERBOSE_PROFILE = false;

    public static final String PROVIDER_AUTHORITY = "com.onyx.android.sdk.OnyxCmsProvider";

    public static boolean getLibraryItems(Context context, String selection, String[] selectionArgs,
                                          SortBy sortBy, AscDescOrder ascOrder,
                                          String fileTypes, int limitNumber, Collection<OnyxLibraryItem> result)
    {
        Cursor c = null;

        String new_selection = selection;
        ArrayList<String> new_selection_args = new ArrayList<String>();
        if (selectionArgs != null) {
            new_selection_args.addAll(Arrays.asList(selectionArgs));
        }

        if (!StringUtils.isNullOrEmpty(fileTypes)) {
            String[] file_types= fileTypes.split(",");
            if (file_types != null && file_types.length > 0) {
                if (StringUtils.isNullOrEmpty(new_selection)) {
                    new_selection = "(type=?";
                }
                else {
                    new_selection = new_selection.concat(" AND (type=?");
                }
                
                for(int i = 0; i < file_types.length - 1; i++) {
                    new_selection = new_selection.concat(" OR type=?");
                }
                new_selection = new_selection.concat(")");

                new_selection_args.addAll(Arrays.asList(file_types));
            }
        }

        try {
            String ascDescSort = null;
            if(ascOrder == AscDescOrder.Asc) {
                ascDescSort = "ASC";
            } else {
                ascDescSort = "DESC";
            }

            String sort_order = null;
            if (sortBy == SortBy.Name) {
                sort_order = OnyxLibraryItem.Columns.NAME + " " + ascDescSort;
            }
            else if(sortBy == SortBy.Size) {
                sort_order = OnyxLibraryItem.Columns.SIZE + " " + ascDescSort +
                        "," + OnyxLibraryItem.Columns.NAME + " ASC";
            }
            else if(sortBy == SortBy.FileType){
                sort_order = OnyxLibraryItem.Columns.TYPE + " " + ascDescSort +
                        "," + OnyxLibraryItem.Columns.NAME + " ASC";
            }
            else if(sortBy == SortBy.CreationTime) {
                sort_order = OnyxLibraryItem.Columns.LAST_MODIFIED + " " + ascDescSort +
                        "," + OnyxLibraryItem.Columns.NAME + " ASC";
            }

            if (limitNumber != -1) {
                if (sort_order == null) {
                    sort_order = " LIMIT " + limitNumber;
                }
                else {
                    sort_order += (" LIMIT " + limitNumber);
                }
            }

            c = context.getContentResolver().query(OnyxLibraryItem.CONTENT_URI, null,
                    new_selection, new_selection_args.toArray(new String[0]),
                    sort_order);
            if (c == null) {
                Log.w(TAG, "getLibraryItems, query database failed");
                return false;
            }
            readLibraryItemCursor(c, result);

            return true;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
    
    /**
     * 
     * @param context
     * @param sortBy
     * @param ascOrder
     * @param fileTypes file types to be searched, null means all types
     * @param limitNumber how many records to select, -1 for no limitation, or you can use a large enough number such as Interger.MAX_VALUE
     * @param result
     * @return
     */
    public static boolean getLibraryItems(Context context, SortBy sortBy, AscDescOrder ascOrder,
            String fileTypes, int limitNumber, Collection<OnyxLibraryItem> result)
    {
        Cursor c = null;

        String selection = null;
        String[] selection_args = null;
        
        if (!StringUtils.isNullOrEmpty(fileTypes)) {
            selection_args = fileTypes.split(",");
            if (selection_args != null && selection_args.length > 0) {
                selection = "type=?";
                for(int i = 0; i < selection_args.length - 1; i++) {
                    selection = selection.concat(" OR type=?");
                }
            }
        }

        try {
            String ascDescSort = null;
            if(ascOrder == AscDescOrder.Asc) {
                ascDescSort = "ASC";
            }
            else {
                ascDescSort = "DESC";
            }

            String sort_order = null;
            if (sortBy == SortBy.Name) {
                sort_order = OnyxLibraryItem.Columns.NAME + " " + ascDescSort;
            }
            else if(sortBy == SortBy.Size) {
                sort_order = OnyxLibraryItem.Columns.SIZE + " " + ascDescSort +
                        "," + OnyxLibraryItem.Columns.NAME + " ASC";
            }
            else if(sortBy == SortBy.FileType){
                sort_order = OnyxLibraryItem.Columns.TYPE + " " + ascDescSort + 
                        "," + OnyxLibraryItem.Columns.NAME + " ASC";
            }
            else if(sortBy == SortBy.CreationTime) {
                sort_order = OnyxLibraryItem.Columns.LAST_MODIFIED + " " + ascDescSort +
                        "," + OnyxLibraryItem.Columns.NAME + " ASC";
            }
            
            if (limitNumber != -1) {
                if (sort_order == null) {
                    sort_order = " LIMIT " + limitNumber;
                }
                else {
                    sort_order += (" LIMIT " + limitNumber);
                }
            }

            c = context.getContentResolver().query(OnyxLibraryItem.CONTENT_URI, null, selection, selection_args, sort_order);
            if (c == null) {
                Log.w(TAG, "getLibraryItems, query database failed");
                return false;
            }
            readLibraryItemCursor(c, result);

            return true;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static boolean getLibraryItems(Context context,
            Collection<OnyxLibraryItem> result)
    {
        return getLibraryItems(context, SortBy.None, AscDescOrder.Asc, null, -1, result);
    }

    public static boolean insertLibraryItem(Context context, File file)
    {
        if (VERBOSE_LOG) {
            Log.d(TAG, "insert LibraryItem: " + file.getAbsolutePath());
        }
        
        int n = context.getContentResolver().delete(OnyxLibraryItem.CONTENT_URI,
                OnyxLibraryItem.Columns.PATH + "=?",
                new String[] { file.getAbsolutePath() });
        if (n > 0) {
            if (VERBOSE_LOG) {
                Log.w(TAG, "delete obsolete library item: " + n);
            }
        }
        
        Uri result = context.getContentResolver().insert(
                OnyxLibraryItem.CONTENT_URI,
                OnyxLibraryItem.Columns.createColumnData(file));
        if (result == null) {
            return false;
        }

        String id = result.getLastPathSegment();
        if (id == null) {
            return false;
        }

        return true;
    }

    public static final ContentValues [] fileListToContentValues(final List<File> fileList, int start, int count) {
        ContentValues [] result = new ContentValues[count];
        int index = 0;
        for(int i = start; i < start + count; ++i) {
            result[index++] = OnyxLibraryItem.Columns.createColumnData(fileList.get(i));
        }
        return result;
    }

    public static boolean bulkInsertLibraryItem(final Context context, final List<File> fileList, int start, int count) {
        int value = context.getContentResolver().bulkInsert(OnyxLibraryItem.CONTENT_URI, fileListToContentValues(fileList, start, count));
        return (value == count);
    }

    public static boolean deleteLibraryItem(Context context , String path ) {
    	int result_code = context.getContentResolver().delete(
    			OnyxLibraryItem.CONTENT_URI , OnyxLibraryItem.Columns.PATH + " = ?" ,
    			new String[]{path});
    	return result_code == 1 ? true : false;
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
        	if (!StringUtils.isNullOrEmpty(data.getISBN())) {
                c = context.getContentResolver().query(OnyxMetadata.CONTENT_URI,
                        null,
                        OnyxMetadata.Columns.ISBN + "=?",
                        new String[] { data.getISBN() }, null);
            } else {
                c = context.getContentResolver().query(OnyxMetadata.CONTENT_URI,
                        null,
                        OnyxMetadata.Columns.NATIVE_ABSOLUTE_PATH + "=?" + " AND " +
                                OnyxMetadata.Columns.SIZE + "=" + data.getSize(),
                        new String[] { data.getNativeAbsolutePath() }, null);
        	}
            if (c == null) {
                Log.w(TAG, "getMetadata, query database failed");
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

    /**
     * get metadata of file, return null if failed
     *
     * @param context
     * @param filePath
     * @return
     */
    public static OnyxMetadata getMetadata(Context context, String filePath) {
        File file = new File(filePath);
        return getMetadata(context, file);
    }

    public static OnyxMetadata getMetadata(Context context, String filePath, boolean useMD5Fallback) {
        File file = new File(filePath);
        return getMetadata(context, file, useMD5Fallback);
    }

    public static OnyxMetadata getMetadata(Context context, final File file) {
        return getMetadata(context, file, false);
    }

    public static OnyxMetadata getMetadata(Context context, final File file, boolean useMD5Fallback) {
        OnyxMetadata data = new OnyxMetadata();
        data.setNativeAbsolutePath(file.getAbsolutePath());
        data.setSize(file.length());
        data.setlastModified(new Date(file.lastModified()));

        if (OnyxCmsCenter.getMetadata(context, data)) {
            return data;
        } else if (useMD5Fallback) {
            try {
                data = getMetadataByMD5(context, FileUtil.computeMD5(file));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return data == null ? null : data;
        }

        return null;
    }

    public static boolean getMetadatas(Context context,
            Collection<OnyxMetadata> result)
    {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(OnyxMetadata.CONTENT_URI,
                    null, null, null, null);
            if (c == null) {
                Log.w(TAG, "getMetadatas, query database failed");
                return false;
            }
            readMetadataCursor(c, result);
            
            return true;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
    
    public static OnyxMetadata getMetadataByMD5(Context context, String md5)
    {
        Cursor c = null;
        OnyxMetadata data = null;
        try {
            c = context.getContentResolver().query(OnyxMetadata.CONTENT_URI,
                    null, OnyxMetadata.Columns.MD5 + "= ?", new String[]{md5}, null);
            if (c == null) {
                Log.w(TAG, "getMetadatas, query database failed");
                return null;
            }
            if (c.moveToFirst()) {
                data = OnyxMetadata.Columns.readColumnData(c);
            }
            return data;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static OnyxMetadata getMetadataByCloudReference(Context context, final String cloudReference) {
        Cursor c = null;
        OnyxMetadata data = null;
        try {
            c = context.getContentResolver().query(OnyxMetadata.CONTENT_URI,
                    null, OnyxMetadata.Columns.CLOUD_REFERENCE + "= ?", new String[]{cloudReference}, null);
            if (c == null) {
                return null;
            }
            if (c.moveToFirst()) {
                data = OnyxMetadata.Columns.readColumnData(c);
            }
            return data;
        } finally {
            FileUtils.closeQuietly(c);
        }
    }

    public static boolean getMetadataBySelectionArgs(Context context, String selection, String[] selectionArgs,
                                                     Collection<OnyxMetadata> result) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(OnyxMetadata.CONTENT_URI, null, selection, selectionArgs, null);
            if (c == null) {
                return false;
            }
            readMetadataCursor(c, result);
            return true;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static boolean insertMetadata(Context context, OnyxMetadata data) {
        deleteMetadata(context, data);
        Uri result = context.getContentResolver().insert(
                OnyxMetadata.CONTENT_URI,
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


    public static final ContentValues [] metaListToContentValues(final List<OnyxMetadata> metadataList,  int start, int count) {
        ContentValues [] result = new ContentValues[count];
        int index = 0;
        for(int i = start; i < start + count; ++i) {
            result[index++] = OnyxMetadata.Columns.createColumnData(metadataList.get(i));
        }
        return result;
    }

    public static List<OnyxMetadata> bulkInsertMetadataItem(final Context context, final List<File> fileList, int start, int count, boolean computeMd5) {
        List<OnyxMetadata> metadataList = new ArrayList<OnyxMetadata>();
        for(int i = start; i < start + count; ++i) {
            metadataList.add(OnyxMetadata.createFromFile(fileList.get(i), computeMd5));
        }
        int value = context.getContentResolver().bulkInsert(OnyxMetadata.CONTENT_URI, metaListToContentValues(metadataList, 0, metadataList.size()));
        if (value != metadataList.size()) {
            Log.w(TAG, "bulk insert failed: " + value + " / " + metadataList.size());
        }
        return metadataList;
    }

    public static boolean deleteMetadata(Context context, OnyxMetadata data) {
        if (StringUtils.isNotBlank(data.getMD5())) {
            if (context.getContentResolver().delete(OnyxMetadata.CONTENT_URI,
                    OnyxMetadata.Columns.MD5 + "=?",
                    new String[] { data.getMD5() }) > 0) {
                return true;
            }
        }
        return (context.getContentResolver().delete(OnyxMetadata.CONTENT_URI,
                OnyxMetadata.Columns.NATIVE_ABSOLUTE_PATH + "=?",
                new String[] { data.getNativeAbsolutePath() }) > 0);
    }

    public static boolean deleteMetadata(Context context, long id) {
        int n = context.getContentResolver().delete(OnyxMetadata.CONTENT_URI,
                OnyxMetadata.Columns._ID + "=?",
                new String[] { String.valueOf(id) });
        return (n > 0);
    }

    public static boolean updateMetadata(Context context, OnyxMetadata data)
    {
        Uri row = Uri.withAppendedPath(OnyxMetadata.CONTENT_URI,
                String.valueOf(data.getId()));
        int count = context.getContentResolver().update(row,
                OnyxMetadata.Columns.createColumnData(data), null, null);
        if (count <= 0) {
            return false;
        }

        assert (count == 1);
        return true;
    }
    
    public static boolean getRecentReading(Context context, String fileTypes,
            int limitNumber, AscDescOrder ascOrder, Collection<OnyxMetadata> result)
    {
        Cursor c = null;
        try {
            String selection = null;
            String[] selection_args = null;
            
            if (!StringUtils.isNullOrEmpty(fileTypes)) {
                selection_args = fileTypes.split(",");
                if (selection_args != null && selection_args.length > 0) {
                    selection = "type=?";
                    for(int i = 0; i < selection_args.length - 1; i++) {
                        selection = selection.concat(" OR type=?");   
                    }
                }
            }
            if (selection == null) {
                selection = "(" + OnyxMetadata.Columns.LAST_ACCESS + " IS NOT NULL) AND ("
                        + OnyxMetadata.Columns.LAST_ACCESS + "!='') AND ("
                        + OnyxMetadata.Columns.LAST_ACCESS + "!=0)";
            }
            else {
                selection += " AND (" + OnyxMetadata.Columns.LAST_ACCESS + " IS NOT NULL) AND ("
                        + OnyxMetadata.Columns.LAST_ACCESS + "!='') AND ("
                        + OnyxMetadata.Columns.LAST_ACCESS + "!=0)";
            }

            String sort_order = null;
            if(ascOrder == AscDescOrder.Asc) {
                sort_order = OnyxMetadata.Columns.LAST_ACCESS + " ASC";
            }
            else {
                sort_order = OnyxMetadata.Columns.LAST_ACCESS + " DESC";
            }
            if (limitNumber != -1) {
                sort_order += (" LIMIT " + limitNumber);
            }

            c = context.getContentResolver().query( OnyxMetadata.CONTENT_URI, null,
                    selection, selection_args, sort_order);
            if (c == null) {
                Log.w(TAG, "getRecentReading, query database failed");
                return false;
            }
            readMetadataCursor(c, result);

            return true;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static boolean getRecentReadings(Context context,
            Collection<OnyxMetadata> result)
    {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    OnyxMetadata.CONTENT_URI,
                    null,
                    "(" + OnyxMetadata.Columns.LAST_ACCESS
                            + " is not null) and ("
                            + OnyxMetadata.Columns.LAST_ACCESS + "!='') and ("
                            + OnyxMetadata.Columns.LAST_ACCESS + "!=0)", null,
                    OnyxMetadata.Columns.LAST_ACCESS + " desc");
            if (c == null) {
                Log.w(TAG, "getRecentReadings, query database failed");
                return false;
            }
            readMetadataCursor(c, result);
            return true;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static List<OnyxMetadata> clearRecentReadings(Context context) {
        final List<OnyxMetadata> list = new ArrayList<OnyxMetadata>();
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    OnyxMetadata.CONTENT_URI,
                    null,
                    "(" + OnyxMetadata.Columns.LAST_ACCESS
                            + " is not null) and ("
                            + OnyxMetadata.Columns.LAST_ACCESS + "!='') and ("
                            + OnyxMetadata.Columns.LAST_ACCESS + "!=0)", null,
                    OnyxMetadata.Columns.LAST_ACCESS + " desc");
            if (c == null) {
                Log.w(TAG, "getRecentReadings, query database failed");
                return null;
            }
            readMetadataCursor(c, list);
        } finally {
            FileUtils.closeQuietly(c);
        }

        // update one by one.
        for(OnyxMetadata metadata : list) {
            metadata.setLastAccess(null);
            updateMetadata(context, metadata);
        }
        return list;

    }
    
    /**
     * get tags for all books
     * 
     * @param context
     * @param result
     * @return
     */
    public static boolean getBookTags(Context context, ArrayList<String> result)
    {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(OnyxMetadata.CONTENT_URI,
                    null, "Tags IS NOT NULL AND Tags!=''", null, null);
            if (c == null) {
                Log.w(TAG, "getBookTags, query database failed");
                return false;
            }

            HashSet<String> tag_dict = new HashSet<String>();
            if (c.moveToFirst()) {
                OnyxMetadata data = OnyxMetadata.Columns.readColumnData(c);
                for (String t : data.getTags()) {
                    if (!tag_dict.contains(t)) {
                        result.add(t);
                        tag_dict.add(t);
                    }
                }
                while (c.moveToNext()) {
                    if (Thread.interrupted()) {
                        return false;
                    }
                    data = OnyxMetadata.Columns.readColumnData(c);
                    for (String t : data.getTags()) {
                        if (!tag_dict.contains(t)) {
                            result.add(t);
                            tag_dict.add(t);
                        }
                    }
                }
            }

            return true;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static boolean getMetadatasByTag(Context context, ArrayList<String> tags, ArrayList<OnyxMetadata> result)
    {
        Cursor c = null;
        try {
            String selection = null;
            if (tags != null && tags.size() > 0) {
                selection = "(Tags LIKE ?";
                for(int i = 0; i < tags.size() - 1; i++) {
                    selection = selection.concat(" OR Tags LIKE ?");   
                }
                selection = selection.concat(")");
            }

            c = context.getContentResolver().query(OnyxMetadata.CONTENT_URI,
                    null, selection, tags.toArray(new String[0]), null);
            if (c == null) {
                Log.w(TAG, "getMetadatas, query database failed");
                return false;
            }
            readMetadataCursor(c, result);

            return true;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    /**
     * return null if not found
     *
     * @param context
     * @param application
     * @param md5
     * @return
     */
    public static OnyxBookConfig getBookConfigByMD5(Context context, String application, String md5) {
        ArrayList<OnyxBookConfig> result = new ArrayList<OnyxBookConfig>();
        String sel = OnyxBookConfig.Columns.MD5 + " LIKE ?";
        String[] args = new String[] { md5 };
        if (!getBookConfigBySelectionArgs(context, application, sel, args, result)) {
            return null;
        }
        if (result.size() <= 0) {
            return null;
        }
        return result.get(0);
    }

    public static boolean getBookConfigBySelectionArgs(Context context, String application, String selection, String[] selectionArgs,
                                                       Collection<OnyxBookConfig> result) {
        Cursor c = null;
        try {
            String sel;
            String[] args;
            if (selection == null) {
                sel = "(" + OnyxBookConfig.Columns.APPLICATION + " LIKE ?)";
                args = new String[] { application };
            } else {
                sel = "(" + OnyxBookConfig.Columns.APPLICATION + " LIKE ? AND " + selection + ")";
                ArrayList<String> list = new ArrayList<String>();
                list.add(application);
                list.addAll(Arrays.asList(selectionArgs));
                args = list.toArray(new String[0]);
            }

            c = context.getContentResolver().query(OnyxBookConfig.CONTENT_URI, null, sel, args, null);
            if (c == null) {
                Log.w(TAG, "getBookConfigBySelectionArgs, query database failed");
                return false;
            }
            if (c.moveToFirst()) {
                result.add(OnyxBookConfig.Columns.readColumnData(c));
                while (c.moveToNext()) {
                    result.add(OnyxBookConfig.Columns.readColumnData(c));
                }
                return true;
            }

            return false;

        } finally {
            if (c != null) {
                c.close();
            }
        }

    }

    public static boolean insertBookConfig(Context context, OnyxBookConfig config) {
        return insertBookConfig(context, context.getPackageName(), config);
    }

    public static boolean insertBookConfig(Context context, String application, OnyxBookConfig config) {
        config.setApplication(application);

        Uri result = context.getContentResolver().insert(OnyxBookConfig.CONTENT_URI,
                OnyxBookConfig.Columns.createColumnData(config));
        if (result == null) {
            return false;
        }
        String id = result.getLastPathSegment();
        if (id == null) {
            return false;
        }
        config.setId(Long.parseLong(id));
        return true;
    }

    public static boolean updateBookConfig(Context context, OnyxBookConfig config) {
        Uri row = Uri.withAppendedPath(OnyxBookConfig.CONTENT_URI, String.valueOf(config.getId()));
        int count = context.getContentResolver().update(row, OnyxBookConfig.Columns.createColumnData(config), null, null);
        if (count <= 0) {
            return false;
        }
        assert(count == 1);
        return true;
    }

    public static boolean deleteBookConfig(Context context, OnyxBookConfig config) {
        Uri row = Uri.withAppendedPath(OnyxBookConfig.CONTENT_URI, String.valueOf(config.getId()));
        int count = context.getContentResolver().delete(row, null, null);
        if (count <= 0) {
            return false;
        }
        assert(count == 1);
        return true;
    }

    public static boolean getBookmarks(Context context, String md5, List<OnyxBookmark> result)
    {
    	return getBookmarks(context, context.getPackageName(), md5, result);
    }
    
    /**
     * 
     * @param context
     * @param metadata
     * @return
     */
    public static boolean updateRecentReading(Context context, OnyxMetadata metadata) 
    {
        Date last_access = new Date();
        
        if (OnyxCmsCenter.getMetadata(context, metadata)) {
            metadata.setLastAccess(last_access);
            if (!OnyxCmsCenter.updateMetadata(context, metadata)) {
                return false;
            }
        } else {
            metadata.setLastAccess(last_access);
            if (!OnyxCmsCenter.insertMetadata(context, metadata)) {
                return false;
            }
        }
        
        return true;
    }
    
    public static boolean getBookmarks(Context context, String application, String md5, List<OnyxBookmark> result)
    {
        Cursor c = null;
        try {
            String selection = "";
            if (application != null) {
                selection = OnyxAnnotation.Columns.APPLICATION + "='" + application + "'";
            }
            if (md5 != null) {
                if (!StringUtils.isNullOrEmpty(selection)) {
                    selection += " AND ";
                }
                selection += OnyxAnnotation.Columns.MD5 + "='" + md5 + "'";
            }
            c = context.getContentResolver().query(OnyxBookmark.CONTENT_URI, null, selection,  null, null);
            if (c == null) {
                Log.w(TAG, "getBookmarks, query database failed");
                return false;
            }
            readBookmarkCursor(c, result);
            return true;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static boolean insertBookmark(Context context, OnyxBookmark bookmark)
    {
    	return insertBookmark(context, context.getPackageName(), bookmark);
    }
    
    public static boolean insertBookmark(Context context, String application, OnyxBookmark bookmark)
    {
    	bookmark.setApplication(application);
    	
        Uri result = context.getContentResolver().insert(
                OnyxBookmark.CONTENT_URI,
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
    
    public static boolean deleteBookmark(Context context, OnyxBookmark bookmark)
    {
        Uri row = Uri.withAppendedPath(OnyxBookmark.CONTENT_URI, String.valueOf(bookmark.getId()));
        int count = context.getContentResolver().delete(row, null, null);
        if (count <= 0) {
            return false;
        }
        
        assert(count == 1);
        return true;
    }
    
    public static boolean updateBookmark(Context context, OnyxBookmark bookmark)
    {
    	return updateBookmark(context, context.getPackageName(), bookmark);
    }

    public static boolean updateBookmark(Context context, String application, OnyxBookmark bookmark)
    {
    	bookmark.setApplication(application);
    	
        Uri row = Uri.withAppendedPath(OnyxBookmark.CONTENT_URI, String.valueOf(bookmark.getId()));
        int count = context.getContentResolver().update(row,
                OnyxBookmark.Columns.createColumnData(bookmark), null, null);
        if (count <= 0) {
            return false;
        }

        assert (count == 1);
        return true;
    }
    
    public static boolean getAnnotations(Context context, List<OnyxAnnotation> result)
    {
        return getAnnotations(context, context.getPackageName(), null, result);
    }
    
    public static boolean getAnnotations(Context context, String md5, List<OnyxAnnotation> result)
    {
    	return getAnnotations(context, context.getPackageName(), md5, result);
    }
    
    /**
     * never return null
     * @param context
     * @param id
     * @return
     */
    public static OnyxAnnotation getAnnotationById(Context context, long id)
    {
        Cursor c = null;
        OnyxAnnotation annotation = new OnyxAnnotation();
        try {
            c = context.getContentResolver().query(OnyxAnnotation.CONTENT_URI,
                    null, OnyxAnnotation.Columns._ID + "='" + id + "'",
                    null, null);
            if (c == null) {
                Log.w(TAG, "getAnnotation, query database failed");
                return annotation;
            }
            if (c.moveToFirst()) {
                annotation = OnyxAnnotation.Columns.readColumnData(c);
            }
            return annotation;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
    
    /**
     * 
     * @param context
     * @param application      null means ignore this property
     * @param md5
     * @param result
     * @return
     */
    public static boolean getAnnotations(Context context, String application, String md5, List<OnyxAnnotation> result)
    {
        Cursor c = null;
        try {
            if (md5 == null) {
                c = context.getContentResolver().query(OnyxAnnotation.CONTENT_URI,
                        null, null, null, null);
            } else {
                if (application == null) {
                    c = context.getContentResolver().query(OnyxAnnotation.CONTENT_URI,
                            null, 
                            OnyxAnnotation.Columns.MD5 + "='" + md5 + "'", 
                            null, null);
                } else {
                    c = context.getContentResolver().query(OnyxAnnotation.CONTENT_URI,
                            null, 
                            OnyxAnnotation.Columns.APPLICATION + "='" + application + "' AND " +
                                    OnyxAnnotation.Columns.MD5 + "='" + md5 + "'", 
                                    null, null);
                }
            }
            if (c == null) {
                Log.w(TAG, "getAnnotations, query database failed");
                return false;
            }
            readAnnotationCursor(c, result);
            return true;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
    
    public static boolean insertAnnotation(Context context, OnyxAnnotation annotation)
    {
    	return insertAnnotation(context, context.getPackageName(), annotation);
    }
    
    public static boolean insertAnnotation(Context context, String application, OnyxAnnotation annotation)
    {
    	annotation.setApplication(application);
    	
        Uri result = context.getContentResolver().insert(
                OnyxAnnotation.CONTENT_URI,
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
    
    public static boolean updateAnnotation(Context context, OnyxAnnotation annotation)
    {
    	return updateAnnotation(context, context.getPackageName(), annotation);
    }

    public static boolean updateAnnotation(Context context, String application, OnyxAnnotation annotation)
    {
    	annotation.setApplication(application);

    	Uri row = Uri.withAppendedPath(OnyxAnnotation.CONTENT_URI, String.valueOf(annotation.getId()));
        int count = context.getContentResolver().update(row,
                OnyxAnnotation.Columns.createColumnData(annotation), null, null);
        if (count <= 0) {
            return false;
        }

        assert (count == 1);
        return true;
    }
    
    public static boolean deleteAnnotation(Context context, OnyxAnnotation annotation)
    {
        Uri row = Uri.withAppendedPath(OnyxAnnotation.CONTENT_URI, String.valueOf(annotation.getId()));
        int count = context.getContentResolver().delete(row, null, null);
        if (count <= 0) {
            return false;
        }
        
        assert(count == 1);
        return true;
    }

    /**
     * get all positions in a book where there are scribbles
     *
     * @param context
     * @param application
     * @param md5 md5 of the book, must need
     * @param result
     * @return
     */
    public static boolean getScribblePositions(Context context, String application, String md5, HashSet<String> result)
    {
        if (md5 == null) {
            return false;
        }

        Cursor c = null;
        try {
            String selection = null;
            if (!StringUtils.isNullOrEmpty(application)) {
                selection = OnyxScribble.Columns.APPLICATION + "='" + application + "'";
            }

            String sel_md5 = OnyxScribble.Columns.MD5 + "='" + md5 + "'";
            if (selection == null) {
                selection = sel_md5;
            } else {
                selection = selection + " AND " + sel_md5;
            }

            String[] projection = new String[] {
                    OnyxScribble.Columns.POSITION
            };
            c = context.getContentResolver().query(OnyxScribble.CONTENT_URI,
                    projection, selection, null, null);
            if (c == null) {
                Log.w(TAG, "getScribblePositions, query database failed");
                return false;
            }
            if (c.moveToFirst()) {
                result.add(c.getString(0));
                while (c.moveToNext()) {
                    if (Thread.interrupted()) {
                        return false;
                    }
                    result.add(c.getString(0));
                }
            }
            return true;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    /**
     *
     * @param context
     * @param application as selection filter if not null
     * @param md5 as selection filter if not null
     * @param result
     * @return
     */
    public static boolean getScribbles(Context context, String application, String md5, List<OnyxScribble> result)
    {
        return getScribbles(context, application, md5, null, result);
    }

    /**
     *
     * @param context
     * @param application as selection filter if not null
     * @param md5 as selection filter if not null
     * @param position as selection filter if not null
     * @param result
     * @return
     */
    public static boolean getScribbles(Context context, String application, String md5, String position, List<OnyxScribble> result)
    {
        Cursor c = null;
        try {
            String selection = null;
            if (!StringUtils.isNullOrEmpty(application)) {
                selection = OnyxScribble.Columns.APPLICATION + "='" + application + "'";
            }
            if (!StringUtils.isNullOrEmpty(md5)) {
                String sel_md5 = OnyxScribble.Columns.MD5 + "='" + md5 + "'";
                if (selection == null) {
                    selection = sel_md5;
                } else {
                    selection = selection + " AND " + sel_md5;
                }
            }
            if (!StringUtils.isNullOrEmpty(position)) {
                String sel_pos = OnyxScribble.Columns.POSITION + "='" + position + "'";
                if (selection == null) {
                    selection = sel_pos;
                } else {
                    selection = selection + " AND " + sel_pos;
                }
            }

            c = context.getContentResolver().query(OnyxScribble.CONTENT_URI,
                    null, selection, null, null);
            if (c == null) {
                Log.w(TAG, "getScribbles, query database failed");
                return false;
            }
            readScribbleCursor(c, result);
            return true;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static final ContentValues [] scribbleListToContentValues(final List<OnyxScribble> scribbleList, final String application) {
        ContentValues [] result = new ContentValues[scribbleList.size()];
        int index = 0;
        for(OnyxScribble scribble : scribbleList) {
            scribble.setApplication(application);
            result[index++] = OnyxScribble.Columns.createColumnData(scribble);
        }
        return result;
    }

    public static boolean insertScribbleBulk(final Context context, final String application, final List<OnyxScribble> scribbleList) {
        int value = context.getContentResolver().bulkInsert(OnyxScribble.CONTENT_URI, scribbleListToContentValues(scribbleList, application));
        return value == scribbleList.size();
    }

    public static boolean insertScribble(Context context, String application, OnyxScribble scribble)
    {
        scribble.setApplication(application);
        Uri result = context.getContentResolver().insert(OnyxScribble.CONTENT_URI,
                OnyxScribble.Columns.createColumnData(scribble));
        if (result == null) {
            return false;
        }
        String id = result.getLastPathSegment();
        if (id == null) {
            return false;
        }
        scribble.setId(Long.parseLong(id));

        return true;
    }

    public static boolean insertScribble(Context context, OnyxScribble scribble)
    {
        return insertScribble(context, context.getPackageName(), scribble);
    }

    public static boolean updateScribble(Context context, OnyxScribble scribble)
    {
        Uri row = Uri.withAppendedPath(OnyxScribble.CONTENT_URI, String.valueOf(scribble.getId()));
        int count = context.getContentResolver().update(row,
                OnyxScribble.Columns.createColumnData(scribble),
                OnyxScribble.Columns.MD5 + "=?",
                new String[] { scribble.getMD5() });
        if (count <= 0) {
            return false;
        }

        assert (count == 1);
        return true;
    }

    public static boolean deleteScribble(Context context, OnyxScribble scribble)
    {
        Uri row = Uri.withAppendedPath(OnyxScribble.CONTENT_URI, String.valueOf(scribble.getId()));
        int count = context.getContentResolver().delete(row,
                OnyxScribble.Columns.MD5 + "=?",
                new String[] { scribble.getMD5() });
        if (count <= 0) {
            return false;
        }

        assert(count == 1);
        return true;
    }

    public static int deleteAllScribbleOfDocument(Context context, final String md5) {
        int count = context.getContentResolver().delete(OnyxScribble.CONTENT_URI, OnyxScribble.Columns.MD5 + "=?", new String[]{md5});
        return count;
    }

    public static boolean deleteScribbleByPosition(Context context, String md5, String position)
    {
        int count = context.getContentResolver().delete(OnyxScribble.CONTENT_URI,
                OnyxScribble.Columns.MD5 + "=? AND " + OnyxScribble.Columns.POSITION + "=?",
                new String[] { md5, position });
        if (count <= 0) {
            return false;
        }

        return true;
    }

    public static boolean deleteScribbleByUniqueId(Context context, String md5, String id) {
        int count = context.getContentResolver().delete(OnyxScribble.CONTENT_URI,
                OnyxScribble.Columns.MD5 + "=? AND " + OnyxScribble.Columns.UNIQUE_ID + "=?",
                new String[] { md5, id });
        if (count <= 0) {
            return false;
        }
        return true;
    }

    public static boolean insertHistory(Context context, OnyxHistoryEntry historyEntry)
    {
    	return insertHistory(context, context.getPackageName(), historyEntry);
    }
    
    public static boolean insertHistory(Context context, String application, OnyxHistoryEntry historyEntry)
    {
    	historyEntry.setApplication(application);
        Uri result = context.getContentResolver().insert(
        		OnyxHistoryEntry.CONTENT_URI,
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
    
    public static boolean updateHistory(Context context, OnyxHistoryEntry onyxHistoryEntry)
    {
    	return updateHistory(context, context.getPackageName(), onyxHistoryEntry);
    }
    
    public static boolean updateHistory(Context context, String application, OnyxHistoryEntry onyxHistoryEntry)
    {
    	onyxHistoryEntry.setApplication(application);
    	
        Uri row = Uri.withAppendedPath(OnyxHistoryEntry.CONTENT_URI,
                String.valueOf(onyxHistoryEntry.getId()));
        int count = context.getContentResolver().update(row,
        		OnyxHistoryEntry.Columns.createColumnData(onyxHistoryEntry), null, null);
        if (count <= 0) {
            return false;
        }
        assert (count == 1);
        return true;
    }
    
    public static List<OnyxHistoryEntry> getHistorysByMD5(Context context, String md5)
    {
    	return getHistorysByMD5(context, context.getPackageName(), md5);
    }
    
    public static List<OnyxHistoryEntry> getHistorysByMD5(Context context, String application, String md5)
    {
        Cursor c = null;
        List<OnyxHistoryEntry> historyEntries = new ArrayList<OnyxHistoryEntry>();
        try {
            c = context.getContentResolver().query(OnyxHistoryEntry.CONTENT_URI,
                    null, OnyxHistoryEntry.Columns.MD5 + "= ?", new String[]{md5}, null);
            if (c == null) {
                Log.w(TAG, "getHistorysByMD5, query database failed");
                return null;
            }
            for (c.moveToFirst();!c.isAfterLast();c.moveToNext()) {
                OnyxHistoryEntry history_entry = OnyxHistoryEntry.Columns.readColumnsData(c);
                historyEntries.add(history_entry);
			}
            return historyEntries;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
    
    public static boolean deleteHistoryByMD5(Context context, String md5) {
    	return deleteHistoryByMD5(context, context.getPackageName(), md5);
    }

    public static boolean deleteHistoryByMD5Only(Context context, String md5) {
        int result_code = context.getContentResolver().delete(
                OnyxHistoryEntry.CONTENT_URI, OnyxHistoryEntry.Columns.MD5 + " = ?" ,
                new String[] { md5 });
        return result_code > 0 ? true : false;
    }

    public static boolean deleteHistoryByMD5(Context context, String application, String md5) {
    	int result_code = context.getContentResolver().delete(
    			OnyxHistoryEntry.CONTENT_URI , 
    			OnyxHistoryEntry.Columns.APPLICATION + " = ? AND "
    			+ OnyxHistoryEntry.Columns.MD5 + " = ?" ,
    			new String[] { application, md5 });
    	return result_code > 0 ? true : false;
	}

    public static boolean deleteAllHistory(Context context) {
        int result_code = context.getContentResolver().delete( OnyxHistoryEntry.CONTENT_URI, null, null);
        ContentValues cv = new ContentValues();
        cv.putNull(OnyxMetadata.Columns.LAST_ACCESS);
        context.getContentResolver().update(OnyxMetadata.CONTENT_URI, cv, null, null);
        return result_code > 0 ? true : false;
    }

    public static boolean hasThumbnail(Context context, OnyxMetadata metadata) {
        if (metadata == null) {
            assert (false);
            return false;
        }

        Cursor c = null;
        try {
            c = getThumbnailCursor(context, metadata.getMD5(), OnyxThumbnail.ThumbnailKind.Original);
            if (c == null) {
                Log.w(TAG, "query original thumbnail failed");
                return false;
            }
            return c.moveToFirst();
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static boolean getThumbnailByMD5(Context context, final String md5, OnyxThumbnail.ThumbnailKind thumbnailKind, RefValue<Bitmap> result) {
        if (StringUtils.isNullOrEmpty(md5)) {
            return false;
        }

        Cursor c = null;
        try {
            c = getThumbnailCursor(context, md5, thumbnailKind);
            if (c == null) {
                Log.w(TAG, "query thumbnail failed: " + thumbnailKind.toString() + ", try to query original thumbnail");
                c = getThumbnailCursor(context, md5, OnyxThumbnail.ThumbnailKind.Original);
                if (c == null) {
                    Log.w(TAG, "query original thumbnail failed");
                    return false;
                }
            }

            if (c.moveToFirst()) {
                OnyxThumbnail data = OnyxThumbnail.Columns.readColumnData(c);
                Uri row = Uri.withAppendedPath(OnyxThumbnail.CONTENT_URI,
                        String.valueOf(data.getId()));
                InputStream is = null;
                try {
                    is = context.getContentResolver().openInputStream(row);
                    if (is == null) {
                        Log.w(TAG, "openInputStream failed");
                        return false;
                    }
                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inPurgeable = true;
                    Bitmap b = BitmapFactory.decodeStream(is, null, o);

                    result.setValue(b);
                    return true;
                } catch (Throwable tr) {
                    Log.e(TAG, "exception", tr);
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            Log.w(TAG, e);
                        }
                    }
                }
            }

            return false;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static boolean getThumbnail(Context context, OnyxMetadata metadata, 
            OnyxThumbnail.ThumbnailKind thumbnailKind, RefValue<Bitmap> result) {
        if (metadata == null) {
            assert (false);
            return false;
        }
        return getThumbnailByMD5(context, metadata.getMD5(), thumbnailKind, result);
    }

    public static boolean getThumbnail(Context context, OnyxMetadata metadata, RefValue<Bitmap> result)
    {
        return getThumbnail(context, metadata, OnyxThumbnail.ThumbnailKind.Original, result);
    }

    /**
     * 
     * @param context
     * @param data
     * @param thumbnail
     * @return
     */
    public static boolean insertThumbnail(Context context, OnyxMetadata data,
            Bitmap thumbnail)
    {
        if (data == null) {
            assert (false);
            return false;
        }
        
        if (!insertThumbnailHelper(context, thumbnail, data.getMD5(), OnyxThumbnail.ThumbnailKind.Original)) {
            return false;
        }
        
        insertThumbnailHelper(context, thumbnail, data.getMD5(), OnyxThumbnail.ThumbnailKind.Large);
        insertThumbnailHelper(context, thumbnail, data.getMD5(), OnyxThumbnail.ThumbnailKind.Middle);
        insertThumbnailHelper(context, thumbnail, data.getMD5(), OnyxThumbnail.ThumbnailKind.Small);
        
        return true;
    }

    private static void readLibraryItemCursor(Cursor c,
            Collection<OnyxLibraryItem> result)
    {
        if (c.moveToFirst()) {
            result.add(OnyxLibraryItem.Columns.readColumnData(c));
            while (c.moveToNext()) {
                if (Thread.interrupted()) {
                    return;
                }
                result.add(OnyxLibraryItem.Columns.readColumnData(c));
            }
        }
    }

    private static void readMetadataCursor(Cursor c,
            Collection<OnyxMetadata> result)
    {
        if (c.moveToFirst()) {
            result.add(OnyxMetadata.Columns.readColumnData(c));
            while (c.moveToNext()) {
                if (Thread.interrupted()) {
                    return;
                }
                result.add(OnyxMetadata.Columns.readColumnData(c));
            }
        }
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

    private static void readScribbleCursor(Cursor c, Collection<OnyxScribble> result)
    {
        if (c.moveToFirst()) {
            result.add(OnyxScribble.Columns.readColumnData(c));
            while (c.moveToNext()) {
                if (Thread.interrupted()) {
                    return;
                }
                result.add(OnyxScribble.Columns.readColumnData(c));
            }
        }
    }
    
    private static boolean insertThumbnailHelper(Context context, Bitmap bmp,
            String md5, OnyxThumbnail.ThumbnailKind thumbnailKind)
    {
        Bitmap thumbnail = bmp;
        OutputStream os = null;
        
        try {
            switch (thumbnailKind) {
            case Original:
                break;
            case Large:
                thumbnail = OnyxThumbnail.createLargeThumbnail(bmp);
                break;
            case Middle:
                thumbnail = OnyxThumbnail.createMiddleThumbnail(bmp);
                break;
            case Small:
                thumbnail = OnyxThumbnail.createSmallThumbnail(bmp);
                break;
            default:
                assert(false);
                break;
            }

            Uri result = context.getContentResolver().insert(
                    OnyxThumbnail.CONTENT_URI,
                    OnyxThumbnail.Columns.createColumnData(md5, thumbnailKind));
            if (result == null) {
                Log.w(TAG, "insertThumbnail db insert failed");
                return false;
            }

            os = context.getContentResolver().openOutputStream(result);
            if (os == null) {
                Log.w(TAG, "openOutputStream failed");
                return false;
            }
            thumbnail.compress(CompressFormat.JPEG, 85, os);
            return true;
        } catch (FileNotFoundException e) {
            Log.w(TAG, e);
            return false;
        } finally {
            if (thumbnail != null && thumbnail != bmp) {
                thumbnail.recycle();
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.w(TAG, e);
                }
            }
        }
    }
    
    public static  boolean getFavoriteItems(Context context, Collection<OnyxMetadata> result)
    {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(OnyxMetadata.CONTENT_URI, null,"favorite=1", null, null);
            if (c == null) {
                return false;
            }
            readMetadataCursor(c, result);
            return true;
        }
        finally {
            if (c != null) {
                c.close();
            }
        }

    }

    /**
     * 
     * @param context
     * @param result
     * @param query the pattern to search
     * @param fileTypes file types to be searched, null means all types
     * @return
     */
    public static boolean searchBooks (Context context, Collection<OnyxLibraryItem> result, String query, String fileTypes)
    {
        Cursor c = null;
        try {
            String selection = null;
            String[] selection_args = null;
            
            if (!StringUtils.isNullOrEmpty(fileTypes)) {
                String[] file_types = fileTypes.split(",");
                if (file_types != null && file_types.length > 0) {
                    selection = "(type=?";
                    for(int i = 0; i < file_types.length - 1; i++) {
                        selection = selection.concat(" OR type=?");   
                    }
                    selection = selection.concat(")");
                }

                selection = selection.concat("AND name LIKE ?");
                selection_args = new String[file_types.length + 1];
                for (int i = 0; i < file_types.length; i++) {
                    selection_args[i] = file_types[i];
                }
                selection_args[selection_args.length - 1] = "%" + query + "%";
            }
            else {
                selection = "name LIKE ?";
                selection_args = new String[] { "%" + query + "%" };
            }
            
            c = context.getContentResolver().query(OnyxLibraryItem.CONTENT_URI, null,
                    selection, selection_args, null);
            if (c == null) {
                return false;
            }
            readLibraryItemCursor(c, result);
            return true;
        }
        finally {
            if (c != null) {
                c.close();
            }
        }
    }
    
    public static boolean searchBooks (Context context, Collection<OnyxMetadata> result, String query) {
        Cursor cursor = null;
        try {
            String selection = null;
            String[] selection_args = new String[2];

            selection = "title LIKE ? OR authors LIKE ?";
            for (int i = 0; i < 2; i ++) {
                selection_args[i] = "%" + query + "%";
            }

            cursor = context.getContentResolver().query(OnyxMetadata.CONTENT_URI, null,
                    selection, selection_args, null);
            if (cursor == null) {
                return false;
            }
            readMetadataCursor(cursor, result);
            return true;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    
    public static boolean getPosition(Context context, String md5, OnyxPosition result)
    {
    	return getPosition(context, context.getPackageName(), md5, result);
    }
    
	public static boolean getPosition(Context context, String application, String md5, OnyxPosition result)
	{
        Cursor c = null;
        try {
            c = context.getContentResolver().query(OnyxPosition.CONTENT_URI, 
                    null, 
                    OnyxPosition.Columns.APPLICATION + "='" + application + "' AND " +
                    OnyxPosition.Columns.MD5 + "='" + md5 + "'", 
                    null, null);
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

    public static boolean insertPosition(Context context, OnyxPosition location)
    {
    	return insertPosition(context, context.getPackageName(), location);
    }
    
	public static boolean insertPosition(Context context, String application, OnyxPosition location)
	{
		location.setApplication(application);
		
        Uri result = context.getContentResolver().insert(
                OnyxPosition.CONTENT_URI,
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

	public static boolean updatePosition(Context context, OnyxPosition location)
	{
		return updatePosition(context, context.getPackageName(), location);
	}
	
	
	public static boolean updatePosition(Context context, String application, OnyxPosition location)
	{
		location.setApplication(application);

    	Uri row = Uri.withAppendedPath(OnyxPosition.CONTENT_URI, String.valueOf(location.getId()));
        int count = context.getContentResolver().update(row,
                OnyxPosition.Columns.createColumnData(location), null, null);
        if (count <= 0) {
            return false;
        }
        assert (count == 1);
        return true;
	}
	
    public static boolean deletePosition(Context context, OnyxPosition location)
    {
        Uri row = Uri.withAppendedPath(OnyxPosition.CONTENT_URI, String.valueOf(location.getId()));
        int count = context.getContentResolver().delete(row, null, null);
        if (count <= 0) {
            return false;
        }
        assert(count == 1);
        return true;
    }
    
    public static boolean deleteBook(Context context, String isbn)
    {
    	// Delete library Item, Metadata, Bookmark, Annotation, Position, 
    	// 		Thumbnail and History Entry of the book
    	OnyxMetadata metadata = new OnyxMetadata();
    	metadata.setISBN(isbn);

    	if (!OnyxCmsCenter.getMetadata(context, metadata)) {
    		return false;
    	}
        
        return deleteBook(context, metadata);
    }

    public static boolean deleteBook(Context context, OnyxMetadata data) {
        String md5 = data.getMD5();
        OnyxCmsCenter.deleteLibraryItem(context, data.getLocation());
        context.getContentResolver().delete(
                OnyxMetadata.CONTENT_URI, OnyxMetadata.Columns.MD5 + " = ?" ,
                new String[] { md5 });
        context.getContentResolver().delete(
                OnyxBookConfig.CONTENT_URI, OnyxBookConfig.Columns.MD5 +" = ?" ,
                new String[] { md5 });
        context.getContentResolver().delete(
                OnyxBookmark.CONTENT_URI, OnyxBookmark.Columns.MD5 + " = ?" ,
                new String[] { md5 });
        context.getContentResolver().delete(
                OnyxAnnotation.CONTENT_URI, OnyxAnnotation.Columns.MD5 + " = ?" ,
                new String[] { md5 });
        context.getContentResolver().delete(
                OnyxPosition.CONTENT_URI, OnyxPosition.Columns.MD5 + " = ?" ,
                new String[] { md5 });
        context.getContentResolver().delete(
                OnyxThumbnail.CONTENT_URI, OnyxThumbnail.Columns.SOURCE_MD5 + " = ?" ,
                new String[] { md5 });
        context.getContentResolver().delete(
                OnyxHistoryEntry.CONTENT_URI, OnyxHistoryEntry.Columns.MD5 + " = ?" ,
                new String[] { md5 });
        context.getContentResolver().delete(
                OnyxScribble.CONTENT_URI, OnyxScribble.Columns.MD5 + " = ?" ,
                new String[] { md5 });

        return true;
    }

    private static Cursor getThumbnailCursor(Context context, String md5, OnyxThumbnail.ThumbnailKind kind) {
        return context.getContentResolver().query(
                OnyxThumbnail.CONTENT_URI,
                null,
                OnyxThumbnail.Columns.SOURCE_MD5 + "='" + md5 +
                        "' AND " + OnyxThumbnail.Columns.THUMBNAIL_KIND + "='" +
                        kind.toString() + "'",
                null, null);
    }

    public static String orderString(final AscDescOrder order) {
        if(order == AscDescOrder.Asc) {
            return "ASC";
        }
        return "DESC";
    }

    public static String filterSortByString(final SortBy sortBy) {
        if (sortBy == SortBy.Name) {
            return OnyxLibraryFilter.Columns.FILTER_NAME;
        }
        return " ";
    }


    public static OnyxLibraryFilter getLibraryFilter(final Context context, long id) {
        Cursor cursor = null;
        String[] selectionArgs = new String[] {String.valueOf(id)};

        try {
            cursor = context.getContentResolver().query(OnyxLibraryFilter.CONTENT_URI,
                    null,
                    OnyxLibraryFilter.Columns._ID + "=?",
                    selectionArgs,
                    null);
            if (cursor == null) {
                Log.w(TAG, "getLibraryFilters, query database failed");
                return null;
            }
            if (cursor.moveToFirst()) {
                return OnyxLibraryFilter.Columns.readColumnData(cursor);
            }
            return null;
        } finally {
            FileUtils.closeQuietly(cursor);
        }
    }

    /**
     * Retrieve all library filters.
     * @param context
     * @param result
     * @return
     */
    public static boolean getLibraryFilters(final Context context, final SortBy sortBy, final AscDescOrder ascOrder, final Collection<OnyxLibraryFilter> result) {
        Cursor cursor = null;
        String orderString = orderString(ascOrder);
        String sortByString = filterSortByString(sortBy) + " " + orderString;

        try {
            cursor = context.getContentResolver().query(OnyxLibraryFilter.CONTENT_URI, null, null, null, sortByString);
            if (cursor == null) {
                Log.w(TAG, "getLibraryFilters, query database failed");
                return false;
            }
            readAllLibraryFilters(cursor, result);
            return true;
        } finally {
            FileUtils.closeQuietly(cursor);
        }
    }

    /**
     *
     * @param context
     * @param result
     * @return
     */
    public static boolean getLibraryFilters(final Context context, long parentId, final SortBy sortBy, final AscDescOrder ascOrder, final Collection<OnyxLibraryFilter> result) {
        Cursor cursor = null;
        String orderString = orderString(ascOrder);
        String sortByString = filterSortByString(sortBy) + " " + orderString;

        String[] selectionArgs = new String[] {String.valueOf(parentId)};
        try {
            cursor = context.getContentResolver().query(OnyxLibraryFilter.CONTENT_URI,
                    null,
                    OnyxLibraryFilter.Columns.FILTER_PARENT + " =? ",
                    selectionArgs,
                    sortByString);
            if (cursor == null) {
                Log.w(TAG, "getLibraryFilters with parent id, query database failed");
                return false;
            }
            readAllLibraryFilters(cursor, result);
            return true;
        } finally {
            FileUtils.closeQuietly(cursor);
        }
    }

    public static OnyxLibraryFilter getLibraryFilterBy(final Context context, final String columnName, final String columnValue) {
        Cursor cursor = null;
        OnyxLibraryFilter filter = null;

        String[] selectionArgs = new String[] {columnValue};
        try {
            cursor = context.getContentResolver().query(OnyxLibraryFilter.CONTENT_URI,
                    null,
                    columnName + " =? ",
                    selectionArgs,
                    null);
            if (cursor == null) {
                Log.w(TAG, "getLibraryFilters by column name and value, query database failed");
                return filter;
            }

            if (cursor.moveToFirst()) {
                filter = OnyxLibraryFilter.Columns.readColumnData(cursor);
            }
            return filter;
        } finally {
            FileUtils.closeQuietly(cursor);
        }
    }

    public static boolean clearLibraryFilters(final Context context) {
        int n = context.getContentResolver().delete(OnyxLibraryFilter.CONTENT_URI, null, null);
        return  n > 0;
    }

    private static void readAllLibraryFilters(final Cursor cursor, final Collection<OnyxLibraryFilter> result) {
        if (cursor.moveToFirst()) {
            result.add(OnyxLibraryFilter.Columns.readColumnData(cursor));
            while (cursor.moveToNext()) {
                result.add(OnyxLibraryFilter.Columns.readColumnData(cursor));
            }
        }
    }

    private static boolean addLibraryFilterImpl(final Context context, final OnyxLibraryFilter filter) {
        Uri result = context.getContentResolver().insert(
                OnyxLibraryFilter.CONTENT_URI,
                OnyxLibraryFilter.Columns.createColumnData(filter));
        if (result == null) {
            return false;
        }
        String id = result.getLastPathSegment();
        if (id == null) {
            return false;
        }
        filter.setId(Long.parseLong(id));
        return true;
    }

    public static boolean addLibraryFilter(final Context context, final OnyxLibraryFilter filter) {
        deleteLibraryFilter(context, filter.getId());
        return addLibraryFilterImpl(context, filter);
    }

    public static boolean updateLibraryFilter(final Context context, final OnyxLibraryFilter filter) {
        deleteLibraryFilter(context, filter.getId());
        return addLibraryFilterImpl(context, filter);
    }

    public static boolean deleteLibraryFilter(final Context context, final long id) {
        Uri row = Uri.withAppendedPath(OnyxLibraryFilter.CONTENT_URI, String.valueOf(id));
        int n = context.getContentResolver().delete(row, null, null);
        return n > 0;
    }

    public static boolean deleteLibraryFilter(final Context context, final String uniqueId) {
        String[] selectionArgs = new String[] {uniqueId};
        int count = context.getContentResolver().delete(OnyxLibraryFilter.CONTENT_URI,
                OnyxLibraryFilter.Columns.FILTER_UNIQUE_ID + " =? ",
                selectionArgs);
        return count > 0;
    }

    public static boolean clearLibraryContainers(final Context context) {
        int n = context.getContentResolver().delete(OnyxLibraryContainer.CONTENT_URI, null, null);
        return  n > 0;
    }

    public static boolean addLibraryContainerEntry(final Context context, final OnyxLibraryContainer mapping) {
        Uri result = context.getContentResolver().insert(
                OnyxLibraryContainer.CONTENT_URI,
                OnyxLibraryContainer.Columns.createColumnData(mapping));
        if (result == null) {
            return false;
        }
        String id = result.getLastPathSegment();
        if (id == null) {
            return false;
        }
        mapping.setId(Long.parseLong(id));
        return true;
    }

    public static boolean removeLibraryContainerEntry(final Context context, final String libraryUniqueId, final String md5) {
        String[] selectionArgs = new String[] {libraryUniqueId, md5};
        int count = context.getContentResolver().delete(OnyxLibraryContainer.CONTENT_URI,
                OnyxLibraryContainer.Columns.LIBRARY_UNIQUE_ID + " =? and " +
                OnyxLibraryContainer.Columns.ITEM_MD5 + " =? ",
                selectionArgs);
        return count > 0;
    }

    public static int removeLibraryContainer(final Context context, final String libraryUniqueId) {
        String[] selectionArgs = new String[] {libraryUniqueId};
        int count = context.getContentResolver().delete(OnyxLibraryContainer.CONTENT_URI,
                OnyxLibraryContainer.Columns.LIBRARY_UNIQUE_ID + " =? ",
                selectionArgs);
        return count;
    }

    public static List<String> getLibraryContainerItems(final Context context, final String libraryId) {
        List<String> list = new ArrayList<String>();
        Cursor cursor = null;
        String[] selectionArgs = new String[] {String.valueOf(libraryId)};
        try {
            cursor = context.getContentResolver().query(OnyxLibraryContainer.CONTENT_URI,
                    null,
                    OnyxLibraryContainer.Columns.LIBRARY_UNIQUE_ID + " =? ",
                    selectionArgs,
                    null);
            if (cursor == null) {
                Log.w(TAG, "getLibraryContainerItems by column name and value, query database failed");
                return list;
            }

            if (cursor.moveToFirst()) {
                list.add(OnyxLibraryContainer.Columns.readColumnData(cursor).getItemMd5());
                while (cursor.moveToNext()) {
                    list.add(OnyxLibraryContainer.Columns.readColumnData(cursor).getItemMd5());
                }
            }
        } finally {
            FileUtils.closeQuietly(cursor);
        }
        return list;
    }

    public static boolean addDictVocabulary(final Context context, final OnyxVocabulary vocabulary) {
        deleteDictVocabulary(context, vocabulary.getId());
        return addVocabularyImpl(context, vocabulary);
    }

    public static boolean updateVocabulary(final Context context, final OnyxVocabulary vocabulary) {
        deleteDictVocabulary(context, vocabulary.getId());
        return addVocabularyImpl(context, vocabulary);
    }

    public static boolean deleteDictVocabulary(final Context context, final long id) {
        Uri row = Uri.withAppendedPath(OnyxVocabulary.CONTENT_URI, String.valueOf(id));
        int n = context.getContentResolver().delete(row, null, null);
        return n > 0;
    }

    public static boolean deleteDictVocabulary(final Context context, final String word) {
        String[] selectionArgs = new String[]{word};
        int count = context.getContentResolver().delete(OnyxLibraryFilter.CONTENT_URI,
                OnyxVocabulary.Columns.WORD + " =? ",
                selectionArgs);
        return count > 0;
    }

    public static boolean clearDictVocabulary(final Context context) {
        int n = context.getContentResolver().delete(OnyxVocabulary.CONTENT_URI, null, null);
        return n > 0;
    }

    private static void readAllDictVocabulary(final Cursor cursor, final Collection<OnyxVocabulary> result) {
        if (cursor.moveToFirst()) {
            result.add(OnyxVocabulary.Columns.readColumnData(cursor));
            while (cursor.moveToNext()) {
                result.add(OnyxVocabulary.Columns.readColumnData(cursor));
            }
        }
    }

    private static boolean addVocabularyImpl(final Context context, final OnyxVocabulary vocabulary) {
        Uri result = context.getContentResolver().insert(
                OnyxVocabulary.CONTENT_URI,
                OnyxVocabulary.Columns.createColumnData(vocabulary));
        if (result == null) {
            return false;
        }
        String id = result.getLastPathSegment();
        if (id == null) {
            return false;
        }
        vocabulary.setId(Long.parseLong(id));
        return true;
    }

    public static OnyxVocabulary getSpecificDictVocabulary(final Context context, final String targetColumn, final String targetValue) {
        Cursor cursor = null;
        OnyxVocabulary vocabulary = null;

        String[] selectionArgs = new String[]{targetValue};
        try {
            cursor = context.getContentResolver().query(OnyxLibraryFilter.CONTENT_URI,
                    null,
                    targetColumn + " =? ",
                    selectionArgs,
                    null);
            if (cursor == null) {
                Log.w(TAG, "getSpecificDictVocabulary: Failed");
                return null;
            }

            if (cursor.moveToFirst()) {
                vocabulary = OnyxVocabulary.Columns.readColumnData(cursor);
            }
            return vocabulary;
        } finally {
            FileUtils.closeQuietly(cursor);
        }
    }
}
