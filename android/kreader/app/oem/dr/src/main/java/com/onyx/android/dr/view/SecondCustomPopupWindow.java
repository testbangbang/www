package com.onyx.android.dr.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.SecondPopupAdapter;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.sdk.ui.view.DisableScrollLinearManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/29.
 */
public class SecondCustomPopupWindow extends PopupWindow implements SecondPopupAdapter.OnItemClickListener {
    private int height;
    private int width;
    private List<String> datas = new ArrayList<>();
    private View contentView;
    private PageRecyclerView popupWindowRecycler;
    private SecondPopupAdapter popupAdapter;
    public static final float secondProportion = 0.6f;
    public static final float firstProportion  = 0.3f;
    private int offsetY;
    private OnItemClickListener listener;
    private int type;
    private DividerItemDecoration dividerItemDecoration;

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public SecondCustomPopupWindow(Activity context) {
        width = context.getWindowManager().getDefaultDisplay().getWidth();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.popup_list, null);
        setContentView(contentView);
    }

    private void initView() {
        popupWindowRecycler = (PageRecyclerView) contentView.findViewById(R.id.popup_window_recycler);
        popupWindowRecycler.setLayoutManager(new DisableScrollLinearManager(DRApplication.getInstance().getBaseContext()));
        dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        popupAdapter = new SecondPopupAdapter();
        popupAdapter.setOnItemClickListener(this);
        popupAdapter.setList(datas);
        popupWindowRecycler.setAdapter(popupAdapter);
        popupWindowRecycler.addItemDecoration(dividerItemDecoration);
    }

    private void initData() {
        if (type == Constants.SCHOOL_CHILDREN) {
            setWidth((int) (firstProportion * width));
        }else if (type == Constants.IDENTITY) {
            setWidth((int) (secondProportion * width));
        }
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(00000000));
        update();
    }

    public void showPopupWindow(View parent, List<String> datas, int type) {
        this.datas = datas;
        this.type = type;
        initView();
        initData();
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
        if (listener != null) {
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
