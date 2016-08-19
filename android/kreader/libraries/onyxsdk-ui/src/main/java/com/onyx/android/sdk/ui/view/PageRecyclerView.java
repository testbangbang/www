package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.ui.utils.PageTurningDetector;
import com.onyx.android.sdk.ui.utils.PageTurningDirection;

/**
 * Created by suicheng on 2016/6/27.
 */
public class PageRecyclerView extends RecyclerView {

    private static final String TAG = PageRecyclerView.class.getSimpleName();
    private GPaginator paginator;
    public enum TouchDirection {Horizontal, Vertical}

    public static class DisableScrollLinearManager extends LinearLayoutManager {
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

    public static class DisableScrollGridManager extends GridLayoutManager{
        private boolean canScroll = false;

        public DisableScrollGridManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public DisableScrollGridManager(Context context, int spanCount) {
            super(context, spanCount);
        }

        public DisableScrollGridManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
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
            throw new IllegalArgumentException("Please use PageAdapter!");
        }
        rows = ((PageAdapter) adapter).getRowCount();
        columns = ((PageAdapter) adapter).getColumnCount();
        int size = adapter.getItemCount();
        paginator = new GPaginator(rows, columns,size);
        paginator.gotoPageByIndex(0);
    }

    public void resize(int newRows, int newColumns, int newSize){
        paginator.resize(newRows,newColumns,newSize);
    }

    public void setCurrentPage(int currentPage){
        if (paginator != null){
            paginator.setCurrentPage(currentPage);
        }
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

    private OnPagingListener onPagingListener;

    public void setOnPagingListener(OnPagingListener listener) {
        this.onPagingListener = listener;
    }

    public interface OnPagingListener {
        void onPrevPage(int prevPosition,int itemCount,int pageSize);

        void onNextPage(int nextPosition,int itemCount,int pageSize);
    }

    private int rows = 0;
    private int columns = 1;

    public void prevPage() {
        if (paginator.prevPage()){
            int position =  paginator.getCurrentPageBegin();
            managerScrollToPosition(position);
            if (onPagingListener != null){
                onPagingListener.onPrevPage(position,getAdapter().getItemCount(), rows * columns);
            }
        }
    }

    public void nextPage() {
        if (paginator.nextPage()){
            int position =  paginator.getCurrentPageBegin() ;
            managerScrollToPosition(position);
            if (onPagingListener != null){
                onPagingListener.onNextPage(position,getAdapter().getItemCount(), rows * columns);
            }
        }
    }

    private void managerScrollToPosition(int position) {
        getDisableLayoutManager().scrollToPositionWithOffset(position,0);
    }

    private LinearLayoutManager getDisableLayoutManager(){
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        if (layoutManager instanceof DisableScrollLinearManager){
            layoutManager = (DisableScrollLinearManager) getLayoutManager();
        }else if (layoutManager instanceof DisableScrollGridManager){
            layoutManager = (DisableScrollGridManager) getLayoutManager();
        }
        return layoutManager;
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
                double itemHeight = ((double)mParent.getMeasuredHeight() - paddingBottom - paddingTop) / getRowCount();
                view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int) Math.ceil(itemHeight)));
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
                    size=  size + blankCount;
                }
            }
            PageRecyclerView pageRecyclerView = (PageRecyclerView)mParent;
            if (pageRecyclerView != null){
                pageRecyclerView.resize(getRowCount(),getColumnCount(),size);
            }
            return size;
        }
    }
}
