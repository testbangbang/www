package com.onyx.android.sun.view;

import com.onyx.android.sun.model.ViewBox;

/**
 * Created by li on 2017/11/3.
 */

public interface TreeLayoutManager {
    void onTreeLayout(TreeView treeView);

    ViewBox onTreeLayoutCallBack();
}
