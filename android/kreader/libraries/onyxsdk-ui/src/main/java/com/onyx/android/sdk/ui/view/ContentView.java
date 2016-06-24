package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.utils.ContentViewUtil;
import com.onyx.android.sdk.ui.utils.PageTurningDetector;
import com.onyx.android.sdk.ui.utils.PageTurningDirection;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.utils.TouchDirection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by solskjaer49 on 14/9/17 16:02.
 * - view which could act as gridView or ListView by passing cols and rows.
 * - to use this view,you need to claim the sub_layout parameter,as the item layout resource , id<->data hash_map
 * - before setup your adapter.
 * - support Divider,setUp ur divider in the item layout,and use key (GAdapterUtil.TAG_DIVIDER_VIEW) to register
 * - the divider view id in the id<->data hash_map,then contentView will do the rest of job,
 * - auto counting the last item of the view and hide the divider.
 */
public class ContentView extends RelativeLayout {
    private static final String TAG = ContentView.class.getSimpleName();

    public void configGotoPageDialogWindowProperties(int height, int width, int positionX, int positionY) {
        this.dialogSetValueHeight = height;
        this.dialogSetValueWidth = width;
        this.dialogSetValuePositionX = positionX;
        this.dialogSetValuePositionY = positionY;
    }

    public void setDialogSetValueLayoutID(int dialogSetValueLayoutID) {
        this.dialogSetValueLayoutID = dialogSetValueLayoutID;
    }

    public enum ViewType {
        Thumbnail, List, Details
    }

    public static class ContentViewCallback {
        /**
         * handle your custom page strategy here,if not using sync load,you should implement this method.
         *
         * @param contentView
         * @param newPage
         * @param oldPage
         */
        public void beforePageChanging(ContentView contentView, int newPage, int oldPage) {
        }

        public void beforeFirstPage(ContentView contentView) {
        }

        public void afterLastPage(ContentView contentView, int pages) {
        }

        public void afterPageChanged(ContentView contentView, int newPage, int oldPage) {
        }

        public void onItemSelected(ContentItemView view) {
        }

        public void onItemClick(ContentItemView view) {
        }

        public boolean onItemLongClick(ContentItemView view) {
            return false;
        }

        public void beforeDPadMoving(ContentView contentView, KeyEvent event) {
        }

        public void afterDPadMoved(ContentView contentView, KeyEvent event) {
        }

        /**
         * for some circumstance,view's properties may change by different data type.
         * implement this method to set your own strategy.
         *
         * @param view
         * @param object
         */
        public void beforeSetupData(ContentItemView view, GObject object) {
        }

        public void onFunctionCopy() {
        }

        public void onFunctionDelete() {
        }

        public void onFunctionCut() {
        }

        public void onFunctionPaste() {
        }

        public void onFunctionCancel() {
        }

        /**
         * to notify some relative view that this view adapter has been change.
         * then those view need to do some relative action with data change.
         * e.q:update outside page count,button visibility.
         */
        public void onDataSetChange() {
        }

    }

    private LayoutInflater inflater = null;
    /**
     * - isShowPageInfoArea -> control the whole message bar visibility.
     * - isAlwaysShowPageIndicator -> only effect when isShowPageInfoArea is true,
     * determine to show or not the whole message bar with only 1 page.
     * - isShowInfo -> only effect when isShowPageInfoArea is true,
     * control the right side total info panel visibility.
     * - isCustomTotalInfo -> only effect when isShowPageInfoArea & isShowPageInfoArea are both true,
     * if isCustomTotalInfo ->true,contentView would not update the total info automatically,you
     * could present the custom message you want.
     * - ignorePageTuringKeyEvent -> means activity will override dispatchKeyEvent to control
     * contentView page turing.
     * - syncLoad -> defaultValue is true,means the adapter is provide all need content,contentView will
     * load page automatically.but if view will show many bitmap , or adapter sizes is pretty large.
     * should load content by page.
     * - isBlankAreaAnswerLongClick -> define dummy object will (or not) answer the long click event.
     * default value is true.(need to set this value before bind adapter),you need to judge
     * the long click item is a dummy object or not.if set false,there is no need to do the judge.
     */
    private boolean isShowPageInfoArea = true;
    private boolean isAlwaysShowPageIndicator = true;
    private boolean isShowInfo = true;
    private boolean isCustomTotalInfo = false;
    private boolean syncLoad = true;
    private boolean isForceFocusInTouchMode = false;
    private boolean isBlankAreaAnswerLongClick = true;

    private View selectedView = null;
    private OnyxBaseGridLayout gridLayout;
    private RelativeLayout pageIndicator;
    private GPaginator paginator = new GPaginator();
    private GAdapter adapter;
    private ContentViewCallback callback;
    private String infoTittle;
    private float lastX = 0f;
    private float lastY = 0f;
    private int lastPage = -1;
    private int itemViewResourceId;
    private HashMap<String, Integer> dataToViewMapping;
    private ArrayList<Integer> styleLayoutList;
    private static GObject dummyObject;
    private TextView infoTextView;
    private LinearLayout functionLayout;
    private Button button_cut, button_delete, button_copy, button_paste, button_cancel, pageProgress;
    private int dialogSetValueHeight = -1, dialogSetValueWidth = -1, dialogSetValuePositionX = -1,
            dialogSetValuePositionY = -1, dialogSetValueLayoutID = 0;
    private ContentItemView.HyphenCallback hyphenCallback = null;
    private int touchDirection = TouchDirection.HORIZONTAL;
    private boolean mNeedBoundaryCheck = true;

    public ContentView(Context context) {
        this(context, null);
    }

    public ContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public ContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context);
    }

    public int getTouchDirection() {
        return touchDirection;
    }

    public void setTouchDirection(@TouchDirection.TouchDirectionDef int touchDirection) {
        this.touchDirection = touchDirection;
    }

    public void setInfoTittle(int resID) {
        infoTittle = getContext().getString(resID);
    }

    public void setInfoTittle(String stringResource) {
        infoTittle = stringResource;
    }

    public void setAlwaysShowPageIndicator(boolean isAlwaysShowPageIndicator) {
        this.isAlwaysShowPageIndicator = isAlwaysShowPageIndicator;
        updatePageIndicator();
    }

    public void setShowInfo(boolean isShowTotalItemCountIndicator) {
        this.isShowInfo = isShowTotalItemCountIndicator;
        updatePageIndicator();
    }

    public boolean isAlwaysShowPageIndicator() {
        return isAlwaysShowPageIndicator;
    }

    public boolean isShowInfo() {
        return isShowInfo;
    }

    public void setShowPageInfoArea(boolean isShowPageIndicator) {
        this.isShowPageInfoArea = isShowPageIndicator;
        updatePageIndicator();
    }

    public boolean isShowPageInfoArea() {
        return isShowPageInfoArea;
    }

    public void setCustomInfoKey(boolean isCustomTotalInfo) {
        this.isCustomTotalInfo = isCustomTotalInfo;
    }

    public boolean isBlankAreaAnswerLongClick() {
        return isBlankAreaAnswerLongClick;
    }

    public void setBlankAreaAnswerLongClick(boolean isBlankAreaAnswerLongClick) {
        this.isBlankAreaAnswerLongClick = isBlankAreaAnswerLongClick;
    }

    public GAdapter getCurrentAdapter() {
        return adapter;
    }

    public View getSelectedView() {
        return selectedView;
    }

    public int getGridRowCount() {
        return gridLayout.getRowCount();
    }

    public int getGridColumnCount() {
        return gridLayout.getColumnCount();
    }

    public int getSizePerPage() {
        return getGridColumnCount() * getGridRowCount();
    }

    public GPaginator getPaginator() {
        return paginator;
    }

    public int getCurrentPage() {
        return paginator.getCurrentPage();
    }

    public int getVisualCurrentPage() {
        return paginator.getCurrentPage() + 1;
    }

    public int getTotalPageCount() {
        return paginator.pages();
    }

    public int getCurrentPageBegin() {
        return paginator.getCurrentPageBegin();
    }

    public int getCurrentPageEnd() {
        return paginator.getCurrentPageEnd();
    }

    public int getPageBegin(int page) {
        return paginator.getPageBegin(page);
    }

    public int getPageEnd(int page) {
        return paginator.getPageEnd(page);
    }

    public boolean isSyncLoad() {
        return syncLoad;
    }

    public void setSyncLoad(boolean async) {
        syncLoad = async;
    }

    public void setBoundaryCheck(boolean need) {
        mNeedBoundaryCheck = need;
    }

    private void saveCurrentPage() {
        lastPage = paginator.getCurrentPage();
    }

    private void beforeSetupData(ContentItemView itemView, GObject data) {
        notifyBeforeSetupData(itemView, data);
    }

    private void beforePageChange() {
        if (lastPage != paginator.getCurrentPage()) {
            notifyBeforePageChanging(paginator.getCurrentPage(), lastPage);
        }
    }

    private void beforeFirstPage() {
        notifyBeforeFirstPage();
    }

    private void afterLastPage() {
        notifyAfterLastPage(paginator.pages());
    }

    private void afterPageChange() {
        if (lastPage != paginator.getCurrentPage()) {
            this.requestFocus();
            notifyAfterPageChanged(paginator.getCurrentPage(), lastPage);
        }
    }

    private void initViews(final Context context) {
        LayoutInflater.from(context).inflate(R.layout.content_view_layout, this, true);
        gridLayout = (OnyxBaseGridLayout) findViewById(R.id.grid_layout_content);
        pageIndicator = (RelativeLayout) findViewById(R.id.contentView_page_indicator_area);
        functionLayout = (LinearLayout) findViewById(R.id.contentView_function_panel);
        button_delete = (Button) findViewById(R.id.button_delete);
        button_copy = (Button) findViewById(R.id.button_copy);
        button_cancel = (Button) findViewById(R.id.button_cancel);
        button_cut = (Button) findViewById(R.id.button_cut);
        button_paste = (Button) findViewById(R.id.button_paste);
        setUpFunctionLayout();
        infoTextView = (TextView) findViewById(R.id.textView_item_total_count);
        pageProgress = (Button) findViewById(R.id.button_progress);
        inflater = LayoutInflater.from(context);
        gridLayout.setCallBack(new OnyxBaseGridLayout.CustomGridLayoutCallBack() {
            @Override
            public void onSizeChange(int height, int width) {
                super.onSizeChange(height, width);
                fillGridLayout(height, width);
                updateCurrentPage();
            }
        });
//        pageProgress.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final DialogSetValue dlgSetValue = new DialogSetValue(context, dialogSetValueLayoutID, getVisualCurrentPage(),
//                        1, getTotalPageCount(), true, false, R.string.go_to_page, R.string.page, new DialogSetValue.DialogCallback() {
//                    @Override
//                    public void valueChange(int newValue) {
//                        gotoPage(newValue - 1);
//                    }
//
//                    @Override
//                    public void done(boolean isValueChange, int newValue) {
//                        if (isValueChange) {
//                            gotoPage(newValue);
//                        }
//                    }
//                });
//                if (dialogSetValueHeight == -1 && dialogSetValueWidth == -1 &&
//                        dialogSetValuePositionX == -1 && dialogSetValuePositionY == -1) {
//                    dlgSetValue.show();
//                } else {
//                    dlgSetValue.show(dialogSetValueHeight, dialogSetValueWidth,
//                            dialogSetValuePositionX, dialogSetValuePositionY);
//                }
//            }
//        });
    }

    public boolean gotoPage(int page) {
        return gotoPage(page, false, false);
    }

    public boolean gotoPageByValue(Object valueObject, final String tag) {
        if (adapter == null || valueObject == null || tag == null) {
            return false;
        }
        int position = adapter.getIndexByValue(valueObject, tag);
        int page = paginator.pageByIndex(position);
        return gotoPage(page);
    }


    /**
     * jump to specific page with force focus on 1st item in contentView Page.
     *
     * @param page
     * @param selectFirst
     * @return if succeed or not to go to page.
     */
    public boolean gotoPage(int page, boolean selectFirst, boolean forceFocusInTouchMode) {
        saveCurrentPage();
        if (!paginator.gotoPage(page)) {
            return false;
        }
        beforePageChange();
        if (isSyncLoad()) {
            arrangePage(paginator.getCurrentPage(), selectFirst, forceFocusInTouchMode);
        }
        return true;
    }

    public boolean nextPage() {
        return nextPage(false, false);
    }

    public boolean nextPage(boolean selectFirst, boolean forceFocusInTouchMode) {
        saveCurrentPage();
        if (!paginator.nextPage()) {
            afterLastPage();
            return false;
        }
        beforePageChange();
        if (isSyncLoad()) {
            arrangePage(paginator.getCurrentPage(), selectFirst, forceFocusInTouchMode);
        }
        return true;
    }

    public boolean prevPage() {
        return prevPage(false, false);
    }

    public boolean prevPage(boolean selectFirst, boolean forceFocusInTouchMode) {
        saveCurrentPage();
        if (!paginator.prevPage()) {
            beforeFirstPage();
            return false;
        }
        beforePageChange();
        if (isSyncLoad()) {
            arrangePage(paginator.getCurrentPage(), selectFirst, forceFocusInTouchMode);
        }
        return true;
    }

    /**
     * use this method to setUp ContentView as grid layout.
     *
     * @param gridRowCount
     * @param gridColumnCount
     * @param forceRefillContentGrid if forceRefillContentGrid ->true ,contentView will throw all child views at once.and refill
     *                               all new views.
     */

    public void setupGridLayout(int gridRowCount, int gridColumnCount, boolean forceRefillContentGrid) {
        dummyObject = getDummyObject();
        if (gridLayout.getRowCount() != gridRowCount || gridLayout.getColumnCount() != gridColumnCount
                || forceRefillContentGrid) {
            gridLayout.removeAllViews();
        }
        gridLayout.setColumnCount(gridColumnCount);
        gridLayout.setRowCount(gridRowCount);
        fillGridLayout();
    }

    /**
     * use this method to setUp ContentView as grid layout.
     *
     * @param gridRowCount
     * @param gridColumnCount if no need to forceRefillContent,just call this method.
     */

    public void setupGridLayout(int gridRowCount, int gridColumnCount) {
        setupGridLayout(gridRowCount, gridColumnCount, false);
    }

    /**
     * use this method to setUp ContentView with adapter,rows and cols .
     *
     * @param gridRowCount
     * @param gridColumnCount
     * @param adapter
     */
    public void setupContent(int gridRowCount, int gridColumnCount, GAdapter adapter, int page) {
        setupGridLayout(gridRowCount, gridColumnCount);
        setAdapter(adapter, page);
    }

    public void setupContent(int gridRowCount, int gridColumnCount, GAdapter data, Object valueObject, final String tag) {
        setupGridLayout(gridRowCount, gridColumnCount);
        setAdapter(data, valueObject, tag);
    }

    /**
     * use this method to setUp ContentView with adapter,rows and cols ,
     * if forceRefillContentGrid ->true,will force contentView throw all child views and refill new
     * views.
     *
     * @param gridRowCount
     * @param gridColumnCount
     * @param adapter
     * @param forceRefillContentGrid
     * @param page                   page you want to load after setup all data.
     */
    public void setupContent(int gridRowCount, int gridColumnCount, GAdapter adapter, int page, boolean forceRefillContentGrid) {
        setupGridLayout(gridRowCount, gridColumnCount, forceRefillContentGrid);
        setAdapter(adapter, page);
    }

    public void setupContent(int gridRowCount, int gridColumnCount, GAdapter data, Object valueObject, final String tag
            , boolean forceRefillContentGrid) {
        setupGridLayout(gridRowCount, gridColumnCount, forceRefillContentGrid);
        setAdapter(data, valueObject, tag);
    }

    /**
     * use this method to setAdapter directly
     *
     * @param dataAdapter data adapter
     * @param valueObject the object which contain specific property
     * @param tag         the specific property tag
     */
    public void setAdapter(GAdapter dataAdapter, Object valueObject, final String tag) {
        paginator.resize(gridLayout.getRowCount(), gridLayout.getColumnCount(), dataAdapter.size());
        int position = dataAdapter.getIndexByValue(valueObject, tag);
        int page = paginator.pageByIndex(position);
        setAdapter(dataAdapter, page);
    }

    /**
     * use this method to setAdapter directly
     *
     * @param dataAdapter data adapter
     * @param page        page you want to load after setup all data.
     */
    public void setAdapter(GAdapter dataAdapter, int page) {
        lastPage = -1;
        adapter = dataAdapter;
        if (adapter == null) {
            return;
        }

        paginator.resize(gridLayout.getRowCount(), gridLayout.getColumnCount(), adapter.size());
        updatePageIndicator();
        updateInfo();
        if (page < 0) {
            page = 0;
        }
        paginator.setCurrentPage(page);
        if (callback != null) {
            callback.onDataSetChange();
        }
        if (paginator.pages() > 0 && page < paginator.pages()) {
            arrangePage(page, true, false);
        } else {
            clearPage();
        }
        requestFocus();
    }

    public boolean updateContentItemView(final GObject object) {
        for (int i = 0; i < gridLayout.getChildCount(); ++i) {
            ContentItemView itemView = (ContentItemView) gridLayout.getChildAt(i);
            if (GAdapterUtil.isEqual(object, itemView.getData(), GAdapterUtil.TAG_UNIQUE_ID)) {
                itemView.setData(object);
                return true;
            }
        }
        return false;
    }

    public void updateInfo() {
        if (!isCustomTotalInfo) {
            infoTextView.setText(infoTittle + paginator.getSize() + "");
        }
    }

    /**
     * set CustomInfoMessage
     *
     * @param customContent    message string resource
     * @param withPrefixTittle choose to show with prefix tittle
     */
    public void setCustomInfo(String customContent, boolean withPrefixTittle) {
        String string;
        if (withPrefixTittle) {
            string = infoTittle + customContent;
        } else {
            string = customContent;
        }
        infoTextView.setText(string);
    }

    /**
     * set CustomInfoMessage
     *
     * @param customContentResID message string resource ID.
     * @param withPrefixTittle   choose to show with prefix tittle
     */
    public void setCustomInfo(int customContentResID, boolean withPrefixTittle) {
        String string;
        if (withPrefixTittle) {
            string = infoTittle + getContext().getString(customContentResID);
        } else {
            string = getContext().getString(customContentResID);
        }
        infoTextView.setText(string);
    }

    private void updatePageIndicator() {
        if ((isAlwaysShowPageIndicator || paginator.pages() > 1) && (isShowPageInfoArea)) {
            showPageIndicator();
        } else {
            hidePageIndicator();
        }
    }

    private void showPageIndicator() {
        pageIndicator.setVisibility(VISIBLE);
        if (isShowInfo) {
            infoTextView.setVisibility(VISIBLE);
        } else {
            infoTextView.setVisibility(GONE);
        }
    }

    private void hidePageIndicator() {
        pageIndicator.setVisibility(GONE);
    }

    /**
     * Called when data changed.
     */
    public void updateCurrentPage() {
        updateCurrentPage(false, false);
    }

    public void updateCurrentPage(boolean forceSelectFirst, boolean forceFocusInTouchMode) {
        arrangePage(paginator.getCurrentPage(), forceSelectFirst, forceFocusInTouchMode);
    }

    public void setSubLayoutParameter(int resId, final HashMap<String, Integer> mapping) {
        setSubLayoutParameter(resId, mapping, null);
    }

    public void setSubLayoutParameter(int resId, final HashMap<String, Integer> mapping, final ArrayList<Integer> styleList) {
        itemViewResourceId = resId;
        dataToViewMapping = mapping;
        styleLayoutList = styleList;
    }

    private void fillGridLayout(int height, int width) {
        int rows = getGridRowCount();
        int cols = getGridColumnCount();
        gridLayout.removeAllViews();
        gridLayout.setAlignmentMode(GridLayout.ALIGN_MARGINS);
        for (int i = gridLayout.getChildCount(); i < rows * cols; ++i) {
            int row = i / cols;
            int col = i % cols;
            GObject item = dummyObject;
            ContentItemView itemView = ContentItemView.create(inflater, item, itemViewResourceId, dataToViewMapping, styleLayoutList);
            if (hyphenCallback != null) {
                itemView.setHyphenCallback(hyphenCallback);
            }
            GridLayout.Spec rowSpec = GridLayout.spec(row);
            GridLayout.Spec columnSpec = GridLayout.spec(col);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
            params.width = width / gridLayout.getColumnCount();
            params.height = height / gridLayout.getRowCount();
            params.setGravity(Gravity.FILL);
            gridLayout.addView(itemView, i, params);
        }
    }

    private void fillGridLayout() {
        int rows = getGridRowCount();
        int cols = getGridColumnCount();
        gridLayout.setAlignmentMode(GridLayout.ALIGN_MARGINS);
        for (int i = gridLayout.getChildCount(); i < rows * cols; ++i) {
            int row = i / cols;
            int col = i % cols;
            GObject item = dummyObject;
            ContentItemView itemView = ContentItemView.create(inflater, item, itemViewResourceId, dataToViewMapping, styleLayoutList);
            if (hyphenCallback != null) {
                itemView.setHyphenCallback(hyphenCallback);
            }
            GridLayout.Spec rowSpec = GridLayout.spec(row);
            GridLayout.Spec columnSpec = GridLayout.spec(col);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
            params.width = gridLayout.getMeasuredWidth() / gridLayout.getColumnCount();
            params.height = gridLayout.getMeasuredHeight() / gridLayout.getRowCount();
            params.setGravity(Gravity.FILL);
            gridLayout.addView(itemView, i, params);
        }
    }

    private void setupItemView(ContentItemView itemView, GObject data, boolean showDivider) {
        beforeSetupData(itemView, data);
        if (data.isDummyObject()) {
            itemView.setFocusable(false);
            itemView.setOnTouchListener(null);
            itemView.setOnClickListener(null);
            if (isBlankAreaAnswerLongClick) {
                itemView.setOnLongClickListener(mItemOnLongClickListener);
            } else {
                itemView.setOnLongClickListener(null);
            }
            itemView.setOnFocusChangeListener(null);
            itemView.setBackgroundResource(0);
        } else {
            itemView.setFocusable(true);
//            itemView.requestFocusFromTouch();
//            itemView.setOnTouchListener(mItemOnTouchListener);
            itemView.setOnClickListener(mItemOnClickListener);
            itemView.setOnLongClickListener(mItemOnLongClickListener);
            itemView.setOnFocusChangeListener(mItemOnFocusChangeListener);
            itemView.setBackgroundResource(ContentItemView.getDefaultBackgroundResource());
            data.putBoolean(GAdapterUtil.TAG_DIVIDER_VIEW, showDivider);
        }
        itemView.setData(data);
    }

    private void arrangePage(int page, boolean selectFirst, boolean forceFocusInTouchMode) {
        if (paginator.getSize() <= 0 || adapter.size() <= 0) {
            return;
        }
        int begin = paginator.getPageBegin(page);
        int end = paginator.getPageEnd(page);
        int cursor;
        int offset;
        ContentItemView itemView;
        for (cursor = begin; cursor <= end; ++cursor) {
            GObject item = adapter.get(cursor);
            offset = paginator.offsetInCurrentPage(cursor);
            itemView = ((ContentItemView) gridLayout.getChildAt(offset));
            setupItemView(itemView, item, cursor != end);
        }
        offset = paginator.offsetInCurrentPage(end) + 1;
        for (; offset < gridLayout.getColumnCount() * gridLayout.getRowCount(); ++offset) {
            itemView = ((ContentItemView) gridLayout.getChildAt(offset));
            setupItemView(itemView, dummyObject, false);
        }
        pageProgress.setText((page + 1) + "/" + paginator.pages());
        if (selectFirst) {
            int position = paginator.getPageBegin(page);
            offset = paginator.offsetInCurrentPage(position);
            isForceFocusInTouchMode = forceFocusInTouchMode;
            setSelection(offset, forceFocusInTouchMode);
        }
        afterPageChange();
    }

    public void clearPage() {
        ContentItemView itemView;
        for (int i = 0; i < gridLayout.getColumnCount() * gridLayout.getRowCount(); ++i) {
            itemView = ((ContentItemView) gridLayout.getChildAt(i));
            setupItemView(itemView, dummyObject, false);
        }
        pageProgress.setText("1/1");
        infoTextView.setText("0");
        afterPageChange();
    }


    private int detectDirection(MotionEvent currentEvent) {
        switch (touchDirection) {
            case TouchDirection.HORIZONTAL:
                return PageTurningDetector.detectHorizontalTuring(getContext(), (int) (currentEvent.getX() - lastX));
            case TouchDirection.VERTICAL:
                return PageTurningDetector.detectVerticalTuring(getContext(), (int) (currentEvent.getY() - lastY));
            case TouchDirection.BOTH:
                return PageTurningDetector.detectBothAxisTuring(getContext(), (int) (currentEvent.getX() - lastX), (int) (currentEvent.getY() - lastY));
            default:
                return PageTurningDetector.detectHorizontalTuring(getContext(), (int) (currentEvent.getX() - lastX));
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
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                switch (detectDirection(ev)) {
                    case PageTurningDirection.PREV:
                        prevPage(true, isForceFocusInTouchMode);
                        break;
                    case PageTurningDirection.NEXT:
                        nextPage(true, isForceFocusInTouchMode);
                        break;
                }
                break;
            default:
                break;
        }

        return true;
    }

    private OnFocusChangeListener mItemOnFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // if focus changed between child views in table layout,
            // it will first lost by old item, then gained by new item
            if (hasFocus) {
                selectedView = v;
                notifyItemSelected((ContentItemView) v);
            } else {
                selectedView = null;
            }
        }
    };


    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_UP:
                prevPage();
                return true;
            case KeyEvent.KEYCODE_PAGE_DOWN:
                nextPage();
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * when ignore page turning request, caller, usually, activity should
     * call nextPage or prevPage.
     *
     * @param event
     * @return
     */
    private boolean dispatchActionUp(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_PAGE_UP:
            case KeyEvent.KEYCODE_PAGE_DOWN:
                return true;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    private boolean dispatchActionDown(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (moveSelectionByKey(event)) {
                    return true;
                }
                break;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    static public boolean activityDispatchKeyEvent(List<ContentView> contentViewArrayList, KeyEvent event, boolean forceContentViewChangePage) {
        if (ContentViewUtil.isActionTainted(event)) {
            return true;
        }
        ContentView targetContentView = null;
        for (ContentView temp : contentViewArrayList) {
            if (temp == null) {
                continue;
            }
            if (temp.hasFocus()) {
                targetContentView = temp;
                break;
            }
        }
        if ((event.getAction() == KeyEvent.ACTION_UP)) {
            if (forceContentViewChangePage && contentViewArrayList.size() > 0 && targetContentView == null) {
                targetContentView = contentViewArrayList.get(0);
            }
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_PAGE_UP:
                    if (targetContentView != null) {
                        targetContentView.prevPage();
                    }
                    return true;
                case KeyEvent.KEYCODE_PAGE_DOWN:
                    if (targetContentView != null) {
                        targetContentView.nextPage();
                    }
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (ContentViewUtil.isActionTainted(event)) {
            return true;
        }

        if (event.getAction() == KeyEvent.ACTION_UP) {
            return dispatchActionUp(event);
        }
        return dispatchActionDown(event);
    }

    private int boundaryCheck(int currentPage, int newPosition) {
        if (newPosition < 0 || newPosition >= paginator.getSize()) {
            return 0;
        }
        int newPage = paginator.pageByIndex(newPosition);
        if (currentPage != newPage && newPage >= 0) {
            return 1;
        }
        if (currentPage == newPage) {
            return 2;
        }
        return -1;
    }

    private boolean isBoundaryItem(int index, int direction) {
        switch (direction) {
            case FOCUS_LEFT:
                return ((index % getGridColumnCount()) == 0);
            case FOCUS_RIGHT:
                return (index >= paginator.getSize() - 1) || (index % getGridColumnCount()) == getGridColumnCount() - 1;
            default:
                return false;
        }
    }

    private int preBoundaryCheck(int index, int direction) {
        if ((isBoundaryItem(index, direction)) && this.focusSearch(direction) != null && mNeedBoundaryCheck) {
            return -1;
        }
        return 0;
    }

    private boolean moveSelectionByKey(KeyEvent keyEvent) {
        if (getChildCount() <= 0) {
            return false;
        }

        if (selectedView == null) {
            if (isFocusOutSideContentGrid()) {
                return false;
            }
            setSelection(0);
            return true;
        }

        int index = locateViewInPage(selectedView) + paginator.getCurrentPageBegin();
        if (index < 0) {
            return false;
        }

        notifyBeforeDPadMove(keyEvent);
        boolean ret = false;
        int currentPage = paginator.getCurrentPage();
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_LEFT: {
                if (preBoundaryCheck(index, FOCUS_LEFT) == -1) {
                    ret = false;
                    break;
                }
                int value = paginator.prevColumn(index);
                switch (boundaryCheck(currentPage, value)) {
                    case 0:
                        ret = false;
                        break;
                    case 1:
                        gotoPage(paginator.pageByIndex(value), true, false);
                        ret = true;
                        break;
                    case 2:
                        setSelection(paginator.offsetInCurrentPage(value));
                        ret = true;
                        break;
                }
                break;
            }
            case KeyEvent.KEYCODE_DPAD_RIGHT: {
                if (preBoundaryCheck(index, FOCUS_RIGHT) == -1) {
                    ret = false;
                    break;
                }
                int value = paginator.nextColumn(index);
                switch (boundaryCheck(currentPage, value)) {
                    case 0:
                        ret = false;
                        break;
                    case 1:
                        gotoPage(paginator.pageByIndex(value), true, false);
                        ret = true;
                        break;
                    case 2:
                        setSelection(paginator.offsetInCurrentPage(value));
                        ret = true;
                        break;
                }
                break;
            }
            case KeyEvent.KEYCODE_DPAD_UP: {
                int value = paginator.prevRow(index);
                switch (boundaryCheck(currentPage, value)) {
                    case 0:
                    case 1:
                        ret = false;
                        break;
                    case 2:
                        setSelection(paginator.offsetInCurrentPage(value));
                        ret = true;
                        break;
                }
                break;
            }
            case KeyEvent.KEYCODE_DPAD_DOWN: {
                int value = paginator.nextRow(index);
                switch (boundaryCheck(currentPage, value)) {
                    case 0:
                    case 1:
                        ret = false;
                        break;
                    case 2:
                        setSelection(paginator.offsetInCurrentPage(value));
                        ret = true;
                        break;
                }
                break;
            }
            default:
                break;
        }

        return ret;
    }

    public void setSelection(int index) {
        setSelection(index, false);
    }

    private void setSelection(int index, boolean forceFocusInTouchMode) {
        if (index < 0 || index >= getGridRowCount() * getGridColumnCount()) {
            return;
        }
        selectedView = gridLayout.getChildAt(index);
        if (isInTouchMode() && forceFocusInTouchMode) {
            selectedView.setFocusableInTouchMode(true);
        }
        if (selectedView.requestFocus()) {
            if (forceFocusInTouchMode && selectedView.isFocusableInTouchMode()) {
                selectedView.setFocusableInTouchMode(false);
            }
        }
    }

    private int locateViewInPage(View view) {
        for (int row = 0; row < getGridRowCount(); row++) {
            for (int col = 0; col < getGridColumnCount(); col++) {
                int index = row * getGridColumnCount() + col;
                if (view == gridLayout.getChildAt(index)) {
                    return index;
                }
            }
        }
        return -1;
    }

    private OnTouchListener mItemOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getActionMasked() != MotionEvent.ACTION_UP) {
//                v.requestFocus();
            }
            return false;
        }
    };

    private OnClickListener mItemOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isForceFocusInTouchMode) {
                if (gridLayout.getFocusedChild() != null) {
                    gridLayout.getFocusedChild().clearFocus();
                }
            }
            ContentView.this.notifyItemClick((ContentItemView) v);
        }
    };

    private OnLongClickListener mItemOnLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (isForceFocusInTouchMode) {
                if (gridLayout.getFocusedChild() != null) {
                    gridLayout.getFocusedChild().clearFocus();
                }
            }
            return ContentView.this.notifyItemLongClick((ContentItemView) v);
        }
    };

    public void setCallback(ContentViewCallback callback) {
        this.callback = callback;
    }

    /**
     * setUp the hyphenCallBack before setUp fillGridLayout,otherwise it this would be useless.
     *
     * @param callback
     */
    public void setHyphenTittleViewCallback(ContentItemView.HyphenCallback callback) {
        hyphenCallback = callback;
    }

    private void notifyBeforeSetupData(ContentItemView itemView, GObject object) {
        if (callback != null) {
            callback.beforeSetupData(itemView, object);
        }
    }

    private void notifyBeforePageChanging(int newPage, int oldPage) {
        if (callback != null) {
            callback.beforePageChanging(this, newPage, oldPage);
        }
    }

    private void notifyBeforeFirstPage() {
        if (callback != null) {
            callback.beforeFirstPage(this);
        }
    }

    private void notifyAfterLastPage(int pages) {
        if (callback != null) {
            callback.afterLastPage(this, pages);
        }
    }

    private void notifyAfterPageChanged(int newPage, int oldPage) {
        if (callback != null) {
            callback.afterPageChanged(this, newPage, oldPage);
        }
    }

    private void notifyItemSelected(ContentItemView view) {
        if (callback != null) {
            callback.onItemSelected(view);
        }
    }

    private void notifyItemClick(ContentItemView view) {
        if (callback != null) {
            callback.onItemClick(view);
        }
    }

    private boolean notifyItemLongClick(ContentItemView view) {
        return callback != null && callback.onItemLongClick(view);
    }

    private void notifyBeforeDPadMove(KeyEvent event) {
        if (callback != null) {
            callback.beforeDPadMoving(this, event);
        }
    }

    private void notifyAfterDPadMove(KeyEvent event) {
        if (callback != null) {
            callback.afterDPadMoved(this, event);
        }
    }

    private static GObject getDummyObject() {
        if (dummyObject == null) {
            dummyObject = new GObject().setDummyObject();
        }
        return dummyObject;
    }

    public void unCheckOtherViews(int excludeIndex) {
        unCheckOtherViews(excludeIndex, false);
    }

    public void unCheckOtherViews(int excludeIndex, boolean hideOtherIcon) {
        for (int i = 0; i < adapter.getList().size(); i++) {
            if (i == excludeIndex) {
                continue;
            }
            GObject temp = adapter.getList().get(i);
            if (hideOtherIcon) {
                temp.removeObject(GAdapterUtil.TAG_SELECTABLE);
            } else {
                temp.putBoolean(GAdapterUtil.TAG_SELECTABLE, false);
            }
            adapter.setObject(i, temp);
        }
    }

    public void unCheckAllViews() {
        for (int i = 1; i < adapter.getList().size(); i++) {
            GObject temp = adapter.getList().get(i);
            temp.putBoolean(GAdapterUtil.TAG_SELECTABLE, false);
            adapter.setObject(i, temp);
        }
    }

    private boolean isFocusOutSideContentGrid() {
        for (int row = 0; row < getGridRowCount(); row++) {
            for (int col = 0; col < getGridColumnCount(); col++) {
                int index = row * getGridColumnCount() + col;
                if (gridLayout.getChildAt(index).isFocused()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showFunctionLayout(int selectionMode) {
        functionLayout.setVisibility(VISIBLE);
        switch (selectionMode) {
            case SelectionMode.PASTE_MODE:
                button_copy.setVisibility(GONE);
                button_cut.setVisibility(GONE);
                button_delete.setVisibility(GONE);
                button_cancel.setVisibility(VISIBLE);
                button_paste.setVisibility(VISIBLE);
                break;
            case SelectionMode.MULTISELECT_MODE:
                button_copy.setVisibility(VISIBLE);
                button_cut.setVisibility(VISIBLE);
                button_delete.setVisibility(VISIBLE);
                button_cancel.setVisibility(VISIBLE);
                button_paste.setVisibility(GONE);
                break;
        }
    }

    public void hideFunctionLayout() {
        functionLayout.setVisibility(GONE);
    }

    public void setUpFunctionLayout() {
        button_copy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onFunctionCopy();
            }
        });
        button_cut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onFunctionCut();
            }
        });
        button_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onFunctionDelete();
            }
        });
        button_paste.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onFunctionPaste();
            }
        });
        button_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onFunctionCancel();
            }
        });
    }

    /**
     * contentView itself contain a page info layout,but maybe some situation we need a different style,
     * we could write a different style in layout xml.
     *
     * @param LayoutID the layout you want to use.
     * @param viewId   the top level view id,for contentView to arrange view sequence.
     */
    public void useCustomPageInfoLayout(int LayoutID, int viewId) {
        isShowPageInfoArea = false;
        hidePageIndicator();
        View customPageInfo = inflater.inflate(LayoutID, null);
        this.addView(customPageInfo);
        LayoutParams customPageInfoParams = (LayoutParams) customPageInfo.getLayoutParams();
        LayoutParams functionLayoutParams = (LayoutParams) functionLayout.getLayoutParams();
        customPageInfoParams.addRule(ALIGN_PARENT_BOTTOM);
        functionLayoutParams.addRule(ABOVE, viewId);
        customPageInfo.setLayoutParams(customPageInfoParams);
        functionLayout.setLayoutParams(functionLayoutParams);
    }
}
