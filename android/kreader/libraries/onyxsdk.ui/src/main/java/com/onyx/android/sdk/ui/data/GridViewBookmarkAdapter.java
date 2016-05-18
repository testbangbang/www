/**
 * 
 */
package com.onyx.android.sdk.ui.data;

import java.util.ArrayList;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.OnyxGridView;
import com.onyx.android.sdk.ui.data.GridViewPageLayout.GridViewMode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author dxwts
 *
 */
public class GridViewBookmarkAdapter extends OnyxPagedAdapter
{

    private LayoutInflater mInflater = null;
    private ArrayList<BookmarkItem> mBookmarkItems = new ArrayList<BookmarkItem>();

    private static final int sItemMinWidth = 145;
    private static final int sItemMinHeight = 60;
    private static final int sHorizontalSpacing = 0;
    private static final int sVerticalSpacing = 0;
    private static final int sItemDetailMinHeight = 60;

    public GridViewBookmarkAdapter(Context context, OnyxGridView gridView, ArrayList<BookmarkItem> BookmarkItems)
    {
        super(gridView);

        mInflater = LayoutInflater.from(context);
        mBookmarkItems.addAll(BookmarkItems);

        this.getPageLayout().setItemMinWidth(sItemMinWidth);
        this.getPageLayout().setItemMinHeight(sItemMinHeight);
        this.getPageLayout().setItemThumbnailMinHeight(sItemMinHeight);
        this.getPageLayout().setItemDetailMinHeight(sItemDetailMinHeight);
        this.getPageLayout().setHorizontalSpacing(sHorizontalSpacing);
        this.getPageLayout().setVerticalSpacing(sVerticalSpacing);
        this.getPageLayout().setViewMode(GridViewMode.Detail);

        this.getPaginator().initializePageData(mBookmarkItems.size(), this.getPaginator().getPageSize());
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View ret_view = null;
        if (convertView != null) {
            ret_view = convertView;
        } else {
            ret_view = mInflater.inflate(R.layout.gridview_bookmark_item, null);
        }

        int idx = this.getPaginator().getAbsoluteIndex(position);
        BookmarkItem bookmark_item = mBookmarkItems.get(idx);

        TextView title = (TextView) ret_view.findViewById(R.id.textview_title);

        if(bookmark_item.getTitle() != null) {
            title.setText(bookmark_item.getTitle());
        } else {
            title.setText("");
        }

        ret_view.setTag(bookmark_item);

        OnyxGridView.LayoutParams ret_view_params = new OnyxGridView.LayoutParams(this.getPageLayout().getItemCurrentWidth(),
                this.getPageLayout().getItemCurrentHeight());
        ret_view.setLayoutParams(ret_view_params);

        return ret_view;
    }

}
