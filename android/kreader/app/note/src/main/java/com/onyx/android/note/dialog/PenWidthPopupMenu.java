package com.onyx.android.note.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.onyx.android.note.R;


/**
 * Created by solskjaer49 on 16/6/30 20:38.
 */

public class PenWidthPopupMenu extends PopupWindow {
    public PenWidthPopupMenu(LayoutInflater inflater, Context context) {
        super(inflater.inflate(R.layout.penwidth_popup_layout, null),
                ViewGroup.LayoutParams.WRAP_CONTENT, context.getResources().getDimensionPixelSize(R.dimen.pen_width_popup_height));
    }
}
