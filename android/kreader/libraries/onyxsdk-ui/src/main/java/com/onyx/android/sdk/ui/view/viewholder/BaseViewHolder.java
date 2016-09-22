package com.onyx.android.sdk.ui.view.viewholder;

import android.view.View;

/**
 * Created by ming on 16/9/22.
 */
public class BaseViewHolder{

    public final View itemView;

    public BaseViewHolder(View itemView) {
        if (itemView == null) {
            throw new IllegalArgumentException("itemView may not be null");
        }
        this.itemView = itemView;
    }
}
