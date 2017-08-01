package com.onyx.android.dr.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.sdk.ui.view.DisableScrollLinearManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 2017/08/01.
 */

public class CustomPopupWindow extends PopupWindow implements PopupAdapter.OnItemClickListener {

    private int width;
    private List<String> datas = new ArrayList<>();
    private View contentView;
    private PageRecyclerView popupWindowRecycler;
    private PopupAdapter popupAdapter;
    public static final int constant = 10;
    private int offsetY;
    private OnItemClickListener listener;

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public CustomPopupWindow(Activity context) {
        width = context.getWindowManager().getDefaultDisplay().getWidth();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.popup_list, null);
    }

    private void initView() {
        popupWindowRecycler = (PageRecyclerView) contentView.findViewById(R.id.popup_window_recycler);
        popupWindowRecycler.setLayoutManager(new DisableScrollLinearManager(DRApplication.getInstance().getBaseContext()));
        popupAdapter = new PopupAdapter();
        popupAdapter.setOnItemClickListener(this);
        popupAdapter.setList(datas);
        popupWindowRecycler.setAdapter(popupAdapter);
    }

    private void initData() {
        setContentView(contentView);
        setWidth(width / constant);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(00000000));
        update();
    }

    public void showPopupWindow(View parent, List<String> datas) {
        this.datas = datas;
        initData();
        initView();
        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        if (!isShowing()) {
            showAsDropDown(parent);
        } else {
            dismiss();
        }
    }

    @Override
    public void onClick(int position, String string) {
        if (listener!=null){
            listener.onClick(position, string);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position, String string);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
