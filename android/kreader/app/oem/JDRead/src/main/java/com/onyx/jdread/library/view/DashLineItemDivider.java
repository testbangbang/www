package com.onyx.jdread.library.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;

/**
 * Created by hehai on 18-1-4.
 */

public class DashLineItemDivider extends RecyclerView.ItemDecoration {
    public void onDrawOver(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.RED);
            Path path = new Path();
            path.moveTo(left, top);
            path.lineTo(right, top);
            PathEffect effects = new DashPathEffect(new float[]{JDReadApplication.getInstance().getResources().getInteger(R.integer.dash_Line_length),
                    JDReadApplication.getInstance().getResources().getInteger(R.integer.dash_space_length)}, 0);
            paint.setPathEffect(effects);
            c.drawPath(path, paint);
        }
    }
}
