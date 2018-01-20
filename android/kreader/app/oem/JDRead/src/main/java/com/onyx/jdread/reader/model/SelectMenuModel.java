package com.onyx.jdread.reader.model;

import android.databinding.ObservableBoolean;
import android.graphics.RectF;
import android.view.View;

import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.highlight.HighlightCursor;

/**
 * Created by huxiaomao on 2018/1/19.
 */

public class SelectMenuModel {
    private ObservableBoolean isShowSelectMenu = new ObservableBoolean(false);
    private ObservableBoolean isShowDictionaryMenu = new ObservableBoolean(false);
    private View selectMenuRootView;

    public ObservableBoolean getIsShowSelectMenu() {
        return isShowSelectMenu;
    }

    public void setIsShowSelectMenu(boolean isShowSelectMenu) {
        this.isShowSelectMenu.set(isShowSelectMenu);
    }

    public ObservableBoolean getIsShowDictionaryMenu() {
        return isShowDictionaryMenu;
    }

    public void setIsShowDictionaryMenu(boolean isShowDictionaryMenu) {
        this.isShowDictionaryMenu.set(isShowDictionaryMenu);
    }

    public void setSelectMenuRootView(View selectMenuRootView) {
        this.selectMenuRootView = selectMenuRootView;
    }

    public void requestLayoutView(ReaderDataHolder readerDataHolder){
        String pagePosition = readerDataHolder.getCurrentPagePosition();
        HighlightCursor beginHighlightCursor = readerDataHolder.getReaderSelectionManager().getHighlightCursor(pagePosition,HighlightCursor.BEGIN_CURSOR_INDEX);
        HighlightCursor endHighlightCursor = readerDataHolder.getReaderSelectionManager().getHighlightCursor(pagePosition,HighlightCursor.END_CURSOR_INDEX);
        if (beginHighlightCursor == null || endHighlightCursor == null) {
            return;
        }
        RectF beginCursorRectF = beginHighlightCursor.getDisplayRect();
        RectF endCursorRectF = endHighlightCursor.getDisplayRect();
        int measuredHeight = selectMenuRootView.getMeasuredHeight();


        RectF start = beginCursorRectF;
        RectF end = endCursorRectF;
        final float screenHeight = readerDataHolder.getReaderTouchHelper().getSurfaceView().getHeight();
        final float diffTop = start.top;
        final float diffBottom = end.bottom;

        final float dividerHeight = 20;

        if(diffTop - measuredHeight - dividerHeight > 0){
            selectMenuRootView.setY(diffTop - dividerHeight - measuredHeight);
            return;
        }

        if(diffBottom + measuredHeight + dividerHeight < screenHeight){
            selectMenuRootView.setY(diffBottom + dividerHeight + measuredHeight);
            return;
        }

        selectMenuRootView.setY(screenHeight / 2);
    }
}
