package com.onyx.android.dr.reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.data.DictionaryQuery;
import com.onyx.android.dr.reader.event.DropDownListViewItemClickEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 2016/8/12.
 */
public class SearchDropDownListAdapter extends BaseAdapter implements View.OnClickListener {
    private static final String TAG = SearchDropDownListAdapter.class.getSimpleName();
    private List<DictionaryQuery> dictList = new ArrayList<>();
    private Context context;


    public SearchDropDownListAdapter(Context context) {
        this.context = context;
    }

    public List<DictionaryQuery> getHeadwordList() {
        return dictList;
    }

    public void setHeadwordList(List<DictionaryQuery> headwordList) {
        this.dictList.clear();
        this.dictList.addAll(headwordList);
    }

    @Override
    public int getCount() {
        return dictList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position > dictList.size() || position <= 0) {
            return null;
        }
        return dictList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null || convertView.getTag() == null || !(convertView.getTag() instanceof Holder)) {
            convertView = LayoutInflater.from(context).inflate(R.layout.dropdown_listview_item, null);
            holder = new Holder();
            holder.dictList = (LinearLayout) convertView.findViewById(R.id.dict_list_layout);
            holder.dictName = (TextView) convertView.findViewById(R.id.tv_headword);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        DictionaryQuery dictionaryQuery = dictList.get(position);
        holder.dictName.setText(dictionaryQuery.getDictName());
        holder.dictList.setTag(position);
        holder.dictList.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        Integer position = (Integer) v.getTag();
        DropDownListViewItemClickEvent dropDownListViewItemClickEvent = new DropDownListViewItemClickEvent(position);
        EventBus.getDefault().post(dropDownListViewItemClickEvent);
    }

    public void getSelectKeyword(final int position){
        if(position >= 0 && position < dictList.size()){
            DictionaryQuery dictionaryQuery = dictList.get(position);
            String name = dictionaryQuery.getDictName();
        }
    }

    private class Holder {
        LinearLayout dictList;
        TextView dictName;
    }
}
