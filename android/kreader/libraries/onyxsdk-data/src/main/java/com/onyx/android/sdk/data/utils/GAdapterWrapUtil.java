package com.onyx.android.sdk.data.utils;

import android.graphics.Bitmap;

import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;

/**
 * Created by suicheng on 2016/9/5.
 */
public class GAdapterWrapUtil {

    static public void loadDefaultThumbnailFromExtension(String ext, GObject object) {
        object.putInt(GAdapterUtil.TAG_THUMBNAIL, ThumbnailUtils.thumbnailDefault());
    }

    static public boolean loadThumbnail(BaseDataRequest request, final Metadata item, GObject object) {
        if (request.thumbnailKind == null) {
            return false;
        }
        Bitmap bitmap = ThumbnailUtils.getThumbnailBitmap(request.getContext(), item.getIdString(), request.thumbnailKind.toString());
        if (bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
            object.putObject(GAdapterUtil.TAG_THUMBNAIL, bitmap);
        } else {
            loadDefaultThumbnailFromExtension(item.getType(), object);
        }
        return true;
    }

    public static GObject objectFromMetadata(final Metadata data, final boolean isShowTittle) {
        if (data == null) {
            return null;
        }

        GObject object = new GObject();
        if (StringUtils.isNotBlank(data.getIdString())) {
            object.putString(GAdapterUtil.TAG_UNIQUE_ID, data.getIdString());
        } else {
            object.putString(GAdapterUtil.TAG_UNIQUE_ID, data.getNativeAbsolutePath());
        }
        if (isShowTittle && StringUtils.isNotBlank(data.getTitle())) {
            object.putString(GAdapterUtil.TAG_TITLE_STRING, data.getTitle());
        } else {
            object.putString(GAdapterUtil.TAG_TITLE_STRING, data.getName());
        }
        object.putString(GAdapterUtil.FILE_TYPE, GAdapterUtil.FILE_FILE);
        object.putString(GAdapterUtil.FILE_PATH, data.getNativeAbsolutePath());
        object.putString(GAdapterUtil.TAG_AUTHOR_STRING, data.getAuthors());

        object.putObject(GAdapterUtil.TAG_LAST_MODIFY_TIME, data.getLastModified());
        if (data.getProgress() != null) {
            object.putObject(GAdapterUtil.TAG_READING_PROGRESS, data.getProgress());
        } else {
            object.putInt(GAdapterUtil.TAG_READING_PROGRESS, 0);
        }
        if (!StringUtils.isNullOrEmpty(data.getType())) {
            object.putString(GAdapterUtil.TAG_DOCUMENT_TYPE, data.getType().toUpperCase());
        }
        object.putString(GAdapterUtil.TAG_DOCUMENT_STORAGE_POSITION, data.getLocation());
        object.putLong(GAdapterUtil.TAG_DOCUMENT_SIZE, data.getSize());
        if (data.getLastAccess() != null) {
            object.putObject(GAdapterUtil.TAG_LAST_ACCESS_TIME, data.getLastAccess());
        }
        if (data.getCloudId() != null) {
            object.putString(GAdapterUtil.TAG_CLOUD_REFERENCE, data.getCloudId());
        }
        if (data.getParentId() != null) {
            object.putString(GAdapterUtil.TAG_PARENT_REFERENCE, data.getParentId());
        }
        object.putBoolean(GAdapterUtil.TAG_SELECTABLE, false);
        return object;
    }

    static public GObject objectFromLibrary(final Library data) {
        if (data == null) {
            return null;
        }

        GObject object = new GObject();
        object.putString(GAdapterUtil.TAG_UNIQUE_ID, data.getIdString());
        object.putString(GAdapterUtil.TAG_TITLE_STRING, data.getName());
        object.putString(GAdapterUtil.TAG_SUB_TITLE_STRING, data.getDescription());
        object.putString(GAdapterUtil.TAG_EXTRA_ATTRIBUTE, data.getExtraAttributes());
        object.putString(GAdapterUtil.FILE_TYPE, GAdapterUtil.FILE_LIBRARY);
        object.putInt(GAdapterUtil.TAG_THUMBNAIL, ThumbnailUtils.subLibraryThumbnailDefault());
        return object;
    }

    public static GAdapter generateBookListAdapter(BaseDataRequest request, List<Metadata> list, final boolean isShowTittle) {
        GAdapter gAdapter = new GAdapter();
        for (int i = 0; i < list.size(); i++) {
            Metadata metadata = list.get(i);
            if (metadata == null) {
                continue;
            }
            GObject object = objectFromMetadata(metadata, isShowTittle);
            if (i < request.thumbnailLimit) {
                loadThumbnail(request, metadata, object);
            }
            gAdapter.addObject(object);
        }
        return gAdapter;
    }

    public static GAdapter generateBookListAdapter(List<Library> list) {
        GAdapter gAdapter = new GAdapter();
        for (Library data : list) {
            GObject object = objectFromLibrary(data);
            if (object != null) {
                gAdapter.addObject(object);
            }
        }
        return gAdapter;
    }
}
