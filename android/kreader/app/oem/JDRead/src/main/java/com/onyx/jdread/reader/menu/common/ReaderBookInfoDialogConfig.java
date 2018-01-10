package com.onyx.jdread.reader.menu.common;


import android.content.Context;
import android.content.DialogInterface;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.ui.view.TreeRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.catalog.dialog.DialogAnnotation;
import com.onyx.jdread.reader.catalog.dialog.ReaderBookInfoDialog;

import java.util.ArrayList;

/**
 * Created by huxiaomao on 17/5/17.
 */

public class ReaderBookInfoDialogConfig {
    public static final int CATALOG_MODE = 1;
    public static final int BOOKMARK_MODE = 2;
    public static final int NOTE_MODE = 3;

    public static ArrayList<TreeRecyclerView.TreeNode> buildTreeNodesFromToc(ReaderDocumentTableOfContent toc) {
        ArrayList<TreeRecyclerView.TreeNode> nodes = new ArrayList<>();
        if (toc != null && toc.getRootEntry().getChildren() != null) {
            for (ReaderDocumentTableOfContentEntry entry : toc.getRootEntry().getChildren()) {
                nodes.add(buildTreeNode(null, entry));
            }
        }
        return nodes;
    }

    private static TreeRecyclerView.TreeNode buildTreeNode(TreeRecyclerView.TreeNode parent, ReaderDocumentTableOfContentEntry entry) {
        String pageName = PagePositionUtils.getPageNumberForDisplay(entry.getPageName());
        TreeRecyclerView.TreeNode node = new TreeRecyclerView.TreeNode(parent, entry.getTitle(), pageName, entry);
        if (entry.getChildren() != null) {
            for (ReaderDocumentTableOfContentEntry child : entry.getChildren()) {
                node.addChild(buildTreeNode(node, child));
            }
        }
        return node;
    }

    public static void showAnnotationEditDialog(final Context context,final Annotation annotation, final int position) {
        DialogAnnotation dlg = new DialogAnnotation(context,
                DialogAnnotation.AnnotationAction.update,
                annotation.getQuote(),
                annotation.getNote(),
                new DialogAnnotation.Callback() {
                    @Override
                    public void onAddAnnotation(String note) {
                    }

                    @Override
                    public void onUpdateAnnotation(String note) {
                    }

                    @Override
                    public void onRemoveAnnotation() {
                    }
                });
        dlg.show();
    }

    public static void onJump(final String pagePosition) {
    }

    public static void onDelete(final Context context, final int position, final int mode) {

        OnyxCustomDialog.getConfirmDialog(context,
                context.getString(R.string.sure_delete),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mode == BOOKMARK_MODE) {
                        } else {
                        }
                    }
                }, null).show();
    }
}
