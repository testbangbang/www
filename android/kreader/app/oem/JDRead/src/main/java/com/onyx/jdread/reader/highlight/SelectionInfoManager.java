package com.onyx.jdread.reader.highlight;

import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.jdread.reader.actions.CleanSelectionAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class SelectionInfoManager {
    private Map<String, ReaderSelectionInfo> readerSelectionInfos = new HashMap<>();
    private int moveSelectCount = 0;

    public synchronized void incrementSelectCount() {
        moveSelectCount++;
    }

    public synchronized void decrementSelectCount() {
        moveSelectCount--;
    }

    public synchronized void setMoveSelectCount(int selectCount) {
        this.moveSelectCount = selectCount;
    }

    public synchronized int getMoveSelectCount() {
        return moveSelectCount;
    }

    public Map<String, ReaderSelectionInfo> getReaderSelectionInfos() {
        return readerSelectionInfos;
    }

    public ReaderSelectionInfo getReaderSelectionInfo(String pagePosition) {
        return readerSelectionInfos.get(pagePosition);
    }

    public String getSelectText(){
        String result = "";
        for(ReaderSelectionInfo readerSelectionInfo : readerSelectionInfos.values()){
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
        ReaderSelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            return readerSelectionInfo.getCurrentSelection();
        }
        return null;
    }

    public HighlightCursor getHighlightCursor(String pagePosition, int index) {
        ReaderSelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            if (index >= 0 && index < readerSelectionInfo.getCursors().size()) {
                return readerSelectionInfo.getCursors().get(index);
            }
        }
        return null;
    }

    public synchronized  void updateSelectInfo(Map<String, ReaderSelectionInfo> readerSelectionInfos){
        this.readerSelectionInfos.clear();
        this.readerSelectionInfos.putAll(readerSelectionInfos);
    }
}
