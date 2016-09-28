package com.onyx.android.sdk.ui.view.viewholder;

import android.support.annotation.IdRes;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by ming on 16/9/22.
 */
public class SimpleMarkerViewHolder extends BaseViewHolder{

    public ImageView markerView;
    public ImageView contentView;

    public SimpleMarkerViewHolder(View itemView, @IdRes int markerViewId, @IdRes int contentViewId) {
        super(itemView);
        markerView = (ImageView) itemView.findViewById(markerViewId);
        contentView = (ImageView) itemView.findViewById(contentViewId);
    }
}
