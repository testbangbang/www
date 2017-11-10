package com.onyx.download.onyxdownloadservice;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 12 on 2017/1/18.
 */

public class DemoActivity extends Activity {

    private RecyclerView recyclerView;
    private DemoAdapter adapter;
    List<DownloadRequest> lists = new ArrayList<DownloadRequest>();
    String[] arr = {"http://mirror.internode.on.net/pub/test/5meg.test5",
            // 6m
            "http://cdn.llsapp.com/android/LLS-v4.0-595-20160908-143200.apk",
            // 8m
            "http://7xjww9.com1.z0.glb.clouddn.com/Hopetoun_falls.jpg",
            // 10m
            "http://dg.101.hk/1.rar",
            // 342m
            "http://180.153.105.144/dd.myapp.com/16891/E2F3DEBB12A049ED921C6257C5E9FB11.apk"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        DownloadTaskManager.getInstance().registerServiceConnectionListener();
        EventBus.getDefault().register(this);
        initData();
        initView();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadTaskManager.getInstance().unRegisterServiceConnectionListener();
        EventBus.getDefault().unregister(this);
        adapter = null;
    }

    public void postNotifyDataChanged() {
        if (adapter != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private void initEvent() {
        DownloadTaskManager.getInstance().setDownloadConnectListener(new DownloadTaskManager.DownloadConnectListener() {
            @Override
            public void connected() {
                postNotifyDataChanged();
            }

            @Override
            public void disConnected() {
                postNotifyDataChanged();
            }
        });

        if(adapter != null){
            adapter.setData(lists);
        }
    }

    private void initData() {
        for (int i = 0; i < arr.length; i++) {
            String substring = arr[i].substring(arr[i].lastIndexOf("/") + 1);
            String path = Environment.getExternalStorageDirectory().getPath() + File.separator + substring;
            DownloadRequest rq = DownloadTaskManager.getInstance().createDownloadRequest(arr[i], path, Constants.WOW+i);
            lists.add(rq);
        }
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DemoAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInfoEvent(InfoEvent event) {
        if(adapter != null){
            adapter.setInfo(event);
        }
    }
}
