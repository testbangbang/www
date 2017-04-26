package com.onyx.android.eschool.model;

import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/4/22.
 */
public class LibraryDataModel {
    public List<Metadata> visibleBookList = new ArrayList<>();
    public List<Library> visibleLibraryList = new ArrayList<>();
    public int bookCount;
    public int libraryCount;
}
