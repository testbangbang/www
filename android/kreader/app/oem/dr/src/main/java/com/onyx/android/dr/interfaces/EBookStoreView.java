package com.onyx.android.dr.interfaces;

import com.onyx.android.sdk.data.model.Metadata;

import java.util.List;

/**
 * Created by hehai on 17-8-2.
 */

public interface EBookStoreView {
    void setBooks(List<Metadata> productResult);

    void setLanguageList(List<String> languageList);

    void setOrderId(String id);

    void setCartCount(int count);
}
