package com.onyx.kreader.ui.actions;

import android.content.*;
import android.widget.RelativeLayout;
import com.onyx.android.sdk.data.OnyxDictionaryInfo;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogAnnotation;
import com.onyx.kreader.ui.dialog.PopupSelectionMenu;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by joy on 7/11/16.
 */
public class ShowTextSelectionMenuAction{

    private static PopupSelectionMenu popupSelectionMenu = null;

    public static void hideTextSelectionPopupMenu(){
        if (popupSelectionMenu != null) {
            popupSelectionMenu.hide();
        }
    }

    public static void showTextSelectionPopupMenu(ReaderDataHolder readerDataHolder, boolean isWord) {
        popupSelectionMenu =  getTextSelectionPopupMenu(readerDataHolder);
        if (popupSelectionMenu != null) {
            popupSelectionMenu.show(readerDataHolder, isWord);
        }
    }

    public static void resetSelectionMenu() {
        if (popupSelectionMenu != null) {
            popupSelectionMenu.hide();
            popupSelectionMenu = null;
        }
    }

    private static PopupSelectionMenu getTextSelectionPopupMenu(final ReaderDataHolder readerDataHolder) {
        if (popupSelectionMenu == null) {
            final ReaderActivity readerActivity = (ReaderActivity)readerDataHolder.getContext();
            popupSelectionMenu = new PopupSelectionMenu(readerDataHolder, (RelativeLayout) readerActivity.findViewById(R.id.main_view), new PopupSelectionMenu.MenuCallback() {
                @Override
                public void resetSelection() {
                    readerDataHolder.getSelectionManager().clear();
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
                    ShowTextSelectionMenuAction.addAnnotation(readerDataHolder, "");
                    closeMenu();
                }

                @Override
                public void addAnnotation() {
                    DialogAnnotation dialogAnnotation = new DialogAnnotation(readerActivity, DialogAnnotation.AnnotationAction.add, new DialogAnnotation.Callback() {
                        @Override
                        public void onAddAnnotation(String annotation) {
                            ShowTextSelectionMenuAction.addAnnotation(readerDataHolder, annotation);
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
                    lookupInDictionary(readerActivity, readerDataHolder, text);
                    closeMenu();
                }

                @Override
                public void startTts() {
                    closeMenu();

                    ReaderSelection readerSelection = readerDataHolder.getReaderUserDataInfo().getHighlightResult();
                    String startPosition = readerSelection == null ? null : readerSelection.getStartPosition();
                    new StartTtsAction(startPosition).execute(readerDataHolder, null);
                }

                @Override
                public boolean supportSelectionMode() {
                    return false;
                }

                @Override
                public void closeMenu() {
                    hideTextSelectionPopupWindow(readerDataHolder, true);
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

    private static void copyText(final Context context, final String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setText(text);
        }
    }

    private static void addAnnotation(final ReaderDataHolder readerDataHolder, final String note) {
        ReaderSelection selection = readerDataHolder.getReaderUserDataInfo().getHighlightResult();
        PageInfo pageInfo = readerDataHolder.getReaderViewInfo().getPageInfo(selection.getPagePosition());
        new AddAnnotationAction(pageInfo, selection.getStartPosition(), selection.getEndPosition(),
                selection.getRectangles(), selection.getText(), note).execute(readerDataHolder, null);
    }

    private static void lookupInDictionary(final ReaderActivity activity, final ReaderDataHolder readerDataHolder, String text) {
        readerDataHolder.onDictionaryLookup(text);
        text = StringUtils.trim(text);
        text = StringUtils.trimPunctuation(text);
        OnyxDictionaryInfo info = OnyxDictionaryInfo.getDefaultDictionary();
        Intent intent = new Intent(info.action).setComponent(new ComponentName(info.packageName, info.className));
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra(info.action, text);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e ) {
            e.printStackTrace();
        }
    }
}
