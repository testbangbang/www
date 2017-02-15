package com.onyx.kreader.ui.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.onyx.kreader.R;

/**
 * Created by joy on 2/13/17.
 */

public class DialogImageView extends DialogBase {

    public DialogImageView(Context context, Bitmap bitmap) {
        super(context);

        setContentView(R.layout.dialog_image_view);

        ((ImageView)findViewById(R.id.image_view)).setImageBitmap(bitmap);
    }

}
