package com.onyx.einfo.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.onyx.einfo.R;
import com.onyx.android.sdk.data.model.LibraryTableOfContentEntry;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.ui.view.TreeRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;

/**
 * Created by suicheng on 2017/4/29.
 */

public class DialogTableOfLibrary extends OnyxAlertDialog {

    private String title;
    private LibraryTableOfContentEntry tocEntry;

    private TreeRecyclerView treeRecyclerView;
    private TextView pageIndicator;

    private TreeRecyclerView.Callback itemActionCallBack;

    public DialogTableOfLibrary() {
    }

    public DialogTableOfLibrary(LibraryTableOfContentEntry tocEntry, String title) {
        this.tocEntry = tocEntry;
        this.title = title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setParams(new Params().setTittleString(title)
                .setCustomContentLayoutResID(R.layout.alert_dialog_tree_page_view)
                .setCustomLayoutHeight(getCustomLayoutHeight())
                .setEnablePageIndicator(true)
                .setEnableFunctionPanel(false)
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageView) {
                        pageIndicator = pageView;
                        initPageRecyclerView(customView);
                    }
                })
        );
        super.onCreate(savedInstanceState);
    }

    private void initPageRecyclerView(View customView) {
        ArrayList<TreeRecyclerView.TreeNode> rootNodes = buildTreeNodesFromLibraryList(tocEntry);
        treeRecyclerView = (TreeRecyclerView) customView.findViewById(R.id.page_tree_view);
        treeRecyclerView.setDefaultPageKeyBinding();
        treeRecyclerView.bindTree(rootNodes, new TreeRecyclerView.Callback() {
            @Override
            public void onTreeNodeClicked(TreeRecyclerView.TreeNode node) {
                if (node.getTag() == null) {
                    return;
                }
                onItemClickAction(node);
            }

            @Override
            public void onItemCountChanged(int position, int itemCount) {
                onPageChanged();
                onItemCountChangedAction(position, itemCount);
            }
        }, 7);

        treeRecyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                onPageChanged();
            }
        });
        if (!CollectionUtils.isNullOrEmpty(rootNodes)) {
            treeRecyclerView.setCurrentNode(rootNodes.get(0));
        }
        updatePageIndicatorView();
    }

    private void updatePageIndicatorView() {
        int currentPage = treeRecyclerView.getPaginator().getCurrentPage() + 1;
        int totalPage = treeRecyclerView.getPaginator().pages();
        if (totalPage == 0) {
            totalPage = 1;
        }
        pageIndicator.setText(currentPage + "/" + totalPage);
    }

    private void onPageChanged() {
        updatePageIndicatorView();
    }

    private void onItemClickAction(TreeRecyclerView.TreeNode node) {
        if (itemActionCallBack != null) {
            itemActionCallBack.onTreeNodeClicked(node);
        }
    }

    private void onItemCountChangedAction(int position, int itemCount) {
        if (itemActionCallBack != null) {
            itemActionCallBack.onItemCountChanged(position, itemCount);
        }
    }

    private ArrayList<TreeRecyclerView.TreeNode> buildTreeNodesFromLibraryList(LibraryTableOfContentEntry tocEntry) {
        ArrayList<TreeRecyclerView.TreeNode> nodes = new ArrayList<>();
        for (LibraryTableOfContentEntry child : tocEntry.children) {
            nodes.add(buildTreeNode(null, child));
        }
        return nodes;
    }

    private TreeRecyclerView.TreeNode buildTreeNode(TreeRecyclerView.TreeNode parent, LibraryTableOfContentEntry entry) {
        String desc = CollectionUtils.isNullOrEmpty(entry.children) ? "" : String.valueOf(CollectionUtils.getSize(entry.children));
        TreeRecyclerView.TreeNode node = new TreeRecyclerView.TreeNode(parent, entry.library.getName(),
                desc, entry.library);
        if (!CollectionUtils.isNullOrEmpty(entry.children)) {
            for (LibraryTableOfContentEntry child : entry.children) {
                node.addChild(buildTreeNode(node, child));
            }
        }
        return node;
    }

    private int getCustomLayoutHeight() {
        return 640;
    }

    public void setItemActionCallBack(TreeRecyclerView.Callback itemActionCallBack) {
        this.itemActionCallBack = itemActionCallBack;
    }
}
