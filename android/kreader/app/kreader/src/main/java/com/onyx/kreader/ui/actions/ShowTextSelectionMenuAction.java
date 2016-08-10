package com.onyx.kreader.ui.actions;

import android.content.*;
import android.widget.RelativeLayout;
import com.onyx.android.sdk.data.OnyxDictionaryInfo;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogAnnotation;
import com.onyx.kreader.ui.dialog.PopupSelectionMenu;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by joy on 7/11/16.
 */
public class ShowTextSelectionMenuAction extends BaseAction {

    private static PopupSelectionMenu popupSelectionMenu = null;
    private PopupSelectionMenu.SelectionType selectionType;
    private int x, y;
    private ReaderActivity readerActivity;

    public ShowTextSelectionMenuAction(final ReaderDataHolder readerDataHolder, final int x, final int y, final PopupSelectionMenu.SelectionType type) {
        readerActivity = (ReaderActivity)readerDataHolder.getContext();
        selectionType = type;
        switch (type){
            case SingleWordType:
                getTextSelectionPopupMenu(readerDataHolder).showTranslation();
                break;
            case MultiWordsType:
                getTextSelectionPopupMenu(readerDataHolder).hideTranslation();
                break;
        }
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        if (selectionType.equals(PopupSelectionMenu.SelectionType.MultiWordsType)){
            getTextSelectionPopupMenu(readerDataHolder).move(x, y);
        }else {
            getTextSelectionPopupMenu(readerDataHolder).show();
        }
    }

    public static void resetSelectionMenu() {
        popupSelectionMenu = null;
    }

    private PopupSelectionMenu getTextSelectionPopupMenu(final ReaderDataHolder readerDataHolder) {
        if (popupSelectionMenu == null) {
            popupSelectionMenu = new PopupSelectionMenu(readerDataHolder, (RelativeLayout) readerActivity.findViewById(R.id.main_view), new PopupSelectionMenu.MenuCallback() {
                @Override
                public void resetSelection() {

                }

                @Override
                public String getSelectionText() {
                    ReaderSelection readerSelection = readerDataHolder.getReaderUserDataInfo().getHighlightResult();
                    return readerSelection != null ? readerSelection.getText() : "";
                }

                @Override
                public void copy() {
                    copyText(readerActivity, getSelectionText());
                    closeMenu();
                }

                @Override
                public void highLight() {
                    ShowTextSelectionMenuAction.this.addAnnotation(readerDataHolder, "");
                    closeMenu();
                }

                @Override
                public void addAnnotation() {
                    DialogAnnotation dialogAnnotation = new DialogAnnotation(readerActivity, DialogAnnotation.AnnotationAction.add, new DialogAnnotation.Callback() {
                        @Override
                        public void onAddAnnotation(String annotation) {
                            ShowTextSelectionMenuAction.this.addAnnotation(readerDataHolder, annotation);
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
                    ShowTextSelectionMenuAction.this.hideTextSelectionPopupWindow(readerDataHolder, true);
                    readerDataHolder.redrawPage();
                }
            });
        }
        return popupSelectionMenu;
    }

    public static void hideTextSelectionPopupWindow(final ReaderDataHolder readerDataHolder, final boolean clear) {
        hideTextSelectionPopupWindow(readerDataHolder, clear, false);
    }

    public static void hideTextSelectionPopupWindow(final ReaderDataHolder readerDataHolder, final boolean clear, boolean instantRefresh) {
        if (popupSelectionMenu == null) {
            return;
        }
        popupSelectionMenu.hide();
        if (clear) {
            popupSelectionMenu = null;
            readerDataHolder.getHandlerManager().resetToDefaultProvider();
        }
    }

    private void copyText(final Context context, final String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setText(text);
        }
    }

    private void addAnnotation(final ReaderDataHolder readerDataHolder, final String note) {
        ReaderSelection selection = readerDataHolder.getReaderUserDataInfo().getHighlightResult();
        PageInfo pageInfo = readerDataHolder.getReaderViewInfo().getPageInfo(selection.getPagePosition());
        new AddAnnotationAction(pageInfo, selection.getStartPosition(), selection.getEndPosition(),
                selection.getRectangles(), selection.getText(), note).execute(readerDataHolder);
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
