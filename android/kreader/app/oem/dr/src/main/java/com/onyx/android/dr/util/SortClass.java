package com.onyx.android.dr.util;

import com.onyx.android.dr.data.database.NewWordNoteBookEntity;

import java.util.Comparator;

/**
 * Created by zhouzhiming on 2017/8/19.
 */
public class SortClass implements Comparator {
    public int compare(Object arg0, Object arg1) {
        NewWordNoteBookEntity user0 = (NewWordNoteBookEntity) arg0;
        NewWordNoteBookEntity user1 = (NewWordNoteBookEntity) arg1;
        String s1 = String.valueOf(user0.currentTime);
        int flag = String.valueOf(user1.currentTime).compareTo(s1);
        return flag;
    }
}
