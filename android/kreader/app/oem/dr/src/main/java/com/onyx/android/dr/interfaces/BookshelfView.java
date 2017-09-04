package com.onyx.android.dr.interfaces;

import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;

import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-8-4.
 */

public interface BookshelfView {
    void setBooks(List<Metadata> productResult);

    void setLanguageCategory(Map<String, List<Metadata>> map);

    void setLibraryList(List<Library> list);

    void setLanguageList(List<String> languageList);
}
