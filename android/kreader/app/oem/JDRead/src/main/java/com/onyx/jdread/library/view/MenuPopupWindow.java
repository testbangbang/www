package com.onyx.jdread.library.view;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.library.adapter.PopMenuAdapter;
import com.onyx.jdread.library.model.PopMenuModel;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by hehai on 17-12-21.
 */

public class MenuPopupWindow extends PopupWindow {
    private View contentView;
    private EventBus eventBus;
    private final int screenWidth;
    private PopMenuAdapter popMenuAdapter;
    private boolean showItemDecoration;
    private long lastTime = 0;

    public void setShowItemDecoration(boolean showItemDecoration) {
        this.showItemDecoration = showItemDecoration;
    }

    public MenuPopupWindow(Activity context, EventBus eventBus) {
        this.eventBus = eventBus;
        contentView = View.inflate(context, R.layout.menu_popupwindow_layout, null);
        screenWidth = Utils.getScreenWidth(JDReadApplication.getInstance());
        initView();
    }

    public void showPopupWindow(View view, List<PopMenuModel> list) {
        popMenuAdapter.setData(list, list.size(), 1);
        showAsDropDown(view);
    }

    public void showPopupWindow(View view, List<PopMenuModel> list, int x, int y) {
        popMenuAdapter.setData(list, list.size(), 1);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        showAtLocation(view, Gravity.NO_GRAVITY, R.integer.personal_book_dropdown_menu, y + location[1] + view.getHeight());
    }

    private void initView() {
        setContentView(contentView);
        setWidth((int) (screenWidth * Float.parseFloat(ResManager.getString(R.string.menu_pop_width))));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        update();

        PageRecyclerView pageRecyclerView = (PageRecyclerView) contentView.findViewById(R.id.pop_menu_recycler);
        DashLineItemDivider dashLineItemDivider = new DashLineItemDivider();
        pageRecyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        pageRecyclerView.addItemDecoration(dashLineItemDivider);
        popMenuAdapter = new PopMenuAdapter();
        if (showItemDecoration) {
            DashLineItemDivider dividerItemDecoration = new DashLineItemDivider();
            pageRecyclerView.addItemDecoration(dividerItemDecoration);
        }
        popMenuAdapter = new PopMenuAdapter();
        popMenuAdapter.setItemListener(new PopMenuAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(Object event) {
                long current = System.currentTimeMillis();
                if (lastTime != 0 && current - lastTime < Constants.LIMIT_TIME) {
                    return;
                }
                lastTime = current;
                eventBus.post(event);
                dismiss();
            }
        });
        pageRecyclerView.setAdapter(popMenuAdapter);
    }

    @Override
    public void dismiss() {
        lastTime = 0;
        super.dismiss();
    }
}
