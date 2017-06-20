package com.onyx.android.note.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.onyx.android.note.data.DataItemType;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.data.NoteModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static com.onyx.android.note.data.DataItemType.TYPE_CREATE;
import static com.onyx.android.note.data.DataItemType.TYPE_DOCUMENT;
import static com.onyx.android.note.data.DataItemType.TYPE_GOTO_UP;
import static com.onyx.android.note.data.DataItemType.TYPE_LIBRARY;


/**
 * Created by solskjaer49 on 16/6/24 11:54.
 */

public class Utils {
    public static int screenWidth, screenHeight;
    static final String TAG = Utils.class.getSimpleName();

    public static final String DOCUMENT_ID = "document_id";
    public static final String PARENT_LIBRARY_ID = "parent_library_id";
    public static final String ACTION_TYPE = "action";
    public static final String ACTION_CREATE = "create";
    public static final String ACTION_EDIT = "edit";
    public static final String ITEM_TYPE_TAG = "item_type";

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static SimpleDateFormat getDateFormat(Locale locale) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
    }

    public static void updateVisualInfo(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            Log.w(TAG, "WINDOW_SERVICE is not ready");
            return;
        }
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    private static void putItemType(GObject object, @DataItemType.DataItemTypeDef int itemType) {
        object.putInt(ITEM_TYPE_TAG, itemType);
    }

    public static GObject createNewItem(String title, int imageRes) {
        GObject object = GAdapterUtil.createTableItem(title, null, 0, 0, null);
        object.putString(GAdapterUtil.TAG_UNIQUE_ID, null);
        object.putInt(GAdapterUtil.TAG_THUMBNAIL, imageRes);
        object.putString(GAdapterUtil.TAG_SUB_TITLE_STRING, "");
        putItemType(object, TYPE_CREATE);
        return object;
    }

    public static GObject createGotoUpItem(String title, int imageRes) {
        GObject object = GAdapterUtil.createTableItem(title, null, imageRes, 0, null);
        object.putString(GAdapterUtil.TAG_UNIQUE_ID, null);
        putItemType(object, TYPE_GOTO_UP);
        return object;
    }

    public static GObject createLibraryItem(final NoteModel noteModel, final int folderRes) {
        GObject object = GAdapterUtil.createTableItem(getLibraryTitleWithSize(noteModel), null, 0, 0, null);
        object.putNonNullObject(GAdapterUtil.TAG_ORIGIN_TITLE_STRING, noteModel.getTitle());
        object.putString(GAdapterUtil.TAG_UNIQUE_ID, noteModel.getUniqueId());
        object.putNonNullObject(GAdapterUtil.TAG_THUMBNAIL, folderRes);
        object.putString(GAdapterUtil.TAG_SUB_TITLE_STRING, getDateFormat().format(noteModel.getUpdatedAt()));
        putItemType(object, TYPE_LIBRARY);
        return object;
    }

    public static GObject createDocumentItem(final NoteModel noteModel, final int docRes) {
        GObject object = GAdapterUtil.createTableItem(noteModel.getTitle(), null, 0, 0, null);
        object.putNonNullObject(GAdapterUtil.TAG_ORIGIN_TITLE_STRING, noteModel.getTitle());
        object.putString(GAdapterUtil.TAG_UNIQUE_ID, noteModel.getUniqueId());
        object.putString(GAdapterUtil.TAG_SUB_TITLE_STRING, getDateFormat().format(noteModel.getUpdatedAt()));
        object.putNonNullObject(GAdapterUtil.TAG_THUMBNAIL, noteModel.getThumbnail() == null ? docRes : noteModel.getThumbnail());
        putItemType(object, TYPE_DOCUMENT);
        return object;
    }

    private static String getLibraryTitleWithSize(final NoteModel noteModel) {
        StringBuilder builder = new StringBuilder(noteModel.getTitle());
        PageNameList pageNameList = noteModel.getPageNameList();
        builder.append("(");
        builder.append(noteModel.getSubDocCount());
        builder.append(")");
        return builder.toString();
    }

    public static GObject createNoteItem(final NoteModel noteModel, final int folderRes, final int docRes) {
        if (noteModel.isLibrary()) {
            return createLibraryItem(noteModel, folderRes);
        }
        return createDocumentItem(noteModel, docRes);
    }

    public static GObject createLibraryItemWithFullPath(final NoteModel noteModel, final int folderRes) {
        GObject object = GAdapterUtil.createTableItem(noteModel.getExtraAttributes(), null, folderRes, 0, null);
        object.putString(GAdapterUtil.TAG_UNIQUE_ID, noteModel.getUniqueId());
        putItemType(object, TYPE_LIBRARY);
        return object;
    }

    public static GAdapter adapterFromNoteModelList(final List<NoteModel> noteModelList, final int folderRes, final int docRes) {
        GAdapter adapter = new GAdapter();
        if (noteModelList == null) {
            return adapter;
        }
        for (NoteModel model : noteModelList) {
            adapter.addObject(createNoteItem(model, folderRes, docRes));
        }
        return adapter;
    }

    public static GAdapter adapterFromNoteModelListWithFullPathTitle(final List<NoteModel> noteModelList, final int folderRes, final int docRes) {
        GAdapter adapter = new GAdapter();
        for (NoteModel model : noteModelList) {
            adapter.addObject(createLibraryItemWithFullPath(model, folderRes));
        }
        return adapter;
    }

    public static boolean isNew(final GObject object) {
        return isSameType(object, TYPE_CREATE);
    }

    public static boolean isGotoUp(final GObject object) {
        return isSameType(object, TYPE_GOTO_UP);
    }

    public static boolean isLibrary(final GObject object) {
        return isSameType(object, TYPE_LIBRARY);
    }

    public static boolean isDocument(final GObject object) {
        return isSameType(object, TYPE_DOCUMENT);
    }

    public static int getItemType(GObject object) {
        if (!object.hasKey(ITEM_TYPE_TAG)) {
            return DataItemType.TYPE_INVALID;
        }
        if (!DataItemType.isValidDataItemType(object.getInt(ITEM_TYPE_TAG))) {
            return DataItemType.TYPE_INVALID;
        }
        return object.getInt(ITEM_TYPE_TAG);
    }

    private static boolean isSameType(final GObject object, int value) {
        if (!object.hasKey(ITEM_TYPE_TAG)) {
            return false;
        }
        int type = object.getInt(ITEM_TYPE_TAG);
        return type == value;
    }

}
