package com.onyx.android.plato.view;

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


import com.onyx.android.plato.common.Constants;

import java.util.Hashtable;
import java.util.Map;

public class PageRecyclerView extends RecyclerView {
    private static final String TAG = PageRecyclerView.class.getSimpleName();
    private GPaginator paginator;
    private boolean allowTouch = true;

    public enum TouchDirection {Horizontal, Vertical}
    private int currentFocusedPosition;
    private OnPagingListener onPagingListener;
    private int rows = 0;
    private int columns = 1;
    private float lastX, lastY;
    private OnChangeFocusListener onChangeFocusListener;
    private Map<Integer, String> keyBindingMap = new Hashtable<>();
    private OnNoMorePageListener onNoMorePageListener;

    public interface OnPagingListener {
        void onPrevPage(int prevPosition, int itemCount, int pageSize);

        void onNextPage(int nextPosition, int itemCount, int pageSize);
    }

    public interface OnChangeFocusListener {
        void onFocusChange(int prev, int current);
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

    public void setCurrentFocusedPosition(int currentFocusedPosition) {
        int lastFocusedPosition = this.currentFocusedPosition;
        this.currentFocusedPosition = currentFocusedPosition;
        getAdapter().notifyItemChanged(lastFocusedPosition);
        getAdapter().notifyItemChanged(currentFocusedPosition);
        if (onChangeFocusListener != null) {
            onChangeFocusListener.onFocusChange(lastFocusedPosition, currentFocusedPosition);
        }
    }

    public int getCurrentFocusedPosition() {
        return currentFocusedPosition;
    }

    private void nextFocus(int focusedPosition) {
        if (!paginator.isItemInCurrentPage(focusedPosition)) {
            nextPage();
        }
        setCurrentFocusedPosition(focusedPosition);
    }

    private void prevFocus(int focusedPosition) {
        if (!paginator.isItemInCurrentPage(focusedPosition)) {
            prevPage();
        }
        setCurrentFocusedPosition(focusedPosition);
    }

    public void nextColumn() {
        int focusedPosition = paginator.nextColumn(currentFocusedPosition);
        if (focusedPosition < paginator.getSize()) {
            nextFocus(focusedPosition);
        }
    }

    public void prevColumn() {
        int focusedPosition = paginator.prevColumn(currentFocusedPosition);
        if (focusedPosition >= Constants.VALUE_ZERO) {
            prevFocus(focusedPosition);
        }
    }

    public void nextRow() {
        int focusedPosition = paginator.nextRow(currentFocusedPosition);
        if (focusedPosition < paginator.getSize()) {
            nextFocus(focusedPosition);
        }
    }

    public void prevRow() {
        int focusedPosition = paginator.prevRow(currentFocusedPosition);
        if (focusedPosition >= Constants.VALUE_ZERO) {
            prevFocus(focusedPosition);
        }
    }

    public void setOnChangeFocusListener(OnChangeFocusListener onChangeFocusListener) {
        this.onChangeFocusListener = onChangeFocusListener;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (!(adapter instanceof PageAdapter)) {
            throw new IllegalArgumentException("Please use PageAdapter!");
        }
        rows = ((PageAdapter) adapter).getRowCount();
        columns = ((PageAdapter) adapter).getColumnCount();
        int size = adapter.getItemCount();
        paginator = new GPaginator(rows, columns, size);
        paginator.gotoPageByIndex(Constants.VALUE_ZERO);

        LayoutManager layoutManager = getDisableLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            ((GridLayoutManager) layoutManager).setSpanCount(columns);
        }
    }

    public PageAdapter getPageAdapter() {
        return ((PageAdapter) getAdapter());
    }

    public void resize(int newRows, int newColumns, int newSize) {
        paginator.resize(newRows, newColumns, newSize);
    }

    public void setCurrentPage(int currentPage) {
        if (paginator != null) {
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
        setLayoutManager(new DisableScrollLinearManager(getContext(), LinearLayoutManager.VERTICAL, false));
        setDefaultMoveKeyBinding();
    }

    private TouchDirection touchDirection = TouchDirection.Vertical;

    private int detectDirection(MotionEvent currentEvent) {
        return PageTurningDetector.detectBothAxisTuring(getContext(), (int) (currentEvent.getX() - lastX), (int) (currentEvent.getY() - lastY));
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
                if (direction == PageTurningDirection.NEXT) {
                    if(allowTouch){
                        nextPage();
                    }
                    return true;
                } else if (direction == PageTurningDirection.PREV) {
                    if(allowTouch){
                        prevPage();
                    }
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setAllowTouch(boolean allowTouch){
        this.allowTouch = allowTouch;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return processKeyAction(keyCode);
    }

    private boolean processKeyAction(final int keyCode) {
        final String args = keyBindingMap.get(keyCode);
        if (args == null) {
            return false;
        }
        switch (args) {
            case KeyAction.NEXT_PAGE:
                nextPage();
                break;
            case KeyAction.PREV_PAGE:
                prevPage();
                break;
            case KeyAction.MOVE_LEFT:
                prevColumn();
                break;
            case KeyAction.MOVE_RIGHT:
                nextColumn();
                break;
            case KeyAction.MOVE_DOWN:
                prevRow();
                break;
            case KeyAction.MOVE_UP:
                nextRow();
                break;
            default:
                nextPage();
        }
        return true;
    }

    public void setKeyBinding(Map<Integer, String> keyBindingMap) {
        this.keyBindingMap = keyBindingMap;
    }

    public void setDefaultPageKeyBinding() {
        keyBindingMap.put(KeyEvent.KEYCODE_PAGE_DOWN, KeyAction.NEXT_PAGE);
        keyBindingMap.put(KeyEvent.KEYCODE_VOLUME_DOWN, KeyAction.NEXT_PAGE);
        keyBindingMap.put(KeyEvent.KEYCODE_PAGE_UP, KeyAction.PREV_PAGE);
        keyBindingMap.put(KeyEvent.KEYCODE_VOLUME_UP, KeyAction.PREV_PAGE);
    }

    public void setDefaultMoveKeyBinding() {
        keyBindingMap.put(KeyEvent.KEYCODE_PAGE_DOWN, KeyAction.MOVE_RIGHT);
        keyBindingMap.put(KeyEvent.KEYCODE_VOLUME_DOWN, KeyAction.MOVE_RIGHT);
        keyBindingMap.put(KeyEvent.KEYCODE_PAGE_UP, KeyAction.MOVE_LEFT);
        keyBindingMap.put(KeyEvent.KEYCODE_VOLUME_UP, KeyAction.MOVE_LEFT);
    }

    public void setOnPagingListener(OnPagingListener listener) {
        this.onPagingListener = listener;
    }

    public void prevPage() {
        if (paginator.prevPage()) {
            int position = paginator.getCurrentPageBegin();
            if (!paginator.isItemInCurrentPage(currentFocusedPosition)) {
                setCurrentFocusedPosition(position);
            }
            managerScrollToPosition(position);
            if (onPagingListener != null) {
                onPagingListener.onPrevPage(position, getAdapter().getItemCount(), rows * columns);
            }
        } else {
            if (onNoMorePageListener != null) {
                onNoMorePageListener.onNoMorePage(Constants.VALUE_ZERO);
            }
        }
    }

    public void nextPage() {
        if (paginator.nextPage()) {
            int position = paginator.getCurrentPageBegin();
            if (!paginator.isItemInCurrentPage(currentFocusedPosition)) {
                setCurrentFocusedPosition(position);
            }
            managerScrollToPosition(position);
            if (onPagingListener != null) {
                onPagingListener.onNextPage(position, getAdapter().getItemCount(), rows * columns);
            }
        } else {
            if (onNoMorePageListener != null) {
                onNoMorePageListener.onNoMorePage(Constants.VALUE_NEGATIVE_ONE);
            }
        }
    }

    public interface OnNoMorePageListener {
        void onNoMorePage(int status);
    }

    public void setOnMorePageListener(OnNoMorePageListener onNoMorePageListener) {
        this.onNoMorePageListener = onNoMorePageListener;
    }

    private void managerScrollToPosition(int position) {
        getDisableLayoutManager().scrollToPositionWithOffset(position, Constants.VALUE_ZERO);
    }

    private LinearLayoutManager getDisableLayoutManager() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        if (layoutManager instanceof DisableScrollLinearManager) {
            layoutManager = (DisableScrollLinearManager) getLayoutManager();
        } else if (layoutManager instanceof DisableScrollGridManager) {
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

    public static abstract class PageAdapter<VH extends ViewHolder> extends Adapter<VH> implements OnClickListener {

        protected ViewGroup mParent;
        protected OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

        public abstract int getRowCount();

        public abstract int getColumnCount();

        public abstract int getDataCount();

        public abstract VH onPageCreateViewHolder(ViewGroup parent, int viewType);

        public abstract void onPageBindViewHolder(VH holder, int position);

        public interface OnRecyclerViewItemClickListener {
            void onItemClick(View view, Object position);
        }

        public void setOnItemClick(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
            this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            mParent = parent;
            return onPageCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(final VH holder, final int position) {
            final int adapterPosition = holder.getAdapterPosition();
            final View view = holder.itemView;
            if (view != null) {
                if (position < getDataCount()) {
                    view.setVisibility(VISIBLE);
                    onPageBindViewHolder(holder, adapterPosition);
                    updateFocusView(view, adapterPosition);
                } else {
                    view.setVisibility(INVISIBLE);
                }

                int paddingBottom = mParent.getPaddingBottom();
                int paddingTop = mParent.getPaddingTop();
                double itemHeight = ((double) mParent.getMeasuredHeight() - paddingBottom - paddingTop) / getRowCount();
                if (itemHeight > Constants.VALUE_ZERO) {
                    view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Math.ceil(itemHeight)));
                }
            }
        }

        @Override
        public int getItemCount() {
            int itemCountOfPage = getRowCount() * getColumnCount();
            int size = getDataCount();
            if (size != Constants.VALUE_ZERO) {
                int remainder = size % itemCountOfPage;
                if (remainder > Constants.VALUE_ZERO) {
                    int blankCount = itemCountOfPage - remainder;
                    size = size + blankCount;
                }
            }
            PageRecyclerView pageRecyclerView = getPageRecyclerView();
            if (pageRecyclerView != null) {
                pageRecyclerView.resize(getRowCount(), getColumnCount(), getDataCount());
            }
            return size;
        }

        public PageRecyclerView getPageRecyclerView() {
            return (PageRecyclerView) mParent;
        }

        private void updateFocusView(final View view, final int position) {
            final PageRecyclerView pageRecyclerView = getPageRecyclerView();
            if (position == pageRecyclerView.getCurrentFocusedPosition()) {
                view.setActivated(true);
            } else {
                view.setActivated(false);
            }

            view.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        pageRecyclerView.setCurrentFocusedPosition(position);
                    }
                    return false;
                }
            });
        }
    }
}
