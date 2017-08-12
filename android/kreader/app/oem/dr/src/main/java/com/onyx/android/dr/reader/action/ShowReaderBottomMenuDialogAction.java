package com.onyx.android.dr.reader.action;

import android.content.ClipboardManager;
import android.content.Context;

import com.onyx.android.dr.reader.activity.ReaderActivity;
import com.onyx.android.dr.reader.data.AnnotationInfo;
import com.onyx.android.dr.reader.dialog.DialogAnnotation;
import com.onyx.android.dr.reader.dialog.DialogDict;
import com.onyx.android.dr.reader.dialog.ReaderBottomDialog;
import com.onyx.android.dr.reader.event.AnnotationsChangeEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuTopSearchEvent;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.common.PageAnnotation;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by hehai on 17-7-24.
 */

public class ShowReaderBottomMenuDialogAction {
    private static ReaderBottomDialog readerBottomDialog = null;

    public static void hideTextSelectionPopupMenu(ReaderPresenter readerPresenter) {
        if (readerBottomDialog != null) {
            readerPresenter.getReaderSelectionManager().clear();
            readerPresenter.getBookOperate().redrawPage();
            readerBottomDialog.dismiss();
        }
    }

    public static boolean isSelectionMenuShow() {
        if (readerBottomDialog != null && readerBottomDialog.isShowing()) {
            return true;
        }
        return false;
    }

    public static void showReaderBottomDialog(ReaderPresenter readerPresenter, boolean isWord, DialogAnnotation.AnnotationAction action) {
        readerBottomDialog = getReaderBottomDialog(readerPresenter, action, isWord);
        if (readerBottomDialog != null) {
            readerBottomDialog.show();
        }
    }

    private static ReaderBottomDialog getReaderBottomDialog(final ReaderPresenter readerPresenter, DialogAnnotation.AnnotationAction action, boolean isWord) {
        if (readerBottomDialog == null) {
            final ReaderActivity readerActivity = (ReaderActivity) readerPresenter.getReaderView().getViewContext();
            readerBottomDialog = new ReaderBottomDialog(readerPresenter,
                    readerActivity, -1, null, isWord, new ReaderBottomDialog.MenuCallback() {
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
                    ShowReaderBottomMenuDialogAction.addAnnotation(readerPresenter, "");
                    closeMenu();
                }

                @Override
                public void addAnnotation() {
                    showAnnotationDialog(readerPresenter, readerBottomDialog.getAction());
                    closeMenu();
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
                    hideReaderBottomDialog(readerPresenter, true);
                }

                @Override
                public void deleteAnnotation() {
                    readerPresenter.getBookOperate().deleteAnnotation(readerPresenter.getPageAnnotation().getAnnotation(), null);
                    closeMenu();
                }
            });
        }
        readerBottomDialog.setAction(action);
        readerBottomDialog.setMode(isWord);
        return readerBottomDialog;
    }

    private static void showDictDialog(final ReaderPresenter readerPresenter) {
        ReaderActivity readerActivity = (ReaderActivity) readerPresenter.getReaderView().getViewContext();
        AnnotationInfo annotationInfo = getAnnotationParam(readerPresenter);
        DialogDict dialogDict = new DialogDict(readerActivity, readerPresenter, annotationInfo.getQuote());
        dialogDict.show();
    }

    private static void closeMenu(ReaderPresenter readerPresenter) {
        hideReaderBottomDialog(readerPresenter, true);
    }

    public static void showAnnotationDialog(final ReaderPresenter readerPresenter, DialogAnnotation.AnnotationAction action) {
        ReaderActivity readerActivity = (ReaderActivity) readerPresenter.getReaderView().getViewContext();
        AnnotationInfo annotationInfo = getAnnotationParam(readerPresenter);
        DialogAnnotation dialogAnnotation = new DialogAnnotation(readerActivity,
                action, annotationInfo.getQuote(), annotationInfo.getNote(),
                new DialogAnnotation.Callback() {
                    @Override
                    public void onAddAnnotation(String annotation) {
                        ShowReaderBottomMenuDialogAction.addAnnotation(readerPresenter, annotation);
                        closeMenu(readerPresenter);
                    }

                    @Override
                    public void onUpdateAnnotation(String annotation) {
                        Annotation an = readerPresenter.getPageAnnotation().getAnnotation();
                        readerPresenter.getBookOperate().updateAnnotation(an, annotation, null);
                        closeMenu(readerPresenter);
                        an.setNote(annotation);
                        AnnotationsChangeEvent event = new AnnotationsChangeEvent(an);
                        EventBus.getDefault().post(event);
                    }

                    @Override
                    public void onRemoveAnnotation() {
                        Annotation an = readerPresenter.getPageAnnotation().getAnnotation();
                        readerPresenter.getBookOperate().deleteAnnotation(an, null);
                        closeMenu(readerPresenter);
                        an.setNote(null);
                        AnnotationsChangeEvent event = new AnnotationsChangeEvent(an);
                        EventBus.getDefault().post(event);
                    }
                });
        dialogAnnotation.show();
    }

    private static AnnotationInfo getAnnotationParam(ReaderPresenter readerPresenter) {
        AnnotationInfo annotationInfo = new AnnotationInfo();
        String selectionText = readerPresenter.getBookOperate().getSelectionText();
        if (StringUtils.isNotBlank(selectionText)) {
            annotationInfo.setQuote(selectionText);
        } else if (readerPresenter.getPageAnnotation() != null) {
            annotationInfo.setQuote(readerPresenter.getPageAnnotation().getAnnotation().getQuote());
            annotationInfo.setNote(readerPresenter.getPageAnnotation().getAnnotation().getNote());
        }
        return annotationInfo;
    }

    public static void hideReaderBottomDialog(final ReaderPresenter readerPresenter, final boolean clear) {
        hideReaderBottomDialog(readerPresenter, clear, false);
    }

    public static void hideReaderBottomDialog(final ReaderPresenter readerPresenter, final boolean clear, boolean instantRefresh) {
        if (readerBottomDialog == null) {
            return;
        }
        readerBottomDialog.dismiss();
        if (clear) {
            readerBottomDialog = null;
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

    public static List<PageAnnotation> getAnnotations(ReaderPresenter readerPresenter) {
        List<PageAnnotation> annotations = new ArrayList<>();
        for (PageInfo pageInfo : readerPresenter.getReaderViewInfo().getVisiblePages()) {
            if (!readerPresenter.getReaderUserDataInfo().hasPageAnnotations(pageInfo)) {
                continue;
            }
            annotations.addAll(readerPresenter.getReaderUserDataInfo().getPageAnnotations(pageInfo));
        }
        return annotations;
    }

    public static void resetReaderBottomDialog() {
        if (readerBottomDialog != null) {
            readerBottomDialog.dismiss();
            readerBottomDialog = null;
        }
    }
}
