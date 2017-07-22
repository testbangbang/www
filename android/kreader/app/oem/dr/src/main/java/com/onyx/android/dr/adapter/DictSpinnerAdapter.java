package com.onyx.android.dr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.onyx.android.dr.R;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/15.
 */
public class DictSpinnerAdapter extends BaseAdapter {
    private List<String> list;
    private Context context;

    public DictSpinnerAdapter(Context context) {
        this.context = context;
    }

    public void setDatas(List<String> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list.size() > 0) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater view = LayoutInflater.from(context);
        convertView = view.inflate(R.layout.item_spinner_expanded_pattern, null);
        if (convertView != null) {
            TextView textView = (TextView) convertView
                    .findViewById(R.id.spinner_textView);
            if (list.size() > 0){
                textView.setText(list.get(position));
            }
        }
        return convertView;
    }
}