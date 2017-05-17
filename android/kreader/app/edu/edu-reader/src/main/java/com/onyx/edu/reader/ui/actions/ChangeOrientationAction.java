package com.onyx.edu.reader.ui.actions;

import android.content.pm.ActivityInfo;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.host.request.UpdateScreenOrientationOptionRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.ChangeOrientationEvent;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class ChangeOrientationAction extends BaseAction {
    private int newOrientation;
    private int currentOrientation;

    public ChangeOrientationAction(int current, int targetOrientation) {
        newOrientation = targetOrientation;
        currentOrientation = current;
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        int orientation = computeNewRotation(currentOrientation, newOrientation);
        readerDataHolder.getEventBus().post(new ChangeOrientationEvent(orientation));
        readerDataHolder.submitNonRenderRequest(new UpdateScreenOrientationOptionRequest(orientation), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    private int computeNewRotation(int currentOrientation, int rotationOperation) {
        switch (rotationOperation) {
            case 0:
                return currentOrientation;
            case 90:
                if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                } else {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                }
            case 180:
                if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                } else {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                }
            case 270:
                if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                } else {
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                }
            default:
                assert(false);
                return currentOrientation;
        }
    }

}
