package com.onyx.kreader.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onyx.kreader.ui.KReaderApp;

/**
 * Created by li on 2017/8/11.
 */

public class BookInfoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String bookName = intent.getStringExtra("bookName");
        String bookId = intent.getStringExtra("bookId");
        Log.d("----------", "onReceive22222: "+bookName +bookId);
        KReaderApp.instance().setBookName(bookName);
        KReaderApp.instance().setBookId(bookId);
    }
}
