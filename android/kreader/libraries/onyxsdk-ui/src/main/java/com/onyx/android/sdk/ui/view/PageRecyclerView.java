package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.onyx.android.sdk.ui.utils.PageTurningDetector;
import com.onyx.android.sdk.ui.utils.PageTurningDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/6/27.
 */
public class PageRecyclerView extends RecyclerView {

    public enum TouchDirection {Horizontal, Vertical}

    private static class DisableScrollLinearManager extends LinearLayoutManager {
        private boolean canScroll = false;

        public DisableScrollLinearManager(Context context) {
            super(context);
        }

        public DisableScrollLinearManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public DisableScrollLinearManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public void setScrollEnable(boolean enable) {
            this.canScroll = enable;
        }

        @Override
        public boolean canScrollVertically() {
            return canScroll;
        }

        @Override
        public boolean canScrollHorizontally() {
            return canScroll;
        }

    }

    public PageRecyclerView(Context context) {
        super(context);
        init();
    }

    public PageRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PageRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getItemAnimator().setAddDuration(0);
        getItemAnimator().setRemoveDuration(0);
        getItemAnimator().setChangeDuration(0);
        getItemAnimator().setMoveDuration(0);
        setLayoutManager(new DisableScrollLinearManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private TouchDirection touchDirection = TouchDirection.Vertical;

    private int detectDirection(MotionEvent currentEvent) {
        switch (touchDirection) {
            case Horizontal:
                return PageTurningDetector.detectHorizontalTuring(getContext(), (int) (currentEvent.getX() - lastX));
            case Vertical:
                return PageTurningDetector.detectVerticalTuring(getContext(), (int) (currentEvent.getY() - lastY));
            default:
                return PageTurningDetector.detectVerticalTuring(getContext(), (int) (currentEvent.getX() - lastX));
        }
    }

    private float lastX, lastY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                return (detectDirection(ev) != PageTurningDirection.NONE);
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                int direction = detectDirection(ev);
                if (direction == PageTurningDirection.NEXT) {
                    prevPage();
                    return true;
                } else if (direction == PageTurningDirection.PREV) {
                    nextPage();
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    private OnPagingListener onPagingListener;

    public void setOnPagingListener(OnPagingListener listener) {
        this.onPagingListener = listener;
    }

    public interface OnPagingListener {
        void onPrevPage(int prevPosition);

        void onNextPage(int nextPosition);
    }

    private List<Integer> positionCount = new ArrayList<>();
    private int currentPosition = 0;

    private void prevPage() {
        if (currentPosition > 0) {
            managerScrollToPosition(positionCount.get(--currentPosition));
        }
    }

    public int getFirstVisiblePosition() {
        DisableScrollLinearManager linearManager = (DisableScrollLinearManager) getLayoutManager();
        return linearManager.findFirstVisibleItemPosition();
    }

    public int getLastVisiblePosition() {
        DisableScrollLinearManager linearManager = (DisableScrollLinearManager) getLayoutManager();
        return linearManager.findLastVisibleItemPosition();
    }

    private void managerScrollToPosition(int position) {
        DisableScrollLinearManager linearManager = (DisableScrollLinearManager) getLayoutManager();
        linearManager.scrollToPositionWithOffset(position, 0);
    }

    private void nextPage() {
        LayoutManager layoutManager = getLayoutManager();
        if (!(layoutManager instanceof DisableScrollLinearManager)) {
            return;
        }
        if (getLastVisiblePosition() < getAdapter().getItemCount()) {
            int lastPosition = getLastVisiblePosition() - getFirstVisiblePosition();
            Rect rect = new Rect();
            View view;
            do {
                view = getChildAt(lastPosition--);
                if (view != null) {
                    view.getGlobalVisibleRect(rect);
                }
            } while (view != null && isClipView(rect, view));
            int finalPosition = lastPosition + getFirstVisiblePosition() + 2;
            if (finalPosition < getAdapter().getItemCount()) {
                positionCount.add(getFirstVisiblePosition());
                currentPosition++;
            }
            managerScrollToPosition(finalPosition);
        }
    }

    private boolean isClipView(Rect rect, View view) {
        switch (touchDirection) {
            case Horizontal:
                return (rect.right - rect.left) < view.getWidth();
            case Vertical:
                return (rect.bottom - rect.top) < view.getHeight();
        }
        return false;
    }
}
