package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.onyx.android.sdk.ui.utils.PageTurningDetector;
import com.onyx.android.sdk.ui.utils.PageTurningDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/6/27.
 */
public class PageRecyclerView extends RecyclerView {

    private static final String TAG = PageRecyclerView.class.getSimpleName();
    private boolean hasAdjust = false;
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

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (!(adapter instanceof PageAdapter)){
            throw new IllegalArgumentException("Use PageAdapter");
        }
        mRow = ((PageAdapter) adapter).getRowCount();
        mColumn = ((PageAdapter) adapter).getColumnCount();
    }

    private void init() {
        setItemAnimator(null);
        setClipToPadding(true);
        setClipChildren(true);
        invalidateItemDecorations();
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

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int firstVisiblePosition = getFirstVisiblePosition();
        int lastVisiblePosition = getLastVisiblePosition();
        int index = lastVisiblePosition - firstVisiblePosition;
        if (getChildCount() > index){
            int top = getChildAt(lastVisiblePosition - firstVisiblePosition).getTop();
            int bottom = getChildAt(lastVisiblePosition - firstVisiblePosition).getBottom();
            int height = getMeasuredHeight();
            if (bottom > height && top < height && getChildCount() > 0 && !hasAdjust){
                Log.d(TAG, "top: "+top +" bottom:"+bottom + " height:" + height);
                setPadding(getPaddingLeft(),getPaddingTop(),getPaddingRight(),getPaddingBottom() + height - top);
                hasAdjust = true;
                getAdapter().notifyDataSetChanged();
            }
        }
    }

    private OnPagingListener onPagingListener;

    public void setOnPagingListener(OnPagingListener listener) {
        this.onPagingListener = listener;
    }

    public interface OnPagingListener {
        void onPrevPage(int prevPosition,int itemCount,int pageSize);

        void onNextPage(int nextPosition,int itemCount,int pageSize);
    }

    private List<Integer> positionCount = new ArrayList<>();
    private int currentPosition = 0;
    private int mRow = 0;
    private int mColumn = 1;

    public void prevPage() {
        int finalPosition = getFirstCompletelyVisibleItemPosition() - mRow;
        finalPosition = finalPosition > 0 ? finalPosition : 0;
        managerScrollToPosition(finalPosition);
        scrollBy(0,-getMeasuredHeight());
        if (onPagingListener != null){
            onPagingListener.onPrevPage(finalPosition,getAdapter().getItemCount(),mRow * mColumn);
        }
    }

    public void nextPage() {
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
                managerScrollToPosition(finalPosition);
                if (onPagingListener != null){
                    onPagingListener.onNextPage(finalPosition,getAdapter().getItemCount(),mRow * mColumn);
                }
            }

        }
    }

    public int getFirstCompletelyVisibleItemPosition() {
        DisableScrollLinearManager linearManager = (DisableScrollLinearManager) getLayoutManager();
        return linearManager.findFirstCompletelyVisibleItemPosition();
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

    private boolean isClipView(Rect rect, View view) {
        switch (touchDirection) {
            case Horizontal:
                return (rect.right - rect.left) < view.getWidth();
            case Vertical:
                return (rect.bottom - rect.top) < view.getHeight();
        }
        return false;
    }

    public static abstract class PageAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{

        protected ViewGroup mParent;

        public abstract int getRowCount();
        public abstract int getColumnCount();
        public abstract int getDataCount();
        public abstract VH onPageCreateViewHolder(ViewGroup parent, int viewType);
        public abstract void onPageBindViewHolder(VH holder, int position);

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            mParent = parent;
            return onPageCreateViewHolder(parent,viewType);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            final View view = holder.itemView;
            if (view != null){
                if (position < getDataCount()){
                    view.setVisibility(VISIBLE);
                    onPageBindViewHolder(holder,position);
                }else {
                    view.setVisibility(INVISIBLE);
                }

                int paddingBottom = mParent.getPaddingBottom();
                int paddingTop = mParent.getPaddingTop();
                int itemHeight = (mParent.getMeasuredHeight() - paddingBottom - paddingTop) / getRowCount();
                view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,itemHeight));
            }
        }

        @Override
        public int getItemCount() {
            int itemCountOfPage = getRowCount() * getColumnCount();
            int size = getDataCount();
            if (size != 0){
                int remainder = size % itemCountOfPage;
                if (remainder > 0){
                    int blankCount =  itemCountOfPage - remainder;
                    return size + blankCount;
                }else {
                    return size;
                }
            }
            return size;
        }
    }
}
