package com.onyx.jdread.reader.highlight;

import com.onyx.android.sdk.reader.api.ReaderSelection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class ReaderSelectionInfo {
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
        for(SelectionInfo readerSelectionInfo : readerSelectionInfos.values()){
            if(readerSelectionInfo.getCurrentSelection() != null){
                result += readerSelectionInfo.getCurrentSelection().getText();
            }
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
