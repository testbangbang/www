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
import java.util.Collection;
import java.util.List;

/**
 * Created by zhuzeng on 6/21/16.
 */
public class ShapeLibraryDataProvider {

    public static ShapeLibraryModel loadShapeDocument(final Context context, final String documentUniqueId) {
        Select select = new Select();
        Where where = select.from(ShapeLibraryModel.class).where(ShapeLibraryModel_Table.documentUniqueId.eq(documentUniqueId));
        return (ShapeLibraryModel)where.querySingle();
    }

    public static List<ShapeLibraryModel> loadShapeDocumentList(final Context context, final String parentUniqueId) {
        Select select = new Select();
        Condition condition;
        if (StringUtils.isNullOrEmpty(parentUniqueId)) {
            condition = ShapeLibraryModel_Table.parentUniqueId.isNull();
        } else {
            condition = ShapeLibraryModel_Table.parentUniqueId.eq(parentUniqueId);
        }
        Where where = select.from(ShapeLibraryModel.class).where(condition);
        List<ShapeLibraryModel> list = where.queryList();
        return list;
    }

    public static void saveShapeDocument(final Context context, final ShapeLibraryModel model) {
        if (model == null) {
            return;
        }
        model.save();
    }

    public static boolean removeShapeDocument(final Context context, final String documentUniqueId) {
        Select select = new Select();
        Where where = select.from(ShapeLibraryModel.class).where(ShapeLibraryModel_Table.documentUniqueId.eq(documentUniqueId));
        where.querySingle().delete();
        return true;
    }

    public static boolean moveShapeDocument(final Context context, final String documentUniqueId, final String newParentId) {
        Select select = new Select();
        Where where = select.from(ShapeLibraryModel.class).where(ShapeLibraryModel_Table.documentUniqueId.eq(documentUniqueId));
        final ShapeLibraryModel model = (ShapeLibraryModel)where.querySingle();
        if (model == null) {
            return false;
        }
        model.setParentUniqueId(newParentId);
        model.save();
        return true;
    }

    public static boolean hasThumbnail(final Context context, final String documentUniqueId) {
        final String path = thumbnailPath(context, documentUniqueId);
        return FileUtils.fileExist(path);
    }

    public static boolean saveThumbnail(final Context context, final String documentUniqueId, final Bitmap bitmap) {
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
