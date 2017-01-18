package com.onyx.android.sdk.data;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joy on 14-3-14.
 */
public final class GAdapterUtil {
    private static final String TAG = GAdapterUtil.class.getSimpleName();
    private static HashMap<String, Integer> defaultGlobalMapping;

    public static final String TAG_UNIQUE_ID = "id";
    public static final String TAG_TITLE_RESOURCE = "title_resource";
    public static final String TAG_TITLE_STRING = "title_string";
    public static final String TAG_ORIGIN_TITLE_STRING = "origin_title_string";
    public static final String TAG_SUB_TITLE_RESOURCE = "sub_title";
    public static final String TAG_SUB_TITLE_STRING = "sub_title_string";
    public static final String TAG_IMAGE_RESOURCE = "image_resource";
    public static final String TAG_IMAGE_DRAWABLE = "image_drawable";
    public static final String TAG_LAYOUT_RESOURCE = "layout_resource";
    public static final String TAG_LAYOUT_PARAMS = "layout_params";
    public static final String TAG_LAYOUT_MAPPING = "layout_mapping";
    public static final String TAG_CALLBACK_MAPPING = "callback_mapping";
    public static final String TAG_THUMBNAIL = "thumbnail";
    public static final String TAG_TYPEFACE = "typeface";
    public static final String TAG_EXTRA_ATTRIBUTE = "extra_attribute";

    public static final String TAG_IN_SELECTION = "in_selection";
    public static final String TAG_SELECTED = "selected";
    public static final String TAG_SELECTION_CHECKED_VIEW_ID = "selection_checked_view_id";
    public static final String TAG_SELECTION_UNCHECKED_VIEW_ID = "selection_unchecked_view_id";

    public static final String TAG_COVER_URL = "cover_url";
    public static final String TAG_DATA_URL = "data_url";
    public static final String TAG_ORIGIN_OBJ = "origin_obj";
    public static final String TAG_SELECTABLE = "selectable";
    public static final String TAG_DIVIDER_VIEW = "divider_view";
    public static final String TAG_MENU_CLOSE_AFTER_CLICK = "menu_close_after_click";

    public static final String TAG_IMAGE_BACKGROUND = "image_background";
    public static final String TAG_AUTHOR_STRING = "author";
    public static final String TAG_READING_PROGRESS = "reading_progress";
    public static final String TAG_LAST_ACCESS_TIME = "last_access_time";
    public static final String TAG_LAST_MODIFY_TIME = "last_modify_time";
    public static final String TAG_CLOUD_REFERENCE = "cloud_ref";
    public static final String TAG_PARENT_REFERENCE = "parent_ref";
    public static final String TAG_DOCUMENT_TYPE = "document_type";
    public static final String TAG_DOCUMENT_STORAGE_POSITION = "document_storage_position";
    public static final String TAG_DOCUMENT_SIZE = "document_size";
    public static final String TAG_SINGLE_METADATA = "metadata";
    public static final String TAG_DECORATION_VIEW = "decoration_view";


    public static GObject createThumbnailItem(int titleResource, int subTitleResource, int imageResource, int layoutResource,
                                              Map<String, Integer> layoutMapping) {
        GObject object = new GObject();
        object.putObject(TAG_TITLE_RESOURCE, titleResource);
        object.putObject(TAG_SUB_TITLE_RESOURCE, subTitleResource);
        object.putObject(TAG_IMAGE_RESOURCE, imageResource);
        object.putInt(TAG_LAYOUT_RESOURCE, layoutResource);
        object.putNonNullObject(TAG_LAYOUT_MAPPING, layoutMapping);
        return object;
    }

    public static GObject createThumbnailItem(final String titleString, final String subTitleString, int imageResource, int layoutResource,
                                              Map<String, Integer> layoutMapping) {
        GObject object = new GObject();
        object.putObject(TAG_TITLE_STRING, titleString);
        object.putObject(TAG_SUB_TITLE_STRING, subTitleString);
        object.putObject(TAG_IMAGE_RESOURCE, imageResource);
        object.putInt(TAG_LAYOUT_RESOURCE, layoutResource);
        object.putNonNullObject(TAG_LAYOUT_MAPPING, layoutMapping);
        return object;
    }

    public static GObject createTableItem(int titleResource, int subTitleResource, int imageResource, int layoutResource,
                                          Map<String, Integer> layoutMapping) {
        GObject o = new GObject();
        o.putObject(TAG_TITLE_RESOURCE, titleResource);
        o.putObject(TAG_SUB_TITLE_RESOURCE, subTitleResource);
        o.putObject(TAG_IMAGE_RESOURCE, imageResource);
        o.putInt(TAG_LAYOUT_RESOURCE, layoutResource);
        o.putNonNullObject(TAG_LAYOUT_MAPPING, layoutMapping);
        return o;
    }

    public static GObject createTableItem(String title, String subTitle, int imageResource, int layoutResource,
                                          Map<String, Integer> layoutMapping) {
        GObject o = new GObject();
        o.putNonNullObject(TAG_TITLE_STRING, title);
        o.putNonNullObject(TAG_SUB_TITLE_STRING, subTitle);
        o.putObject(TAG_IMAGE_RESOURCE, imageResource);
        o.putInt(TAG_LAYOUT_RESOURCE, layoutResource);
        o.putNonNullObject(TAG_LAYOUT_MAPPING, layoutMapping);
        return o;
    }

    public static GObject createTableItem(String title, String subTitle, int imageResource, int layoutResource,
                                          Map<String, Integer> layoutMapping,
                                          Map<Integer, Map<Class<?>, Object>> callbackMapping) {
        GObject o = new GObject();
        o.putNonNullObject(TAG_TITLE_STRING, title);
        o.putNonNullObject(TAG_SUB_TITLE_STRING, subTitle);
        o.putObject(TAG_IMAGE_RESOURCE, imageResource);
        o.putInt(TAG_LAYOUT_RESOURCE, layoutResource);
        o.putNonNullObject(TAG_LAYOUT_MAPPING, layoutMapping);
        o.putNonNullObject(TAG_CALLBACK_MAPPING, callbackMapping);
        return o;
    }

    public static GObject createTableItem(String title, String subTitle, int imageResource, int layoutResource) {
        GObject o = new GObject();
        o.putNonNullObject(TAG_TITLE_STRING, title);
        o.putNonNullObject(TAG_SUB_TITLE_STRING, subTitle);
        o.putNonNullObject(TAG_IMAGE_RESOURCE, Integer.valueOf(imageResource));
        o.putInt(TAG_LAYOUT_RESOURCE, layoutResource);
        return o;
    }

    public static GObject createTableItem(String title, String subTitle, Drawable imageDrawable, int layoutResource,
                                          Map<String, Integer> layoutMapping) {
        GObject o = new GObject();
        o.putObject(TAG_TITLE_STRING, title);
        o.putObject(TAG_SUB_TITLE_STRING, subTitle);
        o.putObject(TAG_IMAGE_DRAWABLE, imageDrawable);
        o.putInt(TAG_LAYOUT_RESOURCE, layoutResource);
        o.putNonNullObject(TAG_LAYOUT_MAPPING, layoutMapping);
        return o;
    }

    public static GObject createTableItem(String title, String subTitle, int imageResource, int layoutResource,
                                          int selectionCheckedViewId, int selectionUncheckedViewId,
                                          Map<String, Integer> layoutMapping) {
        GObject o = new GObject();
        o.putNonNullObject(TAG_TITLE_STRING, title);
        o.putNonNullObject(TAG_SUB_TITLE_STRING, subTitle);
        o.putInt(TAG_IMAGE_RESOURCE, imageResource);
        o.putInt(TAG_LAYOUT_RESOURCE, layoutResource);
        o.putInt(TAG_SELECTION_CHECKED_VIEW_ID, selectionCheckedViewId);
        o.putInt(TAG_SELECTION_UNCHECKED_VIEW_ID, selectionUncheckedViewId);
        o.putNonNullObject(TAG_LAYOUT_MAPPING, layoutMapping);
        return o;
    }

    public static GObject createTableItem(String title, String subTitle, Drawable imageDrawable, int layoutResource,
                                          int selectionCheckedViewId, int selectionUncheckedViewId,
                                          Map<String, Integer> layoutMapping) {
        GObject o = new GObject();
        o.putNonNullObject(TAG_TITLE_STRING, title);
        o.putNonNullObject(TAG_SUB_TITLE_STRING, subTitle);
        o.putObject(TAG_IMAGE_DRAWABLE, imageDrawable);
        o.putInt(TAG_LAYOUT_RESOURCE, layoutResource);
        o.putInt(TAG_SELECTION_CHECKED_VIEW_ID, selectionCheckedViewId);
        o.putInt(TAG_SELECTION_UNCHECKED_VIEW_ID, selectionUncheckedViewId);
        o.putNonNullObject(TAG_LAYOUT_MAPPING, layoutMapping);
        return o;
    }

    public static int getLayoutResource(GObject object) {
        return object.getInt(TAG_LAYOUT_RESOURCE);
    }

    public static ViewGroup.LayoutParams getLayoutParams(GObject object) {
        return (ViewGroup.LayoutParams) object.getObject(TAG_LAYOUT_PARAMS);
    }

    public static Map<String, Integer> getLayoutMapping(GObject object) {
        return (Map<String, Integer>) object.getObject(TAG_LAYOUT_MAPPING);
    }

    static public final String FILE_TITLE = "name";
    static public final String FILE_PATH = "path";
    static public final String FILE_SIZE = "size";
    static public final String FILE_TYPE = "type";
    static public final String FILE_FILE = "file";
    static public final String FILE_DIRECTORY = "dir";
    static public final String FILE_LIBRARY = "lib";
    static public final String GO_UP_TYPE = "up";

    static public GObject objectFromFile(final File file) {
        GObject object = new GObject();
        object.putString(TAG_TITLE_STRING, file.getName());
        object.putString(FILE_PATH, file.getAbsolutePath());
        object.putLong(FILE_SIZE, file.length());
        object.putString(FILE_TYPE, FILE_FILE);
        object.putBoolean(GAdapterUtil.TAG_SELECTABLE, false);
        return object;
    }

    static public GObject objectFromDirectory(final File file) {
        GObject object = new GObject();
        object.putString(TAG_TITLE_STRING, file.getName());
        object.putString(FILE_PATH, file.getAbsolutePath());
        object.putString(FILE_TYPE, FILE_DIRECTORY);
        object.putBoolean(GAdapterUtil.TAG_SELECTABLE, false);
        return object;
    }

    static public void sortByKey(GAdapter adapter, final String key) {

    }

    static public boolean hasFilePath(GObject object) {
        return object.hasKey(GAdapterUtil.FILE_PATH);
    }

    static public File getFilePath(GObject object) {
        return new File(object.getString(GAdapterUtil.FILE_PATH));
    }

    static public boolean isFile(final GObject object) {
        return object.getString(GAdapterUtil.FILE_TYPE).
                equalsIgnoreCase(GAdapterUtil.FILE_FILE);
    }

    static public boolean isGoUp(GObject object) {
        return object.getString(GAdapterUtil.FILE_TYPE).equals(GAdapterUtil.GO_UP_TYPE);
    }

    static public boolean isDirectory(GObject object) {
        return object.getString(GAdapterUtil.FILE_TYPE).
                equalsIgnoreCase(GAdapterUtil.FILE_DIRECTORY);
    }

    static public boolean isSubLibrary(final GObject object) {
        return object.hasKey(FILE_TYPE) && object.getString(FILE_TYPE).equals(FILE_LIBRARY);
    }

    static public boolean hasThumbnail(GObject object) {
        if (object == null) {
            return false;
        }
        if (!object.hasKey(GAdapterUtil.TAG_THUMBNAIL)) {
            return false;
        }
        Object thumbnail = object.getObject(GAdapterUtil.TAG_THUMBNAIL);
        return !(thumbnail == null || !(thumbnail instanceof Bitmap));
    }

    /**
     * Method to update Adapter's Content.
     *
     * @param originalAdapter
     * @param newDataAdapter
     * @param key                the key to specify which object to change.
     * @param valueTagList       list of the value tag which is intended to change.
     * @param lookupTable        the table which specific the update range.
     * @param isReplaceOldObject the key to specify the update operate method. true is replace whole object
     *                           false is just update the value which define in @param valueTagList.
     */

    static public void updateAdapterContent(final GAdapter originalAdapter, GAdapter newDataAdapter,
                                            String key, ArrayList<String> valueTagList, Map<String, Integer> lookupTable,
                                            boolean isReplaceOldObject) {
        if (GAdapterUtil.isNullOrEmpty(newDataAdapter)) {
            return;
        }
        for (int i = 0; i < newDataAdapter.size(); ++i) {
            GObject object = newDataAdapter.get(i);
            final String id = object.getString(key);
            if (lookupTable.containsKey(id)) {
                int index = lookupTable.get(id);
                if (index >= 0 && index < originalAdapter.size()) {
                    if (isReplaceOldObject) {
                        originalAdapter.getList().set(index, object);
                        continue;
                    }
                    GObject temp = originalAdapter.getList().get(index);
                    for (String valueTag : valueTagList) {
                        temp.putObject(valueTag, object.getObject(valueTag));
                    }
                    originalAdapter.getList().set(index, temp);
                }
            }
        }
    }

    /**
     * Overload Method updateAdapterContent,which is ready for replace all old object.
     *
     * @param originalAdapter
     * @param newDataAdapter
     * @param key             the key to specify which object to change.
     * @param lookupTable     the table which specific the update range.
     */

    static public void updateAdapterContent(final GAdapter originalAdapter, GAdapter newDataAdapter,
                                            String key, Map<String, Integer> lookupTable) {
        updateAdapterContent(originalAdapter, newDataAdapter, key, new ArrayList<String>(), lookupTable, true);
    }

    /**
     * Overload Method updateAdapterContent,which is ready for only update one value.
     *
     * @param originalAdapter
     * @param newDataAdapter
     * @param key             the key to specify which object to change.
     * @param valueTag
     * @param lookupTable     the table which specific the update range.
     */

    static public void updateAdapterContent(final GAdapter originalAdapter, GAdapter newDataAdapter,
                                            String key, String valueTag, Map<String, Integer> lookupTable,
                                            boolean isReplaceOldObject) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(valueTag);
        updateAdapterContent(originalAdapter, newDataAdapter, key, list, lookupTable, isReplaceOldObject);
    }

    static public int getUniqueIdAsIntegerType(GObject object) {
        return object.getInt(GAdapterUtil.TAG_UNIQUE_ID);
    }

    static public String getUniqueId(GObject object) {
        return object.getString(GAdapterUtil.TAG_UNIQUE_ID);
    }

    static public boolean hasCoverUrl(GObject object) {
        return object.hasKey(GAdapterUtil.TAG_COVER_URL);
    }

    static public String getCoverUrl(GObject object) {
        return object.getString(GAdapterUtil.TAG_COVER_URL);
    }

    static public String getDataUrl(GObject object) {
        return object.getString(GAdapterUtil.TAG_DATA_URL);
    }

    static public final Object getOriginObject(final GObject object) {
        return object.getObject(GAdapterUtil.TAG_ORIGIN_OBJ);
    }

    static public int indexOf(List<GObject> list, GObject object, final String tag) {
        for (int i = 0; i < list.size(); ++i) {
            Object source = list.get(i).getObject(tag);
            Object target = object.getObject(tag);
            if (source.equals(target)) {
                return i;
            }
        }
        return -1;
    }

    static public List<String> toFileList(GAdapter adapter) {
        return toFileList(adapter.getList());
    }

    static public List<String> toFileList(List<GObject> list) {
        List<String> result = new ArrayList<String>();
        for (GObject object : list) {
            result.add(GAdapterUtil.getFilePath(object).getAbsolutePath());
        }
        return result;
    }

    static public List<String> toFileList(GObject object) {
        List<String> list = new ArrayList<String>();
        if (object == null) {
            return list;
        }
        list.add(GAdapterUtil.getFilePath(object).getAbsolutePath());
        return list;
    }

    static public boolean isNullOrEmpty(GAdapter adapter) {
        if (adapter == null || adapter.getList() == null || adapter.size() <= 0) {
            return true;
        }
        return false;
    }

    static public boolean isEqual(final GObject source, final GObject target, final String tag) {
        if (source == null && target == null) {
            return false;
        }
        if (source == null || target == null) {
            return false;
        }
        Object sourceObject = source.getObject(tag);
        Object targetObject = target.getObject(tag);
        if (sourceObject != null && targetObject != null) {
            return sourceObject.equals(targetObject);
        }
        return false;
    }

}
