package com.onyx.android.dr.reader.dialog;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.data.SingletonSharedPreference;
import com.onyx.android.dr.reader.event.RedrawPageEvent;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.dr.reader.view.PreviewViewHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderNavigator;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.request.GetPositionFromPageNumberRequest;
import com.onyx.android.sdk.reader.host.request.GotoPositionRequest;
import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joy on 7/15/16.
 */
public class DialogQuickPreview extends OnyxBaseDialog {

    private static final String TAG = "DialogQuickPreview";
    private static final int LONG_CLICK_TIME_INTERVAL = 2000;
    private TextView prevPage;
    private TextView nextPage;

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

            final GetPositionFromPageNumberRequest readerRequest = new GetPositionFromPageNumberRequest(toRequest, true);
            readerPresenter.getReader().submitRequest(getContext(), readerRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    List<String> positions = readerRequest.getDocumentPositions();
                    if (positions == null || positions.size() < 1) {
                        return;
                    }

                    callback.requestPreview(positions, toRequest);
                    setJumpChapter(false);
                }
            });
        }

        public void setGridType(Grid grid) {
            this.grid = grid;
            gridRecyclerView.resize(grid.getRows(), grid.getColumns(), readerPresenter.getReader().getNavigator().getTotalPage());
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
            ReaderNavigator navigator = readerPresenter.getReader().getNavigator();
            return navigator == null ? 0 : navigator.getTotalPage();
        }

        @Override
        public PreviewViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
            final PreviewViewHolder previewViewHolder = new PreviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.preview_list_item_view, parent, false));
            previewViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final BaseReaderRequest gotoPosition = new GotoPositionRequest(previewViewHolder.getPage());
                    gotoPosition.setAbortPendingTasks(true);
                    readerPresenter.getReader().submitRequest(getContext(), gotoPosition, new BaseCallback() {
                        @Override
                        public void done(BaseRequest request, Throwable e) {
                            DialogQuickPreview.this.dismiss();
                            RedrawPageEvent event = new RedrawPageEvent(previewViewHolder.getPage(), previewViewHolder.getPagePosition());
                            EventBus.getDefault().post(event);
                        }
                    });
                }
            });
            return previewViewHolder;
        }

        @Override
        public void onPageBindViewHolder(PreviewViewHolder holder, final int position) {
            holder.getContainer().setActivated(readerPresenter.getPageInformation().getCurrentPage() == position);
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
    private SeekBar seekBarProgress;
    private ImageView oneImageGrid;
    private ImageView fourImageGrid;
    private ImageView nineImageGrid;

    private Grid grid = new Grid();
    private Map<PageInfo, Bitmap> previewMap = new HashMap<>();
    private PagePreviewAdapter adapter = new PagePreviewAdapter();

    private ReaderPresenter readerPresenter;
    private int currentPage;
    private String currentPagePosition;
    private Callback callback;
    private boolean jumpChapter = false;

    public DialogQuickPreview(@NonNull final ReaderPresenter readerPresenter, Callback callback) {
        super(readerPresenter.getReaderView().getViewContext(), R.style.android_dialog_no_title);
        setContentView(R.layout.dialog_quick_preview);

        this.readerPresenter = readerPresenter;
        this.callback = callback;
        currentPage = readerPresenter.getPageInformation().getCurrentPage();
        currentPagePosition = readerPresenter.getCurrentPagePosition();

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

        seekBarProgress = (SeekBar) findViewById(R.id.seek_bar_page);
        oneImageGrid = (ImageView) findViewById(R.id.image_view_one_grids);
        fourImageGrid = (ImageView) findViewById(R.id.image_view_four_grids);
        nineImageGrid = (ImageView) findViewById(R.id.image_view_nine_grids);
        prevPage = (TextView) findViewById(R.id.prev_page);
        nextPage = (TextView) findViewById(R.id.next_page);

        TextView title = (TextView) findViewById(R.id.title_bar_title);
        title.setText(getContext().getString(R.string.dialog_reader_menu_back));
        findViewById(R.id.menu_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogQuickPreview.this.dismiss();
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
                BaseReaderRequest gotoPosition = new GotoPositionRequest(currentPagePosition);
                gotoPosition.setAbortPendingTasks(true);
                readerPresenter.getReader().submitRequest(getContext(), gotoPosition, null);
            }
        });

        prevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevPage();
            }
        });

        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPage();
            }
        });

        gridRecyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                onPageDataChanged();
            }
        });
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
        } else {
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

    public void setJumpChapter(boolean jumpChapter) {
        this.jumpChapter = jumpChapter;
    }
}
