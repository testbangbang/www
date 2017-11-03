package com.onyx.android.dr.reader.action;

import android.content.ClipboardManager;
import android.content.Context;
import android.widget.RelativeLayout;

import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.activity.ReaderActivity;
import com.onyx.android.dr.reader.data.AnnotationInfo;
import com.onyx.android.dr.reader.dialog.DialogAnnotation;
import com.onyx.android.dr.reader.dialog.DialogDict;
import com.onyx.android.dr.reader.dialog.PopupSelectionMenu;
import com.onyx.android.dr.reader.event.ReaderMainMenuTopSearchEvent;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderSelection;

import org.greenrobot.eventbus.EventBus;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by joy on 7/11/16.
 */
public class ShowTextSelectionMenuAction {

    private static PopupSelectionMenu popupSelectionMenu = null;

    public static void hideTextSelectionPopupMenu(ReaderPresenter readerPresenter) {
        if (popupSelectionMenu != null) {
            readerPresenter.getReaderSelectionManager().clear();
            readerPresenter.getBookOperate().redrawPage();
            popupSelectionMenu.hide();
        }
    }

    public static boolean isSelectionMenuShow() {
        if (popupSelectionMenu != null && popupSelectionMenu.isShow()) {
            return true;
        }
        return false;
    }

    public static void showTextSelectionPopupMenu(ReaderPresenter readerPresenter, boolean isWord, DialogAnnotation.AnnotationAction action) {
        popupSelectionMenu = getTextSelectionPopupMenu(readerPresenter, action);
        if (popupSelectionMenu != null) {
            popupSelectionMenu.show(readerPresenter, isWord);
            popupSelectionMenu.initDictList(readerPresenter);
        }
    }

    public static void resetSelectionMenu() {
        if (popupSelectionMenu != null) {
            popupSelectionMenu.hide();
            popupSelectionMenu = null;
        }
    }

    private static PopupSelectionMenu getTextSelectionPopupMenu(final ReaderPresenter readerPresenter, DialogAnnotation.AnnotationAction action) {
        if (popupSelectionMenu == null) {
            final ReaderActivity readerActivity = (ReaderActivity) readerPresenter.getReaderView().getViewContext();
            popupSelectionMenu = new PopupSelectionMenu(readerPresenter,
                    (RelativeLayout) readerActivity.findViewById(R.id.main_view),
                    action,
                    new PopupSelectionMenu.MenuCallback() {
                        @Override
                        public void resetSelection() {
                            readerPresenter.getReaderSelectionManager().clear();
                        }

                        @Override
                        public String getSelectionText() {
                            return readerPresenter.getBookOperate().getSelectionText();
                        }

                        @Override
                        public void copy() {
                            copyText(readerActivity, getSelectionText());
                            closeMenu();
                        }

                        @Override
                        public void onLineation() {
                            ShowTextSelectionMenuAction.addAnnotation(readerPresenter, "");
                            closeMenu();
                        }

                        @Override
                        public void addAnnotation() {
                            showAnnotationDialog(readerPresenter);
                        }

                        @Override
                        public void showDictionary() {
                            showDictDialog(readerPresenter);
                        }

                        @Override
                        public void startTts() {
                            EventBus.getDefault().post(new ReaderMainMenuTopSearchEvent(getSelectionText()));
                            closeMenu();
                        }

                        @Override
                        public boolean supportSelectionMode() {
                            return false;
                        }

                        @Override
                        public void closeMenu() {
                            hideTextSelectionPopupWindow(readerPresenter, true);
                            readerPresenter.getBookOperate().redrawPage();
                        }

                        @Override
                        public void deleteAnnotation() {
                            readerPresenter.getBookOperate().deleteAnnotation(readerPresenter.getPageAnnotation().getAnnotation(), null);
                            closeMenu();
                        }
                    });
        }
        popupSelectionMenu.setAction(action);
        return popupSelectionMenu;
    }

    private static void showDictDialog(final ReaderPresenter readerPresenter){
        ReaderActivity readerActivity = (ReaderActivity)readerPresenter.getReaderView().getViewContext();
        AnnotationInfo annotationInfo = getAnnotationParam(readerPresenter);
        DialogDict dialogDict = new DialogDict(readerActivity, readerPresenter ,annotationInfo.getQuote());
        dialogDict.show();
        readerPresenter.onDictionaryLookup(annotationInfo.getQuote());
    }

    private static void closeMenu(ReaderPresenter readerPresenter){
        hideTextSelectionPopupWindow(readerPresenter, true);
        readerPresenter.getBookOperate().redrawPage();
    }

    public static void showAnnotationDialog(final ReaderPresenter readerPresenter){
        ReaderActivity readerActivity = (ReaderActivity)readerPresenter.getReaderView().getViewContext();
        AnnotationInfo annotationInfo = getAnnotationParam(readerPresenter);
        DialogAnnotation dialogAnnotation = new DialogAnnotation(readerActivity,
                popupSelectionMenu.getAction(), annotationInfo.getQuote(), annotationInfo.getNote(),
                new DialogAnnotation.Callback() {
                    @Override
                    public void onAddAnnotation(String annotation) {
                        ShowTextSelectionMenuAction.addAnnotation(readerPresenter, annotation);
                        closeMenu(readerPresenter);
                    }

                    @Override
                    public void onUpdateAnnotation(String annotation) {
                        readerPresenter.getBookOperate().updateAnnotation(readerPresenter.getPageAnnotation().getAnnotation(), annotation, null);
                        closeMenu(readerPresenter);
                    }

                    @Override
                    public void onRemoveAnnotation() {
                        readerPresenter.getBookOperate().deleteAnnotation(readerPresenter.getPageAnnotation().getAnnotation(), null);
                        closeMenu(readerPresenter);
                    }
                });
        dialogAnnotation.show();
    }

    private static AnnotationInfo getAnnotationParam(ReaderPresenter readerPresenter){
        AnnotationInfo annotationInfo = new AnnotationInfo();
        annotationInfo.setQuote(readerPresenter.getBookOperate().getSelectionText());

        if (readerPresenter.getPageAnnotation() != null) {
            annotationInfo.setQuote(readerPresenter.getPageAnnotation().getAnnotation().getQuote());
            annotationInfo.setNote(readerPresenter.getPageAnnotation().getAnnotation().getNote());
        }
        return annotationInfo;
    }

    public static void hideTextSelectionPopupWindow(final ReaderPresenter readerPresenter, final boolean clear) {
        hideTextSelectionPopupWindow(readerPresenter, clear, false);
    }

    public static void hideTextSelectionPopupWindow(final ReaderPresenter readerPresenter, final boolean clear, boolean instantRefresh) {
        if (popupSelectionMenu == null) {
            return;
        }
        popupSelectionMenu.hide();
        if (clear) {
            popupSelectionMenu = null;
            readerPresenter.getHandlerManger().close();
        }
    }

    private static void copyText(final Context context, final String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setText(text);
        }
    }

    private static void addAnnotation(final ReaderPresenter readerPresenter, final String note) {
        ReaderSelection selection = readerPresenter.getReaderUserDataInfo().getHighlightResult();
        PageInfo pageInfo = readerPresenter.getReaderViewInfo().getPageInfo(selection.getPagePosition());
        readerPresenter.getBookOperate().addAnnotationAction(pageInfo, selection.getStartPosition(), selection.getEndPosition(),
                selection.getRectangles(), selection.getText(), note);
    }

}
