package com.onyx.edu.reader.ui.actions.form;

import android.view.ViewGroup;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.ui.data.ReaderMenuViewData;
import com.onyx.android.sdk.ui.data.ReaderMenuViewHolder;
import com.onyx.android.sdk.ui.view.OnyxToolbar;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.ui.actions.ShowScribbleMenuAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.sdk.data.ReaderMenuAction.EXIT;
import static com.onyx.android.sdk.data.ReaderMenuAction.SUBMIT;

/**
 * Created by ming on 2017/7/27.
 */

public class ShowFormVoteMenuAction extends BaseFormMenuAction {


    public ShowFormVoteMenuAction(ReaderMenuViewData menuViewData, ShowScribbleMenuAction.ActionCallback actionCallback) {
        super(menuViewData, actionCallback);
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        super.execute(readerDataHolder, baseCallback);
        show(readerDataHolder);
    }

    @Override
    public List<ReaderMenuViewHolder> getBottomMenuViewHolders(ReaderDataHolder readerDataHolder, final ViewGroup parent) {
        return null;
    }

    @Override
    public List<ReaderMenuViewHolder> getTopMenuViewHolders(ReaderDataHolder readerDataHolder, final ViewGroup parent) {
        return null;
    }

    @Override
    public List<ReaderMenuViewHolder> getChildrenViewHolders(ReaderDataHolder readerDataHolder, ReaderMenuAction parent) {
        return null;
    }

    @Override
    public ReaderMenuAction getChildrenSelectedAction(ReaderMenuAction parent) {
        return null;
    }

    @Override
    public void updateChildrenMenuStateOnCreated(ReaderDataHolder readerDataHolder, ReaderMenuAction selectAction, List<ReaderMenuViewHolder> viewHolders, ReaderMenuViewHolder parent) {

    }

    @Override
    public void updateChildrenMenuStateOnClicked(ReaderDataHolder readerDataHolder, ReaderMenuAction selectAction, List<ReaderMenuViewHolder> viewHolders, ReaderMenuViewHolder parent) {

    }
}
