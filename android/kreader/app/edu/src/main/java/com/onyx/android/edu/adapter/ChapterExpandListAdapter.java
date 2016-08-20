package com.onyx.android.edu.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.ui.exerciserespond.ExerciseRespondActivity;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ming on 16/3/19.
 */
public class ChapterExpandListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "ChapterListAdapter";

    private LinkedHashMap<String,List<String>> mDataMap;
    private Context mContext;

    public ChapterExpandListAdapter(LinkedHashMap<String,List<String>> list){
        mDataMap = list;
    }

    @Override
    public int getGroupCount() {
        return mDataMap.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        int index = 0;
        for (String key : mDataMap.keySet()) {
            if (index == groupPosition){
                return key;
            }
            index++;
        }
        return null;
    }

    @Override
    public List<String> getChild(int groupPosition, int childPosition) {
        int index = 0;
        for (String key : mDataMap.keySet()) {
            if (index == groupPosition){
                return mDataMap.get(key);
            }
            index++;
        }
        return null;
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

        String title = getGroup(groupPosition).toString();
        viewHolder.mTitle.setText(title);

        final ImageView label = viewHolder.mGroupLabel;
        final ExpandableListView listView = (ExpandableListView)parent;
        viewHolder.mGroupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded){
                    label.setImageResource(R.mipmap.add);
                    listView.collapseGroup(groupPosition);
                }else {
                    label.setImageResource(R.mipmap.reduce);
                    listView.expandGroup(groupPosition);
                }

            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        ChildViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_child_view, null);
            viewHolder = new ChildViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }

        if (viewHolder.mChildView.getChildCount() < 1){
            List<String> content = getChild(groupPosition,childPosition);
            for (String str : content) {
                View view = getSingleChapterView(parent.getContext());
                TextView text = (TextView)view.findViewById(R.id.content);
                ImageView write = (ImageView)view.findViewById(R.id.write);
                text.setText(str);
                write.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext,ExerciseRespondActivity.class));
                    }
                });
                viewHolder.mChildView.addView(view);
            }
        }


        return convertView;
    }

    private View getSingleChapterView(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.single_chapter_item, null);
        return view;
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
