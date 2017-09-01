package com.onyx.edu.note.ui;

import android.databinding.BaseObservable;
import android.support.annotation.CallSuper;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.onyx.android.sdk.ui.data.MenuAction;
import com.onyx.android.sdk.utils.Debug;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2017/8/31.
 */

public abstract class BaseMenuViewModel extends BaseObservable {

    private static final String TAG = "BaseMenuViewModel";

    private SparseArray<View> views;
    private EventBus eventBus;

    public BaseMenuViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
        views = new SparseArray<>();
    }

    public void bindMenu(@MenuAction.ActionDef int menuAction, View view) {
        views.put(menuAction, view);
    }

    public View getMenuView(@MenuAction.ActionDef int menuAction) {
        return views.get(menuAction);
    }

    public @MenuAction.ActionDef
    int getMenuAction(View view) {
        Debug.d(getClass(), "getMenuAction start: ");
        int index = views.indexOfValue(view);
        @MenuAction.ActionDef int  menuAction = MenuAction.translate(views.keyAt(index));
        Debug.d(getClass(), "getMenuAction end: ");
        return menuAction;
    }

    public void itemClicked(View view) {
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
