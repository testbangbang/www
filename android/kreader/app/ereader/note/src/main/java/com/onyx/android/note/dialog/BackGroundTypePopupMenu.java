package com.onyx.android.note.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.onyx.android.note.R;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;

import java.util.HashMap;


/**
 * Created by solskjaer49 on 16/7/1 11:38.
 */

public class BackGroundTypePopupMenu extends PopupWindow {
    private ContentView bgContentView;
    private PopupMenuCallback callback;
    private View parentView;
    private int displayLocX, displayLocY;
    private @NoteBackgroundType.NoteBackgroundDef int currentBackground;
    private GAdapter adapter;

    public interface PopupMenuCallback {
        void onBackGroundChanged(@NoteBackgroundType.NoteBackgroundDef int newBackground);
    }

    public BackGroundTypePopupMenu(Context context, LayoutInflater inflater, @NoteBackgroundType.NoteBackgroundDef int curBackGround, View parentView, int locX, int locY, PopupMenuCallback menuCallback) {
        super(inflater.inflate(R.layout.note_bg_popup_layout, null),
                context.getResources().getDimensionPixelSize(R.dimen.note_bg_popup_width), context.getResources().getDimensionPixelSize(R.dimen.note_bg_popup_height));
        setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));
        setOutsideTouchable(true);
        setFocusable(true);
        this.callback = menuCallback;
        this.currentBackground = curBackGround;
        this.parentView = parentView;
        this.displayLocX = locX;
        this.displayLocY = locY;
        this.bgContentView = (ContentView) getContentView().findViewById(R.id.note_bg_contentView);
        bgContentView.setShowPageInfoArea(false);
        bgContentView.setSubLayoutParameter(R.layout.note_bg_item, getItemViewDataMap());
        bgContentView.setCallback(new ContentView.ContentViewCallback() {
            @Override
            public void onItemClick(ContentItemView view) {
                GObject temp = view.getData();
                int dataIndex = bgContentView.getCurrentAdapter().getGObjectIndex(temp);
                temp.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
                currentBackground = NoteBackgroundType.translate(GAdapterUtil.getUniqueIdAsIntegerType(temp));
                bgContentView.getCurrentAdapter().setObject(dataIndex, temp);
                bgContentView.unCheckOtherViews(dataIndex, true);
                bgContentView.updateCurrentPage();
                if (callback != null) {
                    callback.onBackGroundChanged(currentBackground);
                }
            }
        });
        bgContentView.setupContent(3, 1, getNoteBgAdapter(), 0);
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
        mapping.put(GAdapterUtil.TAG_IMAGE_RESOURCE, R.id.bg_img);
        mapping.put(GAdapterUtil.TAG_SELECTABLE, R.id.bg_indicator);
        mapping.put(GAdapterUtil.TAG_TITLE_RESOURCE, R.id.bg_text);
        return mapping;
    }

    private GAdapter getNoteBgAdapter() {
        if (adapter == null) {
            adapter = new GAdapter();
            adapter.addObject(createBgItem(R.drawable.ic_business_write_white_gray_40dp, R.string.note_bg_empty, NoteBackgroundType.EMPTY));
            adapter.addObject(createBgItem(R.drawable.ic_business_write_matts_gray_40dp, R.string.note_bg_grid, NoteBackgroundType.GRID));
            adapter.addObject(createBgItem(R.drawable.ic_business_write_four_lines_gray_40dp, R.string.note_bg_line, NoteBackgroundType.LINE));
        }
        return adapter;
    }

    private GObject createBgItem(final int bgIconRes, int bgTitleRes, @NoteBackgroundType.NoteBackgroundDef int bgType) {
        GObject object = GAdapterUtil.createTableItem(bgTitleRes, 0, bgIconRes, 0, null);
        object.putString(GAdapterUtil.TAG_UNIQUE_ID, Integer.toString(bgType));
        if (bgType == currentBackground) {
            object.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
        }
        return object;
    }
}
