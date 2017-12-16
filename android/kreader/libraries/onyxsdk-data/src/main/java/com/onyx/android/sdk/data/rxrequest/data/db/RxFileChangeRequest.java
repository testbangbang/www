package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.MimeTypeUtils;

import java.io.File;
import java.util.List;

/**
 * Created by hehai on 17-12-14.
 */

public class RxFileChangeRequest extends RxBaseDBRequest {
    private List<String> pathList;

    public RxFileChangeRequest(DataManager dm, List<String> pathList) {
        super(dm);
        this.pathList = pathList;
    }

    @Override
    public RxFileChangeRequest call() throws Exception {
        for (String path : pathList) {
            File file = new File(path);
            if (!MimeTypeUtils.getDocumentExtension().contains(FileUtils.getFileExtension(file))) {
                continue;
            }
            modifyMetadataByPath(file);
        }
        return this;
    }

    private void modifyMetadataByPath(File file) {
        if (file.exists()) {
            Metadata metadata = getDataProvider().findMetadataByPath(getAppContext(), file.getAbsolutePath());
            if (metadata == null || !metadata.hasValidId()) {
                metadata = Metadata.createFromFile(file, false);
                metadata.setHashTag(file.getAbsolutePath());
                getDataProvider().saveMetadata(getAppContext(), metadata);
            }
            String parent = file.getParentFile().getName();
            Library library = getDataProvider().findLibraryByName(getAppContext(), parent);
            if (library == null || !library.hasValidId()) {
                library = new Library();
                library.setIdString(Library.generateUniqueId());
                library.setName(parent);
                library.setParentUniqueId(null);
                getDataProvider().addLibrary(library);
            }
            getDataProvider().addMetadataCollection(getAppContext(), MetadataCollection.create(metadata.getIdString(), library.getIdString()));
        } else {
            Metadata metadata = getDataProvider().findMetadataByPath(getAppContext(), file.getAbsolutePath());
            if (metadata != null && metadata.hasValidId()) {
                getDataProvider().removeMetadata(getAppContext(), metadata);
                Library library = getDataProvider().findLibraryByName(getAppContext(), file.getParentFile().getName());
                if (library != null && library.hasValidId()) {
                    getDataProvider().deleteMetadataCollection(getAppContext(), library.getIdString(), metadata.getNativeAbsolutePath());
                }
            }
        }
    }
}
