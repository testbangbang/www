package com.onyx.android.eschool.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;

/**
 * Created by suicheng on 2017/4/18.
 */

public class ContentBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = ContentBroadcastReceiver.class.getSimpleName();

    public static final String CONTENT_EXTRA_NAME = "data";
    public static final String ACTION_LIBRARY_BUILD = "com.onyx.content.database.ACTION_BUILD_LIBRARY";
    public static final String ACTION_ADD_FILE_TO_LIBRARY = "com.onyx.content.database.ACTION_ADD_TO_LIBRARY";

    @Override
    public void onReceive(Context context, Intent intent) {
        String data = intent.getStringExtra(CONTENT_EXTRA_NAME);
        if (ACTION_LIBRARY_BUILD.equals(intent.getAction())) {
            buildLibrary(data);
        } else if (ACTION_ADD_FILE_TO_LIBRARY.equals(intent.getAction())) {
            addFileToLibrary(context, data);
        }
    }

    private void buildLibrary(String data) {
        if (StringUtils.isNullOrEmpty(data)) {
            return;
        }
        Library library = JSON.parseObject(data, Library.class);
        if (library == null || StringUtils.isNullOrEmpty(library.getIdString())) {
            return;
        }
        DataProviderBase providerBase = SchoolApp.getDataManager().getRemoteContentProvider();
        Library loadLibrary = providerBase.loadLibrary(library.getIdString());
        if (loadLibrary != null) {
            library.setId(loadLibrary.getId());
            providerBase.updateLibrary(library);
        } else {
            providerBase.addLibrary(library);
        }
    }

    private void addFileToLibrary(Context context, String data) {
        if (StringUtils.isNullOrEmpty(data)) {
            return;
        }
        ContentModel contentModel = JSON.parseObject(data, ContentModel.class);
        if (contentModel == null || StringUtils.isNullOrEmpty(contentModel.path)) {
            Log.w(TAG, "jsonString or path is null");
            return;
        }
        File file = new File(contentModel.path);
        if (!file.exists()) {
            Log.w(TAG, "add file to library, but file does't exists");
            return;
        }
        Metadata findMeta = SchoolApp.getDataManager().getRemoteContentProvider().findMetadataByPath(
                context, contentModel.path);
        if (findMeta == null || !findMeta.hasValidId()) {
            createMetadataAndAddToLibrary(context, contentModel);
        } else {
            updateMetadataAndAddToLibrary(context, findMeta, contentModel);
        }
    }

    private void updateMetadataAndAddToLibrary(Context context, Metadata metadata, ContentModel contentModel) {
        DataProviderBase providerBase = SchoolApp.getDataManager().getRemoteContentProvider();
        MetadataCollection collection = providerBase.findMetadataCollection(context, metadata.getIdString());
        if (collection == null) {
            if (StringUtils.isNullOrEmpty(contentModel.libraryIdString)) {
                return;
            }
            collection = MetadataCollection.create(metadata.getIdString(), contentModel.libraryIdString);
            providerBase.addMetadataCollection(context, collection);
        } else {
            if (StringUtils.isNullOrEmpty(contentModel.libraryIdString)) {
                providerBase.deleteMetadataCollection(context, collection.getLibraryUniqueId(), collection.getDocumentUniqueId());
            } else {
                collection.setLibraryUniqueId(contentModel.libraryIdString);
                providerBase.updateMetadataCollection(collection);
            }
        }
    }

    private void createMetadataAndAddToLibrary(Context context, ContentModel contentModel) {
        DataProviderBase providerBase = SchoolApp.getDataManager().getRemoteContentProvider();
        File file = new File(contentModel.path);
        Metadata metadata = Metadata.createFromFile(file, false);
        if (metadata == null) {
            return;
        }
        metadata.setTitle(contentModel.title);
        providerBase.saveMetadata(context, metadata);
        if (StringUtils.isNullOrEmpty(contentModel.libraryIdString)) {
            return;
        }
        MetadataCollection metadataCollection = MetadataCollection.create(metadata.getIdString(),
                contentModel.libraryIdString);
        providerBase.addMetadataCollection(context, metadataCollection);
    }

    public static class ContentModel {
        public String path;
        public String thumbnail;
        public String title;
        public String libraryIdString;
        public String libraryName;
    }
}
