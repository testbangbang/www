package com.onyx.edu.note.scribble;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.support.annotation.Nullable;

import com.onyx.edu.note.data.ScribbleFunctionMenuIDType;
import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;
import com.onyx.edu.note.data.ScribbleSubMenuID;
import com.onyx.edu.note.util.ScribbleFunctionItemUtils;

import java.lang.ref.WeakReference;

/**
 * Created by solskjaer49 on 2017/7/10 18:00.
 */

public class ScribbleFunctionItemViewModel extends BaseObservable {
    private static final String TAG = ScribbleFunctionItemViewModel.class.getSimpleName();

    public void setNavigator(ScribbleItemNavigator navigator) {
        this.mNavigator = new WeakReference<>(navigator);
    }

    // This navigator is s wrapped in a WeakReference to avoid leaks because it has references to an
    // activity. There's no straightforward way to clear it for each item in a recycler adapter.
    @Nullable
    private WeakReference<ScribbleItemNavigator> mNavigator;

    public final ObservableInt mIconRes = new ObservableInt();
    public final ObservableBoolean mIsChecked = new ObservableBoolean();
    public final ObservableBoolean mShowIndicator = new ObservableBoolean();

    public int getItemID() {
        return mItemID;
    }

    private int mItemID;
    private @ScribbleFunctionMenuIDType.ScribbleMenuIDTypeDef
    int mItemMenuIDType;

    public ScribbleFunctionItemViewModel(int itemID, @ScribbleFunctionMenuIDType.ScribbleMenuIDTypeDef int itemMenuIDType) {
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

    public void itemClicked() {
        switch (mItemMenuIDType) {
            case ScribbleFunctionMenuIDType.FUNCTION_BAR_MENU:
                if (mNavigator != null && mNavigator.get() != null) {
                    mNavigator.get().onFunctionBarMenuFunctionItem(ScribbleFunctionBarMenuID.translate(mItemID));
                }
                break;
            case ScribbleFunctionMenuIDType.SUB_MENU:
                if (mNavigator != null && mNavigator.get() != null) {
                    mNavigator.get().onSubMenuFunctionItem(ScribbleSubMenuID.translate(mItemID));
                }
                break;
            case ScribbleFunctionMenuIDType.TOOL_BAR_MENU:
                if (mNavigator != null && mNavigator.get() != null) {
                    mNavigator.get().onToolBarMenuFunctionItem(ScribbleSubMenuID.translate(mItemID));
                }
                break;
        }
    }


}