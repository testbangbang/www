package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by suicheng on 2017/4/26.
 */
public class SinglePageRecyclerView extends PageRecyclerView {

    private OnChangePageListener onChangePageListener;

    public SinglePageRecyclerView(Context context) {
        super(context);
    }

    public SinglePageRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SinglePageRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface OnChangePageListener {
        void prev();
        void next();
    }

    public void setOnChangePageListener(OnChangePageListener onChangePageListener) {
        this.onChangePageListener = onChangePageListener;
    }

    @Override
    public void nextPage() {
        onNextPage();
    }

    @Override
    public void prevPage() {
        onPrevPage();
    }

    private void onNextPage() {
        if (onChangePageListener != null) {
            onChangePageListener.next();
        }
    }

    private void onPrevPage() {
        if (onChangePageListener != null) {
            onChangePageListener.prev();
        }
    }
}
