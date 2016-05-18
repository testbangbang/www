package com.onyx.android.sdk.ui.data;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.dialog.data.AnnotationItem;
import com.onyx.android.sdk.ui.dialog.data.AnnotationItem.TitleType;

/**
 * 
 * @author peekaboo
 *
 */
public class DirectoryAnnotationPhoneAdapter extends BaseAdapter {

	 private LayoutInflater mInflater = null;
	 private ArrayList<AnnotationItem> mAnnotationItems = new ArrayList<AnnotationItem>();
	 
	 public DirectoryAnnotationPhoneAdapter(Context context , ArrayList<AnnotationItem> items) {
		mInflater = LayoutInflater.from(context);
        mAnnotationItems.addAll(items);
        
	}
	
	@Override
	public int getCount() {
		return mAnnotationItems.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View ret_view = null;
        if (convertView != null) {
            ret_view = convertView;
        } else {
            ret_view = mInflater.inflate(R.layout.gridview_directory_item, null);
        }

        AnnotationItem annotation_item = mAnnotationItems.get(position);

        TextView title = (TextView) ret_view.findViewById(R.id.textview_title);
        TextView page = (TextView) ret_view.findViewById(R.id.textview_page);

        if (annotation_item.getType() == TitleType.note) {
            title.setTextColor(Color.BLACK);
        }
        else {
            title.setTextColor(Color.rgb(50, 50, 50));
        }

        if(annotation_item.getTitle() != null) {
            title.setText(annotation_item.getTitle());
        } else {
            title.setText("");
        }
        page.setText(annotation_item.getPage());

        ret_view.setTag(annotation_item);

        return ret_view;
	}

}
