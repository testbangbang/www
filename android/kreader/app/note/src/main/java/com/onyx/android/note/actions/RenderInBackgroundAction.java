package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.navigation.PageFlushRequest;
import com.onyx.android.sdk.scribble.request.navigation.PageRenderRequest;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 7/3/16.
 */
public class RenderInBackgroundAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    private volatile List<Shape> shapeList = new ArrayList<>();

    public RenderInBackgroundAction(final List<Shape> list) {
        shapeList.addAll(list);
    }

    public void execute(final T activity, final BaseCallback callback) {
        final PageRenderRequest renderRequest = new PageRenderRequest(shapeList);
        activity.getNoteViewHelper().submit(activity, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(renderRequest, true);
            }
        });
    }

}
