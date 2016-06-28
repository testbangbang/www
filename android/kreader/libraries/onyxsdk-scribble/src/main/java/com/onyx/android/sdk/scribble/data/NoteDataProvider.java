package com.onyx.android.sdk.scribble.data;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.io.File;
import java.util.List;

/**
 * Created by zhuzeng on 6/21/16.
 */
public class NoteDataProvider {

    public static NoteModel load(final Context context, final String uniqueId) {
        Select select = new Select();
        Where where = select.from(NoteModel.class).where(NoteModel_Table.uniqueId.eq(uniqueId));
        return (NoteModel)where.querySingle();
    }

    /**
     * Returns note document and library.
     * @param context
     * @param parentUniqueId
     * @return
     */
    public static List<NoteModel> loadNoteList(final Context context, final String parentUniqueId) {
        Select select = new Select();
        Condition condition;
        if (StringUtils.isNullOrEmpty(parentUniqueId)) {
            condition = NoteModel_Table.parentUniqueId.isNull();
        } else {
            condition = NoteModel_Table.parentUniqueId.eq(parentUniqueId);
        }
        Where where = select.from(NoteModel.class).where(condition);
        List<NoteModel> list = where.queryList();
        return list;
    }

    /**
     * Returns note document and library.
     * @param context
     * @return
     */
    public static List<NoteModel> loadAllNoteLibraryList(final Context context) {
        Select select = new Select();
        Condition condition;
        condition = NoteModel_Table.type.eq(NoteModel.TYPE_LIBRARY);
        Where where = select.from(NoteModel.class).where(condition);
        List<NoteModel> list = where.queryList();
        return list;
    }

    public static void saveNote(final Context context, final NoteModel model) {
        if (model == null) {
            return;
        }
        model.save();
    }

    public static boolean remove(final Context context, final String uniqueId) {
        Select select = new Select();
        Where where = select.from(NoteModel.class).where(NoteModel_Table.uniqueId.eq(uniqueId));
        where.querySingle().delete();
        return true;
    }

    public static boolean moveNote(final Context context, final String uniqueId, final String newParentId) {
        Select select = new Select();
        Where where = select.from(NoteModel.class).where(NoteModel_Table.uniqueId.eq(uniqueId));
        final NoteModel model = (NoteModel)where.querySingle();
        if (model == null) {
            return false;
        }
        model.setParentUniqueId(newParentId);
        model.save();
        return true;
    }

    public static NoteModel createNote(final Context context, final String parentUniqueId, final String title) {
        NoteModel noteModel = NoteModel.createNote(parentUniqueId, title);
        saveNote(context, noteModel);
        return noteModel;
    }

    public static NoteModel createLibrary(final Context context, final String parentUniqueId, final String title) {
        NoteModel noteModel = NoteModel.createLibrary(parentUniqueId, title);
        saveNote(context, noteModel);
        return noteModel;
    }

    public static boolean hasThumbnail(final Context context, final String documentUniqueId) {
        final String path = thumbnailPath(context, documentUniqueId);
        return FileUtils.fileExist(path);
    }

    public static boolean saveThumbnail(final Context context, final String documentUniqueId, final Bitmap bitmap) {
        if (StringUtils.isNullOrEmpty(documentUniqueId) || bitmap == null) {
            return false;
        }
        final String path = thumbnailPath(context, documentUniqueId);
        return BitmapUtils.saveBitmap(bitmap, path);
    }

    public static Bitmap loadThumbnail(final Context context, final String documentUniqueId) {
        final String path = thumbnailPath(context, documentUniqueId);
        return BitmapUtils.loadBitmapFromFile(path);
    }

    public static void removeAllThumbnails(final Context context) {
        FileUtils.purgeDirectory(new File(thumbnailBasePath(context)));
    }

    public static String thumbnailBasePath(final Context context) {
        PackageManager packageManager = context.getPackageManager();
        String s = context.getPackageName();
        try {
            PackageInfo p = packageManager.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
        } catch (Exception e) {
            e.printStackTrace();
        }

        String path = s + "/thumbnails";
        if (!FileUtils.fileExist(path)) {
            FileUtils.mkdirs(path);
        }
        return path;
    }

    public static String thumbnailPath(final Context context, final String id) {
        String path = thumbnailBasePath(context);
        return path + "/" + id + ".png";
    }

}
