package com.onyx.edu.reader.ui.actions.form;

import android.view.ViewGroup;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.ui.data.ReaderMenuViewData;
import com.onyx.android.sdk.ui.data.ReaderMenuViewHolder;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by ming on 2017/7/27.
 */

public abstract class BaseMenuAction extends BaseAction {

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {

    }

    public abstract void show(ReaderDataHolder readerDataHolder);

    public abstract List<ReaderMenuViewHolder> getBottomMenuViewHolders(final ReaderDataHolder readerDataHolder, final ViewGroup parent);

    public abstract List<ReaderMenuViewHolder> getTopMenuViewHolders(final ReaderDataHolder readerDataHolder, final ViewGroup parent);

    public abstract List<ReaderMenuViewHolder> getChildrenViewHolders (final ReaderDataHolder readerDataHolder, final ReaderMenuAction parent);

    public abstract void onMenuClicked(final ReaderDataHolder readerDataHolder, final ReaderMenuAction action);

    public abstract void onToggleMenuGroup(final ReaderDataHolder readerDataHolder, final ReaderMenuAction action, boolean expand);

    public abstract ReaderMenuAction getChildrenSelectedAction(ReaderMenuAction parent);

    public abstract void updateChildrenMenuStateOnCreated(final ReaderDataHolder readerDataHolder, ReaderMenuAction selectAction, List<ReaderMenuViewHolder> viewHolders, final ReaderMenuViewHolder parent);

    public abstract void updateChildrenMenuStateOnClicked(final ReaderDataHolder readerDataHolder, ReaderMenuAction selectAction, List<ReaderMenuViewHolder> viewHolders, final ReaderMenuViewHolder parent);
}
