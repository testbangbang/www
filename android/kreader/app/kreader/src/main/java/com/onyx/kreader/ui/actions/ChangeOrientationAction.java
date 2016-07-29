package com.onyx.kreader.ui.actions;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.ChangeOrientationEvent;

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

    public void execute(final ReaderDataHolder readerDataHolder) {
        int orientation = computeNewRotation(currentOrientation, newOrientation);
        readerDataHolder.getEventBus().post(new ChangeOrientationEvent(orientation));
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
