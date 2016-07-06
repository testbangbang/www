package com.onyx.kreader.ui.dialog;

import android.app.Dialog;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.onyx.android.sdk.ui.view.TreeRecyclerView;
import com.onyx.kreader.R;
import com.onyx.kreader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.actions.GotoPageAction;
import com.onyx.kreader.utils.PagePositionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 7/6/16.
 */
public class DialogTableOfContent extends Dialog {

    public DialogTableOfContent(final ReaderActivity activity, final ReaderDocumentTableOfContent toc) {
        super(activity);

        setContentView(R.layout.dialog_table_of_content);
        fitDialogToWindow();

        ArrayList<TreeRecyclerView.TreeNode> rootNodes = buildTreeNodesFromToc(toc);

        TreeRecyclerView view = (TreeRecyclerView)findViewById(R.id.treeview);
        view.bindTree(rootNodes, new TreeRecyclerView.Callback() {
            @Override
            public void onTreeNodeClicked(TreeRecyclerView.TreeNode node) {
                ReaderDocumentTableOfContentEntry entry = (ReaderDocumentTableOfContentEntry)node.getTag();
                if (entry == null) {
                    return;
                }
                if (PagePositionUtils.isValidPosition(entry.getPosition())) {
                    DialogTableOfContent.this.hide();
                    new GotoPageAction(entry.getPosition()).execute(activity);
                }
            }
        });

        if (hasChildren(toc.getRootEntry())) {
            ReaderDocumentTableOfContentEntry entry = locateEntry(toc.getRootEntry().getChildren(), activity.getCurrentPage());
            TreeRecyclerView.TreeNode treeNode = findTreeNodeByTag(rootNodes, entry);
            if (treeNode != null) {
                view.expandTo(treeNode);
            }
        }
    }

    private void fitDialogToWindow() {
        Window mWindow = getWindow();
        WindowManager.LayoutParams mParams = mWindow.getAttributes();
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.gravity = Gravity.BOTTOM;
        mWindow.setAttributes(mParams);
        //force use all space in the screen.
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private ReaderDocumentTableOfContentEntry locateEntry(List<ReaderDocumentTableOfContentEntry> entries, int page) {
        for (int i = 0; i < entries.size() - 1; i++) {
            ReaderDocumentTableOfContentEntry current = entries.get(i);
            int currentPage = PagePositionUtils.getPageNumber(current.getPosition());
            int nextPage = PagePositionUtils.getPageNumber(entries.get(i + 1).getPosition());
            if (currentPage <= page && page < nextPage) {
                return locateEntryWithChildren(current, page);
            }
        }

        ReaderDocumentTableOfContentEntry current = entries.get(entries.size() - 1);
        return locateEntryWithChildren(current, page);
    }

    private ReaderDocumentTableOfContentEntry locateEntryWithChildren(ReaderDocumentTableOfContentEntry entry, int page) {
        int currentPage = PagePositionUtils.getPageNumber(entry.getPosition());
        if (!hasChildren(entry)) {
            return entry;
        }
        int firstChildPage = PagePositionUtils.getPageNumber(entry.getChildren().get(0).getPosition());
        if (currentPage <= page && page < firstChildPage) {
            return entry;
        }
        return locateEntry(entry.getChildren(), page);
    }

    private TreeRecyclerView.TreeNode findTreeNodeByTag(List<TreeRecyclerView.TreeNode> nodeList, ReaderDocumentTableOfContentEntry entry) {
        for (TreeRecyclerView.TreeNode node : nodeList) {
            if (node.getTag() == entry) {
                return node;
            }
            if (node.hasChildren()) {
                TreeRecyclerView.TreeNode find = findTreeNodeByTag(node.getChildren(), entry);
                if (find != null) {
                    return find;
                }
            }
        }
        return null;
    }

    private boolean hasChildren(ReaderDocumentTableOfContentEntry entry) {
        return entry.getChildren() != null && entry.getChildren().size() > 0;
    }

    private ArrayList<TreeRecyclerView.TreeNode> buildTreeNodesFromToc(ReaderDocumentTableOfContent toc) {
        ArrayList<TreeRecyclerView.TreeNode> nodes = new ArrayList<>();
        if (toc != null && toc.getRootEntry().getChildren() != null) {
            for (ReaderDocumentTableOfContentEntry entry : toc.getRootEntry().getChildren()) {
                nodes.add(buildTreeNode(null, entry));
            }
        }
        return nodes;
    }

    private TreeRecyclerView.TreeNode buildTreeNode(TreeRecyclerView.TreeNode parent, ReaderDocumentTableOfContentEntry entry) {
        int page = PagePositionUtils.getPageNumber(entry.getPosition());
        String pos = page < 0 ? "" : String.valueOf(page + 1);
        TreeRecyclerView.TreeNode node = new TreeRecyclerView.TreeNode(parent, entry.getTitle(), pos, entry);
        if (entry.getChildren() != null) {
            for (ReaderDocumentTableOfContentEntry child : entry.getChildren()) {
                node.addChild(buildTreeNode(node, child));
            }
        }
        return node;
    }

}
