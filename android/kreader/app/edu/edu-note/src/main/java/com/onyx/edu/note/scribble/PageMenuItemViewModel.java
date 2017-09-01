package com.onyx.edu.note.scribble;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;
import com.onyx.edu.note.data.ScribbleFunctionMenuIDType;
import com.onyx.edu.note.data.ScribbleSubMenuID;
import com.onyx.edu.note.ui.MenuClickEvent;
import com.onyx.edu.note.util.ScribbleFunctionItemUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

/**
 * Created by lxm on 2017/9/1.
 */

public class PageMenuItemViewModel extends BaseObservable {

    private static final String TAG = PageMenuItemViewModel.class.getSimpleName();

    public final ObservableInt mIconRes = new ObservableInt();
    public final ObservableBoolean mIsChecked = new ObservableBoolean();
    public final ObservableBoolean mShowIndicator = new ObservableBoolean();

    public int getItemID() {
        return mItemID;
    }

    private int mItemID;
    private EventBus eventBus;
    private @ScribbleFunctionMenuIDType.ScribbleMenuIDTypeDef
    int mItemMenuIDType;

    public PageMenuItemViewModel(EventBus eventBus,
                                 int itemID,
                                 @ScribbleFunctionMenuIDType.ScribbleMenuIDTypeDef int itemMenuIDType) {
        this.eventBus = eventBus;
        mItemMenuIDType = itemMenuIDType;
        mItemID = itemID;
        switch (mItemMenuIDType) {
            case ScribbleFunctionMenuIDType.FUNCTION_BAR_MENU:
                mIconRes.set(ScribbleFunctionItemUtils.getFunctionBarItemIDIconRes(itemID));
                mIsChecked.set(false);
                mShowIndicator.set(false);
                break;
            case ScribbleFunctionMenuIDType.SUB_MENU:
                mIconRes.set(ScribbleFunctionItemUtils.getSubItemIDIconRes(itemID));
                mShowIndicator.set(true);
                break;
            case ScribbleFunctionMenuIDType.TOOL_BAR_MENU:
                mIconRes.set(ScribbleFunctionItemUtils.getToolBarItemIDIconRes(itemID));
                mShowIndicator.set(false);
                break;
        }
    }

    public void itemClicked(View view) {
//        Log.d(TAG, "onClick: " + view.getId() + "---action" + getMenuAction(view));
//        eventBus.post(MenuClickEvent.create(view, mItemID);
//        switch (mItemMenuIDType) {
//            case ScribbleFunctionMenuIDType.FUNCTION_BAR_MENU:
//                if (mNavigator != null && mNavigator.get() != null) {
//                    mNavigator.get().onFunctionBarMenuFunctionItem(ScribbleFunctionBarMenuID.translate(mItemID));
//                }
//                break;
//            case ScribbleFunctionMenuIDType.SUB_MENU:
//                if (mNavigator != null && mNavigator.get() != null) {
//                    mNavigator.get().onSubMenuFunctionItem(ScribbleSubMenuID.translate(mItemID));
//                }
//                break;
//            case ScribbleFunctionMenuIDType.TOOL_BAR_MENU:
//                if (mNavigator != null && mNavigator.get() != null) {
//                    mNavigator.get().onToolBarMenuFunctionItem(ScribbleSubMenuID.translate(mItemID));
//                }
//                break;
//        }
    }


}
