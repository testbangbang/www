package com.onyx.edu.reader.ui.actions.form;

import android.view.ViewGroup;

import com.onyx.android.sdk.ui.data.ReaderMenuViewData;
import com.onyx.android.sdk.ui.data.ReaderMenuViewHolder;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.ui.actions.ShowScribbleMenuAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.sdk.data.ReaderMenuAction.EXIT;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_ERASER;
import static com.onyx.android.sdk.data.ReaderMenuAction.SUBMIT;

/**
 * Created by ming on 2017/8/1.
 */

public class ShowFormSignMenuAction extends ShowFormMenuAction {

    public ShowFormSignMenuAction(ReaderMenuViewData menuViewData, ShowScribbleMenuAction.ActionCallback actionCallback) {
        super(menuViewData, actionCallback);
    }

    @Override
    public List<ReaderMenuViewHolder> getBottomMenuViewHolders(ReaderDataHolder readerDataHolder, final ViewGroup parent) {
        List<ReaderMenuViewHolder> bottomMenuViewHolders = new ArrayList<>();
        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_exit, EXIT, R.string.exit));
        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_submit, SUBMIT, R.string.submit));
        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_eraser_part, SCRIBBLE_ERASER, R.string.eraser));
        return bottomMenuViewHolders;
    }
}
