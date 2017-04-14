package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;

import java.util.List;

/**
 * Created by suicheng on 2016/9/8.
 */
public class RemoveFromLibraryRequest extends BaseDBRequest {

    private Library library;
    private List<Metadata> removeList;

    public RemoveFromLibraryRequest(Library library, List<Metadata> list) {
        this.library = library;
        this.removeList = list;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        for (Metadata metadata : removeList) {
            dataManager.getRemoteContentProvider().deleteMetadataCollection(getContext(),
                    library.getIdString(), metadata.getIdString());
        }
    }
}
