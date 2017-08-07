package com.onyx.edu.reader.ui.actions.form;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
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

/**
 * Created by ming on 2017/8/1.
 */

public class ShowFormMeetingMenuAction extends ShowFormMenuAction {

    public ShowFormMeetingMenuAction(ReaderMenuViewData menuViewData, ShowScribbleMenuAction.ActionCallback actionCallback) {
        super(menuViewData, actionCallback);
    }

    @Override
    public List<ReaderMenuViewHolder> getBottomMenuViewHolders(ReaderDataHolder readerDataHolder, final ViewGroup parent) {
        List<ReaderMenuViewHolder> bottomMenuViewHolders = new ArrayList<>();

        boolean showScribbleMenu = getReaderMenuViewData().isShowScribbleMenu();
        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), showScribbleMenu ? R.drawable.ic_write_black : R.drawable.ic_write_forbidden, ReaderMenuAction.SWITCH_SCRIBBLE, R.string.scribble));
        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), showScribbleMenu ? R.drawable.ic_eraser_all_black : R.drawable.ic_eraser_all_gray, ReaderMenuAction.SCRIBBLE_ERASER, R.string.eraser, showScribbleMenu));
        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_meeting_submit, ReaderMenuAction.SUBMIT, R.string.submit));
        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_meeting_undo, ReaderMenuAction.SCRIBBLE_UNDO, R.string.undo, showScribbleMenu));
        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_meeting_redo, ReaderMenuAction.SCRIBBLE_REDO, R.string.redo, showScribbleMenu));

        bottomMenuViewHolders.add(ReaderMenuViewHolder.create(OnyxToolbar.Builder.createSpaceView(readerDataHolder.getContext(), 1f)));

        bottomMenuViewHolders.add(createSignatureTextViewHolder(readerDataHolder.getContext(),ReaderMenuAction.SIGNATURE, parent));
        return bottomMenuViewHolders;
    }

    private ReaderMenuViewHolder createSignatureTextViewHolder(Context context, final ReaderMenuAction action, final ViewGroup parent) {
        TextView textView =  (TextView) LayoutInflater.from(context).inflate(R.layout.signature_text_view, parent, false);
        textView.setTag(action);
        return ReaderMenuViewHolder.create(textView, action);
    }
}
