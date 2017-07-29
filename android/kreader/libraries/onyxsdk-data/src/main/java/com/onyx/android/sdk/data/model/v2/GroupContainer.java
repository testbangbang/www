package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.model.Library;

import java.io.Serializable;
import java.util.List;

/**
 * Created by suicheng on 2017/7/29.
 */
public class GroupContainer implements Serializable {
    public CloudGroup group;
    public List<Library> libraryList;
}
