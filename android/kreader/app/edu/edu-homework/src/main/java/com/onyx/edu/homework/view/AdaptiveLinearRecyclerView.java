package com.onyx.edu.homework.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.onyx.android.sdk.data.KeyAction;
import com.onyx.android.sdk.ui.utils.PageTurningDetector;
import com.onyx.android.sdk.ui.utils.PageTurningDirection;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.DisableScrollLinearManager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2016/6/27.
 */
public class AdaptiveLinearRecyclerView extends RecyclerView {

    private static final String TAG = AdaptiveLinearRecyclerView.class.getSimpleName();
    public enum TouchDirection {Horizontal, Vertical}
    private float lastX, lastY;
    private Map<Integer, String> keyBindingMap = new Hashtable<>();
    private boolean pageTurningCycled = false;

    public interface OnPagingListener {
        void onPageChange(int startShowedPosition, int endShowedPosition);
    }

    public AdaptiveLinearRecyclerView(Context context) {
        super(context);
        init();
    }

    public AdaptiveLinearRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdaptiveLinearRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setItemAnimator(null);
        setClipToPadding(true);
        setClipChildren(true);
        setLayoutManager(new DisableScrollLinearManager(getContext(), LinearLayoutManager.VERTICAL, false));
        setDefaultPageKeyBinding();
        initChildAttachStateChangeListener();
    }

    private void initChildAttachStateChangeListener() {
        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(final View view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.VISIBLE);
                        DisableScrollLinearManager layoutManager = (DisableScrollLinearManager) getLayoutManager();
                        int lastCompletelyVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                        int lastVisible = layoutManager.findLastVisibleItemPosition();
                        if (lastVisible > lastCompletelyVisible) {
                            ViewHolder viewHolder =  findViewHolderForAdapterPosition(lastVisible);
                            if (viewHolder != null) {
                                viewHolder.itemView.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return processKeyAction(event) || super.dispatchKeyEvent(event);
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

    private boolean processKeyAction(KeyEvent event){
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return false;
        }
        final String args = keyBindingMap.get(event.getKeyCode());
        if (args == null){
            return false;
        }
        switch (args){
            case KeyAction.NEXT_PAGE:
                nextPage();
                break;
            case KeyAction.PREV_PAGE:
                prevPage();
                break;
            case KeyAction.MOVE_LEFT:
                break;
            case KeyAction.MOVE_RIGHT:
                break;
            case KeyAction.MOVE_DOWN:
                break;
            case KeyAction.MOVE_UP:
                break;
            default:
                nextPage();
        }
        return true;
    }

    public void setKeyBinding(Map<Integer, String> keyBindingMap){
        this.keyBindingMap = keyBindingMap;
    }

    public void setDefaultPageKeyBinding(){
        keyBindingMap.put(KeyEvent.KEYCODE_PAGE_DOWN, KeyAction.NEXT_PAGE);
        keyBindingMap.put(KeyEvent.KEYCODE_VOLUME_DOWN, KeyAction.NEXT_PAGE);
        keyBindingMap.put(KeyEvent.KEYCODE_PAGE_UP, KeyAction.PREV_PAGE);
        keyBindingMap.put(KeyEvent.KEYCODE_VOLUME_UP, KeyAction.PREV_PAGE);
    }

    public void setDefaultMoveKeyBinding(){
        keyBindingMap.put(KeyEvent.KEYCODE_PAGE_DOWN, KeyAction.MOVE_RIGHT);
        keyBindingMap.put(KeyEvent.KEYCODE_VOLUME_DOWN, KeyAction.MOVE_RIGHT);
        keyBindingMap.put(KeyEvent.KEYCODE_PAGE_UP, KeyAction.MOVE_LEFT);
        keyBindingMap.put(KeyEvent.KEYCODE_VOLUME_UP, KeyAction.MOVE_LEFT);
    }

    public boolean isPageTurningCycled() {
        return pageTurningCycled;
    }

    public void setPageTurningCycled(boolean cycled) {
        this.pageTurningCycled = cycled;
    }

    private List<Integer> firstPositionList = new ArrayList<>();
    public void prevPage() {
        if (firstPositionList.size() == 0) {
            return;
        }
        int position = firstPositionList.remove(firstPositionList.size() - 1);
        DisableScrollLinearManager layoutManager = (DisableScrollLinearManager) getLayoutManager();
        int end = layoutManager.findFirstVisibleItemPosition();
        for (int i = position; i < end; i++) {
            ViewHolder viewHolder =  findViewHolderForAdapterPosition(i);
            if (viewHolder != null) {
//                viewHolder.itemView.setVisibility(View.INVISIBLE);
            }
        }
        onPageChange(position);
    }

    public void nextPage() {
        DisableScrollLinearManager layoutManager = (DisableScrollLinearManager) getLayoutManager();
        int firstVisible = layoutManager.findFirstVisibleItemPosition();
        firstPositionList.add(firstVisible);
        int lastCompletelyVisible = layoutManager.findLastCompletelyVisibleItemPosition();
        int lastVisible = layoutManager.findLastVisibleItemPosition();
        int position = lastCompletelyVisible + 1;
        if (position >= getAdapter().getItemCount()) {
            return;
        }

        ViewHolder viewHolder =  findViewHolderForAdapterPosition(lastVisible);
        if (viewHolder != null) {
            viewHolder.itemView.setVisibility(View.VISIBLE);
        }
        onPageChange(position);
    }

    private void onPageChange(int position) {
        managerScrollToPosition(position);
    }

    private void managerScrollToPosition(int position) {
        getDisableLayoutManager().scrollToPositionWithOffset(position, 0);
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
}
