package com.onyx.edu.reader.ui.actions.form;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

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
import static com.onyx.android.sdk.data.ReaderMenuAction.FETCH_REVIEW_DATA;
import static com.onyx.android.sdk.data.ReaderMenuAction.SUBMIT;

/**
 * Created by ming on 2017/8/1.
 */

public class ShowFormUserHomeWorkMenuAction extends ShowFormMenuAction {

    public ShowFormUserHomeWorkMenuAction(ReaderMenuViewData menuViewData, ShowScribbleMenuAction.ActionCallback actionCallback) {
        super(menuViewData, actionCallback);
    }

    @Override
    public List<ReaderMenuViewHolder> getBottomMenuViewHolders(ReaderDataHolder readerDataHolder, final ViewGroup parent) {
        List<ReaderMenuViewHolder> bottomMenuViewHolders = new ArrayList<>();

        boolean showScribbleMenu = getReaderMenuViewData().isShowScribbleMenu();
        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_exit, EXIT, R.string.exit));
        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), showScribbleMenu ? R.drawable.ic_write_black : R.drawable.ic_write_forbidden, ReaderMenuAction.TOGGLE_FORM_SCRIBBLE, showScribbleMenu ? R.string.scribble : R.string.touch));
        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_submit, SUBMIT, R.string.submit));
        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_download, FETCH_REVIEW_DATA, R.string.fetch));
        if (showScribbleMenu) {
            bottomMenuViewHolders.addAll(getScribbleMenuViewHolders(readerDataHolder));
        }

        bottomMenuViewHolders.add(ReaderMenuViewHolder.create(OnyxToolbar.Builder.createSpaceView(readerDataHolder.getContext(), 1f)));

        bottomMenuViewHolders.addAll(getPageTextViewHolder());

        return bottomMenuViewHolders;
    }

}
