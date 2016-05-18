/**
 *
 */
package com.onyx.android.sdk.ui.data;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.OnyxGridView;
import com.onyx.android.sdk.ui.data.GridViewPageLayout.GridViewMode;
import com.onyx.android.sdk.ui.dialog.data.AnnotationItem;
import com.onyx.android.sdk.ui.dialog.data.AnnotationItem.TitleType;

import java.util.ArrayList;

/**
 * @author qingyue
 *
 */
public class GridViewAnnotationAdapter extends OnyxPagedAdapter
{
    private LayoutInflater mInflater = null;
    private final ArrayList<AnnotationItem> mAnnotationItems = new ArrayList<AnnotationItem>();

    private static final int sItemMinWidth = 145;
    private static final int sItemMinHeight = 60;
    private static final int sHorizontalSpacing = 0;
    private static final int sVerticalSpacing = 0;
    private static final int sItemDetailMinHeight = 60;
    private int mTargetIndex = 0;

    public GridViewAnnotationAdapter(Context context, OnyxGridView gridView, ArrayList<AnnotationItem> items) {
        this(context, gridView, items, -1);
    }

    public GridViewAnnotationAdapter(Context context, OnyxGridView gridView, ArrayList<AnnotationItem> items, int targetIndex) {
        super(gridView);

        mInflater = LayoutInflater.from(context);
        mAnnotationItems.addAll(items);
        mTargetIndex = targetIndex;

        this.getPageLayout().setItemMinWidth(sItemMinWidth);
        this.getPageLayout().setItemMinHeight(sItemMinHeight);
        this.getPageLayout().setItemThumbnailMinHeight(sItemMinHeight);
        this.getPageLayout().setItemDetailMinHeight(sItemDetailMinHeight);
        this.getPageLayout().setHorizontalSpacing(sHorizontalSpacing);
        this.getPageLayout().setVerticalSpacing(sVerticalSpacing);
        this.getPageLayout().setViewMode(GridViewMode.Detail);

        this.getPaginator().initializePageData(mAnnotationItems.size(), this.getPaginator().getPageSize());
    }

    public void remove(int position) {
        mAnnotationItems.remove(position);
        super.setItemCount(mAnnotationItems.size());
    }

    public void update(AnnotationItem item, int position) {
        mAnnotationItems.remove(position);
        mAnnotationItems.add(position, item);
        GridViewAnnotationAdapter.this.notifyDataSetChanged();
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
            ret_view = mInflater.inflate(R.layout.gridview_directory_item, null);
        }

        int idx = this.getPaginator().getAbsoluteIndex(position);
        AnnotationItem annotation_item = mAnnotationItems.get(idx);

        TextView title = (TextView) ret_view.findViewById(R.id.textview_title);
        TextView page = (TextView) ret_view.findViewById(R.id.textview_page);

        if (mTargetIndex != -1) {
            if (idx == mTargetIndex) {
                ret_view.setBackgroundResource(R.drawable.imagebtn_focused);
            }else {
                ret_view.setBackgroundResource(0);
            }
        }

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

        OnyxGridView.LayoutParams ret_view_params = new OnyxGridView.LayoutParams(this.getPageLayout().getItemCurrentWidth(),
                this.getPageLayout().getItemCurrentHeight());
        ret_view.setLayoutParams(ret_view_params);

        return ret_view;
    }
}
