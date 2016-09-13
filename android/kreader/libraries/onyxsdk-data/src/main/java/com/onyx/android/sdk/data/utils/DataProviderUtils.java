package com.onyx.android.sdk.data.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail.ThumbnailKind;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.utils.ComparatorUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by suicheng on 2016/9/12.
 */
public class DataProviderUtils {

    public static List<Bitmap> getThumbnailList(Context context, DataProviderBase providerBase, final List<File> fileList, int limit, ThumbnailKind kind) {
        List<Bitmap> thumbnailList = new ArrayList<>();
        Bitmap bitmap = null;
        int thumbCount = 0;
        for (File file : fileList) {
            if (file.isDirectory()) {
                continue;
            }
            String md5 = null;
            try {
                md5 = FileUtils.computeMD5(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (md5 == null) {
                continue;
            }
            if (thumbCount++ < Math.min(limit, fileList.size())) {
                bitmap = providerBase.loadThumbnailBitmap(context, md5, kind);
            }
            thumbnailList.add(bitmap == null ?
                    ThumbnailUtils.loadDefaultThumbnailFromExtension(context, FileUtils.getFileExtension(file)) :
                    bitmap);
        }
        return thumbnailList;
    }

    public static void arrangeFileList(final List<File> fileList, SortBy sortBy, final SortOrder sortOrder) {
        switch (sortBy) {
            case Name:
                Collections.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File lhs, File rhs) {
                        int i = ComparatorUtils.booleanComparator(
                                lhs.isDirectory(), rhs.isDirectory(), SortOrder.Desc);
                        if (i == 0) {
                            return ComparatorUtils.stringComparator(lhs.getName(), rhs.getName(), sortOrder);
                        }
                        return i;
                    }
                });
                break;
            case CreationTime:
                //Todo:Java 6 and belows seems could only get file's last modified time,could not get creation time.
                //reference site:http://stackoverflow.com/questions/6885269/getting-date-time-of-creation-of-a-file
                Collections.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File lhs, File rhs) {
                        int i = ComparatorUtils.booleanComparator(
                                lhs.isDirectory(), rhs.isDirectory(), SortOrder.Desc);
                        if (i == 0) {
                            return ComparatorUtils.longComparator(lhs.lastModified(), rhs.lastModified(), sortOrder);
                        }
                        return i;
                    }
                });
                break;
            case FileType:
                Collections.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File lhs, File rhs) {
                        int i = ComparatorUtils.booleanComparator(
                                lhs.isDirectory(), rhs.isDirectory(), SortOrder.Desc);
                        if (i == 0) {
                            return ComparatorUtils.stringComparator(FileUtils.getFileExtension(lhs),
                                    FileUtils.getFileExtension(rhs), sortOrder);
                        }
                        return i;
                    }
                });
                break;
            case Size:
                Collections.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File lhs, File rhs) {
                        int i = ComparatorUtils.booleanComparator(
                                lhs.isDirectory(), rhs.isDirectory(), SortOrder.Desc);
                        if (i == 0) {
                            return ComparatorUtils.longComparator(lhs.length(),
                                    rhs.length(), sortOrder);
                        }
                        return i;
                    }
                });
                break;
        }
    }

    public static void addCollections(Context context, DataProviderBase providerBase, Library library, List<Metadata> addList) {
        for (Metadata metadata : addList) {
            providerBase.deleteMetadataCollection(context, library.getParentUniqueId(), metadata.getIdString());
            MetadataCollection collection = new MetadataCollection();
            collection.setLibraryUniqueId(library.getIdString());
            collection.setDocumentUniqueId(metadata.getIdString());
            providerBase.addMetadataCollection(context, collection);
        }
    }

    public static void removeCollections(Context context, DataProviderBase providerBase, Library library, List<Metadata> removeList) {
        for (Metadata metadata : removeList) {
            providerBase.deleteMetadataCollection(context, library.getIdString(), metadata.getIdString());
            if (library.getParentUniqueId() != null) {
                MetadataCollection collection = new MetadataCollection();
                collection.setLibraryUniqueId(library.getParentUniqueId());
                collection.setDocumentUniqueId(metadata.getIdString());
                providerBase.addMetadataCollection(context, collection);
            }
        }
    }
}
