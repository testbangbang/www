package com.onyx.edu.reader.ui.actions.form;

import android.view.ViewGroup;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.ui.data.ReaderMenuViewData;
import com.onyx.android.sdk.ui.data.ReaderMenuViewHolder;
import com.onyx.android.sdk.ui.view.OnyxToolbar;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.ui.actions.ShowScribbleMenuAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.sdk.data.ReaderMenuAction.EXIT;
import static com.onyx.android.sdk.data.ReaderMenuAction.FETCH_REVIEW_DATA;
import static com.onyx.android.sdk.data.ReaderMenuAction.SUBMIT;
import static com.onyx.android.sdk.data.ReaderMenuAction.TIMER;

/**
 * Created by ming on 2017/7/27.
 */

public class ShowFormExerciseMenuAction extends ShowFormMenuAction {


    public ShowFormExerciseMenuAction(ReaderMenuViewData menuViewData, ShowScribbleMenuAction.ActionCallback actionCallback) {
        super(menuViewData, actionCallback);
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        super.execute(readerDataHolder, baseCallback);
        show(readerDataHolder);
    }

    @Override
    public List<ReaderMenuViewHolder> getBottomMenuViewHolders(ReaderDataHolder readerDataHolder, final ViewGroup parent) {
        List<ReaderMenuViewHolder> bottomMenuViewHolders = new ArrayList<>();

        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_exit, EXIT, R.string.exit));
        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_submit, SUBMIT, R.string.submit));
        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_download, FETCH_REVIEW_DATA, R.string.fetch));
        if (getReaderMenuViewData().isShowScribbleMenu()) {
            bottomMenuViewHolders.addAll(getScribbleMenuViewHolders(readerDataHolder));
        }

        bottomMenuViewHolders.add(ReaderMenuViewHolder.create(OnyxToolbar.Builder.createSpaceView(readerDataHolder.getContext(), 1f)));

        bottomMenuViewHolders.addAll(getPageTextViewHolder());
        return bottomMenuViewHolders;
    }
}
