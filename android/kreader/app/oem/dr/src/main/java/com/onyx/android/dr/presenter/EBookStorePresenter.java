package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.EBookStoreData;
import com.onyx.android.dr.interfaces.EBookStoreView;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudChildLibraryListLoadRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-8-2.
 */

public class EBookStorePresenter {
    private EBookStoreView eBookStoreView;
    private EBookStoreData eBookStoreData;
    private Map<String, QueryResult<Metadata>> map = new HashMap<>();

    public EBookStorePresenter(EBookStoreView eBookStoreView) {
        this.eBookStoreView = eBookStoreView;
        eBookStoreData = new EBookStoreData();
    }

    public void getRootLibraryList(final String parentId) {
        final CloudChildLibraryListLoadRequest req = new CloudChildLibraryListLoadRequest(parentId);
        eBookStoreData.getRootLibraryList(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                eBookStoreView.setLibraryList(req.getLibraryList());
            }
        });
    }

    public void getBooks(String language) {
        eBookStoreView.setBooks(eBookStoreData.getLanguageCategoryMap().get(language));
    }

    public void getLanguageCategoryBooks(final Library library) {
        QueryArgs queryArgs = new QueryArgs();
        queryArgs.libraryUniqueId = library.getIdString();
        final CloudContentListRequest req = new CloudContentListRequest(queryArgs);
        eBookStoreData.getLanguageBooks(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                Map<String, List<Metadata>> map = eBookStoreData.getLanguageCategoryMap();
                eBookStoreView.setLanguageCategory(library.getName(), map);
            }
        });
    }
}
