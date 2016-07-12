package com.onyx.kreader.ui.actions;

import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.widget.RelativeLayout;
import com.onyx.android.sdk.data.OnyxDictionaryInfo;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.dialog.DialogAnnotation;
import com.onyx.kreader.ui.dialog.PopupSelectionMenu;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by joy on 7/11/16.
 */
public class ShowTextSelectionMenuAction extends BaseAction {

    private static PopupSelectionMenu popupSelectionMenu = null;
    private int x, y;

    public ShowTextSelectionMenuAction(final ReaderActivity readerActivity, final int x, final int y, final PopupSelectionMenu.SelectionType type) {
        switch (type){
            case SingleWordType:
                getTextSelectionPopupMenu(readerActivity).showTranslation();
                break;
            case MultiWordsType:
                getTextSelectionPopupMenu(readerActivity).hideTranslation();
                break;
        }
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute(ReaderActivity readerActivity) {
        getTextSelectionPopupMenu(readerActivity).show();
        getTextSelectionPopupMenu(readerActivity).move(x, y);
    }

    public static void resetSelectionMenu() {
        popupSelectionMenu = null;
    }

    private PopupSelectionMenu getTextSelectionPopupMenu(final ReaderActivity readerActivity) {
        if (popupSelectionMenu == null) {
            popupSelectionMenu = new PopupSelectionMenu(readerActivity, (RelativeLayout) readerActivity.findViewById(R.id.main_view), new PopupSelectionMenu.MenuCallback() {
                @Override
                public void resetSelection() {

                }

                @Override
                public String getSelectionText() {
                    return readerActivity.getReaderUserDataInfo().getHighlightResult().getText();
                }

                @Override
                public void copy() {
                    copyText(readerActivity, getSelectionText());
                    closeMenu();
                }

                @Override
                public void highLight() {
                    ShowTextSelectionMenuAction.this.addAnnotation(readerActivity, "");
                    closeMenu();
                }

                @Override
                public void addAnnotation() {
                    DialogAnnotation dialogAnnotation = new DialogAnnotation(readerActivity, DialogAnnotation.AnnotationAction.add, new DialogAnnotation.Callback() {
                        @Override
                        public void onAddAnnotation(String annotation) {
                            ShowTextSelectionMenuAction.this.addAnnotation(readerActivity, annotation);
                            closeMenu();
                        }

                        @Override
                        public void onUpdateAnnotation(String annotation) {

                        }

                        @Override
                        public void onRemoveAnnotation() {

                        }
                    });
                    dialogAnnotation.show();
                }

                @Override
                public void showDictionary() {
                    String text = getSelectionText();
                    if (StringUtils.isNullOrEmpty(text)) {
                        return;
                    }
                    lookupInDictionary(readerActivity, text);
                    closeMenu();
                }

                @Override
                public boolean supportSelectionMode() {
                    return false;
                }

                @Override
                public void closeMenu() {
                    ShowTextSelectionMenuAction.this.hideTextSelectionPopupWindow(readerActivity, true);
                    readerActivity.redrawPage();
                }
            });
        }
        return popupSelectionMenu;
    }

    public static void hideTextSelectionPopupWindow(final ReaderActivity activity, final boolean clear) {
        hideTextSelectionPopupWindow(activity, clear, false);
    }

    public static void hideTextSelectionPopupWindow(final ReaderActivity activity, final boolean clear, boolean instantRefresh) {
        if (popupSelectionMenu == null) {
            return;
        }
        popupSelectionMenu.hide();
        if (clear) {
            popupSelectionMenu = null;
            activity.getHandlerManager().resetToDefaultProvider();
        }
    }

    private void copyText(final ReaderActivity readerActivity, final String text) {
        ClipboardManager clipboard = (ClipboardManager)readerActivity.getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setText(text);
        }
    }

    private void addAnnotation(final ReaderActivity readerActivity, final String note) {
        ReaderSelection selection = readerActivity.getReaderUserDataInfo().getHighlightResult();
        PageInfo pageInfo = readerActivity.getReaderViewInfo().getPageInfo(selection.getPagePosition());
        new AddAnnotationAction(pageInfo, selection.getStartPosition(), selection.getEndPosition(),
                selection.getRectangles(), selection.getText(), note).execute(readerActivity);
    }

    private void lookupInDictionary(final ReaderActivity activity, final String text) {
        OnyxDictionaryInfo info = OnyxDictionaryInfo.getDefaultDictionary();
        Intent intent = new Intent(info.action).setComponent(new ComponentName(info.packageName, info.className));
        intent.putExtra(info.dataKey, text);

        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e ) {
            e.printStackTrace();
        }
    }

}
