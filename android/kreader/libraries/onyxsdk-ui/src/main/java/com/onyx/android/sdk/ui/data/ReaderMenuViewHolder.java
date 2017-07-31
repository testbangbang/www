package com.onyx.android.sdk.ui.data;

import android.view.View;

import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.ui.view.CommonViewHolder;

import java.util.List;

/**
 * Created by ming on 2017/7/27.
 */

public class ReaderMenuViewHolder extends CommonViewHolder {

    private ReaderMenuViewHolder parent;
    private ReaderMenuAction menuAction;

    public ReaderMenuViewHolder(View itemView) {
        super(itemView);
    }

    public ReaderMenuViewHolder(View itemView, ReaderMenuAction menuAction) {
        super(itemView);
        this.menuAction = menuAction;
    }

    public ReaderMenuViewHolder(View itemView, ReaderMenuViewHolder parent, ReaderMenuAction menuAction) {
        super(itemView);
        this.parent = parent;
        this.menuAction = menuAction;
    }

    public ReaderMenuAction getMenuAction() {
        return menuAction;
    }

    public void setMenuAction(ReaderMenuAction menuAction) {
        this.menuAction = menuAction;
    }

    public ReaderMenuViewHolder getParent() {
        return parent;
    }

    public void setParent(ReaderMenuViewHolder parent) {
        this.parent = parent;
    }

    public static ReaderMenuViewHolder create(View itemView) {
        return new ReaderMenuViewHolder(itemView);
    }

    public static ReaderMenuViewHolder create(View itemView, ReaderMenuAction menuAction) {
        return new ReaderMenuViewHolder(itemView, menuAction);
    }

    public static ReaderMenuViewHolder create(View itemView, ReaderMenuViewHolder parent, ReaderMenuAction menuAction) {
        return new ReaderMenuViewHolder(itemView, parent, menuAction);
    }

    public static ReaderMenuViewHolder create(CommonViewHolder viewHolder, ReaderMenuAction menuAction) {
        return new ReaderMenuViewHolder(viewHolder.itemView, menuAction);
    }
}
