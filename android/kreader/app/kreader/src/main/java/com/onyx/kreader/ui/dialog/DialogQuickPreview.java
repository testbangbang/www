package com.onyx.kreader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.ImageView;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.Size;
import com.onyx.kreader.R;
import com.onyx.kreader.common.Debug;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joy on 7/15/16.
 */
public class DialogQuickPreview extends Dialog {

    public static abstract class Callback {
        public abstract void requestPreview(final int pageStart, final int pageEnd, final Size desiredSize);
    }

    private enum GridType { Four, Nine }

    private static class Grid {
        private GridType grid = GridType.Four;

        public void setGridType(GridType grid) {
            this.grid = grid;
        }

        public int getRows() {
            return grid == GridType.Four ? 2 : 3;
        }

        public int getColumns() {
            return grid == GridType.Four ? 2 : 3;
        }

        public int getGridSize() {
            return getRows() * getColumns();
        }
    }

    private static class PreviewViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public PreviewViewHolder(ImageView itemView) {
            super(itemView);

            imageView = itemView;
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        public void bindPreview(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }

    private class PreviewAdapter extends RecyclerView.Adapter<PreviewViewHolder> {

        private final Bitmap BlankBitmap = Bitmap.createBitmap(300, 400, Bitmap.Config.ARGB_8888);

        private ViewGroup parent;
        private Size childSize = new Size(300, 400);
        private ArrayList<Bitmap> bitmapList = new ArrayList<>();

        private HashMap<Integer, Bitmap> bitmapCache = new HashMap<>();

        public PreviewAdapter() {
            BlankBitmap.eraseColor(Color.WHITE);
        }

        public void requestMissingBitmaps() {
            HashMap<Integer, Bitmap> cache = new HashMap<>();
            for (int i = 0; i < bitmapList.size(); i++) {
                int page = paginator.indexByPageOffset(i);
                if (bitmapCache.containsKey(page)) {
                    cache.put(page, bitmapCache.get(page));
                    setBitmap(i, cache.get(page));
                    bitmapCache.remove(page);
                    continue;
                }
                callback.requestPreview(page, page, childSize);
            }
            for (Bitmap bitmap : bitmapCache.values()) {
                bitmap.recycle();
            }
            bitmapCache.clear();
            bitmapCache.putAll(cache);
        }

        public void resetListSize(int size) {
            bitmapList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                bitmapList.add(BlankBitmap);
            }
            notifyDataSetChanged();
        }

        public Size getDesiredSize() {
            return childSize;
        }

        public void setBitmap(int index, Bitmap bitmap) {
            Debug.d("setBitmap: " + bitmap);
            bitmapList.set(index, bitmap);
            bitmapCache.put(paginator.indexByPageOffset(index), bitmap);
            notifyItemChanged(index);
        }

        @Override
        public PreviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            this.parent = parent;
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            return new PreviewViewHolder(new ImageView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(PreviewViewHolder holder, int position) {
            ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
            }
            childSize.width = parent.getMeasuredWidth() / grid.getColumns();
            childSize.height = parent.getMeasuredHeight() / grid.getRows();

            params.height = parent.getMeasuredHeight() / grid.getRows();
            holder.imageView.setLayoutParams(params);
            Bitmap bmp = bitmapList.get(position);
            if (bmp == null) {
                bmp = BlankBitmap;
            }
            holder.bindPreview(bmp);
        }

        @Override
        public int getItemCount() {
            return bitmapList.size();
        }
    }

    private RecyclerView gridRecyclerView;
    private Grid grid = new Grid();
    private GPaginator paginator;
    private PreviewAdapter adapter = new PreviewAdapter();

    private int pageCount;
    private int currentPage;
    private Callback callback;

    public DialogQuickPreview(@NonNull Context context, final int pageCount, final int currentPage,
                              final Bitmap currentPageBitmap, Callback callback) {
        super(context, R.style.dialog_no_title);
        setContentView(R.layout.dialog_quick_preview);

        this.pageCount = pageCount;
        this.currentPage = currentPage;
        this.callback = callback;

        fitDialogToWindow();
        setupLayout();
        setupContent(pageCount, currentPage, currentPageBitmap);
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

    private void setupLayout() {
        gridRecyclerView = (RecyclerView)findViewById(R.id.grid_view_preview);
        gridRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), grid.getColumns()));
        gridRecyclerView.setAdapter(adapter);

        findViewById(R.id.image_view_prev_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paginator.prevPage()) {
                    currentPage = paginator.getCurrentPageBegin();
                    onPageDataChanged();
                }
            }
        });

        findViewById(R.id.image_view_next_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paginator.nextPage()) {
                    currentPage = paginator.getCurrentPageBegin();
                    onPageDataChanged();
                }
            }
        });

        findViewById(R.id.image_view_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogQuickPreview.this.dismiss();
            }
        });

        findViewById(R.id.image_view_four_grids).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grid.setGridType(GridType.Four);
                paginator.resize(grid.getRows(), grid.getColumns(), pageCount);
                paginator.gotoPageByIndex(currentPage);
                gridRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), grid.getColumns()));
                onPageDataChanged();
            }
        });

        findViewById(R.id.image_view_nine_grids).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grid.setGridType(GridType.Nine);
                paginator.resize(grid.getRows(), grid.getColumns(), pageCount);
                paginator.gotoPageByIndex(currentPage);
                gridRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), grid.getColumns()));
                onPageDataChanged();
            }
        });
    }

    private void setupContent(int pageCount, int currentPage, Bitmap currentPageBitmap) {
        paginator = new GPaginator(grid.getRows(), grid.getColumns(), pageCount);
        paginator.gotoPageByIndex(currentPage);
        adapter.resetListSize(paginator.itemsInCurrentPage());
        updatePreview(currentPage, currentPageBitmap);
        if (callback != null) {
            adapter.requestMissingBitmaps();
        }
    }

    /**
     * will clone a copy of passed in bitmap
     * @param page
     * @param bitmap
     */
    public void updatePreview(int page, Bitmap bitmap) {
        if (paginator.isItemInCurrentPage(page)) {
            adapter.setBitmap(paginator.offsetInCurrentPage(page), getScaledPreview(bitmap));
        }
    }

    private Bitmap getScaledPreview(Bitmap pageBitmap) {
        return Bitmap.createScaledBitmap(pageBitmap, adapter.getDesiredSize().width, adapter.getDesiredSize().height, false);
    }

    private void onPageDataChanged() {
        adapter.resetListSize(paginator.itemsInCurrentPage());
        adapter.requestMissingBitmaps();
    }

}
