package com.onyx.edu.note.ui;

import android.databinding.BaseObservable;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableShort;
import android.util.Log;
import android.view.View;

import com.onyx.edu.note.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2017/9/5.
 */

public class MenuItem {

    private static final String TAG = "MenuItem";

    public boolean checked;

    public int visibility = View.GONE;

    public int menuIcon = android.R.color.transparent;

    public String text;

    private EventBus eventBus;
    private int menuId;

    public MenuItem(int visibility) {
        this.visibility = visibility;
    }
    public MenuItem(int visibility, String text) {
        this.visibility = visibility;
        this.text = text;
    }

    public MenuItem(int visibility, int menuIcon) {
        this.visibility = visibility;
        this.menuIcon = menuIcon;
    }


    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public void setMenuIcon(int menuIcon) {
        this.menuIcon = menuIcon;
    }

    public MenuItem setMenuId(int menuId) {
        this.menuId = menuId;
        return this;
    }

    public MenuItem setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static MenuItem createVisibleMenu() {
        return new MenuItem(View.VISIBLE);
    }

    public static MenuItem createVisibleMenu(String text) {
        return new MenuItem(View.VISIBLE, text);
    }

    public static MenuItem createVisibleMenu(int menuIcon) {
        return new MenuItem(View.VISIBLE, menuIcon);
    }

    public void menuClicked(View view) {
        eventBus.post(MenuClickEvent.create(view, menuId));
    }
}
