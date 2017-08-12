package com.onyx.android.dr.interfaces;

import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;

import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-8-2.
 */

public interface EBookStoreView {
    void setLibraryList(List<Library> list);

    void setBooks(List<Metadata> productResult);

    void setLanguageCategory(String name, Map<String, List<Metadata>> map);
}
