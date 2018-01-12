package com.onyx.download.onyxdownloadservice;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 12 on 2017/2/9.
 */

public class DownloadPopView extends PopupWindow {

    private View view;
    private RecyclerView recyclerView;
    private DemoAdapter adapter;
    List<DownloadRequest> data = new ArrayList<DownloadRequest>();
    private final LinearLayoutManager linearLayoutManager;
    private final int width;

    public DownloadPopView(Activity context) {
        width = context.getWindowManager().getDefaultDisplay().getWidth();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.activity_demo, null);
        view.setBackgroundResource(R.drawable.stoke_line);
        linearLayoutManager = new LinearLayoutManager(context);
    }

    private void initView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new DemoAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        setContentView(view);
        setWidth(width - (width / Constants.WIDTH_DIVIDER));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(00000000));
        update();
    }

    private void notifyChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void initEvent() {
        DownloadTaskManager.getInstance().setDownloadConnectListener(new DownloadTaskManager.DownloadConnectListener() {
            @Override
            public void connected() {
                notifyChanged();
            }

            @Override
            public void disConnected() {
                notifyChanged();
            }
        });

        if (adapter != null) {
            adapter.setData(data);
        }
    }

    public void show(View parent, List<DownloadRequest> data) {
        this.data = data;
        initData();
        initView();
        initEvent();
        if (!isShowing()) {
            showAsDropDown(parent);
        } else {
            dismiss();
        }
    }

    public void register() {
        DownloadTaskManager.getInstance().registerServiceConnectionListener();
        EventBus.getDefault().register(this);
    }

    public void unregister() {
        DownloadTaskManager.getInstance().unRegisterServiceConnectionListener();
        EventBus.getDefault().unregister(this);
        adapter = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInfoEvent(InfoEvent event) {
        if (adapter != null) {
            adapter.setInfo(event);
        }
    }
}
