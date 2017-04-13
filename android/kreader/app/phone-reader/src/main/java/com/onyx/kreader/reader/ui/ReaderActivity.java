package com.onyx.kreader.reader.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.onyx.kreader.reader.data.ReaderDataHolder;

/**
 * Created by ming on 2017/4/13.
 */

public class ReaderActivity extends AppCompatActivity {

    private ReaderDataHolder readerDataHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponents();
    }

    private void initComponents() {
        initReaderDataHolder();
    }

    private void initReaderDataHolder() {
        readerDataHolder = new ReaderDataHolder(this);
        readerDataHolder.registerEventBus(this);
    }

    public ReaderDataHolder getReaderDataHolder() {
        return readerDataHolder;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        readerDataHolder.unRegisterEventBus(this);
        super.onDestroy();
    }
}
