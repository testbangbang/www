package com.onyx.android.sdk.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.data.PageTurningDetector;
import com.onyx.android.sdk.data.PageTurningDirection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Joy on 14-3-11.
 */
public final class OnyxTableView extends TableLayout {
    private static final String TAG = OnyxTableView.class.getSimpleName();
    private static final boolean VERBOSE_LOG = false;

    private static class PageInfo {
        /**
         * absolute index for page's first item
         */
        private int mStartIndexOfPage;
        private int mRows;
        private int mColumns;

        public PageInfo(int startIndexOfPage, int rows, int columns) {
            mStartIndexOfPage = startIndexOfPage;
            mRows = rows;
            mColumns = columns;
        }

        public int getIndexOfItem(int row, int col) {
            return mStartIndexOfPage + col + (row * mColumns);
        }

        public int getRows() {
            return mRows;
        }

        public int getColumns() {
            return mColumns;
        }
    }

    private static final class GridViewPaginator {
        private final int mRows;
        private final int mColumns;
        private int mItemCount;
        private ArrayList<Integer> mPageSizeList = null;

        /**
         *
         * @param rows
         * @param columns
         * @param itemCount
         * @param pageSizeList when not null, total page count and each page's size is specified by the list,
         *                     and either rows or columns number should be 1
         *
         */
        public GridViewPaginator(int rows, int columns, int itemCount, Collection<Integer> pageSizeList) {
            if (rows <= 0) {
                rows = 1;
            }
            if (columns <= 0) {
                columns = 1;
            }
            mRows = rows;
            mColumns = columns;
            mItemCount = itemCount;
            if (pageSizeList != null) {
                if (rows != 1 && columns != 1) {
                    throw new IllegalArgumentException();
                }
                mPageSizeList = new ArrayList<Integer>(pageSizeList);
            }
        }

        public void updateData(int itemCount, Collection<Integer> pageSizeList) {
            mItemCount = itemCount;
            if (pageSizeList == null) {
                mPageSizeList = null;
            } else {
                if (mRows != 1 && mColumns != 1) {
                    throw new IllegalArgumentException();
                }
                mPageSizeList.clear();
                mPageSizeList.addAll(pageSizeList);
            }
        }

        public void updateData(int itemCount) {
            mItemCount = itemCount;
        }

        public final int getGridViewRows() {
            return mRows;
        }

        public final int getGridViewColumns() {
            return mColumns;
        }

        public int getPageCount() {
            if (mPageSizeList != null) {
                return mPageSizeList.size();
            } else {
                return mItemCount / this.getPageSize() +
                        (mItemCount % this.getPageSize() != 0 ? 1 : 0);
            }
        }

        public PageInfo getPageInfo(int page) {
            int idx = this.getStartIndexOfPage(page);
            if (mPageSizeList != null) {
                if (page >= mPageSizeList.size()) {
                    throw new IllegalAccessError();
                }
                if (mRows != 1 && mColumns != 1) {
                    throw new IllegalArgumentException();
                }
                if (mRows == 1) {
                    return new PageInfo(idx, 1, mPageSizeList.get(page));
                } else {
                    return new PageInfo(idx, mPageSizeList.get(page), 1);
                }
            } else {
                return new PageInfo(idx, mRows, mColumns);
            }
        }

        private int getPageSize() {
            return mRows * mColumns;
        }

        private int getStartIndexOfPage(int page) {
            if (mPageSizeList != null) {
                if (page >= mPageSizeList.size()) {
                    throw new IllegalAccessError();
                }
                int idx = 0;
                for (int i = 0; i < page; i++) {
                    idx += mPageSizeList.get(i);
                }
                return idx;
            } else {
                return page * this.getPageSize();
            }
        }
    }

    public static abstract class TableViewCallback {
        public void beforePageChanging(OnyxTableView tableView, int newPage, int oldPage) { }

        /**
         * first event will be page from -1 to 0
         *
         * @param newPage
         * @param oldPage
         */
        public void afterPageChanged(OnyxTableView tableView, int newPage, int oldPage) { }

        public void onItemSelected(OnyxTableItemView view) { }

        public void onItemClick(OnyxTableItemView view) { }

        /**
         * view will be null if long clicking on empty area of table layout
         *
         * @param view
         * @return
         */
        public boolean onItemLongClick(OnyxTableItemView view) {
            return false;
        }

        public void beforeDPadMoving(OnyxTableView tableView, KeyEvent event) { }

        public void afterDPadMoved(OnyxTableView tableView, KeyEvent event) { }
    }

    private TableViewCallback mCallback = null;
    public void setCallback(TableViewCallback callback) {
        mCallback = callback;
    }

    private void notifyBeforePageChanging(int newPage, int oldPage) {
        if (mCallback != null) {
            mCallback.beforePageChanging(this, newPage, oldPage);
        }
    }

    private void notifyAfterPageChanged(int newPage, int oldPage) {
        if (mCallback != null) {
            mCallback.afterPageChanged(this, newPage, oldPage);
        }
    }

    private void notifyItemSelected(OnyxTableItemView view) {
        if (mCallback != null) {
            mCallback.onItemSelected(view);
        }
    }

    private void notifyItemClick(OnyxTableItemView view) {
        if (mCallback != null) {
            mCallback.onItemClick(view);
        }
    }

    private boolean notifyItemLongClick(OnyxTableItemView view) {
        if (mCallback != null) {
            return mCallback.onItemLongClick(view);
        }
        return false;
    }

    private void notifyBeforeDPadMove(KeyEvent event) {
        if (mCallback != null) {
            mCallback.beforeDPadMoving(this, event);
        }
    }

    private void notifyAfterDPadMove(KeyEvent event) {
        if (mCallback != null) {
            mCallback.afterDPadMoved(this, event);
        }
    }

    private GAdapter mAdapter = null;
    private int mItemViewGlobalLayoutResource = -1;
    private Map<String, Integer> mItemViewGlobalMapping = null;

    private GridViewPaginator mPaginator = null;
    private int mPageIndex = -1;

    private LayoutInflater mInflater = null;
    private int mItemViewWidth = 0;
    private int mItemViewHeight = 0;

    private View mSelectedView = null;

    private boolean mCaptureHorizontalKeyMovement = true;
    private boolean mCaptureVerticalKeyMovement = false;

    private float mLastTouchDownX = 0f;

    private OnTouchListener mItemOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (VERBOSE_LOG) Log.d(TAG, "mItemOnTouchListener, onTouch: " + event.getActionMasked());
            if (event.getActionMasked() != MotionEvent.ACTION_UP) {
                v.requestFocus();
            }
            return false;
        }
    };

    private OnClickListener mItemOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (VERBOSE_LOG) Log.d(TAG, "mItemOnClickListener, onClick");
            OnyxTableView.this.notifyItemClick((OnyxTableItemView) v);
        }
    };

    private OnLongClickListener mItemOnLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (VERBOSE_LOG) Log.d(TAG, "mItemOnLongClickListener, onLongClick");
            return OnyxTableView.this.notifyItemLongClick((OnyxTableItemView) v);
        }
    };

    private OnFocusChangeListener mItemOnFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (VERBOSE_LOG) Log.d(TAG, "mItemOnFocusChangeListener, onFocusChange: " + v + ", " + hasFocus);
            // if focus changed between child views in table layout,
            // it will first lost by old item, then gained by new item
            if (hasFocus) {
                mSelectedView = v;
                OnyxTableView.this.notifyItemSelected((OnyxTableItemView) v);
            } else {
                mSelectedView = null;
            }
            if (VERBOSE_LOG) Log.d(TAG, "selected item: " + mSelectedView);
        }
    };

    public OnyxTableView(Context context) {
        super(context);
        this.init();
    }

    public OnyxTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();

        TypedArray typed_array=context.obtainStyledAttributes(attrs, R.styleable.OnyxTableView);
        int rows = typed_array.getInt(R.styleable.OnyxTableView_numRows, 1);
        int columns = typed_array.getInt(R.styleable.OnyxTableView_numColumns, 1);
        mPaginator = new GridViewPaginator(rows, columns, 0, null);
    }

    private void init() {
        this.setFocusable(false);
        this.setShrinkAllColumns(false);
        this.setStretchAllColumns(false);

        mInflater = LayoutInflater.from(this.getContext());

        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (VERBOSE_LOG) Log.d(TAG, "OnyxTableView, onLongClick");
                return OnyxTableView.this.notifyItemLongClick(null);
            }
        });
    }

    /**
     * for table view whose item views share same layout resource and mapping options
     *
     * @param adapter
     * @param rows
     * @param columns
     * @param itemViewLayoutResource
     * @param itemViewMapping
     */
    public void setupAsGridView(GAdapter adapter, int rows, int columns, Collection<Integer> pageSizeList,
                                int itemViewLayoutResource, Map<String, Integer> itemViewMapping) {
        mPaginator = new GridViewPaginator(rows, columns, adapter.getList().size(), pageSizeList);
        this.setItemViewStyleInternal(itemViewLayoutResource, itemViewMapping);
        this.setAdapter(adapter);
    }

    public void setupAsGridView(GAdapter adapter, int rows, int columns, Collection<Integer> pageSizeList) {
        this.setupAsGridView(adapter, rows, columns, pageSizeList, -1, null);
    }

    public void setupAsGridView(GAdapter adapter, int rows, int columns) {
        this.setupAsGridView(adapter, rows, columns, null);
    }

    public void setupAsGridView(int rows, int columns) {
        mPaginator = new GridViewPaginator(rows, columns, 0, null);
    }

    public void setItemViewStyle(int itemViewLayoutResource, Map<String, Integer> itemViewMapping) {
        this.setItemViewStyleInternal(itemViewLayoutResource, itemViewMapping);
        if (mAdapter == null || mPaginator == null) {
            return;
        }
        this.updateCurrentPage();
    }

    public void setAdapter(GAdapter adapter) {
        mAdapter = adapter;
        if (adapter == null) {
            return;
        }

        if (mPaginator == null) {
            return;
        }
        mItemViewWidth = this.getRegionWidth() / mPaginator.getGridViewColumns();
        mItemViewHeight = this.getRegionHeight() / mPaginator.getGridViewRows();
        mPaginator.updateData(adapter.size());

        this.removeAllViews();
        if (mPaginator.getPageCount() > 0) {
            this.gotoPage(0);
        }
    }

    public int getPageIndex() {
        return mPageIndex;
    }

    public int getPageCount() {
        return mPaginator.getPageCount();
    }

    public boolean gotoPage(int page) {
        if (page < 0 || page >= mPaginator.getPageCount()) {
            return false;
        }

        this.notifyBeforePageChanging(page, mPageIndex);
        this.removeAllViews();

        PageInfo page_info = mPaginator.getPageInfo(page);
        for (int row = 0; row < page_info.getRows(); row++) {
            TableRow table_row = new TableRow(this.getContext());
            for (int col = 0; col < page_info.getColumns(); col++) {
                int index = page_info.getIndexOfItem(row, col);
                GObject item = mAdapter.get(index);
                if (item == null) {
                    break;
                }
                OnyxTableItemView item_view = mItemViewGlobalLayoutResource > 0
                        ? OnyxTableItemView.create(mInflater, item, mItemViewGlobalLayoutResource, mItemViewGlobalMapping)
                        : OnyxTableItemView.create(mInflater, item);
                TableRow.LayoutParams params = new TableRow.LayoutParams();
                params.width = item_view.getLayoutWidth() == TableRow.LayoutParams.WRAP_CONTENT ?
                        TableRow.LayoutParams.WRAP_CONTENT : mItemViewWidth;
                params.height = item_view.getLayoutHeight() == TableRow.LayoutParams.WRAP_CONTENT ?
                        TableRow.LayoutParams.WRAP_CONTENT : mItemViewHeight;
                if (item_view.getWeight() > 0) {
                    // weight only works for either 1 row or 1 column table
                    if (page_info.getRows() == 1) {
                        params.width *= (int) item_view.getWeight();
                    } else if (page_info.getColumns() == 1) {
                        params.height *= (int) item_view.getWeight();
                    }
                }
                item_view.setLayoutParams(params);
                item_view.setFocusable(true);
//                item_view.setFocusableInTouchMode(true);
                item_view.setOnTouchListener(this.mItemOnTouchListener);
                item_view.setOnClickListener(this.mItemOnClickListener);
                item_view.setOnLongClickListener(this.mItemOnLongClickListener);
                item_view.setOnFocusChangeListener(this.mItemOnFocusChangeListener);
                table_row.addView(item_view);
            }
            this.addView(table_row);
        }

        int old_page = mPageIndex;
        mPageIndex = page;
        this.notifyAfterPageChanged(page, old_page);
        return true;
    }

    public OnyxTableItemView getSelectedItemView() {
        return (OnyxTableItemView)mSelectedView;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mItemViewWidth = this.getRegionWidth() / mPaginator.getGridViewColumns();
        mItemViewHeight = this.getRegionHeight() / mPaginator.getGridViewRows();
        //Use Handler to add the update method message queue bottom.
        Handler updateHandler=new Handler();
        updateHandler.post(new Runnable() {
            @Override
            public void run() {
                OnyxTableView.this.updateCurrentPage();
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (VERBOSE_LOG) Log.d(TAG, "onInterceptTouchEvent, " + ev);
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchDownX = ev.getX();
                return false;
            case MotionEvent.ACTION_MOVE:
                return PageTurningDetector.detectHorizontalTuring(this.getContext(),
                        (int) (ev.getX() - mLastTouchDownX)) != PageTurningDirection.None;
            default:
                break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (VERBOSE_LOG) Log.d(TAG, "onTouchEvent, " + ev);
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchDownX = ev.getX();
                return true;
            case MotionEvent.ACTION_UP:
                PageTurningDirection direction = PageTurningDetector.detectHorizontalTuring(this.getContext(),
                        (int) (ev.getX() - mLastTouchDownX));
                if (VERBOSE_LOG) Log.d(TAG, "delta x: " + (int)(ev.getX() - mLastTouchDownX) + ", direction: " + direction);
                switch (direction) {
                    case Left:
                        this.previousPage();
                        break;
                    case Right:
                        this.nextPage();
                        break;
                    default:
                        break;
                }
            default:
                break;
        }

        return true;
    }

    private void setItemViewStyleInternal(int itemViewlayoutResource, Map<String, Integer> itemViewmapping) {
        mItemViewGlobalLayoutResource = itemViewlayoutResource;
        mItemViewGlobalMapping = itemViewmapping;
    }

    private void updateCurrentPage() {
        this.removeAllViews();
        if (mPaginator.getPageCount() > 0) {
            int page = Math.min(mPageIndex, mPaginator.getPageCount() - 1);
            this.gotoPage(page);
        }
    }

    /**
     * return null if not found, else return (row, column) pair
     * @param view
     * @return
     */
    private Pair<Integer, Integer> locateViewInPage(View view) {
        PageInfo page = mPaginator.getPageInfo(mPageIndex);
        for (int row = 0; row < page.getRows() && row < this.getChildCount(); row++) {
            TableRow table_row = (TableRow)this.getChildAt(row);
            for (int col = 0; col < page.getColumns() && col < table_row.getChildCount(); col++) {
                if (view == table_row.getChildAt(col)) {
                    return new Pair<Integer, Integer>(row, col);
                }
            }
        }

        return null;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean ret = false;
        if (VERBOSE_LOG) Log.d(TAG, "dispatchKeyEvent: " + event.getAction() + ", " + event.getKeyCode());
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (VERBOSE_LOG) Log.d(TAG, "handle KeyEvent.ACTION_DOWN");
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_PAGE_UP:
                    if (this.canPreviousPage()) {
                        this.previousPage();
                        PageInfo page = mPaginator.getPageInfo(mPageIndex);
                        this.setSelection(page.getRows() - 1, page.getColumns() - 1);
                    }
                    return true;
                case KeyEvent.KEYCODE_PAGE_DOWN:
                    if (this.canNextPage()) {
                        this.nextPage();
                        this.setSelection(0, 0);
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    try {
                        this.notifyBeforeDPadMove(event);
                        ret = this.moveSelectionByKey(event.getKeyCode());
                    } finally {
                        this.notifyAfterDPadMove(event);
                        if (ret) {
                            return ret;
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    private boolean moveSelectionByKey(int keyCode) {
        if (this.getChildCount() <= 0) {
            return false;
        }

        if (mSelectedView == null) {
            this.setSelection(0, 0);
            return true;
        }

        Pair<Integer, Integer> loc = this.locateViewInPage(mSelectedView);
        if (loc == null) {
            return false;
        }
        
        int row = loc.first;
        int col = loc.second;
        PageInfo page = mPaginator.getPageInfo(mPageIndex);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT: {
                if (col == 0) {
                    if (row <= 0) {
                        if (mCaptureHorizontalKeyMovement) {
                            if (this.canPreviousPage()) {
                                this.previousPage();
                                page = mPaginator.getPageInfo(mPageIndex);
                                this.setSelection(page.getRows() - 1, page.getColumns() - 1);
                            }
                            return true;
                        }
                    } else {
                        if (mCaptureHorizontalKeyMovement) {
                            this.setSelection(row - 1, page.getColumns() - 1);
                            return true;
                        }
                    }
                }
                break;
            }
            case KeyEvent.KEYCODE_DPAD_RIGHT: {
                if (col == page.getColumns() - 1) {
                    if (row >= page.getRows() - 1) {
                        if (mCaptureHorizontalKeyMovement) {
                            if (this.canNextPage()) {
                                this.nextPage();
                                this.setSelection(0, 0);
                            }
                            return true;
                        }
                    } else {
                        if (mCaptureHorizontalKeyMovement) {
                            this.setSelection(row + 1, 0);
                            return true;
                        }
                    }
                }
                break;
            }
            case KeyEvent.KEYCODE_DPAD_UP:
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                break;
            default:
                break;
        }

        return false;
    }

    private void setSelection(int row, int column) {
        PageInfo page = mPaginator.getPageInfo(mPageIndex);
        assert(row < page.getRows() || column < page.getColumns());

        if (row < this.getChildCount()) {
            TableRow table_row = (TableRow)this.getChildAt(row);
            if (column < table_row.getChildCount()) {
                table_row.getChildAt(column).requestFocus();
            }
        }
    }

    private boolean canPreviousPage() {
        return mPageIndex > 0;
    }

    private boolean canNextPage() {
        return mPageIndex < mPaginator.getPageCount()  - 1;
    }

    public void previousPage() {
        if (!this.canPreviousPage()) {
            return;
        }
        this.gotoPage(mPageIndex - 1);
    }

    public void nextPage() {
        if (!this.canNextPage()) {
            return;
        }
        this.gotoPage(mPageIndex + 1);
    }

    private int getRegionWidth() {
        return this.getWidth() - this.getPaddingLeft() - this.getPaddingRight();
    }

    private int getRegionHeight() {
        return this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
    }

}
