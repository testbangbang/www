package com.onyx.jdread.library.utils;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.library.adapter.PageAdapter;


import java.util.List;

/**
 * Created by solskjaer49 on 2016/11/26 10:31.
 */

public class OnyxDataBindingUtil {
    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, int resource) {
        if (resource != 0) {
            imageView.setImageResource(resource);
        }
    }

    @BindingAdapter({"imageState"})
    public static void setImageViewState(ImageView imageView, int[] value) {
        imageView.setImageState(value, true);
    }

    @BindingAdapter({"thumbnail"})
    public static void setImageViewBitmap(ImageView imageView, CloseableReference<Bitmap> bitmap) {
        if (bitmap != null && bitmap.get() != null && !bitmap.get().isRecycled()) {
            imageView.setImageBitmap(bitmap.get());
        }
    }

    @SuppressWarnings("unchecked")
    @BindingAdapter("items")
    public static void setItems(PageRecyclerView recyclerView, List items) {
        PageAdapter adapter = (PageAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setRawData(items, recyclerView.getContext());
        }
    }

    @SuppressWarnings("unchecked")
    @BindingAdapter("levels")
    public static void setItems(LinearLayout parentLibraryRefLayout, List<DataModel> levels) {
        parentLibraryRefLayout.removeAllViews();
        for (DataModel item : levels) {
            TextView textView = new TextView(parentLibraryRefLayout.getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setText(item.title.get());
            textView.setBackgroundResource(R.drawable.square_bg);
            textView.setPadding(5, 9, 9, 5);
            parentLibraryRefLayout.addView(textView);
        }
    }

    @BindingAdapter("android:layout_centerInParent")
    public static void setCenterInParent(View view, boolean centerInParent) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (centerInParent) {
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
            } else {
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
            }
        }
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:layout_marginTop")
    public static void setLayoutMarginTop(View view, float topMargin) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (topMargin > 0) {
            layoutParams.setMargins(layoutParams.leftMargin, (int) topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
        }
        view.setLayoutParams(layoutParams);
    }
}
