package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.utils.DimenUtils;

/**
 * 工具条
 * 线性布局
 * 可以插入SpaceView来调整布局
 * SpaceView可以通过Weight来设置占有的百分比
 * 填充方式为Average时会忽悠SpaceView
 * Created by ming on 16/9/20.
 */
public class OnyxToolbar extends ViewGroup{

    public interface OnMenuClickListener {
        OnyxToolbar OnClickListener(View view);
    }

    private OnyxToolbar currentExpandedToolbar;
    private View currentExpandedView;
    private View dividerView;
    private float totalWeight = 0f;
    private int menuViewTotalWidth = 0;
    private int menuViewCount = 0;
    private int menuViewIndex = 0;
    private OnMenuClickListener onMenuClickListener;
    private Direction direction = Direction.Bottom;
    private FillStyle fillStyle = FillStyle.Average;
    int paddingLeft;
    int paddingRight;
    int paddingTop;
    int paddingBottom;

    //工具条出现的位置
    public enum Direction{Left,Top,Right,Bottom}
    //工具条内容的填充方式 WrapContent 根据内容填充  Average 平分
    public enum FillStyle{WrapContent,Average}

    public OnyxToolbar(Context context) {
        super(context);
        init();
    }

    public OnyxToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OnyxToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener;
    }

    private void init(){
        dividerView = generateDefaultDividerView();
        addView(dividerView);
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
        requestLayout();
        invalidate();
    }

    public void setFillStyle(FillStyle fillStyle) {
        this.fillStyle = fillStyle;
        requestLayout();
        invalidate();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setupListener(child);
    }

    private void setupListener(View view){
        if (!isMenuView(view)){
            return;
        }
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMenuClickListener != null){
                    OnyxToolbar toolbar = onMenuClickListener.OnClickListener(v);
                    if (currentExpandedView != v && toolbar != null){
                        if (currentExpandedToolbar != null){
                            removeView(currentExpandedToolbar);
                        }
                        currentExpandedToolbar = toolbar;
                        currentExpandedToolbar.setDirection(direction);
                        addView(currentExpandedToolbar);
                        currentExpandedView = v;
                    }else {
                        removeView(currentExpandedToolbar);
                        currentExpandedView = null;
                        currentExpandedToolbar = null;
                    }

                }
            }
        });
    }

    private View generateDefaultDividerView(){
        View view = new View(getContext());
        view.setBackgroundColor(getResources().getColor(android.R.color.black));
        view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimenUtils.dip2px(getContext(), 1)));
        return view;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int rw = MeasureSpec.getSize(widthMeasureSpec);
        int childCount = this.getChildCount();
        int maxHeight = 0;
        menuViewTotalWidth = 0;
        totalWeight = 0f;
        menuViewCount = 0;
        for (int i = 0; i < childCount; i++) {
            View childView = this.getChildAt(i);
            this.measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            LayoutParams lp = (LayoutParams) childView.getLayoutParams();
            totalWeight += lp.weight;
            if (isMenuView(childView)){
                menuViewTotalWidth += childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                maxHeight = Math.max(childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin, maxHeight);
                menuViewCount ++;
            }

            if (childView == dividerView){
                maxHeight += dividerView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            }
            if (childView == currentExpandedToolbar){
                maxHeight += currentExpandedToolbar.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            }
        }
        setMeasuredDimension(rw, maxHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();

        int totalWidth = paddingLeft;
        int childCount = getChildCount();
        menuViewIndex = 0;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);

            int measureHeight = childView.getMeasuredHeight();
            int measuredWidth = childView.getMeasuredWidth();
            LayoutParams lp = (LayoutParams)childView.getLayoutParams();

            if (childView instanceof SpaceView){
                measuredWidth = calculateSpaceViewWidth(lp.weight, menuViewTotalWidth);
            }

            switch (direction){
                case Left:
                    break;
                case Top:
                    totalWidth = layoutChildTop(childView, totalWidth,  measuredWidth, measureHeight);
                    break;
                case Right:
                    break;
                case Bottom:
                    totalWidth = layoutChildBottom(childView, totalWidth, measuredWidth, measureHeight);
                    break;
            }
        }
    }

    /**
     * 底部布局
     * currentExpandedToolbar 展开的工具条在顶部，Top位置在顶部
     * dividerView 分割View 总高度减去展开的工具条的高度得出他的Top位置
     * menuView 菜单项 总高度 减去 展开的工具条和分割View的高度 得出他的Top位置
     * @param childView 子view
     * @param totalWidth menuView的总长度
     * @param measuredWidth 子view的宽度
     * @param measureHeight 子view的高度
     * @return
     */
    private int layoutChildBottom(View childView, int totalWidth, int measuredWidth, int measureHeight){
        LayoutParams lp = (LayoutParams)childView.getLayoutParams();

        if (childView == currentExpandedToolbar){
            childView.layout(lp.leftMargin
                    ,lp.topMargin
                    , getMeasuredWidth()  - lp.rightMargin
                    , measureHeight + lp.topMargin);
        }else if (childView == dividerView){
            int expandedToolbarHeight = getHeightWidthMargin(currentExpandedToolbar);
            childView.layout(paddingLeft + lp.leftMargin
                    , expandedToolbarHeight + lp.topMargin + paddingTop
                    , getMeasuredWidth() - paddingRight - lp.rightMargin
                    , expandedToolbarHeight + measureHeight + lp.topMargin + paddingTop);
        }else {
            int height = getHeightWidthMargin(currentExpandedToolbar) + getHeightWidthMargin(dividerView);
            int startLeft = totalWidth + lp.leftMargin;
            int startTop = lp.topMargin + height + paddingTop;

            if (!isSpaceView(childView) && fillStyle == FillStyle.Average){
                startLeft = getChildStartWhenAverage(menuViewIndex, childView);
            }

            if (!isSpaceView(childView)){
                menuViewIndex++;
            }

            childView.layout(startLeft
                    , startTop
                    , startLeft + measuredWidth
                    , startTop + measureHeight);
            totalWidth += measuredWidth + lp.leftMargin + lp.rightMargin;
        }
        return totalWidth;
    }

    /**
     * 顶部布局
     * currentExpandedToolbar 展开的工具条在底部，总高度 减去 它自己的高度 得出他的Top位置
     * dividerView 分割View 总高度减去展开的工具条的高度得出他的Top位置
     * menuView 菜单项在顶部，Top位置在顶部
     * @param childView 子view
     * @param totalWidth menuView的总长度
     * @param measuredWidth 子view的宽度
     * @param measureHeight 子view的高度
     * @return
     */
    private int layoutChildTop(View childView, int totalWidth, int measuredWidth, int measureHeight){
        LayoutParams lp = (LayoutParams)childView.getLayoutParams();

        if (childView == currentExpandedToolbar){
            childView.layout(lp.leftMargin
                    ,getMeasuredHeight()  - lp.topMargin - measureHeight
                    , getMeasuredWidth() - lp.rightMargin
                    , getMeasuredHeight() - lp.bottomMargin);
        }else if (childView == dividerView){
            int expandedToolbarHeight = currentExpandedToolbar == null ? 0 : currentExpandedToolbar.getMeasuredHeight();
            childView.layout(paddingLeft + lp.leftMargin
                    ,getMeasuredHeight() - paddingBottom - lp.bottomMargin - measureHeight - expandedToolbarHeight
                    , getMeasuredWidth() - paddingRight - lp.rightMargin
                    , getMeasuredHeight() - paddingBottom - lp.bottomMargin - expandedToolbarHeight);
        }else {
            int startLeft = totalWidth + lp.leftMargin;
            int startTop = lp.topMargin + paddingTop;

            if (!isSpaceView(childView) && fillStyle == FillStyle.Average){
                startLeft = getChildStartWhenAverage(menuViewIndex, childView);
            }

            if (!isSpaceView(childView)){
                menuViewIndex++;
            }

            childView.layout(startLeft
                    , startTop
                    , startLeft + measuredWidth
                    , startTop + measureHeight);
            totalWidth += measuredWidth + lp.leftMargin + lp.rightMargin;
        }
        return totalWidth;
    }

    private int getChildStartWhenAverage(int index, View childView){
        int averageWidth = getMeasuredWidth()/menuViewCount;
        int centerPointX = averageWidth/2 + index * averageWidth;
        return centerPointX - childView.getMeasuredWidth()/2;
    }

    private int getHeightWidthMargin(View view){
        int height = 0;
        if (view != null){
            LayoutParams lp = (LayoutParams)view.getLayoutParams();
            height = view.getMeasuredHeight() + lp.bottomMargin + lp.topMargin;
        }
        return height;
    }


    private boolean isMenuView(View view){
        return !(view instanceof SpaceView) && view != dividerView && view != currentExpandedToolbar;
    }

    private boolean isSpaceView(View view){
        return view instanceof SpaceView;
    }

    private int calculateSpaceViewWidth(float weight, int totalWidth){
        int spaceTotalWidth = getMeasuredWidth() - totalWidth - getPaddingLeft() - getPaddingRight();
        return (int) (spaceTotalWidth * weight / totalWeight);
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        public float weight;

        public LayoutParams(Context arg0, AttributeSet arg1) {
            super(arg0, arg1);
            TypedArray a =
                    arg0.obtainStyledAttributes(arg1, R.styleable.OnyxToolbar);
            weight = a.getFloat(R.styleable.OnyxToolbar_weight, 0);
        }

        public LayoutParams(int arg0, int arg1) {
            super(arg0, arg1);
            weight = 0;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams arg0) {
            super(arg0);
            weight = 0;
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class Builder{

        public static ImageView createImageView(Context content, int resId){
            ImageView image = new ImageView(content);
            image.setImageResource(resId);
            setDefaultLayoutParams(content, image);
            return image;
        }

        public static TextView createTextView(Context context){
            TextView textView = new TextView(context);
            setDefaultLayoutParams(context, textView);
            return textView;
        }

        public static SpaceView createSpaceView(Context context, float weight){
            SpaceView spaceView = new SpaceView(context);
            OnyxToolbar.LayoutParams lp = new OnyxToolbar.LayoutParams(0, 1);
            lp.weight = weight;
            spaceView.setLayoutParams(lp);
            return spaceView;
        }

        public static void setDefaultLayoutParams(Context content, View view){
            int margin = DimenUtils.dip2px(content, 5);
            view.setBackgroundResource(R.drawable.imagebtn_bg);
            OnyxToolbar.LayoutParams lp = new OnyxToolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(margin, margin, margin, margin);
            view.setLayoutParams(lp);
        }
    }
}
