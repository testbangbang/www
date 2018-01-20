package com.onyx.jdread.reader.model;

import android.databinding.ObservableBoolean;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.onyx.jdread.R;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.highlight.HighlightCursor;
import com.onyx.jdread.reader.event.PopupBaidupediaClickEvent;
import com.onyx.jdread.reader.event.PopupCopyClickEvent;
import com.onyx.jdread.reader.event.PopupLineationClickEvent;
import com.onyx.jdread.reader.event.PopupNoteClickEvent;
import com.onyx.jdread.reader.event.PopupTranslationClickEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/19.
 */

public class SelectMenuModel {
    private ObservableBoolean isShowSelectMenu = new ObservableBoolean(false);
    private ObservableBoolean isShowDictionaryMenu = new ObservableBoolean(false);
    private View selectMenuRootView;
    private float selectY = 0;

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

    public void onLineationClick() {
        EventBus.getDefault().post(new PopupLineationClickEvent());
        setIsShowSelectMenu(false);
    }

    public void onNoteClick() {
        EventBus.getDefault().post(new PopupNoteClickEvent());
        setIsShowSelectMenu(false);
    }

    public void onCopyClick() {
        EventBus.getDefault().post(new PopupCopyClickEvent());
        setIsShowSelectMenu(false);
    }

    public void onTranslationClick() {
        EventBus.getDefault().post(new PopupTranslationClickEvent());
        setIsShowSelectMenu(false);
    }

    public void onBaidupediaClick() {
        EventBus.getDefault().post(new PopupBaidupediaClickEvent());
        setIsShowSelectMenu(false);
    }

    public void requestLayoutView(ReaderDataHolder readerDataHolder,boolean isDictionary) {
        String pagePosition = readerDataHolder.getCurrentPagePosition();
        HighlightCursor beginHighlightCursor = readerDataHolder.getReaderSelectionManager().getHighlightCursor(pagePosition, HighlightCursor.BEGIN_CURSOR_INDEX);
        HighlightCursor endHighlightCursor = readerDataHolder.getReaderSelectionManager().getHighlightCursor(pagePosition, HighlightCursor.END_CURSOR_INDEX);
        if (beginHighlightCursor == null || endHighlightCursor == null) {
            return;
        }

        final float screenHeight = readerDataHolder.getReaderTouchHelper().getSurfaceView().getHeight();
        final float screenWidth = readerDataHolder.getReaderTouchHelper().getSurfaceView().getWidth();

        final float dictMeasuredHeight = screenHeight * 0.4f;
        final float selectTitleHeight = readerDataHolder.getAppContext().getResources().getDimension(R.dimen.reader_popup_select_menu_height);
        final float measureWidth = screenWidth * 0.9f;
        final float x = (screenWidth - measureWidth) / 2;

        float measuredHeight;
        if (isShowDictionaryMenu.get()) {
            setLayoutParams((int) measureWidth, (int) dictMeasuredHeight);
            measuredHeight = dictMeasuredHeight;
        }else{
            setLayoutParams((int) measureWidth, (int) selectTitleHeight);
            measuredHeight = selectTitleHeight;
        }

        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        selectMenuRootView.measure(w, h);

        final float diffTop = beginHighlightCursor.getDisplayRect().top;
        final float diffBottom = endHighlightCursor.getDisplayRect().bottom;

        final float dividerHeight = readerDataHolder.getAppContext().getResources().getDimension(R.dimen.reader_popup_selection_divider_height);
        if ((diffTop - measuredHeight - dividerHeight) > 0) {
            updateSelectMenuViewPotion(x,diffTop - dividerHeight - measuredHeight);
            return;
        }

        if ((diffBottom + measuredHeight + dividerHeight) < screenHeight) {
            updateSelectMenuViewPotion(x,diffBottom + dividerHeight);
            return;
        }

        updateSelectMenuViewPotion(x,screenHeight / 2);
    }

    private void updateSelectMenuViewPotion(float x,float y) {
        if (Math.abs(selectY - y) <= 0) {
            return;
        }
        selectY = y;
        selectMenuRootView.setY(y);
        selectMenuRootView.setX(x);

        selectMenuRootView.post(new Runnable() {
            @Override
            public void run() {
                selectMenuRootView.requestLayout();
            }
        });
    }

    public void setLayoutParams(int w, int h) {
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(w, h);
        selectMenuRootView.setLayoutParams(p);
    }
}
