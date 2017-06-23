package com.onyx.android.sdk.data;

import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/4/22.
 */
public class LibraryDataModel implements Serializable {
    public List<Metadata> visibleBookList = new ArrayList<>();
    public List<Library> visibleLibraryList = new ArrayList<>();
    public Map<String, CloseableReference<Bitmap>> thumbnailMap = new HashMap<>();
    public int bookCount;
    public int libraryCount;
}
