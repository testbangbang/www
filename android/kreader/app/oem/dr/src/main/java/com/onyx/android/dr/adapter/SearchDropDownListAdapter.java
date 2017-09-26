package com.onyx.android.dr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.event.SelectHeadwordEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 2016/8/12.
 */
public class SearchDropDownListAdapter extends BaseAdapter implements View.OnClickListener {
    private static final String TAG = SearchDropDownListAdapter.class.getSimpleName();
    private List<String> headwordList = new ArrayList<String>();
    private Context context;

    public SearchDropDownListAdapter(Context context) {
        this.context = context;
    }

    public List<String> getHeadwordList() {
        return headwordList;
    }

    public void setHeadwordList(List<String> headwordList) {
        this.headwordList.clear();
        this.headwordList.addAll(headwordList);
    }

    @Override
    public int getCount() {
        return headwordList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position > headwordList.size() || position <= 0) {
            return null;
        }
        return headwordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null || convertView.getTag() == null || !(convertView.getTag() instanceof Holder)) {
            convertView = LayoutInflater.from(context).inflate(R.layout.search_dropdown_listview_item, null);
            holder = new Holder();
            holder.headwordLayout = (LinearLayout) convertView.findViewById(R.id.headword_layout);
            holder.tvHeadWord = (TextView) convertView.findViewById(R.id.tv_headword);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tvHeadWord.setText(headwordList.get(position));
        holder.headwordLayout.setTag(position);
        holder.headwordLayout.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        Integer position = (Integer) v.getTag();
        String headword = headwordList.get(position);

        SelectHeadwordEvent selectHeadwordEvent = new SelectHeadwordEvent();
        selectHeadwordEvent.setObj(headword);
        EventBus.getDefault().post(selectHeadwordEvent);
    }

    public void getSelectKeyword(final int position){
        if(position >= 0 && position < headwordList.size()){
            String headword = headwordList.get(position);
            SelectHeadwordEvent selectHeadwordEvent = new SelectHeadwordEvent();
            selectHeadwordEvent.setObj(headword);
            EventBus.getDefault().post(selectHeadwordEvent);
        }
    }

    private class Holder {
        LinearLayout headwordLayout;
        TextView tvHeadWord;
    }
}
