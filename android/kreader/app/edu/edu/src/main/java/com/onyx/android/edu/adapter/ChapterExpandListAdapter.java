package com.onyx.android.edu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.sdk.data.model.BaseData;
import com.onyx.libedu.model.BookNode;
import com.onyx.libedu.model.KnowledgePoint;

import java.util.List;

/**
 * Created by ming on 16/3/19.
 */
public class ChapterExpandListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "ChapterListAdapter";

    public interface OnBookNodeClickListener {
        void onItemClick(BookNode bookNode1, BookNode bookNode2);
    }

    public interface OnKnowPointClickListener {
        void onItemClick(KnowledgePoint knowledgePoint1, KnowledgePoint knowledgePoint2);
    }

    private boolean useBookNode = false;
    private List<BookNode> bookNodes;
    private List<KnowledgePoint> knowledgePoints;
    private Context mContext;
    private int lastExpandedGroupPosition;
    private ExpandableListView listView;
    private OnBookNodeClickListener onBookNodeClickListener;
    private OnKnowPointClickListener onKnowPointClickListener;

    public ChapterExpandListAdapter(ExpandableListView listView, boolean useBookNode, List<BookNode> bookNodes, List<KnowledgePoint> knowledgePoints){
        this.bookNodes = bookNodes;
        this.listView = listView;
        this.useBookNode = useBookNode;
        this.knowledgePoints = knowledgePoints;
    }

    @Override
    public int getGroupCount() {
        return useBookNode ? bookNodes.size() : knowledgePoints.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return useBookNode ? bookNodes.get(groupPosition) : knowledgePoints.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return useBookNode ? bookNodes.get(groupPosition).getChildrens() : knowledgePoints.get(groupPosition).getChildrens();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        mContext = parent.getContext();
        GroupViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_group_view, null);
            viewHolder = new GroupViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) convertView.getTag();
        }


        String title;
        if (useBookNode) {
            BookNode bookNode = (BookNode) getGroup(groupPosition);
            title = bookNode.getName();
        } else {
            KnowledgePoint point = (KnowledgePoint) getGroup(groupPosition);
            title = point.getName();
        }
        viewHolder.mTitle.setText(title);

        final ImageView label = viewHolder.mGroupLabel;
        final ExpandableListView listView = (ExpandableListView)parent;
        viewHolder.mGroupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded){
                    listView.collapseGroup(groupPosition);
                }else {
                    listView.expandGroup(groupPosition);
                }

            }
        });

        label.setImageResource(isExpanded ? R.mipmap.reduce : R.mipmap.add);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        ChildViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_child_view, null);
            viewHolder = new ChildViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }

        viewHolder.mChildView.removeAllViews();

        if (useBookNode) {
            List<BookNode> bookNodes = (List<BookNode>) getChild(groupPosition,childPosition);
            for (final BookNode bookNode : bookNodes) {
                View childView = generateChildView(parent.getContext(), bookNode.getName());
                childView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onBookNodeClickListener != null) {
                            onBookNodeClickListener.onItemClick((BookNode) getGroup(groupPosition), bookNode);
                        }
                    }
                });
                viewHolder.mChildView.addView(childView);
            }
        } else {
            List<KnowledgePoint> knowledgePoints = (List<KnowledgePoint>) getChild(groupPosition,childPosition);
            for (final KnowledgePoint knowledgePoint : knowledgePoints) {
                View childView = generateChildView(parent.getContext(), knowledgePoint.getName());
                childView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onKnowPointClickListener != null) {
                            onKnowPointClickListener.onItemClick((KnowledgePoint) getGroup(groupPosition), knowledgePoint);
                        }
                    }
                });
                viewHolder.mChildView.addView(childView);
            }
        }
        return convertView;
    }

    private View generateChildView(Context context, String name) {
        View view = getSingleChapterView(context);
        TextView text = (TextView)view.findViewById(R.id.content);
        text.setText(name);
        return view;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        if (groupPosition != lastExpandedGroupPosition){
            listView.collapseGroup(lastExpandedGroupPosition);
        }
        lastExpandedGroupPosition = groupPosition;
    }

    private View getSingleChapterView(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.single_chapter_item, null);
        return view;
    }

    public void setOnBookNodeClickListener(OnBookNodeClickListener onBookNodeClickListener) {
        this.onBookNodeClickListener = onBookNodeClickListener;
    }

    public void setOnKnowPointClickListener(OnKnowPointClickListener onKnowPointClickListener) {
        this.onKnowPointClickListener = onKnowPointClickListener;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class GroupViewHolder {

        private TextView mTitle;
        private LinearLayout mGroupView;
        private ImageView mGroupLabel;

        public GroupViewHolder(View itemView) {
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mGroupView = (LinearLayout) itemView.findViewById(R.id.group_view);
            mGroupLabel = (ImageView) itemView.findViewById(R.id.group_label);
        }
    }

    static class ChildViewHolder {

        private LinearLayout mChildView;

        public ChildViewHolder(View itemView) {
            mChildView = (LinearLayout) itemView.findViewById(R.id.child_view);
        }
    }
}
