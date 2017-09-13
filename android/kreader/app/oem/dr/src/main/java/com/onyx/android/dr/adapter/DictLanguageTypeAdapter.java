package com.onyx.android.dr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.LanguageTypeBean;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/15.
 */
public class DictLanguageTypeAdapter extends BaseAdapter {
    private List<LanguageTypeBean> list;
    private Context context;
    private boolean flag = false;

    public DictLanguageTypeAdapter(Context context) {
        this.context = context;
    }

    public void setDatas(List<LanguageTypeBean> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
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
        ViewHolder viewHolder = null;
        LanguageTypeBean bean = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_dict_language_type, null);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.dict_language_type_item_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(bean.getName());
        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}