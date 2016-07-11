package com.onyx.android.note.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.onyx.android.note.R;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;

import java.util.HashMap;


/**
 * Created by solskjaer49 on 16/7/4 15:15.
 */

public class PenColorPopupMenu extends PopupWindow {
    private ContentView colorContentView;
    private PopupMenuCallback callback;
    private View parentView;
    private int displayLocX, displayLocY;
    private GAdapter adapter;

    public interface PopupMenuCallback {
        void onPenColorChanged(int newPenColor);
    }

    public PenColorPopupMenu(Context context, LayoutInflater inflater, View parentView, int locX, int locY, PopupMenuCallback menuCallback) {
        super(inflater.inflate(R.layout.pen_color_popup_layout, null),
                context.getResources().getDimensionPixelSize(R.dimen.pen_color_popup_width),
                context.getResources().getDimensionPixelSize(R.dimen.pen_color_popup_height));
        setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));
        setOutsideTouchable(true);
        setFocusable(true);
        this.callback = menuCallback;
        this.parentView = parentView;
        this.displayLocX = locX;
        this.displayLocY = locY;
        this.colorContentView = (ContentView) getContentView().findViewById(R.id.pen_color_contentView);
        colorContentView.setShowPageInfoArea(false);
        colorContentView.setSubLayoutParameter(R.layout.pen_color_item, getItemViewDataMap());
        colorContentView.setCallback(new ContentView.ContentViewCallback() {
            @Override
            public void onItemClick(ContentItemView view) {
                GObject temp = view.getData();
                if (callback != null) {
                    callback.onPenColorChanged(GAdapterUtil.getUniqueIdAsIntegerType(temp));
                }
            }
        });
        colorContentView.setupContent(getPenColorAdapter().size(), 1, getPenColorAdapter(), 0);
    }

    public void toggleStatus() {
        if (isShowing()) {
            dismiss();
        } else {
            showAtLocation(parentView, Gravity.NO_GRAVITY, displayLocX, displayLocY);
        }
    }

    public void show() {
        if (!isShowing()) {
            showAtLocation(parentView, Gravity.NO_GRAVITY, displayLocX, displayLocY);
        }
    }

    private HashMap<String, Integer> getItemViewDataMap() {
        HashMap<String, Integer> mapping = new HashMap<>();
        mapping.put(GAdapterUtil.TAG_IMAGE_RESOURCE, R.id.pen_color_img);
        return mapping;
    }

    private GAdapter getPenColorAdapter() {
        if (adapter == null) {
            adapter = new GAdapter();
            adapter.addObject(createColorItem(R.drawable.ic_business_write_color_white_46dp, Color.WHITE));
            //TODO:temp disable grey color.
//            adapter.addObject(createColorItem(R.drawable.ic_business_write_color_gray_1_gray_46dp, Color.LTGRAY));
//            adapter.addObject(createColorItem(R.drawable.ic_business_write_color_gray_2_gray_46dp, Color.GRAY));
            adapter.addObject(createColorItem(R.drawable.ic_business_write_color_black_black_46dp, Color.BLACK));
        }
        return adapter;
    }

    private GObject createColorItem(final int colorIconRes, int color) {
        GObject object = GAdapterUtil.createTableItem(0, 0, colorIconRes, 0, null);
        object.putInt(GAdapterUtil.TAG_UNIQUE_ID, color);
        return object;
    }
}
