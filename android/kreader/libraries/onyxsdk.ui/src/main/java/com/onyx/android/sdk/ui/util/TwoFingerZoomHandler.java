/**
 * 
 */
package com.onyx.android.sdk.ui.util;

import com.onyx.android.sdk.device.DeviceInfo;
import com.onyx.android.sdk.device.IDeviceFactory.TouchType;
import com.onyx.android.sdk.device.RK2906Factory.RK2906Controller;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

/**
 * @author joy
 *
 */
public class TwoFingerZoomHandler
{
    private static final String TAG = "TwoFingerZoomHandler";
    private static final boolean VERBOSE_LOG = false;

    public static interface OnScaleUpdatedListener
    {
        void onScaleUpdated(Matrix matrix);
    }
    public static interface OnScaleFinishedListener
    {
        void onScaleFinished(Matrix matrix);
    }

    private OnScaleUpdatedListener mOnScaleUpdatedListener = null;
    private OnScaleFinishedListener mOnScaleFinishedListener = null;

    public void setOnScaleUpdatedListener(OnScaleUpdatedListener l)
    {
        mOnScaleUpdatedListener = l;
    }

    public void setOnScaleFinishedListener(OnScaleFinishedListener l)
    {
        mOnScaleFinishedListener = l;
    }
    
    private static final int TWO_FINGER_SCALE_MAX = 4;
    private static final float TWO_FINGER_SCALE_MIN = 0.25f;

    private boolean mTwoFingerZooming = false;
    private PointF mTwoFingerDownCenter = null;
    private double mTwoFingerDownDistance = 0;
    private Matrix mTwoFingerMatrix = new Matrix();
    
    public boolean isTwoFingerZooming()
    {
        return mTwoFingerZooming;
    }

    public boolean onPointerDown(MotionEvent e)
    {
        if (mTwoFingerZooming) {
            return false;
        }

        double distance = Math.hypot(e.getX(0) - e.getX(1), e.getY(0) - e.getY(1));
        if (distance > 10) {
            mTwoFingerZooming = true;
            // because coordinate of points in ACTION_POINTER_DOWN are not valid, 
            // so we will use coordinates in ACTION_MOVE to determine the center of two finger zooming
            mTwoFingerDownCenter = null;

            return true;
        }

        return false;
    }

    public boolean onPointerMove(MotionEvent e)
    {
        if (!mTwoFingerZooming) {
            return false;
        }

        double distance = Math.hypot(e.getX(0) - e.getX(1), e.getY(0) - e.getY(1));
        if (mTwoFingerDownCenter == null) {
            mTwoFingerDownCenter = new PointF((e.getX(0) + e.getX(1)) / 2, (e.getY(0) + e.getY(1)) / 2);
            mTwoFingerDownDistance = distance;

            if (VERBOSE_LOG) {
                float[] values = new float[9];
                mTwoFingerMatrix.getValues(values);
                Log.d(TAG, "enter two finger scale, start distance: " + distance +
                        ", scaleX: " + values[Matrix.MSCALE_X] + ", scaleY: " + values[Matrix.MSCALE_Y]);
            }

        }
        else {
            if (distance > 10) {
                float scale = (float)(distance / mTwoFingerDownDistance);
                if (VERBOSE_LOG) Log.d(TAG, "new two finger scale, delta: " + scale);

                scale = Math.min(TWO_FINGER_SCALE_MAX, Math.max(scale, TWO_FINGER_SCALE_MIN));

                mTwoFingerMatrix.reset();
                mTwoFingerMatrix.postScale(scale, scale, mTwoFingerDownCenter.x, mTwoFingerDownCenter.y);

                float[] values = new float[9];
                mTwoFingerMatrix.getValues(values);
                if (VERBOSE_LOG) {
                    Log.d(TAG, "new two finger scale, distance: " + distance + ", scaleX: " + values[Matrix.MSCALE_X] +
                            ", scaleY: " + values[Matrix.MSCALE_Y]);
                }

                scale = Math.max(values[Matrix.MSCALE_X], values[Matrix.MSCALE_Y]);
                this.notifyScaleUpdated(mTwoFingerMatrix);
            }
        }
        return true;
    }
    public boolean onPointerUp(MotionEvent e)
    {
        if (!mTwoFingerZooming) {
            return false;
        }

        mTwoFingerZooming = false;
        if (VERBOSE_LOG) {
            float[] values = new float[9];
            mTwoFingerMatrix.getValues(values);
            Log.d(TAG, "post two finger scale action, scaleX: " + values[Matrix.MSCALE_X] +
                    ", scaleY: " + values[Matrix.MSCALE_Y]);
        }

        this.notifyScaleFinished(mTwoFingerMatrix);

        return true;
    }

    public double getZoomFactor() {
        float[] values = new float[9];
        mTwoFingerMatrix.getValues(values);
        return Math.max(values[Matrix.MSCALE_X], values[Matrix.MSCALE_Y]);
    }
    
    public double getFontScaleFactor(Context context, double currentFontSize)
    {
        double scale = getZoomFactor();

        if (DeviceInfo.currentDevice instanceof RK2906Controller &&
                DeviceInfo.currentDevice.getTouchType(context) == TouchType.Capacitive) {
            // TP on RK2906 sometimes return wrong values for two finger zooming, so force delta change to relieve scale result
            return scale >= 1 ? 1.1 : 0.9;
        }

        return scale;
    }

    private void notifyScaleUpdated(Matrix matrix)
    {
        if (mOnScaleUpdatedListener != null) {
            mOnScaleUpdatedListener.onScaleUpdated(matrix);
        }
    }

    private void notifyScaleFinished(Matrix matrix)
    {
        if (mOnScaleFinishedListener != null) {
            mOnScaleFinishedListener.onScaleFinished(matrix);
        }
    }
}
