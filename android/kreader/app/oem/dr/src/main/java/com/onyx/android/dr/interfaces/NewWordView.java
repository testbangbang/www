package com.onyx.android.dr.interfaces;

import com.onyx.android.dr.data.database.NewWordNoteBookEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public interface NewWordView {
    void setNewWordData(List<NewWordNoteBookEntity> dataList, ArrayList<Boolean> checkList);
    void setHtmlTitleData(ArrayList<String> dataList);
}
