package com.onyx.android.sdk.ui.data;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;

/**
 * 
 * @author peekaboo
 *
 */
public class DirectoryPhoneAdapter extends BaseAdapter {

	 private LayoutInflater mInflater = null;
	 private ArrayList<DirectoryItem> mDirectoryItems = new ArrayList<DirectoryItem>();
	    
	public DirectoryPhoneAdapter(Context context , ArrayList<DirectoryItem> DirectoryItems) {
		mInflater = LayoutInflater.from(context);
	    this.mDirectoryItems.addAll(DirectoryItems);
	}
	
	@Override
	public int getCount() {
		return mDirectoryItems.size();
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
        DirectoryItem directory_item = mDirectoryItems.get(position);

        TextView title = (TextView) ret_view.findViewById(R.id.textview_title);
        TextView page = (TextView) ret_view.findViewById(R.id.textview_page);

        if(directory_item.getTitle() != null) {
            title.setText(directory_item.getTitle());
        } else {
            title.setText("");
        }
        page.setText(directory_item.getPage());

        ret_view.setTag(directory_item);

        return ret_view;
	}

}
