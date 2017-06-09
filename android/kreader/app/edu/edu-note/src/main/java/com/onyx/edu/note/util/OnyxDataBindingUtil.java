package com.onyx.edu.note.util;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.edu.note.data.NoteModel;
import com.onyx.edu.note.manager.ManagerFragment;

import java.util.List;

/**
 * Created by solskjaer49 on 2016/11/26 10:31.
 */

public class OnyxDataBindingUtil {
    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
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
    public static void setItems(PageRecyclerView recyclerView, List<NoteModel> items) {
        ManagerFragment.ManagerAdapter adapter = (ManagerFragment.ManagerAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setRawData(items, recyclerView.getContext());
        }
    }
}
