package com.onyx.edu.manager.adapter;

import android.view.View;

/**
 * Created by suicheng on 2017/7/6.
 */

public interface ItemClickListener {
    void onClick(int position, View view);

    void onLongClick(int position, View view);
}
