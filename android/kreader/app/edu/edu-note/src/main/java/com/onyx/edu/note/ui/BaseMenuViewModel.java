package com.onyx.edu.note.ui;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayMap;
import android.support.annotation.CallSuper;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;

import com.onyx.android.sdk.ui.data.MenuClickEvent;
import com.onyx.android.sdk.ui.data.MenuItem;
import com.onyx.android.sdk.utils.Debug;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by lxm on 2017/8/31.
 */

public class BaseMenuViewModel extends BaseObservable {

    private static final String TAG = "BaseMenuViewModel";

//    private Set<Integer> checkedMenuIds;
//    private Set<Integer> enabledMenuIds;
//    private Set<Integer> showedMenuIds;

    private ObservableArrayMap<Integer, MenuItem> menuItems = new ObservableArrayMap<>();
    private SparseArray<View> views;
    private EventBus eventBus;

    public BaseMenuViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
        views = new SparseArray<>();

    }

    public void bindMenu(int menuAction, View view) {
        views.put(menuAction, view);
    }

    public BaseMenuViewModel show(List<Integer> showedMenuIds) {
        for(int i = 0; i < views.size(); i++) {
            int key = views.keyAt(i);
            View view = views.get(key);
            view.setVisibility(showedMenuIds.contains(key) ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public BaseMenuViewModel check(List<Integer> checkedMenuIds) {
        for(int i = 0; i < views.size(); i++) {
            int key = views.keyAt(i);
            View view = views.get(key);
            checkImpl(view, checkedMenuIds.contains(key));
        }
        return this;
    }

    private void checkChildView(ViewGroup parent, boolean checked) {
        int size = parent.getChildCount();
        for (int i = 0; i < size; i++) {
            View view = parent.getChildAt(i);
            checkImpl(view, checked);
        }
    }

    private void checkImpl(View view, boolean checked) {
        if (view instanceof Checkable) {
            ((Checkable) view).setChecked(checked);
        }
        if (view instanceof ViewGroup) {
            checkChildView((ViewGroup) view, checked);
        }
    }

    public BaseMenuViewModel enable(List<Integer> enabledMenuIds) {
        for(int i = 0; i < views.size(); i++) {
            int key = views.keyAt(i);
            View view = views.get(key);
            view.setEnabled(enabledMenuIds.contains(key));
        }
        return this;
    }

    public View getMenuView(int menuAction) {
        return views.get(menuAction);
    }

    public int getMenuAction(View view) {
        Debug.d(getClass(), "getMenuId start: ");
        int index = views.indexOfValue(view);
        int  menuAction = views.keyAt(index);
        Debug.d(getClass(), "getMenuId end: ");
        return menuAction;
    }

    public void menuClicked(View view) {
        Log.d(TAG, "onClick: " + view.getId() + "---action" + getMenuAction(view));
        eventBus.post(MenuClickEvent.create(view, getMenuAction(view)));
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    @CallSuper
    public void onDestroy() {
        views.clear();
        views = null;
        eventBus.unregister(this);
    }
}
