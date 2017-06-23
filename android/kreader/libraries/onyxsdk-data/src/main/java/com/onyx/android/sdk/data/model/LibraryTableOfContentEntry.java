package com.onyx.android.sdk.data.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/4/30.
 */

public class LibraryTableOfContentEntry {
    public Library library;
    public List<LibraryTableOfContentEntry> children = new ArrayList<>();
}
