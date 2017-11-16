package com.onyx.android.plato.view;

import com.onyx.android.plato.model.ViewBox;

/**
 * Created by li on 2017/11/3.
 */

public interface TreeLayoutManager {
    void onTreeLayout(TreeView treeView);

    ViewBox onTreeLayoutCallBack();
}
