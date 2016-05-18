/**
 *
 */
package com.onyx.android.sdk.ui;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.onyx.android.sdk.data.PageTurningDetector;
import com.onyx.android.sdk.data.PageTurningDirection;
import com.onyx.android.sdk.device.EpdController;
import com.onyx.android.sdk.device.EpdController.UpdateMode;
import com.onyx.android.sdk.ui.data.OnyxPagedAdapter;
import com.onyx.android.sdk.ui.util.IBoundaryItemLocator;
import com.onyx.android.sdk.ui.util.OnyxFocusFinder;

/**
 * base class for pagination grid view
 *
 * tighten coupled with OnyxPagedAdapter
 *
 * @author joy
 *
 */
public class OnyxGridView extends GridView implements IBoundaryItemLocator, GestureDetector.OnGestureListener
{
    private static final String TAG = "OnyxGridView";
    private static final boolean VERBOSE_LOG = true;

    public interface OnAdapterChangedListener
    {
        void onAdapterChanged();
    }
    public interface OnSizeChangedListener
    {
        void onSizeChanged();
    }
    public interface OnLongPressListener
    {
        void onLongPress();
    }

    private ArrayList<OnAdapterChangedListener> mOnAdapterChangedListenerList = new ArrayList<OnAdapterChangedListener>();
    public void registerOnAdapterChangedListener(OnAdapterChangedListener l)
    {
        mOnAdapterChangedListenerList.add(l);
    }
    public void unregisterOnAdapterChangedListener(OnAdapterChangedListener l)
    {
        mOnAdapterChangedListenerList.remove(l);
    }

    private ArrayList<OnSizeChangedListener> mOnSizeChangedListenerList = new ArrayList<OnSizeChangedListener>();
    public void registerOnSizeChangedListener(OnSizeChangedListener l)
    {
        mOnSizeChangedListenerList.add(l);
    }
    public void unregisterOnSizeChangedListener(OnSizeChangedListener l)
    {
        mOnSizeChangedListenerList.remove(l);
    }

    private ArrayList<OnLongPressListener> mOnLongPressListenerList = new ArrayList<OnLongPressListener>();
    public void registerOnLongPressListener(OnLongPressListener l)
    {
        mOnLongPressListenerList.add(l);
    }
    public void unregisterOnLongPressListener(OnLongPressListener l)
    {
        mOnLongPressListenerList.remove(l);
    }

    // TODO should use "dp" as unit
    private static final int sMinFlingLength = 20;

    private OnyxPagedAdapter mAdapter = null;

    private boolean mIsClickEvent = false;
    private boolean mCrossVertical = false;
    private boolean mCrossHorizon = false;
    private int mSelectionInTouchMode = AdapterView.INVALID_POSITION;

    private GestureDetector mGestureDetector = null;

    private float mLastTouchDownX = 0;
    private float mLastTouchUpX = 0;
    private long mLastTouchDownTime = 0;
    private boolean enableOnFling = true;
    private boolean mIsInterceptVolumeKey = false;

    private boolean mOnFlinged = false;
    private boolean mOnLongPressed = false;

    public OnyxGridView(Context context) {
        this(context, null);
    }

    public OnyxGridView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.gridViewStyle);
    }

    public OnyxGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // default behavior as non-paged GridView
        this.setCrossHorizon(true);
        this.setCrossVertical(true);

        // by disabling ScrollBar, we can eliminate noisy onDraw when navigating items in GridView
        // same effect as android:scrollbars="none" style
        this.setHorizontalScrollBarEnabled(false);
        this.setVerticalScrollBarEnabled(false);

        mGestureDetector = new GestureDetector(this.getContext(), this);
    }

    @Override
    public void setOnItemLongClickListener(final OnItemLongClickListener l) {
        if (l == null) {
            OnyxGridView.super.setOnItemLongClickListener(null);
            return;
        }

        final OnItemLongClickListener listener = new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mOnLongPressed = true;
                return l.onItemLongClick(parent, view, position, id);
            }
        };
        OnyxGridView.super.setOnItemLongClickListener(listener);
    }

    /**
     * warning: ListAdapter is not accepted, use OnyxBasePagedAdapter instead
     */
    @Override
    public void setAdapter(ListAdapter adapter)
    {
        throw new IllegalArgumentException();
    }

    public void setAdapter(OnyxPagedAdapter adapter)
    {
        if (mAdapter != adapter) {
            super.setAdapter(adapter);

            mAdapter = adapter;
            adapter.getPageLayout().setupLayout(this);

            for (OnAdapterChangedListener l : mOnAdapterChangedListenerList) {
                l.onAdapterChanged();
            }
        }
    }

    /**
     * in convenience, use getPagedAdapter() instead
     * override just for commentary
     */
    @Override
    public ListAdapter getAdapter()
    {
        return super.getAdapter();
    }
    // simple wrapper around getAdapter()
    public OnyxPagedAdapter getPagedAdapter()
    {
        return (OnyxPagedAdapter)this.getAdapter();
    }

    public void setCrossVertical(boolean value)
    {
        mCrossVertical = value;
    }

    public void setCrossHorizon(boolean value)
    {
        mCrossHorizon = value;
    }

    public int getSelectionInTouchMode()
    {
        return mSelectionInTouchMode;
    }
    private void setSelectionInTouchMode(int value)
    {
        mSelectionInTouchMode = value;
    }

    @Override
    public void setSelection(int position)
    {
        if (VERBOSE_LOG) Log.d(TAG, "setSelection: " + position);
        this.requestFocus();
        super.setSelection(position);
    }

    @Override
    public View getSelectedView()
    {
        if (this.isInTouchMode()) {
            if (super.getSelectedView() != null) {
                return super.getSelectedView();
            }
            else {
                if (mSelectionInTouchMode != AdapterView.INVALID_POSITION) {
                    return this.getChildAt(mSelectionInTouchMode);
                }
                else {
                    return null;
                }
            }
        }

        return super.getSelectedView();
    }

    // ========================= IBoundaryItemLocator ======================
    /**
     *
     * @param srcRect
     * @param boundarySide
     */
    @Override
    public void selectBoundaryItemBySearch(Rect srcRect, BoundarySide boundarySide)
    {
        int item_count = this.getCount();

        if (item_count <= 0) {
            return;
        }
        else if (item_count == 1) {
            this.setSelection(0);
            return;
        }
        else {
            final int columns = Math.min(this.getCount(), mAdapter.getPageLayout().getLayoutColumnCount());
            if (columns <= 0) {
                return;
            }

            final int column_mod = item_count % columns;
            final int rows = (this.getCount() / columns) + ((column_mod > 0) ? 1 : 0);

            if (srcRect == null) {
                switch (boundarySide) {
                case TOP:
                    this.setSelection(0);
                    break;
                case BOTTOM:
                    this.setSelection((rows -1 ) * columns);
                    break;
                case LEFT:
                    this.setSelection(0);
                    break;
                case RIGHT:
                    this.setSelection(columns - 1);
                    break;
                default:
                    assert(false);
                    throw new IndexOutOfBoundsException();
                }

                return;
            }

            if (boundarySide == BoundarySide.TOP) {
                if (srcRect.left < OnyxFocusFinder.getAbsoluteLeft(this.getChildAt(0))) {
                    this.setSelection(0);
                    return;
                }
                else if (srcRect.left > OnyxFocusFinder.getAbsoluteRight(this.getChildAt(columns - 1))) {
                    this.setSelection(columns - 1);
                    return;
                }
                else {
                    int min_distance = Integer.MAX_VALUE;
                    int best_item_index = 0;

                    for (int i = 0; i < columns; i++) {
                        int distance = Math.abs(OnyxFocusFinder.getAbsoluteLeft(this.getChildAt(i)) - srcRect.left);
                        if (distance < min_distance) {
                            min_distance = distance;
                            best_item_index = i;
                        }
                    }

                    this.setSelection(best_item_index);
                    return;
                }
            }
            else if (boundarySide == BoundarySide.BOTTOM) {
                int base_index = (rows - 1) * columns;

                if (srcRect.left < OnyxFocusFinder.getAbsoluteLeft(this.getChildAt(base_index))) {
                    this.setSelection(base_index);
                    return;
                }
                else if (srcRect.left > OnyxFocusFinder.getAbsoluteRight(this.getChildAt(item_count - 1))) {
                    this.setSelection(item_count - 1);
                    return;
                }
                else {
                    int min_distance = Integer.MAX_VALUE;
                    int best_item_index = 0;

                    for (int i = base_index; i < item_count; i++) {
                        int distance = Math.abs(OnyxFocusFinder.getAbsoluteLeft(this.getChildAt(i)) - srcRect.left);
                        if (distance < min_distance) {
                            min_distance = distance;
                            best_item_index = i;
                        }
                    }

                    this.setSelection(best_item_index);
                    return;
                }
            }
            else if (boundarySide == BoundarySide.LEFT) {
                final int first_column_of_last_row = (rows - 1) * columns;

                if (srcRect.top < OnyxFocusFinder.getAbsoluteTop(this.getChildAt(0))) {
                    this.setSelection(0);
                    return;
                }
                else if (srcRect.top > OnyxFocusFinder.getAbsoluteBottom(this.getChildAt(first_column_of_last_row))) {
                    this.setSelection(first_column_of_last_row);
                    return;
                }
                else {
                    int min_distance = Integer.MAX_VALUE;
                    int best_item_index = 0;

                    for (int i = 0; i < rows; i++) {
                        final int current_idx = i * columns;
                        int distance = Math.abs(OnyxFocusFinder.getAbsoluteTop(this.getChildAt(current_idx)) - srcRect.top);
                        if (distance < min_distance) {
                            min_distance = distance;
                            best_item_index = current_idx;
                        }
                    }

                    this.setSelection(best_item_index);
                    return;
                }
            }
            else if (boundarySide == BoundarySide.RIGHT) {
                final int last_row_of_right_column = ((column_mod == 0) ? rows : rows - 1) - 1;
                final int right_column_last_row = (last_row_of_right_column * columns) + columns - 1;

                if (srcRect.top < OnyxFocusFinder.getAbsoluteTop(this.getChildAt(columns - 1))) {
                    this.setSelection(columns - 1);
                    return;
                }
                else if (srcRect.top > OnyxFocusFinder.getAbsoluteBottom(this.getChildAt(right_column_last_row))) {
                    this.setSelection(right_column_last_row);
                    return;
                }
                else {
                    int min_distance = Integer.MAX_VALUE;
                    int best_item_index = 0;

                    for (int i = 0; i <= last_row_of_right_column; i++) {
                        int current_right_column = (i * columns) + columns - 1;
                        int distance = Math.abs(OnyxFocusFinder.getAbsoluteTop(this.getChildAt(current_right_column)) - srcRect.top);
                        if (distance < min_distance) {
                            min_distance = distance;
                            best_item_index = current_right_column;
                        }
                    }

                    this.setSelection(best_item_index);
                    return;
                }
            }
            else {
                assert(false);
                throw new IndexOutOfBoundsException();
            }
        }
    }

    public boolean getInterceptVolumeKey() {
        return mIsInterceptVolumeKey;
    }

    public void setInterceptVolumeKey(boolean isInterceptVolumeKey) {
    	this.mIsInterceptVolumeKey = isInterceptVolumeKey;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (VERBOSE_LOG) Log.d(TAG, "onTouchEvent: " + event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mOnLongPressed = false;
                mOnFlinged = false;
                mLastTouchDownX = event.getX();
                mLastTouchDownTime = event.getEventTime();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!mOnFlinged) {
                    PageTurningDirection direction = PageTurningDetector.detectHorizontalTuring(OnyxGridView.this.getContext(),
                            (int) (event.getX() - mLastTouchDownX));
                    if (VERBOSE_LOG) Log.d(TAG, "delta x: " + (int)(event.getX() - mLastTouchDownX) +
                            ", direction: " + direction);
                    if (direction != PageTurningDirection.None) {
                        mOnFlinged = true;
                        event.setAction(MotionEvent.ACTION_CANCEL);
                        super.onTouchEvent(event);
                        return true;
                    }
                }
                else {
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                mLastTouchUpX = event.getX();
                long currentTime = event.getEventTime();
                long time = currentTime - mLastTouchDownTime;

                if (!mOnLongPressed) {
                    PageTurningDirection direction = PageTurningDetector.detectHorizontalTuring(OnyxGridView.this.getContext(),
                            (int) (event.getX() - mLastTouchDownX));
                    if (VERBOSE_LOG) Log.d(TAG, "delta x: " + (int)(event.getX() - mLastTouchDownX) +
                            ", direction: " + direction);
                    if (direction != PageTurningDirection.None) {
                        mOnFlinged = true;
                        event.setAction(MotionEvent.ACTION_CANCEL);
                        super.onTouchEvent(event);
                        switch (direction) {
                            case Left:
                                if (mAdapter.getPaginator().canPrevPage()) {
                                    mAdapter.getPaginator().prevPage();
                                }
                                break;
                            case Right:
                                if (mAdapter.getPaginator().canNextPage()) {
                                    mAdapter.getPaginator().nextPage();
                                }
                                break;
                            default:
                                assert(false);
                                break;
                        }
                        return true;
                    }
                }

                if (Math.abs(mLastTouchUpX - mLastTouchDownX) >= sMinFlingLength) {
                    mIsClickEvent = false;
                } else {
                    mIsClickEvent = true;
                }

                if (time >= 300 && mLastTouchDownX != mLastTouchUpX) {
                    return true;
                }
                break;
            }
        }

        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        try {
            return super.onTouchEvent(event);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return true;
        }
    }

    // ========================= GestureDetector.OnGestureListener ======================
    @Override
    public boolean onDown(MotionEvent e)
    {
        if (VERBOSE_LOG) Log.v(TAG, "onDown");
//        EpdController.addDirtyRegion(this, UpdateMode.GU_FAST);
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e)
    {
        if (VERBOSE_LOG) Log.v(TAG, "onShowPress");
        Rect r = new Rect();
        for (int i = 0; i < this.getChildCount(); i++) {
            View v = this.getChildAt(i);
            r.set(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            if (r.contains((int)e.getX(), (int)e.getY())) {
                this.setSelectionInTouchMode(i);
                EpdController.invalidate(v, UpdateMode.GU_FAST);
//                v.invalidate();
                return;
            }
        }

        this.setSelectionInTouchMode(AdapterView.INVALID_POSITION);
    }


    @Override
    public boolean onSingleTapUp(MotionEvent e)
    {
        if (VERBOSE_LOG) Log.v(TAG, "onSingleTapUp");
        if (mIsClickEvent) {
        Rect r = new Rect();
        for (int i = 0; i < this.getChildCount(); i++) {
            View v = this.getChildAt(i);
            r.set(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            if (r.contains((int)e.getX(), (int)e.getY())) {
                if (this.getSelectedView() == v) {
                    // if onShowPress() happens, it will be before onSingleTapUp(), and view may has already been selected in it
                    Log.w(TAG, "child view is already selected");
                    return false;
                }

                this.setSelectionInTouchMode(i);
                EpdController.invalidate(v, UpdateMode.GU_FAST);
//                v.invalidate();
                return false;
            }
        }

        return false;
        } else {
           return true;
        }
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY)
    {
        if (VERBOSE_LOG) Log.v(TAG, "onScroll");
        return false;
    }
    @Override
    public void onLongPress(MotionEvent e)
    {
        if (VERBOSE_LOG) Log.v(TAG, "onLongPress");

        if (!mOnFlinged) {
            mOnLongPressed = true;
            for (OnLongPressListener l : mOnLongPressListenerList) {
                l.onLongPress();
            }
        }
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY)
    {
        if (VERBOSE_LOG) Log.v(TAG, "onFling");
        return false;
    }

    public void enableOnFling(boolean value){
    	enableOnFling = value;
    }

    /**
     * special focusable search method to select GridView's corresponding item
     *
     * @param direction
     * @param previouslyFocusedRect
     * @return
     */
    public boolean searchAndSelectNextFocusableChildItem(int direction, Rect previouslyFocusedRect)
    {
        if (this.getChildCount() > 0) {
            BoundarySide side = BoundarySide.valueOf(direction);
            if (side != BoundarySide.NONE) {
                this.selectBoundaryItemBySearch(previouslyFocusedRect, side);
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu)
    {
        if (VERBOSE_LOG) Log.d(TAG, "create context menu");
        super.onCreateContextMenu(menu);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        if (VERBOSE_LOG) Log.d(TAG, "onSizeChanged: from " + oldw + ", " + oldh + " to " + w + ", " + h);
        super.onSizeChanged(w, h, oldw, oldh);

        for (OnSizeChangedListener l : mOnSizeChangedListenerList) {
            l.onSizeChanged();
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
//        Log.d(sTag, "onDraw");
        super.onDraw(canvas);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
            Rect previouslyFocusedRect)
    {
        if (gainFocus) {
            if (previouslyFocusedRect != null)
            {
                Rect r = OnyxFocusFinder.getAbsoluteCoorinateRect(this, previouslyFocusedRect);
                if (this.searchAndSelectNextFocusableChildItem(direction, r)) {
                    return;
                }
            }
        }

        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (mIsInterceptVolumeKey
				&& (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				this.onKeyDown(keyCode, event);
			}
			return true;
		}
        if (keyCode == KeyEvent.KEYCODE_BUTTON_START || keyCode == KeyEvent.KEYCODE_CLEAR) {
            return true;
        }
		return super.dispatchKeyEvent(event);
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
//            Log.d(sTag, "onKeyDown");
            if ((mIsInterceptVolumeKey && keyCode == KeyEvent.KEYCODE_VOLUME_UP) ||
                    keyCode == KeyEvent.KEYCODE_PAGE_UP) {
                if (mAdapter.getPaginator().canPrevPage()) {
//                    EpdController.invalidate(this, UpdateMode.GU);
                    mAdapter.getPaginator().prevPage();
                }
                return true;
            }
            else if ((mIsInterceptVolumeKey && keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) ||
                    keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
                if (mAdapter.getPaginator().canNextPage()) {
//                    EpdController.invalidate(this, UpdateMode.GU);
                    mAdapter.getPaginator().nextPage();
                }
                return true;
            }

            if (mCrossVertical && ((keyCode == KeyEvent.KEYCODE_DPAD_UP) ||
                    (keyCode == KeyEvent.KEYCODE_DPAD_DOWN))) {
                EpdController.invalidate(this, UpdateMode.GU_FAST);
                return super.onKeyDown(keyCode, event);
            }

            if (mCrossHorizon && ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT) ||
                    (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT))) {
                EpdController.invalidate(this, UpdateMode.GU_FAST);
                return super.onKeyDown(keyCode, event);
            }

            int item_count = this.getCount();

            if (item_count >= 1) {
                int columns = Math.min(this.getCount(), mAdapter.getPageLayout().getLayoutColumnCount());
                if (columns <= 0) {
                    return super.onKeyDown(keyCode, event);
                }

                int current_idx = this.getSelectedItemPosition();
                int last_idx = item_count - 1;

                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    if ((current_idx % columns) == 0) {
                        if (current_idx != 0) {
                            EpdController.invalidate(this, UpdateMode.GU_FAST);
                            this.setSelection(current_idx - 1);
                            return true;
                        }
                        else {
                            if (mAdapter.getPaginator().canPrevPage()) {
//                                EpdController.invalidate(this, UpdateMode.GU);
                                mAdapter.getPaginator().prevPage();
                                this.setSelection(mAdapter.getPaginator().getPageSize()-1);
                            }
                            return true;
                        }
                    }
                }
                else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    if ((current_idx == last_idx) || (((current_idx + 1) % columns) == 0)) {
                        if (current_idx == last_idx) {
                            if (mAdapter.getPaginator().canNextPage()) {
//                                EpdController.invalidate(this, UpdateMode.GU);
                                mAdapter.getPaginator().nextPage();
                            }
                            return true;
                        }
                        else {
                            EpdController.invalidate(this, UpdateMode.GU_FAST);
                            this.setSelection(current_idx + 1);
                            return true;
                        }
                    }
                }
                else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    if (current_idx < columns) {
                        int mod = last_idx % columns;
                        if (current_idx > mod) {
                            EpdController.invalidate(this, UpdateMode.GU_FAST);
                            this.setSelection(last_idx);
                            return true;
                        }
                        else {
                            EpdController.invalidate(this, UpdateMode.GU_FAST);
                            this.setSelection(last_idx - mod + current_idx);
                            return true;
                        }
                    }
                }
                else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    if (current_idx > (last_idx - columns)) {
                        int head_of_last_row = last_idx - (last_idx % columns);
                        if (current_idx < head_of_last_row) {
                            EpdController.invalidate(this, UpdateMode.GU_FAST);
                            this.setSelection(last_idx);
                            return true;
                        }
                        else {
                            EpdController.invalidate(this, UpdateMode.GU_FAST);
                            this.setSelection(current_idx % columns);
                            return true;
                        }
                    }
                }
            }

            if ((keyCode == KeyEvent.KEYCODE_DPAD_UP) ||
                    (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) ||
                    (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) ||
                    (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                EpdController.invalidate(this, UpdateMode.GU_FAST);
            }

            return super.onKeyDown(keyCode, event);
        }
        finally {
//            Log.d(sTag, "onKeydown finished");
        }
    }
}
