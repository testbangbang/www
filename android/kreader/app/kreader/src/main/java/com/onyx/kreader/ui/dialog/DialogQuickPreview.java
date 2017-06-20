package com.onyx.kreader.ui.dialog;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.utils.TocUtils;
import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.kreader.ui.actions.GetPageNumberFromPositionAction;
import com.onyx.kreader.ui.actions.GetPositionFromPageNumberAction;
import com.onyx.kreader.ui.actions.GetTableOfContentAction;
import com.onyx.kreader.ui.actions.GotoPageAction;
import com.onyx.kreader.ui.actions.GotoPositionAction;
import com.onyx.kreader.ui.actions.ShowReaderMenuAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.ui.view.PreviewViewHolder;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by joy on 7/15/16.
 */
public class DialogQuickPreview extends OnyxBaseDialog {

    private static final String TAG = "DialogQuickPreview";
    private static final int LONG_CLICK_TIME_INTERVAL = 2000;

    public static abstract class Callback {
        public abstract void abort();

        public abstract void requestPreview(final List<String> positions, final List<Integer> pages);

        public abstract void recycleBitmap();
    }

    static class GridType {
        final static int One = 1;
        final static int Four = 4;
        final static int Nine = 9;
    }

    private static class Grid {
        private int grid = GridType.Four;

        public void setGridType(int grid) {
            this.grid = grid;
        }

        public int getGridType() {
            return grid;
        }

        public int getRows() {
            switch (grid) {
                case GridType.One:
                    return 1;
                case GridType.Four:
                    return 2;
                case GridType.Nine:
                    return 3;
                default:
                    return 1;
            }
        }

        public int getColumns() {
            switch (grid) {
                case GridType.One:
                    return 1;
                case GridType.Four:
                    return 2;
                case GridType.Nine:
                    return 3;
                default:
                    return 1;
            }
        }

        public int getGridSize() {
            return getRows() * getColumns();
        }
    }

    private static class TouchHandler extends Handler {

        private PageRecyclerView pageRecyclerView;

        public TouchHandler(PageRecyclerView pageRecyclerView) {
            this.pageRecyclerView = pageRecyclerView;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int viewId = msg.what;
            switch (viewId){
                case R.id.image_view_next_page:{
                    int currentPage = pageRecyclerView.getPaginator().getCurrentPage();
                    int pages = pageRecyclerView.getPaginator().pages();
                    pageRecyclerView.gotoPage(Math.min(currentPage + 10, pages));
                }
                    break;
                case R.id.image_view_prev_page:{
                    int currentPage = pageRecyclerView.getPaginator().getCurrentPage();
                    pageRecyclerView.gotoPage(Math.max(currentPage - 10, 0));
                }
                    break;
            }
        }
    }

    private class PagePreviewAdapter extends PageRecyclerView.PageAdapter<PreviewViewHolder> {

        private Grid grid;

        public void requestMissingBitmaps() {
            final int pageBegin = getPaginator().getCurrentPageBegin();
            int pageEnd = getPaginator().getCurrentPageEnd();

            if (pageBegin < 0 || pageBegin > pageEnd) {
                return;
            }

            final List<Integer> toRequest = new ArrayList<>();
            for (int i = pageBegin; i <= pageEnd; i++) {
                toRequest.add(i);
            }

            final GetPositionFromPageNumberAction action = new GetPositionFromPageNumberAction(toRequest, true);
            action.execute(readerDataHolder, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    List<String> positions = action.getDocumentPositions();
                    if (positions == null || positions.size() < 1) {
                        return;
                    }
                    if (isJumpChapter()) {
                        //Due to the flow document can only rely on position location, so the first always jump section position
                        positions.set(0, PagePositionUtils.fromPosition(chapterPosition));
                    }
                    callback.requestPreview(positions, toRequest);
                    setJumpChapter(false);
                }
            });
        }

        public void setGridType(Grid grid) {
            this.grid = grid;
            gridRecyclerView.resize(grid.getRows(), grid.getColumns(), readerDataHolder.getPageCount());
        }

        public void setBitmap(PageInfo pageInfo, Bitmap bitmap) {
            previewMap.put(pageInfo, bitmap);
            notifyItemChanged(pageInfo.getPageNumber());
        }

        @Override
        public int getRowCount() {
            return grid.getRows();
        }

        @Override
        public int getColumnCount() {
            return grid.getColumns();
        }

        @Override
        public int getDataCount() {
            return readerDataHolder.getPageCount();
        }

        @Override
        public PreviewViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
            final PreviewViewHolder previewViewHolder = new PreviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.preview_list_item_view, parent, false));
            previewViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GotoPositionAction(previewViewHolder.getPagePosition(), true).execute(readerDataHolder, new BaseCallback() {
                        @Override
                        public void done(BaseRequest request, Throwable e) {
                            DialogQuickPreview.this.dismiss();
                        }
                    });
                }
            });
            return previewViewHolder;
        }

        @Override
        public void onPageBindViewHolder(PreviewViewHolder holder, final int position) {
            holder.getContainer().setActivated(readerDataHolder.getCurrentPage() == position);
            holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        if (gridRecyclerView.getPaginator().isInNextPage(position)) {
                            oneImageGrid.requestFocus();
                        }
                    }
                }
            });
            Map.Entry<PageInfo, Bitmap> entry = getPageInfoEntry(previewMap, position);
            if (entry == null) {
                return;
            }

            holder.bindPreview(entry.getValue(), entry.getKey().getPageNumber(), entry.getKey().getPosition());
        }

        @Override
        public void onViewDetachedFromWindow(PreviewViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            holder.getImageView().setImageDrawable(new ColorDrawable(Color.WHITE));
        }
    }

    private Map.Entry<PageInfo, Bitmap> getPageInfoEntry(final Map<PageInfo, Bitmap> previewMap, final int pageNumber) {
        for (Map.Entry<PageInfo, Bitmap> entry : previewMap.entrySet()) {
            if (entry.getKey().getPageNumber() == pageNumber) {
                return entry;
            }
        }
        return null;
    }

    private PageRecyclerView gridRecyclerView;
    private TextView textViewProgress;
    private SeekBar seekBarProgress;
    private ImageView oneImageGrid;
    private ImageView fourImageGrid;
    private ImageView nineImageGrid;
    private ImageButton chapterBack;
    private ImageButton chapterForward;
    private ImageButton btnNext;
    private ImageButton btnPrev;

    private Grid grid = new Grid();
    private Map<PageInfo, Bitmap> previewMap = new HashMap<>();
    private PagePreviewAdapter adapter = new PagePreviewAdapter();

    private ReaderDataHolder readerDataHolder;
    private int currentPage;
    private String currentPagePosition;
    private Callback callback;
    private TouchHandler touchHandler;
    private long touchDownTime;
    private Timer timer;
    private int chapterPosition;
    private boolean jumpChapter = false;

    public DialogQuickPreview(@NonNull final ReaderDataHolder readerDataHolder, Callback callback) {
        super(readerDataHolder.getContext(), R.style.android_dialog_no_title);
        setContentView(R.layout.dialog_quick_preview);

        this.readerDataHolder = readerDataHolder;
        this.callback = callback;
        currentPage = readerDataHolder.getCurrentPage();
        currentPagePosition = readerDataHolder.getCurrentPagePosition();

        fitDialogToWindow();
        initGridType();
        setupLayout();
        setupContent();
    }

    private void fitDialogToWindow() {
        Window mWindow = getWindow();
        WindowManager.LayoutParams mParams = mWindow.getAttributes();
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.gravity = Gravity.BOTTOM;
        mWindow.setAttributes(mParams);
        //force use all space in the screen.
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    private void setupLayout() {
        gridRecyclerView = (PageRecyclerView) findViewById(R.id.grid_view_preview);
        gridRecyclerView.setLayoutManager(new DisableScrollGridManager(getContext(), grid.getColumns()));
        adapter.setGridType(grid);
        gridRecyclerView.setAdapter(adapter);
        gridRecyclerView.setDefaultPageKeyBinding();

        textViewProgress = (TextView) findViewById(R.id.text_view_progress);
        seekBarProgress = (SeekBar) findViewById(R.id.seek_bar_page);
        oneImageGrid = (ImageView) findViewById(R.id.image_view_one_grids);
        fourImageGrid = (ImageView) findViewById(R.id.image_view_four_grids);
        nineImageGrid = (ImageView) findViewById(R.id.image_view_nine_grids);
        chapterBack = (ImageButton) findViewById(R.id.chapter_back);
        chapterForward = (ImageButton) findViewById(R.id.chapter_forward);
        btnNext = (ImageButton) findViewById(R.id.image_view_next_page);
        btnPrev = (ImageButton) findViewById(R.id.image_view_prev_page);
        textViewProgress.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        textViewProgress.getPaint().setAntiAlias(true);
        touchHandler = new TouchHandler(gridRecyclerView);

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    touchDownTime = System.currentTimeMillis();
                    handleTouchDownEvent(v.getId());
                    v.setBackgroundResource(R.drawable.pressed_btn_bg);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    v.setBackgroundResource(R.drawable.imagebtn_bg);
                    long current = System.currentTimeMillis();
                    boolean onClick = current - touchDownTime < LONG_CLICK_TIME_INTERVAL;
                    handleTouchUpEvent(v.getId(), onClick);
                }
                return true;
            }
        };

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.image_view_next_page:
                        nextPage();
                        break;
                    case R.id.image_view_prev_page:
                        prevPage();
                        break;
                }
            }
        };

        btnNext.setOnClickListener(onClickListener);
        btnPrev.setOnClickListener(onClickListener);
        btnPrev.setOnTouchListener(onTouchListener);
        btnNext.setOnTouchListener(onTouchListener);

        findViewById(R.id.image_view_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogQuickPreview.this.cancel();
            }
        });

        fourImageGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGridTypeChange(GridType.Four);
            }
        });

        nineImageGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGridTypeChange(GridType.Nine);
            }
        });

        oneImageGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGridTypeChange(GridType.One);
            }
        });

        oneImageGrid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onGridFocusChange(oneImageGrid, GridType.One, hasFocus);
            }
        });

        fourImageGrid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onGridFocusChange(fourImageGrid, GridType.Four, hasFocus);
            }
        });

        nineImageGrid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onGridFocusChange(nineImageGrid, GridType.Nine, hasFocus);
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (callback != null) {
                    callback.abort();
                }
            }
        });

        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (callback != null) {
                    callback.abort();
                }
                new GotoPositionAction(currentPagePosition, true).execute(readerDataHolder);
            }
        });

        chapterBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareGotoChapterIndex(true);
            }
        });

        chapterForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareGotoChapterIndex(false);
            }
        });

        textViewProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final OnyxCustomDialog dlg = OnyxCustomDialog.getInputDialog(getContext(), getContext().getString(R.string.dialog_quick_view_enter_page_number), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OnyxCustomDialog onyxCustomDialog = (OnyxCustomDialog) dialog;
                        String page = onyxCustomDialog.getInputValue().toString();
                        if (!StringUtils.isNullOrEmpty(page)) {
                            int pageNumber = PagePositionUtils.getPageNumber(page);
                            pageNumber--;
                            if (pageNumber >= 0 && pageNumber < readerDataHolder.getPageCount()) {
                                new GotoPageAction(pageNumber, true).execute(readerDataHolder, new BaseCallback() {
                                    @Override
                                    public void done(BaseRequest request, Throwable e) {
                                        DialogQuickPreview.this.dismiss();
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), getContext().getString(R.string.dialog_quick_view_enter_page_number_out_of_range_error), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), getContext().getString(R.string.dialog_quick_view_enter_page_number_empty_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dlg.getInputEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
                dlg.getInputEditText().setHint("1-" + readerDataHolder.getPageCount());
                readerDataHolder.trackDialog(dlg);
                dlg.show();
            }
        });

        gridRecyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                onPageDataChanged();
            }
        });
    }

    private void handleTouchDownEvent(final int viewId) {
        timer = new Timer(true);
        TimerTask task = new TimerTask(){
            public void run() {
                Message msg = new Message();
                msg.what = viewId;
                touchHandler.sendMessage(msg);
            }
        };
        timer.schedule(task, LONG_CLICK_TIME_INTERVAL, LONG_CLICK_TIME_INTERVAL);
    }

    private void handleTouchUpEvent(final int viewId, final boolean onClick) {
        if (onClick) {
            switch (viewId) {
                case R.id.image_view_next_page:
                    nextPage();
                    break;
                case R.id.image_view_prev_page:
                    prevPage();
                    break;
            }
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void initGridType() {
        int defaultGridType = getContext().getResources().getInteger(R.integer.quick_view_default_grid_type);
        grid.setGridType(SingletonSharedPreference.getQuickViewGridType(getContext(), defaultGridType));
    }

    private void onGridFocusChange(ImageView view, int gridType, boolean isFocus) {

        int resId = R.drawable.ic_dialog_reader_page_one_white_focused;
        switch (gridType) {
            case GridType.One:
                resId = R.drawable.ic_dialog_reader_page_one_white_focused;
                break;
            case GridType.Four:
                resId = R.drawable.ic_dialog_reader_page_four_white_focused;
                break;
            case GridType.Nine:
                resId = R.drawable.ic_dialog_reader_page_nine_white_focused;
                break;
        }
        if (isFocus) {
            view.setImageResource(resId);
        }else {
            onPressedImageView(grid.getGridType());
        }
    }

    private void onGridTypeChange(int gridType) {
        grid.setGridType(gridType);
        SingletonSharedPreference.setQuickViewGridType(getContext(), gridType);
        gridRecyclerView.setLayoutManager(new DisableScrollGridManager(getContext(), grid.getColumns()));
        adapter.setGridType(grid);
        gridRecyclerView.gotoPage(getPaginator().pageByIndex(currentPage));
        onPressedImageView(gridType);
    }

    private int getGridPage(int page) {
        return page / grid.getGridType();
    }

    private GPaginator getPaginator() {
        return gridRecyclerView.getPaginator();
    }

    private void prepareGotoChapterIndex(final boolean back) {
        List<Integer> tocChapterNodeList = ShowReaderMenuAction.getTocChapterNodeList();
        if (tocChapterNodeList == null) {
            new GetTableOfContentAction().execute(readerDataHolder, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    BaseReaderRequest readerRequest = (BaseReaderRequest) request;
                    ReaderDocumentTableOfContent toc = readerRequest.getReaderUserDataInfo().getTableOfContent();
                    
                    boolean hasToc = toc != null && !toc.isEmpty();
                    chapterBack.setEnabled(hasToc);
                    chapterForward.setEnabled(hasToc);
                    if (!hasToc) {
                        Toast.makeText(readerDataHolder.getContext(), readerDataHolder.getContext().getString(R.string.no_chapters), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Integer> readTocChapterNodeList = TocUtils.buildChapterNodeList(toc);
                    ShowReaderMenuAction.setTocChapterNodeList(readTocChapterNodeList);
                    gotoChapterIndex(back, readTocChapterNodeList);
                }
            });
        }else {
            gotoChapterIndex(back, tocChapterNodeList);
        }
    }

    private void gotoChapterIndex(final boolean back, final List<Integer> tocChapterNodeList) {
        if (tocChapterNodeList.size() <= 0) {
            return;
        }

        List<Integer> pages = new ArrayList<>();
        pages.add(Math.max(getPaginator().getCurrentPageBegin() - 1, 0));
        pages.add(Math.min(getPaginator().getCurrentPageEnd() + 1, getPaginator().getSize()));
        final GetPositionFromPageNumberAction action =  new GetPositionFromPageNumberAction(pages, true);
        action.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<String> documentPositions = action.getDocumentPositions();
                if (documentPositions != null && documentPositions.size() >= 2) {
                    int pageStartPosition = PagePositionUtils.getPosition(documentPositions.get(0));
                    int pageEndPosition = PagePositionUtils.getPosition(documentPositions.get(1));
                    if (back && (getPaginator().isFirstPage() || pageStartPosition <= tocChapterNodeList.get(0))) {
                        Toast.makeText(readerDataHolder.getContext(), readerDataHolder.getContext().getString(R.string.first_chapter), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!back && (getPaginator().isLastPage() || pageEndPosition >= tocChapterNodeList.get(tocChapterNodeList.size() - 1 ))) {
                        Toast.makeText(readerDataHolder.getContext(), readerDataHolder.getContext().getString(R.string.last_chapter), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    chapterPosition = getChapterPosition(pageStartPosition, pageEndPosition, back, tocChapterNodeList);
                    jumpToChapter(chapterPosition);
                }
            }
        });
    }

    private void jumpToChapter(int position) {
        final GetPageNumberFromPositionAction pageAction = new GetPageNumberFromPositionAction(PagePositionUtils.fromPosition(position));
        pageAction.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                setJumpChapter(true);
                gridRecyclerView.gotoPage(getGridPage(pageAction.getPageNumber()));
            }
        });

    }

    private int getChapterPosition(final int pageStartPosition, final int pageEndPosition, final boolean back, final List<Integer> tocChapterNodeList) {
        int count = pageEndPosition - pageStartPosition;
        int size = tocChapterNodeList.size();
        int position;
        if (back) {
            position = pageStartPosition;
        }else {
            position = pageEndPosition;
        }
        for (int i = 0; i < size; i++) {
            if (position <= tocChapterNodeList.get(i)) {
                if (back) {
                    int index = i - 1;
                    if (index < 0) {
                        return 0;
                    }
                    int result = tocChapterNodeList.get(Math.max(0, index));
                    if (result > pageStartPosition && result < pageEndPosition) {
                        return getChapterPosition(pageStartPosition - count, pageStartPosition, back, tocChapterNodeList);
                    } else {
                        return result;
                    }
                } else {
                    int result = tocChapterNodeList.get(i);
                    if (result > pageStartPosition && result < pageEndPosition) {
                        return getChapterPosition(pageEndPosition, pageEndPosition + count, back, tocChapterNodeList);
                    } else {
                        return result;
                    }
                }
            }
        }

        if (back) {
            return position - 1;
        } else {
            return position + 1;
        }

    }

    private void onPressedImageView(int gridType) {
        nineImageGrid.setImageResource(gridType == GridType.Nine ? R.drawable.ic_dialog_reader_page_nine_black_focused
                : R.drawable.ic_dialog_reader_page_nine_white_focused);
        fourImageGrid.setImageResource(gridType == GridType.Four ? R.drawable.ic_dialog_reader_page_four_black_focused
                : R.drawable.ic_dialog_reader_page_four_white_focused);
        oneImageGrid.setImageResource(gridType == GridType.One ? R.drawable.ic_dialog_reader_page_one_black_focused
                : R.drawable.ic_dialog_reader_page_one_white_focused);
    }

    private void setupContent() {
        gridRecyclerView.gotoPage(getPaginator().pageByIndex(currentPage));
        onPressedImageView(grid.getGridType());
        initPageProgress();
    }

    /**
     * will clone a copy of passed in bitmap
     *
     * @param pageInfo
     * @param bitmap
     */
    public void updatePreview(PageInfo pageInfo, Bitmap bitmap) {
        if (pageInfo == null) {
            return;
        }
        int page = pageInfo.getPageNumber();
        if (getPaginator().isItemInCurrentPage(page)) {
            adapter.setBitmap(pageInfo, bitmap);
        }
    }

    private void onPageDataChanged() {
        currentPage = getPaginator().getCurrentPageBegin();
        callback.abort();
        previewMap.clear();
        adapter.requestMissingBitmaps();
        updatePageProgress();
    }

    private void initPageProgress() {
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                int page = progress - 1;
                gridRecyclerView.gotoPage(page);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updatePageProgress();
    }

    private void updatePageProgress() {
        seekBarProgress.setMax(getPaginator().pages());
        seekBarProgress.setProgress(getPaginator().getCurrentPage() + 1);
        textViewProgress.setText((getPaginator().getCurrentPage() + 1) + "/" + getPaginator().pages());
    }

    private void nextPage() {
        gridRecyclerView.nextPage();
    }

    private void prevPage() {
        gridRecyclerView.prevPage();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (callback != null) {
            callback.recycleBitmap();
        }
    }

    public boolean isJumpChapter() {
        return jumpChapter;
    }

    public void setJumpChapter(boolean jumpChapter) {
        this.jumpChapter = jumpChapter;
    }
}
