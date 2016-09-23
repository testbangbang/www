package com.onyx.android.sdk.ui.view.viewholder;

import android.view.View;
import android.widget.ImageView;

import com.onyx.android.sdk.ui.R;

/**
 * Created by ming on 16/9/22.
 */
public class SimpleSelectViewHolder extends BaseViewHolder{

    public ImageView selectView;
    public ImageView imageView;

    public SimpleSelectViewHolder(View itemView) {
        super(itemView);
        selectView = (ImageView) itemView.findViewById(R.id.select_view);
        imageView = (ImageView) itemView.findViewById(R.id.image_view);
    }
}
