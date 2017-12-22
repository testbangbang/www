package com.onyx.jdread.library.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.library.adapter.PopMenuAdapter;
import com.onyx.jdread.library.model.PopMenuModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by hehai on 17-12-21.
 */

public class MenuPopupWindow extends PopupWindow {

    private View contentView;
    private EventBus eventBus;
    private int width;

    public MenuPopupWindow(Activity context, EventBus eventBus) {
        this.eventBus = eventBus;
        width = context.getWindowManager().getDefaultDisplay().getWidth();
        contentView = View.inflate(context, R.layout.menu_popupwindow_layout, null);
    }

    public void showPopupWindow(View view, List<PopMenuModel> list) {
        setContentView(contentView);
        initView(list);
        setWidth(JDReadApplication.getInstance().getResources().getInteger(R.integer.library_pop_menu_width));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        update();
        showAsDropDown(view);
    }

    private void initView(List<PopMenuModel> list) {
        PageRecyclerView pageRecyclerView = (PageRecyclerView) contentView.findViewById(R.id.pop_menu_recycler);
        pageRecyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        PopMenuAdapter popMenuAdapter = new PopMenuAdapter(list, list.size(), 1);
        popMenuAdapter.setItemListener(new PopMenuAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(Object event) {
                eventBus.post(event);
                dismiss();
            }
        });
        pageRecyclerView.setAdapter(popMenuAdapter);
    }
}
