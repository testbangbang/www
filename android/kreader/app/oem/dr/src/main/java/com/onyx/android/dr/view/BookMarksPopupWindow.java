package com.onyx.android.dr.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.BookMarksAdapter;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.sdk.data.model.v2.CommentsBean;

import java.util.List;

/**
 * Created by li on 2017/9/22.
 */

public class BookMarksPopupWindow extends PopupWindow {
    private Activity context;
    private final int width;
    private BookMarksAdapter bookMarksAdapter;
    private final int height;
    private final View view;
    private List<CommentsBean> data;

    public BookMarksPopupWindow(Activity context) {
        this.context = context;
        width = context.getWindowManager().getDefaultDisplay().getWidth();
        height = context.getWindowManager().getDefaultDisplay().getHeight();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.book_marks_layout, null);
    }

    private void initData(View view) {
        setContentView(view);
        setWidth((int) (width * 0.8));
        setHeight((int) (height * 0.8));
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(00000000));
        update();
    }

    private void initView(View view) {
        PageRecyclerView recyclerView = (PageRecyclerView) view.findViewById(R.id.book_marks_recycler);
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        recyclerView.addItemDecoration(dividerItemDecoration);
        bookMarksAdapter = new BookMarksAdapter();
        recyclerView.setAdapter(bookMarksAdapter);
        if (data != null && data.size() > 0) {
            bookMarksAdapter.setData(data);
        }
    }

    public void show(View parent, List<CommentsBean> data) {
        this.data = data;
        initData(view);
        initView(view);

        if (!isShowing()) {
            showAtLocation(parent, Gravity.CENTER, 0, 0);
        } else {
            dismiss();
        }
    }
}
