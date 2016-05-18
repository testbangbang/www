/**
 * 
 */
package com.onyx.android.sdk.ui.dialog.data;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.OnyxGridView;
import com.onyx.android.sdk.ui.data.GridViewPageLayout.GridViewMode;
import com.onyx.android.sdk.ui.data.OnyxPagedAdapter;
import com.onyx.android.sdk.ui.dialog.DialogSearchResult.SearchResultItem;

/**
 * @author joy
 *
 */
public class SearchResultAdapter extends OnyxPagedAdapter
{
    private static final int sItemMinWidth = 145;
    private static final int sItemMinHeight = 60;
    private static final int sHorizontalSpacing = 0;
    private static final int sVerticalSpacing = 0;
    private static final int sItemDetailMinHeight = 60;
    
    private LayoutInflater mInflater = null;
    private ArrayList<SearchResultItem> mItems = null;

    public SearchResultAdapter(OnyxGridView gridView, ArrayList<SearchResultItem> items)
    {
        super(gridView);
        
        this.getPageLayout().setItemMinWidth(sItemMinWidth);
        this.getPageLayout().setItemMinHeight(sItemMinHeight);
        this.getPageLayout().setItemThumbnailMinHeight(sItemMinHeight);
        this.getPageLayout().setItemDetailMinHeight(sItemDetailMinHeight);
        this.getPageLayout().setHorizontalSpacing(sHorizontalSpacing);
        this.getPageLayout().setVerticalSpacing(sVerticalSpacing);
        this.getPageLayout().setViewMode(GridViewMode.Detail);
        
        mInflater = LayoutInflater.from(gridView.getContext());
        mItems = items;
        
        this.getPaginator().initializePageData(mItems.size(), this.getPaginator().getPageSize());
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View ret_view = null;
        if (convertView != null) {
            ret_view = convertView;
        } else {
            ret_view = mInflater.inflate(R.layout.gridview_directory_item, null);
        }

        int idx = this.getPaginator().getItemIndex(position, this.getPaginator().getPageIndex());
        SearchResultItem item = mItems.get(idx);
        
        TextView title = (TextView) ret_view.findViewById(R.id.textview_title);
        TextView page = (TextView) ret_view.findViewById(R.id.textview_page);

        if(item.getTitle() != null) {
            title.setText(item.getTitle());
        } else {
            title.setText("");
        }
        page.setText(item.getPage());

        ret_view.setTag(item);

        OnyxGridView.LayoutParams ret_view_params = new OnyxGridView.LayoutParams(this.getPageLayout().getItemCurrentWidth(),
                this.getPageLayout().getItemCurrentHeight());
        ret_view.setLayoutParams(ret_view_params);

        return ret_view;
    }

}
