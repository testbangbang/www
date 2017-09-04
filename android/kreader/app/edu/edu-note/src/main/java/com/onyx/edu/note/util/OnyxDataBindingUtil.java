package com.onyx.edu.note.util;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.edu.note.ui.BaseMenuViewModel;
import com.onyx.edu.note.ui.PageAdapter;
import com.onyx.edu.note.ui.PageDataBindingAdapter;

import java.util.List;

/**
 * Created by solskjaer49 on 2016/11/26 10:31.
 */

public class OnyxDataBindingUtil {

    @BindingAdapter({"viewModel", "menuId"})
    public static void bindMenu(View view, BaseMenuViewModel viewModel, int menuId) {
        viewModel.bindMenu(menuId, view);
    }

    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }

    @BindingAdapter("android:layout_width")
    public static void setLayoutWidth(View view, int width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = width;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter({"imageState"})
    public static void setImageViewState(ImageView imageView, int[] value) {
        imageView.setImageState(value, true);
    }

    @BindingAdapter({"thumbnail"})
    public static void setImageViewBitmap(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
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
    @BindingAdapter("menuActions")
    public static void setMenuActions(PageRecyclerView recyclerView, List menuActions) {
        PageDataBindingAdapter adapter = (PageDataBindingAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setRawData(recyclerView, menuActions);
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
