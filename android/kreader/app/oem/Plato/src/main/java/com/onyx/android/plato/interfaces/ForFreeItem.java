package com.onyx.android.plato.interfaces;

import com.onyx.android.plato.model.NodeModel;

/**
 * Created by li on 2017/11/2.
 */

public interface ForFreeItem<T extends NodeModel<?>> {
    void next(int msg, T next);
}