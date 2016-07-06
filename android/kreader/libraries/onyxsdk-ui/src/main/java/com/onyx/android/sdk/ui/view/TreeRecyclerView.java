package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.onyx.android.sdk.ui.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

/**
 * Created by joy on 7/6/16.
 */
public class TreeRecyclerView extends PageRecyclerView {

    public static abstract class Callback {
        public abstract void onTreeNodeClicked(TreeNode node);
    }

    public static class TreeNode {
        private TreeNode parent;
        private int treeDepth;
        private ArrayList<TreeNode> children;
        private String title;
        private String description;
        private Object tag;

        public TreeNode(TreeNode parent, String title, String description, Object tag) {
            this.parent = parent;
            this.title = title;
            this.description = description;
            this.tag = tag;

            treeDepth = parent == null ? 0 : parent.treeDepth + 1;
        }

        public boolean hasChildren() {
            return children != null && children.size() > 0;
        }

        public ArrayList<TreeNode> getChildren() {
            return children;
        }

        public void addChild(TreeNode node) {
            if (children == null) {
                children = new ArrayList<>();
            }
            children.add(node);
        }

        public Object getTag() {
            return tag;
        }
    }

    private static class FlattenTreeNodeDataList {
        private static abstract class Callback {
            public abstract void notifyItemRangeInserted(int position, int size);
            public abstract void notifyItemRangeRemoved(int position, int size);
        }

        private ArrayList<TreeNode> list = new ArrayList<>();
        private HashSet<TreeNode> expandedSet = new HashSet<>();
        private Callback callback;

        public void init(Collection<TreeNode> rootNodes) {
            list.clear();
            expandedSet.clear();
            list.addAll(rootNodes);
        }

        public void registerCallback(Callback callback) {
            this.callback = callback;
        }

        public int size() {
            return list.size();
        }

        public TreeNode get(int index) {
            return list.get(index);
        }

        public boolean isNodeExpanded(TreeNode node) {
            return expandedSet.contains(node);
        }

        public void expand(TreeNode parent) {
            if (!parent.hasChildren()) {
                assert false;
                return;
            }

            int idx = list.indexOf(parent);
            if (idx < 0) {
                return;
            }
            list.addAll(idx + 1, parent.children);
            expandedSet.add(parent);

            if (callback != null) {
                callback.notifyItemRangeInserted(idx + 1, parent.children.size());
            }
        }

        public void expandTo(TreeNode node) {
            Stack<TreeNode> stack = new Stack<>();
            while (node.parent != null) {
                stack.push(node.parent);
                node = node.parent;
            }
            while (!stack.isEmpty()) {
                expand(stack.pop());
            }
        }

        public void collapse(TreeNode parent) {
            if (!isNodeExpanded(parent)) {
                assert false;
                return;
            }

            int idx = list.indexOf(parent);
            if (idx < 0) {
                return;
            }
            int size = summarizeFlattenChildrenSize(parent);
            for (int i = 0; i < size; i++) {
                expandedSet.remove(list.get(idx + 1));
                list.remove(idx + 1);
            }
            expandedSet.remove(parent);

            if (callback != null) {
                callback.notifyItemRangeRemoved(idx + 1, size);
            }
        }

        private int summarizeFlattenChildrenSize(TreeNode parent) {
            int size = parent.children.size();
            for (TreeNode node : parent.children) {
                if (expandedSet.contains(node)) {
                    size += summarizeFlattenChildrenSize(node);
                }
            }
            return size;
        }

        private ArrayList<TreeNode> flatTreeNodes(Collection<TreeNode> nodes) {
            ArrayList<TreeNode> list = new ArrayList<>();
            for (TreeNode node : nodes) {
                list.add(node);
                if (node.hasChildren()) {
                    expandedSet.add(node);
                    list.addAll(flatTreeNodes(node.children));
                }
            }
            return list;
        }
    }

    private static class TreeNodeViewHolder extends ViewHolder {
        private View itemView;
        private Callback callback;

        private ImageView imageViewIndicator;
        private TextView textViewTitle;
        private TextView textViewDescription;

        public TreeNodeViewHolder(View itemView, Callback callback) {
            super(itemView);

            this.itemView = itemView;
            this.callback = callback;

            imageViewIndicator = (ImageView)itemView.findViewById(R.id.image_view_indicator);
            textViewTitle = (TextView)itemView.findViewById(R.id.text_view_title);
            textViewDescription = (TextView)itemView.findViewById(R.id.text_view_description);
        }

        public void bindView(final FlattenTreeNodeDataList list, final int position) {
            final TreeNode node = list.get(position);
            final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)imageViewIndicator.getLayoutParams();
            params.leftMargin = 20 * node.treeDepth;
            imageViewIndicator.setLayoutParams(params);
            textViewTitle.setText(node.title);
            textViewDescription.setText(node.description);

            if (!node.hasChildren()) {
                imageViewIndicator.setVisibility(INVISIBLE);
            } else {
                imageViewIndicator.setVisibility(VISIBLE);
            }
            if (list.isNodeExpanded(node)) {
                imageViewIndicator.setRotation(90);
            } else {
                imageViewIndicator.setRotation(0);
            }
            imageViewIndicator.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.isNodeExpanded(node)) {
                        imageViewIndicator.setRotation(0);
                        list.collapse(node);
                    } else {
                        imageViewIndicator.setRotation(90);
                        list.expand(node);
                    }
                }
            });

            textViewTitle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.onTreeNodeClicked(node);
                    }
                }
            });
        }

    }

    private static class TreeAdapter extends RecyclerView.Adapter<ViewHolder> {

        private FlattenTreeNodeDataList list;
        private Callback callback;

        public TreeAdapter(FlattenTreeNodeDataList list, Callback callback) {
            this.list = list;
            this.callback = callback;

            this.list.registerCallback(new FlattenTreeNodeDataList.Callback() {
                @Override
                public void notifyItemRangeInserted(int position, int size) {
                    TreeAdapter.this.notifyItemRangeInserted(position, size);
                }

                @Override
                public void notifyItemRangeRemoved(int position, int size) {
                    TreeAdapter.this.notifyItemRangeRemoved(position, size);
                }
            });
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tree_recycler_item_view, parent, false);
            return new TreeNodeViewHolder(view, callback);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ((TreeNodeViewHolder)holder).bindView(list, position);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    FlattenTreeNodeDataList list = new FlattenTreeNodeDataList();

    public TreeRecyclerView(Context context) {
        super(context);
    }

    public TreeRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TreeRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void bindTree(Collection<TreeNode> rootNodes, Callback callback) {
        list.init(rootNodes);
        TreeAdapter adapter = new TreeAdapter(list, callback);
        this.setAdapter(adapter);
    }

    public void expandTo(TreeNode node) {
        list.expandTo(node);
    }
}
