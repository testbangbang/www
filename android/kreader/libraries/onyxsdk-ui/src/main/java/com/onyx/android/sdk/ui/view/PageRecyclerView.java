package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
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
    private int currentFocusedPosition;
    private boolean focusMode = false;
    private OnPagingListener onPagingListener;
    private int rows = 0;
    private int columns = 1;
    private float lastX, lastY;
    private OnChangeFocusListener onChangeFocusListener;

    public interface OnPagingListener {
        void onPrevPage(int prevPosition,int itemCount,int pageSize);
        void onNextPage(int nextPosition,int itemCount,int pageSize);
    }

    public interface OnChangeFocusListener {
        void onNextFocus(int position);
        void onPrevFocus(int position);
    }

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

        public DisableScrollGridManager(Context context) {
            super(context, 1);
        }

        public DisableScrollGridManager(Context context, int orientation, boolean reverseLayout) {
            super(context, 1, orientation, reverseLayout);
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

    public void setFocusMode(boolean focusMode) {
        this.focusMode = focusMode;
    }

    public boolean isFocusMode() {
        return focusMode;
    }

    public void setCurrentFocusedPosition(int currentFocusedPosition) {
        int lastFocusedPosition = this.currentFocusedPosition;
        this.currentFocusedPosition = currentFocusedPosition;
        getAdapter().notifyItemChanged(lastFocusedPosition);
        getAdapter().notifyItemChanged(currentFocusedPosition);
    }

    public int getCurrentFocusedPosition() {
        return currentFocusedPosition;
    }

    public void nextFocus(){
        int focusedPosition = currentFocusedPosition + 1;
        if (focusedPosition < getPageAdapter().getDataCount()){
            if ((focusedPosition % (columns * rows)) == 0){
                nextPage();
            }
            setCurrentFocusedPosition(focusedPosition);
            if (onChangeFocusListener != null){
                onChangeFocusListener.onNextFocus(focusedPosition);
            }
        }
    }

    public void prevFocus(){
        int focusedPosition = currentFocusedPosition - 1;
        if (focusedPosition >= 0){
            if (currentFocusedPosition % (columns * rows) == 0){
                prevPage();
            }
            setCurrentFocusedPosition(focusedPosition);
            if (onChangeFocusListener != null){
                onChangeFocusListener.onPrevFocus(focusedPosition);
            }
        }
    }

    public void setOnChangeFocusListener(OnChangeFocusListener onChangeFocusListener) {
        this.onChangeFocusListener = onChangeFocusListener;
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

        LayoutManager layoutManager = getDisableLayoutManager();
        if (layoutManager instanceof GridLayoutManager){
            ((GridLayoutManager) layoutManager).setSpanCount(columns);
        }
    }

    public PageAdapter getPageAdapter() {
        return ((PageAdapter) getAdapter());
    }

    public void resize(int newRows, int newColumns, int newSize){
        paginator.resize(newRows,newColumns,newSize);
    }

    public void setCurrentPage(int currentPage){
        if (paginator != null){
            paginator.setCurrentPage(currentPage);
        }
    }

    public GPaginator getPaginator() {
        return paginator;
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
                if (direction == PageTurningDirection.NEXT && !isFocusMode()) {
                    prevPage();
                    return true;
                } else if (direction == PageTurningDirection.PREV && !isFocusMode()) {
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isFocusMode()){
            switch (keyCode) {
                case KeyEvent.KEYCODE_PAGE_DOWN:
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    prevFocus();
                    return true;
                case KeyEvent.KEYCODE_PAGE_UP:
                case KeyEvent.KEYCODE_VOLUME_UP:
                    nextFocus();
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
    }

    public void setOnPagingListener(OnPagingListener listener) {
        this.onPagingListener = listener;
    }

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

        public interface OnLongPressListener<VH extends RecyclerView.ViewHolder>{
            void onItemLongPress(VH holder, int position);
        }

        public interface OnClickListener<VH extends RecyclerView.ViewHolder>{
            void onItemClick(VH holder, int position);
        }

        protected ViewGroup mParent;
        private OnLongPressListener onLongPressListener;
        private OnClickListener onClickListener;

        public abstract int getRowCount();
        public abstract int getColumnCount();
        public abstract int getDataCount();
        public abstract VH onPageCreateViewHolder(ViewGroup parent, int viewType);
        public abstract void onPageBindViewHolder(VH holder, int position);

        public void setOnLongPressListener(OnLongPressListener onLongPressListener) {
            this.onLongPressListener = onLongPressListener;
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            mParent = parent;
            return onPageCreateViewHolder(parent,viewType);
        }

        @Override
        public void onBindViewHolder(final VH holder, final int position) {
            final int adapterPosition = holder.getAdapterPosition();
            final View view = holder.itemView;
            if (view != null){
                if (position < getDataCount()){
                    view.setVisibility(VISIBLE);
                    onPageBindViewHolder(holder,adapterPosition);
                    setItemViewListener(view, holder, adapterPosition);
                    setFocusState(view,adapterPosition);
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
            PageRecyclerView pageRecyclerView = getPageRecyclerView();
            if (pageRecyclerView != null){
                pageRecyclerView.resize(getRowCount(),getColumnCount(),size);
            }
            return size;
        }

        public PageRecyclerView getPageRecyclerView(){
            return (PageRecyclerView)mParent;
        }

        private void setItemViewListener(View view, final VH holder, final int position){
            if (onClickListener != null){
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.onItemClick(holder, position);
                    }
                });
            }
            if (onLongPressListener != null){
                view.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onLongPressListener.onItemLongPress(holder, position);
                        return true;
                    }
                });
            }
        }

        private void setFocusState(final View view, final int position){
            final PageRecyclerView pageRecyclerView = getPageRecyclerView();
            if (pageRecyclerView.isFocusMode()){
                if (position == pageRecyclerView.getCurrentFocusedPosition()){
                    view.setActivated(true);
                }else {
                    view.setActivated(false);
                }

                view.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        pageRecyclerView.setCurrentFocusedPosition(position);
                        return false;
                    }
                });
            }
        }
    }
}
