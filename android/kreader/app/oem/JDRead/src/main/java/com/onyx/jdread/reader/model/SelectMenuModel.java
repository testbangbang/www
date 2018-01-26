package com.onyx.jdread.reader.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.view.View;
import android.widget.FrameLayout;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.PopupSelectionMenuBinding;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.WordTranslateResultEvent;
import com.onyx.jdread.reader.highlight.HighlightCursor;
import com.onyx.jdread.reader.event.PopupBaidupediaClickEvent;
import com.onyx.jdread.reader.event.PopupCopyClickEvent;
import com.onyx.jdread.reader.event.PopupLineationClickEvent;
import com.onyx.jdread.reader.event.PopupNoteClickEvent;
import com.onyx.jdread.reader.event.PopupTranslationClickEvent;
import com.onyx.jdread.reader.ui.view.HTMLReaderWebView;
import com.onyx.jdread.setting.action.TranslateAction;
import com.onyx.jdread.setting.model.SettingBundle;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/19.
 */

public class SelectMenuModel {
    private ObservableBoolean isShowSelectMenu = new ObservableBoolean(false);
    private ObservableBoolean isShowDictionaryMenu = new ObservableBoolean(false);
    private ObservableField<String> page = new ObservableField<>();
    private View selectMenuRootView;
    private float selectY = 0;
    private PopupSelectionMenuBinding binding;
    private String inputWords = "";
    private EventBus eventBus;

    public ObservableField<String> getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page.set(page);
    }

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

    public void setBinding(PopupSelectionMenuBinding binding,EventBus eventBuss) {
        this.eventBus = eventBuss;
        this.binding = binding;
        this.selectMenuRootView = binding.getRoot();
        this.binding.translateContentView.registerOnOnPageChangedListener(new HTMLReaderWebView.OnPageChangedListener() {
            @Override
            public void onPageChanged(int totalPage, int curPage) {
                updatePageNumber(totalPage,curPage);
            }
        });
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    private void updatePageNumber(int totalPage, int curPage){
        setPage(curPage + "/" + totalPage);
    }

    public void onLineationClick() {
        getEventBus().post(new PopupLineationClickEvent());
        setIsShowSelectMenu(false);
    }

    public void onNoteClick() {
        getEventBus().post(new PopupNoteClickEvent());
        setIsShowSelectMenu(false);
    }

    public void onCopyClick() {
        getEventBus().post(new PopupCopyClickEvent());
        setIsShowSelectMenu(false);
    }

    public void onTranslationClick() {
        getEventBus().post(new PopupTranslationClickEvent());
        setIsShowSelectMenu(false);
    }

    public void onBaidupediaClick() {
        getEventBus().post(new PopupBaidupediaClickEvent());
        setIsShowSelectMenu(false);
    }

    public void requestLayoutView(ReaderDataHolder readerDataHolder,boolean isDictionary) {
        String pagePosition = readerDataHolder.getCurrentPagePosition();
        HighlightCursor beginHighlightCursor = readerDataHolder.getReaderSelectionInfo().getHighlightCursor(pagePosition, HighlightCursor.BEGIN_CURSOR_INDEX);
        HighlightCursor endHighlightCursor = readerDataHolder.getReaderSelectionInfo().getHighlightCursor(pagePosition, HighlightCursor.END_CURSOR_INDEX);
        if (beginHighlightCursor == null || endHighlightCursor == null) {
            return;
        }

        final float screenHeight = readerDataHolder.getReaderTouchHelper().getContentHeight();
        final float screenWidth = readerDataHolder.getReaderTouchHelper().getContentWidth();

        final float dictMeasuredHeight = readerDataHolder.getAppContext().getResources().getDimension(R.dimen.reader_select_menu_translate_height);
        final float selectTitleHeight = readerDataHolder.getAppContext().getResources().getDimension(R.dimen.reader_popup_select_menu_height);

        final float measureWidth = readerDataHolder.getAppContext().getResources().getDimension(R.dimen.reader_select_menu_width);
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
            updateSelectMenuViewPotion(readerDataHolder,isDictionary,x,diffTop - dividerHeight - measuredHeight);
            return;
        }

        if ((diffBottom + measuredHeight + dividerHeight) < screenHeight) {
            updateSelectMenuViewPotion(readerDataHolder,isDictionary,x,diffBottom + dividerHeight);
            return;
        }

        updateSelectMenuViewPotion(readerDataHolder,isDictionary,x,screenHeight / 2);
    }

    private void updateSelectMenuViewPotion(ReaderDataHolder readerDataHolder,boolean isDictionary,float x,float y) {
        isSearch(readerDataHolder,isDictionary);
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

    private void isSearch(ReaderDataHolder readerDataHolder,boolean isDictionary){
        if(isDictionary) {
            String text = readerDataHolder.getReaderSelectionInfo().getSelectText();
            if(text != null && !text.equals(inputWords)){
                inputWords = text;
                translate();
            }
        }
    }

    private void translate() {
        final TranslateAction action = new TranslateAction(inputWords);
        action.execute(SettingBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                WordTranslateResultEvent event = new WordTranslateResultEvent(action.getTranslateResult());
                eventBus.post(event);
            }
        });
    }

    public void updateTranslateResult(String result){
        binding.translateContentView.loadData(result, "text/html; charset=UTF-8", null);
    }
}
