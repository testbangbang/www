package com.onyx.android.plato.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.adapter.ScribbleToolPopupAdapter;
import com.onyx.android.plato.bean.ScribbleToolBean;

import java.util.List;

/**
 * Created by li on 2017/12/12.
 */

public class ScribbleToolPopupWindow extends PopupWindow {
    private Activity activity;
    private final View view;
    private ScribbleToolPopupAdapter adapter;

    public ScribbleToolPopupWindow(Activity activity) {
        this.activity = activity;
        LayoutInflater inflate = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflate.inflate(R.layout.scribble_popup_window_layout, null);
        setContentView(view);
        initView();
    }

    public void show(View parent, List<ScribbleToolBean> tools) {
        adapter.setTools(tools);
        if (!isShowing()) {
            showAsDropDown(parent, 0, SunApplication.getInstance().getResources().getInteger(R.integer.scribble_tool_menu_offset_y));
        } else {
            dismiss();
        }
    }

    private void initView() {
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(00000000));
        update();

        PageRecyclerView recycler = (PageRecyclerView) view.findViewById(R.id.scribble_tool_recycle);
        recycler.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(SunApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(false);
        recycler.addItemDecoration(dividerItemDecoration);
        adapter = new ScribbleToolPopupAdapter();
        recycler.setAdapter(adapter);
    }
}
