package com.onyx.jdread.shop.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.onyx.android.sdk.ui.utils.PageTurningDetector;
import com.onyx.android.sdk.ui.utils.PageTurningDirection;
import com.onyx.android.sdk.ui.view.DisableScrollLinearManager;
import com.onyx.jdread.shop.adapter.ShopMainConfigAdapter;
import com.onyx.jdread.shop.model.BaseSubjectViewModel;

import java.util.List;

/**
 * Created by jackdeng on 2018/2/1.
 */

public class CustomRecycleView extends RecyclerView {

    private static final String TAG = CustomRecycleView.class.getSimpleName();
    private float lastX;
    private float lastY;
    private int curPageIndex;
    private OnPagingListener onPagingListener;

    public CustomRecycleView(Context context) {
        super(context);
        init();
    }

    public CustomRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomRecycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setItemAnimator(null);
        setClipToPadding(true);
        setClipChildren(true);
        setLayoutManager(new DisableScrollLinearManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    public void setOnPagingListener(OnPagingListener listener) {
        this.onPagingListener = listener;
    }

    public interface OnPagingListener {
        void onPageChange(int curIndex);
    }

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
                    nextPage();
                    return true;
                } else if (direction == PageTurningDirection.PREV) {
                    prevPage();
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void prevPage() {
        ShopMainConfigAdapter adapter = getAdapter();
        if (adapter != null) {
            if (curPageIndex <= 0) {
                return;
            }
            List<BaseSubjectViewModel> datas = adapter.datas;
            int preIndex = 0;
            for (int i = 0; i < datas.size(); i++) {
                int pageIndex = datas.get(i).getPageIndex();
                if (pageIndex == curPageIndex - 1) {
                    preIndex = i;
                    break;
                }
            }
            if (curPageIndex > 0) {
                curPageIndex--;
            }
            managerScrollToPosition(preIndex);
            if (onPagingListener != null){
                onPagingListener.onPageChange(curPageIndex);
            }
        }
    }

    public void nextPage() {
        ShopMainConfigAdapter adapter = getAdapter();
        if (adapter != null) {
            int lastCompletelyPosition = getDisableLayoutManager().findLastCompletelyVisibleItemPosition();
            List<BaseSubjectViewModel> datas = adapter.datas;
            if (lastCompletelyPosition == datas.size() - 1) {
                return;
            }
            int lastVisibleItemPosition = getDisableLayoutManager().findLastVisibleItemPosition();
            int nextPageIndex = datas.get(lastVisibleItemPosition).getPageIndex();
            if (nextPageIndex == 0) {
                int finalIndex = 0;
                for (int i = 1; i < datas.size(); i++) {
                    int pageIndex = datas.get(i).getPageIndex();
                    if (pageIndex > finalIndex) {
                        finalIndex = pageIndex;
                    }
                }
                nextPageIndex = finalIndex + 1;
                datas.get(lastVisibleItemPosition).setPageIndex(nextPageIndex);
            }

            curPageIndex = nextPageIndex;
            managerScrollToPosition(lastVisibleItemPosition);
            if (onPagingListener != null){
                onPagingListener.onPageChange(curPageIndex);
            }
        }
    }

    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position);
        if (onPagingListener != null){
            if (position == 0) {
                curPageIndex = 0;
                onPagingListener.onPageChange(curPageIndex);
            }
        }
    }

    private void managerScrollToPosition(int position) {
        getDisableLayoutManager().scrollToPositionWithOffset(position, 0);
    }

    private LinearLayoutManager getDisableLayoutManager() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        if (layoutManager instanceof DisableScrollLinearManager) {
            layoutManager = (DisableScrollLinearManager) getLayoutManager();
        }
        return layoutManager;
    }

    @Override
    public ShopMainConfigAdapter getAdapter() {
        return (ShopMainConfigAdapter) super.getAdapter();
    }
}
