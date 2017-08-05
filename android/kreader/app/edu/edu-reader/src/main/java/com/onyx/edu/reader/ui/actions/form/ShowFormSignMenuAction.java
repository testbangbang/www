package com.onyx.edu.reader.ui.actions.form;

import android.view.ViewGroup;

import com.onyx.android.sdk.data.ReaderMenuAction;
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
        return null;
    }
}
