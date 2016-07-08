package com.onyx.kreader.ui.dialog;

import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.TabHost;
import android.widget.TextView;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.ui.view.TreeRecyclerView;
import com.onyx.kreader.R;
import com.onyx.kreader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.kreader.dataprovider.Annotation;
import com.onyx.kreader.dataprovider.Bookmark;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.actions.GotoPageAction;
import com.onyx.kreader.utils.PagePositionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 7/6/16.
 */
public class DialogTableOfContent extends Dialog {

    public enum DirectoryTab { TOC, Bookmark, Annotation }

    private class SimpleListViewItemViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle;
        private TextView textViewDescription;
        private String position;

        public SimpleListViewItemViewHolder(final ReaderActivity readerActivity, final View itemView) {
            super(itemView);

            textViewTitle = (TextView)itemView.findViewById(R.id.text_view_title);
            textViewDescription = (TextView)itemView.findViewById(R.id.text_view_description);

            textViewTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogTableOfContent.this.hide();
                    new GotoPageAction(position).execute(readerActivity);
                }
            });
        }

        public void setTitle(String title) {
            textViewTitle.setText(title);
        }

        public void setDescription(String description) {
            textViewDescription.setText(description);
        }

        public void setPosition(String position) {
            this.position = position;
        }
    }

    public DialogTableOfContent(final ReaderActivity activity, DirectoryTab tab,
                                final ReaderDocumentTableOfContent toc,
                                final List<Bookmark> bookmarks,
                                final List<Annotation> annotations) {
        super(activity);

        setContentView(R.layout.dialog_table_of_content);
        fitDialogToWindow();

        setupTabHost(tab);
        setupToc(activity, toc);
        setupBookmarks(activity, bookmarks);
        setupAnnotations(activity, annotations);
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

    private void setupTabHost(DirectoryTab tab) {
        TabHost tabHost = (TabHost)findViewById(R.id.tab_host);
        tabHost.setup();

        TextView toc = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_table_of_content_tab_indicator_view, null);
        toc.setText(R.string.toc);
        TextView bookmark = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_table_of_content_tab_indicator_view, null);
        bookmark.setText(R.string.bookmark);
        TextView annotation = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_table_of_content_tab_indicator_view, null);
        annotation.setText(R.string.annotation);

        tabHost.addTab(tabHost.newTabSpec(getContext().getResources().getString(R.string.toc)).setIndicator(toc).setContent(R.id.tree_view_toc));
        tabHost.addTab(tabHost.newTabSpec(getContext().getResources().getString(R.string.bookmark)).setIndicator(bookmark).setContent(R.id.list_view_bookmark));
        tabHost.addTab(tabHost.newTabSpec(getContext().getResources().getString(R.string.annotation)).setIndicator(annotation).setContent(R.id.list_view_annotation));

        tabHost.setCurrentTab(getTabIndex(tab));
    }

    private int getTabIndex(DirectoryTab tab) {
        switch (tab) {
            case TOC:
                return 0;
            case Bookmark:
                return 1;
            case Annotation:
                return 2;
            default:
                return 0;
        }
    }

    private void setupToc(final ReaderActivity activity, final ReaderDocumentTableOfContent toc) {
        ArrayList<TreeRecyclerView.TreeNode> rootNodes = buildTreeNodesFromToc(toc);

        TreeRecyclerView view = (TreeRecyclerView)findViewById(R.id.tree_view_toc);
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

        if (toc != null && hasChildren(toc.getRootEntry())) {
            ReaderDocumentTableOfContentEntry entry = locateEntry(toc.getRootEntry().getChildren(), activity.getCurrentPage());
            TreeRecyclerView.TreeNode treeNode = findTreeNodeByTag(rootNodes, entry);
            if (treeNode != null) {
                view.expandTo(treeNode);
            }
        }
    }

    private void setupBookmarks(final ReaderActivity activity, final List<Bookmark> bookmarks) {
        PageRecyclerView view = (PageRecyclerView) findViewById(R.id.list_view_bookmark);
        view.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new SimpleListViewItemViewHolder(activity, LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_table_of_content_list_item_view, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((SimpleListViewItemViewHolder)holder).setTitle(bookmarks.get(position).getQuote());
                ((SimpleListViewItemViewHolder)holder).setDescription(bookmarks.get(position).getPosition());
                ((SimpleListViewItemViewHolder)holder).setPosition(bookmarks.get(position).getPosition());
            }

            @Override
            public int getItemCount() {
                return bookmarks.size();
            }
        });
    }

    private void setupAnnotations(final ReaderActivity activity, final List<Annotation> annotations) {
        PageRecyclerView view = (PageRecyclerView) findViewById(R.id.list_view_annotation);
        view.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new SimpleListViewItemViewHolder(activity, LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_table_of_content_list_item_view, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((SimpleListViewItemViewHolder)holder).setTitle(annotations.get(position).getQuote());
                ((SimpleListViewItemViewHolder)holder).setDescription(annotations.get(position).getPosition());
                ((SimpleListViewItemViewHolder)holder).setPosition(annotations.get(position).getPosition());
            }

            @Override
            public int getItemCount() {
                return annotations.size();
            }
        });
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
