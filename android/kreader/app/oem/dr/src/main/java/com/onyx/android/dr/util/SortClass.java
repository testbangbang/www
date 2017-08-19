package com.onyx.android.dr.util;

import com.onyx.android.dr.data.database.NewWordNoteBookEntity;

import java.util.Comparator;

/**
 * Created by zhouzhiming on 2017/8/19.
 */
public class SortClass implements Comparator {

    public int compare(Object arg0, Object arg1) {
        NewWordNoteBookEntity firstBean = (NewWordNoteBookEntity) arg0;
        NewWordNoteBookEntity secondBean = (NewWordNoteBookEntity) arg1;
        String firstCurrentTime = String.valueOf(firstBean.currentTime);
        int flag = String.valueOf(secondBean.currentTime).compareTo(firstCurrentTime);
        return flag;
    }
}
