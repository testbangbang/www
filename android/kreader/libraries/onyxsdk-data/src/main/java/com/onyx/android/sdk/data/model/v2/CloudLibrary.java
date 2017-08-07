package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/5/16.
 */
@Table(database = ContentDatabase.class)
public class CloudLibrary extends Library {

    public CloudLibrary() {
    }

    public CloudLibrary(Library library) {
        setGuid(library.getGuid());
        setId(library.getId());
        setIdString(library.getIdString());
        setParentUniqueId(library.getParentUniqueId());
        setCreatedAt(library.getCreatedAt());
        setUpdatedAt(library.getUpdatedAt());
        setName(library.getName());
        setDescription(library.getDescription());
        setExtraAttributes(library.getExtraAttributes());
        setQueryString(library.getQueryString());
    }

    public static List<Library> getCloudLibraryList(List<Library> loadedLibraryList) {
        if (CollectionUtils.isNullOrEmpty(loadedLibraryList)) {
            return new ArrayList<>();
        }
        List<Library> cloudLibraryList = new ArrayList<>();
        for (Library library : loadedLibraryList) {
            CloudLibrary cloudLibrary = new CloudLibrary(library);
            cloudLibraryList.add(cloudLibrary);
        }
        return cloudLibraryList;
    }
}
