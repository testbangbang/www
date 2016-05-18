package com.onyx.kreader.actions;

import android.content.pm.ActivityInfo;
import com.onyx.kreader.host.request.NextScreenRequest;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class ChangeOrientationAction extends BaseAction {
    private int newOrientation;

    public ChangeOrientationAction(int targetOrientation) {
        newOrientation = targetOrientation;
    }

    public void execute(final ReaderActivity readerActivity) {
        int orientation = computeNewRotation(readerActivity.getRequestedOrientation(), newOrientation);
        readerActivity.setRequestedOrientation(orientation);
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
