package com.onyx.android.sdk.ui.data;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.OnyxGridView;
import com.onyx.android.sdk.ui.data.GridViewPageLayout.GridViewMode;
import com.onyx.android.sdk.ui.dialog.DialogReaderSettings.ReaderSettingsItem;

public class ReaderSettingsAdapter extends OnyxPagedAdapter
{
    private Context mContext = null;
    private ArrayList<ReaderSettingsItem> mReaderSettingsItems = new ArrayList<ReaderSettingsItem>();

    private static final int sItemMinWidth = 145;

    public ReaderSettingsAdapter(OnyxGridView gridView, ArrayList<ReaderSettingsItem> items)
    {
        super(gridView);

        mContext = gridView.getContext();
        mReaderSettingsItems.addAll(items);

        this.getPageLayout().setViewMode(GridViewMode.Detail);

        this.getPageLayout().setItemMinWidth(sItemMinWidth);
        this.getPageLayout().setItemMinHeight((int)mContext.getResources().getDimension(R.dimen.reader_settings_gridview_adapter_min_height));
        this.getPageLayout().setItemThumbnailMinHeight((int)mContext.getResources().getDimension(R.dimen.reader_settings_gridview_adapter_min_height));
        this.getPageLayout().setItemDetailMinHeight((int)mContext.getResources().getDimension(R.dimen.reader_settings_gridview_adapter_min_height));

        this.getPaginator().initializePageData(mReaderSettingsItems.size(), this.getPaginator().getPageSize());
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
        TextView textView_name = null;
        int idx = this.getPaginator().getItemIndex(position, this.getPaginator().getPageIndex());
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.reader_settings_gridview_item, null);
        }
        textView_name = (TextView) convertView.findViewById(R.id.textview_name);
        textView_name.setText(mReaderSettingsItems.get(idx).getReaderSettingsItemName());
        convertView.setTag(mReaderSettingsItems.get(idx).getReaderSettingsItemProperty());

        OnyxGridView.LayoutParams ret_view_params = new OnyxGridView.LayoutParams(this.getPageLayout().getItemCurrentWidth(),
                this.getPageLayout().getItemCurrentHeight());
        convertView.setLayoutParams(ret_view_params);

        return convertView;
    }

}
