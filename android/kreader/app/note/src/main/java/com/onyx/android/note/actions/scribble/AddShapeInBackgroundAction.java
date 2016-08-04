package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.navigation.AddShapeRequest;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 7/3/16.
 */
public class AddShapeInBackgroundAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {

    private volatile List<Shape> shapeList = new ArrayList<>();
    private AddShapeRequest renderRequest = new AddShapeRequest(shapeList);


    public AddShapeInBackgroundAction(final Shape shape) {
        shapeList.add(shape);
    }

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(renderRequest, true);
            }
        });
    }

    public void execute(final T activity, final BaseCallback callback) {
        renderRequest = new AddShapeRequest(shapeList);
        activity.submitRequest(renderRequest, callback);
    }

}
