package com.onyx.android.edu.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/7/4.
 */
public class PageRecyclerView extends RecyclerView{

    private static final String TAG = PageRecyclerView.class.getSimpleName();
    
    private int mShortestDistance = 100; // 超过此距离的滑动才有效
    private float mDownY = 0; // 手指按下的X轴坐标
    private List<Integer> mPositionRecord = new ArrayList<>();

    public PageRecyclerView(Context context) {
        super(context);
        initView();
    }

    public PageRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PageRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView(){
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getY();
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float upY = event.getY();
                if (upY - mDownY > mShortestDistance) {
                    goToPre();
                } else if (mDownY - upY > mShortestDistance){
                    goToNext();
                }
            default:
                break;
        }
        return true;
    }

    private void goToPre(){
        if (mPositionRecord.size() > 0){
            int location = mPositionRecord.size() - 1;
            int position = mPositionRecord.get(location);
            mPositionRecord.remove(location);
            scrollToPosition(position);
        }
        stopScroll();
    }

    private void goToNext(){
        final LinearLayoutManager manager = (LinearLayoutManager) getLayoutManager();
        int first = manager.findFirstVisibleItemPosition();
        int end = manager.findLastVisibleItemPosition();
        int firstTop = getChildAt(0).getTop();
        int scrollHeight = getChildAt(end - first).getTop() - firstTop;

        int location = mPositionRecord.size() > 0 ? mPositionRecord.size() - 1 : 0;
        if (location == 0 || mPositionRecord.get(location) != first){
            mPositionRecord.add(first);
        }
        scrollBy(0, scrollHeight);
        stopScroll();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
//        final LinearLayoutManager manager = (LinearLayoutManager) getLayoutManager();
//        int first = manager.findFirstVisibleItemPosition();
//        int end = manager.findLastVisibleItemPosition();
//        int top = getChildAt(end - first).getTop();
//        int bottom = getChildAt(end - first).getBottom();
//        int height = getMeasuredHeight();
//        LogUtils.d(TAG, "top: "+top +" bottom:"+bottom + " height:" + height);
//        if (bottom > height && top < height && getChildCount() > 0){
////            setPadding(0,0,0,height - top);
//            int differ = height - top;
//            int count = end - first - 1;
//            int h = differ / count;
//            for (int i = 0; i < count; i++) {
//                View view = getChildAt(i);
//                int wView = view.getLayoutParams().width;
//                int hView = view.getLayoutParams().height + h;
//                view.setLayoutParams(new LayoutParams(wView,hView));
//            }
//        }
    }
}
