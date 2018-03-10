package com.onyx.jdread.reader.highlight;

import android.util.Log;

import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.jdread.reader.utils.MapKeyComparator;
import com.onyx.jdread.shop.common.RequestKeyComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class ReaderSelectionInfo {
    private static final String TAG = ReaderSelectionInfo.class.getSimpleName();
    private Map<String, SelectionInfo> readerSelectionInfos = new HashMap<>();
    private int moveSelectCount = 0;

    public void increaseSelectCount() {
        moveSelectCount++;
    }

    public void decreaseSelectCount() {
        moveSelectCount--;
    }

    public void setMoveSelectCount(int selectCount) {
        this.moveSelectCount = selectCount;
    }

    public int getMoveSelectCount() {
        return moveSelectCount;
    }

    public Map<String, SelectionInfo> getReaderSelectionInfos() {
        return readerSelectionInfos;
    }

    public SelectionInfo getReaderSelectionInfo(String pagePosition) {
        return readerSelectionInfos.get(pagePosition);
    }

    public String getSelectText(){
        String result = "";
        List<Map.Entry<String, SelectionInfo>> list = new ArrayList<Map.Entry<String, SelectionInfo>>(readerSelectionInfos.entrySet());
        Collections.sort(list, new MapKeyComparator());
        for (int i = 0; i < list.size(); i++) {
            Map.Entry<String, SelectionInfo> stringStringEntry = list.get(i);
            SelectionInfo readerSelectionInfo = stringStringEntry.getValue();
            result += readerSelectionInfo.getCurrentSelection().getText();
        }
        return result;
    }

    public void clear() {
        readerSelectionInfos.clear();
    }

    public ReaderSelection getCurrentSelection(String pagePosition) {
        SelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            return readerSelectionInfo.getCurrentSelection();
        }
        return null;
    }

    public HighlightCursor getHighlightCursor(String pagePosition, int index) {
        SelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            if (index >= 0 && index < readerSelectionInfo.getCursors().size()) {
                return readerSelectionInfo.getCursors().get(index);
            }
        }
        return null;
    }

    public synchronized  void updateSelectInfo(Map<String, SelectionInfo> readerSelectionInfos){
        this.readerSelectionInfos.clear();
        this.readerSelectionInfos.putAll(readerSelectionInfos);
    }
}
