package com.onyx.android.dr.interfaces;

import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public interface GoodSentenceView {
    void setGoodSentenceData(List<GoodSentenceNoteEntity> goodSentenceList, ArrayList<Boolean> checkList);
    void setGoodSentenceByTime(List<GoodSentenceNoteEntity> goodSentenceList, ArrayList<Boolean> checkList);
}
