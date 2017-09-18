/*
 * This is a modified version of a class from the Android
 * Open Source Project. The original copyright and license information follows.
 * 
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onyx.android.dr.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

/**
 * This class is used to initiate a drag within a view or across multiple views.
 * When a drag starts it creates a special view (a DragView) that moves around the screen
 * until the user ends the drag. As feedback to the user, this object causes the device to
 * vibrate as the drag begins.
 *
 */
public class DragController {
    private static final String TAG = "DragController";
    /** Indicates the drag is a move.  */
    public static int DRAG_ACTION_MOVE = 0;
    /** Indicates the drag is a copy.  */
    public static int DRAG_ACTION_COPY = 1;
    private static final int VIBRATE_DURATION = 35;
    private static final boolean PROFILE_DRAWING_DURING_DRAG = false;
    private Context context;
    private Rect rectTemp = new Rect();
    private final int[] coordinatesTemp = new int[2];
    /** Whether or not we're dragging. */
    private boolean dragging;
    /** X coordinate of the down event. */
    private float motionDownX;
    /** Y coordinate of the down event. */
    private float motionDownY;
    /** Info about the screen for clamping. */
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    /** Original view that is being dragged.  */
    private View originator;
    /** X offset from the upper-left corner of the cell to where we touched.  */
    private float touchOffsetX;
    /** Y offset from the upper-left corner of the cell to where we touched.  */
    private float touchOffsetY;
    /** Where the drag originated */
    private DragSource dragSource;
    /** The data associated with the object being dragged */
    private Object dragInfo;
    /** The view that moves around while you drag.  */
    private DragView dragView;
    /** Who can receive drop events */
    private ArrayList<DropTarget> dropTargets = new ArrayList<DropTarget>();
    private DragListener listener;
    /** The window token used as the parent for the DragView. */
    private IBinder windowToken;
    private View moveTarget;
    private DropTarget lastDropTarget;
    private InputMethodManager inputMethodManager;

    /**
     * Used to create a new DragLayer from XML.
     *
     * @param context The application's context.
     */
    public DragController(Context context) {
        this.context = context;
    }

    /**
     * Used to notify the on drag event
     * */
    public void onDrag() {
  	  if (listener != null) {
            listener.onDrag();
        }
    }
    
    /**
     * Starts a drag. 
     * It creates a bitmap of the view being dragged. That bitmap is what you see moving.
     * The actual view can be repositioned if that is what the onDrop handle chooses to do.
     * 
     * @param v The view that is being dragged
     * @param source An object representing where the drag originated
     * @param dragInfo The data associated with the object that is being dragged
     * @param dragAction The drag action: either {@link #DRAG_ACTION_MOVE} or
     *        {@link #DRAG_ACTION_COPY}
     */
    public void startDrag(View v, DragSource source, Object dragInfo, int dragAction) {
        // Start dragging, but only if the source has something to drag.
        boolean doDrag = source.allowDrag ();
        if (!doDrag) return;
        originator = v;
        Bitmap b = getViewBitmap(v);
        if (b == null) {
            // out of memory?
            return;
        }
        int[] loc = coordinatesTemp;
        v.getLocationOnScreen(loc);
        int screenX = loc[0];
        int screenY = loc[1];
        startDrag(b, screenX, screenY, 0, 0, b.getWidth(), b.getHeight(),
                source, dragInfo, dragAction);
        b.recycle();
        if (dragAction == DRAG_ACTION_MOVE) {
            v.setVisibility(View.GONE);
        }
    }

    /**
     * Starts a drag.
     * 
     * @param b The bitmap to display as the drag image.  It will be re-scaled to the
     *          enlarged size.
     * @param screenX The x position on screen of the left-top of the bitmap.
     * @param screenY The y position on screen of the left-top of the bitmap.
     * @param textureLeft The left edge of the region inside b to use.
     * @param textureTop The top edge of the region inside b to use.
     * @param textureWidth The width of the region inside b to use.
     * @param textureHeight The height of the region inside b to use.
     * @param source An object representing where the drag originated
     * @param dragInfo The data associated with the object that is being dragged
     * @param dragAction The drag action: either {@link #DRAG_ACTION_MOVE} or
     *        {@link #DRAG_ACTION_COPY}
     */
    public void startDrag(Bitmap bitmap, int screenX, int screenY,
            int textureLeft, int textureTop, int textureWidth, int textureHeight,
            DragSource source, Object dragInfo, int dragAction) {
        if (PROFILE_DRAWING_DURING_DRAG) {
            android.os.Debug.startMethodTracing("Launcher");
        }
        // Hide soft keyboard, if visible
        if (inputMethodManager == null) {
            inputMethodManager = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
        if (listener != null) {
            listener.onDragStart(source, dragInfo, dragAction);
        }
        int registrationX = ((int)motionDownX) - screenX;
        int registrationY = ((int)motionDownY) - screenY;
        touchOffsetX = motionDownX - screenX;
        touchOffsetY = motionDownY - screenY;
        dragging = true;
        dragSource = source;
        this.dragInfo = dragInfo;
        dragView = new DragView(context, bitmap, registrationX, registrationY,
                textureLeft, textureTop, textureWidth, textureHeight);
        dragView.show(windowToken, (int)motionDownX, (int)motionDownY);
    }

    /**
     * Draw the view into a bitmap.
     */
    private Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e(TAG, "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

    /**
     * Call this from a drag source view like this:
     *
     * <pre>
     *  @Override
     *  public boolean dispatchKeyEvent(KeyEvent event) {
     *      return mDragController.dispatchKeyEvent(this, event)
     *              || super.dispatchKeyEvent(event);
     * </pre>
     */
    public boolean dispatchKeyEvent(KeyEvent event) {
        return dragging;
    }

    /**
     * Stop dragging without dropping.
     */
    public void cancelDrag() {
        endDrag();
    }

    private void endDrag() {
        if (dragging) {
            dragging = false;
            if (originator != null) {
                originator.setVisibility(View.VISIBLE);
            }
            if (listener != null) {
                listener.onDragEnd();
            }
            if (dragView != null) {
                dragView.remove();
                dragView = null;
            }
        }
    }

    /**
     * Call this from a drag source view.
     */
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            recordScreenSize();
        }
        final float screenX = clamp((int)ev.getRawX(), 0, displayMetrics.widthPixels);
        final float screenY = clamp((int)ev.getRawY(), 0, displayMetrics.heightPixels);
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_DOWN:
                // Remember location of down touch
                motionDownX = screenX;
                motionDownY = screenY;
                lastDropTarget = null;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (dragging) {
                    drop(screenX, screenY);
                }
                endDrag();
                break;
        }
        return dragging;
    }

    /**
     * Sets the view that should handle move events.
     */
    void setMoveTarget(View view) {
        moveTarget = view;
    }    

    public boolean dispatchUnhandledMove(View focused, int direction) {
        return moveTarget != null && moveTarget.dispatchUnhandledMove(focused, direction);
    }

    /**
     * Call this from a drag source view.
     */
    public boolean onTouchEvent(MotionEvent ev) {
        if (!dragging) {
            return false;
        }
        final int action = ev.getAction();
        final float screenX = clamp((int)ev.getRawX(), 0, displayMetrics.widthPixels);
        final float screenY = clamp((int)ev.getRawY(), 0, displayMetrics.heightPixels);
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            // Remember where the motion event started
            motionDownX = screenX;
            motionDownY = screenY;
            break;
        case MotionEvent.ACTION_MOVE:
            // Update the drag view.  Don't use the clamped pos here so the dragging looks
            // like it goes off screen a little, intead of bumping up against the edge.
            dragView.move((int)ev.getRawX(), (int)ev.getRawY());
            // Drop on someone?
            final int[] coordinates = coordinatesTemp;
            DropTarget dropTarget = findDropTarget(screenX, screenY, coordinates);
            if (dropTarget != null) {
                if (lastDropTarget == dropTarget) {
                    dropTarget.onDragOver(dragSource, coordinates[0], coordinates[1],
                        (int) touchOffsetX, (int) touchOffsetY, dragView, dragInfo);
                } else {
                    if (lastDropTarget != null) {
                        lastDropTarget.onDragExit(dragSource, coordinates[0], coordinates[1],
                            (int) touchOffsetX, (int) touchOffsetY, dragView, dragInfo);
                    }
                    dropTarget.onDragEnter(dragSource, coordinates[0], coordinates[1],
                        (int) touchOffsetX, (int) touchOffsetY, dragView, dragInfo);
                }
            } else {
                if (lastDropTarget != null) {
                    lastDropTarget.onDragExit(dragSource, coordinates[0], coordinates[1],
                        (int) touchOffsetX, (int) touchOffsetY, dragView, dragInfo);
                }
            }
            lastDropTarget = dropTarget;
            if (dragSource!=null && dragging){
        		onDrag();
        	}
            break;
        case MotionEvent.ACTION_UP:
            if (dragging) {
                drop(screenX, screenY);
            }
            endDrag();

            break;
        case MotionEvent.ACTION_CANCEL:
            cancelDrag();
        }
        return true;
    }

    private boolean drop(float x, float y) {
        final int[] coordinates = coordinatesTemp;
        DropTarget dropTarget = findDropTarget((int) x, (int) y, coordinates);
        if (dropTarget != null) {
            dropTarget.onDragExit(dragSource, coordinates[0], coordinates[1],
                    (int) touchOffsetX, (int) touchOffsetY, dragView, dragInfo);
            if (dropTarget.acceptDrop(dragSource, coordinates[0], coordinates[1],
                    (int) touchOffsetX, (int) touchOffsetY, dragView, dragInfo)) {
                dropTarget.onDrop(dragSource, coordinates[0], coordinates[1],
                        (int) touchOffsetX, (int) touchOffsetY, dragView, dragInfo);
                dragSource.onDropCompleted((View) dropTarget, true);
                return true;
            } else {
                dragSource.onDropCompleted((View) dropTarget, false);
                return true;
            }
        }
        return false;
    }

    private DropTarget findDropTarget(float x, float y, int[] dropCoordinates) {
        final Rect r = rectTemp;
        final ArrayList<DropTarget> targets = dropTargets;
        final int count = targets.size();
        for (int i=count-1; i>=0; i--) {
            final DropTarget target = targets.get(i);
            target.getHitRect(r);
            target.getLocationOnScreen(dropCoordinates);
            r.offset(dropCoordinates[0] - target.getLeft(), dropCoordinates[1] - target.getTop());
            if (r.contains((int)x, (int)y)) {
                dropCoordinates[0] = (int) x - dropCoordinates[0];
                dropCoordinates[1] = (int) y - dropCoordinates[1];
                return target;
            }
        }
        return null;
    }

    /**
     * Get the screen size so we can clamp events to the screen size so even if
     * you drag off the edge of the screen, we find something.
     */
    private void recordScreenSize() {
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(displayMetrics);
    }

    /**
     * Clamp val to be &gt;= min and &lt; max.
     */
    private static float clamp(float val, float min, float max) {
        if (val < min) {
            return min;
        } else if (val >= max) {
            return max - 1;
        } else {
            return val;
        }
    }

    public void setWindowToken(IBinder token) {
        windowToken = token;
    }

    /**
     * Sets the drag listner which will be notified when a drag starts or ends.
     */
    public void setDragListener(DragListener l) {
        listener = l;
    }

    /**
     * Remove a previously installed drag listener.
     */
    public void removeDragListener(DragListener l) {
        listener = null;
    }

    /**
     * Add a DropTarget to the list of potential places to receive drop events.
     */
    public void addDropTarget(DropTarget target) {
        dropTargets.add(target);
    }

    /**
     * Don't send drop events to <em>target</em> any more.
     */
    public void removeDropTarget(DropTarget target) {
        dropTargets.remove(target);
    }
}
