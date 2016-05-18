/**
 * 
 */
package com.onyx.android.sdk.ui.data;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.OnyxGridView;
import com.onyx.android.sdk.ui.data.GridViewPageLayout.GridViewMode;

/**
 * @author Joy
 *
 */
public class MultipleSelectionAdapter extends OnyxPagedAdapter
{
    /**
     * absolute index of adapter's items
     */
    private HashSet<Integer> mSelections = new HashSet<Integer>();
    private String[] mAppNames = null;
    private ArrayList<Pair<String, ? extends Object>> mItems = null;
    private Context mContext = null;
    
    private static final int sItemMinWidth = 145;
    private static final int sItemMinHeight = 60;
    private static final int sHorizontalSpacing = 0;
    private static final int sVerticalSpacing = 0;
    
    public MultipleSelectionAdapter(Context context, OnyxGridView gridView)
    {
        this(context, gridView, null);
    }
    
    public MultipleSelectionAdapter(Context context, OnyxGridView gridView, String[] arrays)
    {
        this(context, gridView, arrays, new ArrayList<Integer>());
    }
    
    public MultipleSelectionAdapter(Context context, OnyxGridView gridView, String[] arrays, ArrayList<Integer> initialSelections)
    {
        super(gridView);
        
        mSelections.addAll(initialSelections);
        mContext = context;
        mAppNames = arrays;

        init(mAppNames.length);
    }
    
    /**
     * 
     * @param context
     * @param gridView
     * @param items String stands for item name, Object stores item tag
     */
    public MultipleSelectionAdapter(Context context, OnyxGridView gridView, ArrayList<Pair<String, ? extends Object>> items, ArrayList<Integer> initialSelections)
    {
        super(gridView);
        
        mSelections.addAll(initialSelections);
        mContext = context;
        mItems = items;

        init(mItems.size());
    }

    private void init(int count)
    {
        int itemDetailMinHeight = (int)mContext.getResources().getDimension(R.dimen.selection_adapter_min_height);
        this.getPageLayout().setItemMinWidth(sItemMinWidth);
        this.getPageLayout().setItemMinHeight(sItemMinHeight);
        this.getPageLayout().setItemThumbnailMinHeight(sItemMinHeight);
        this.getPageLayout().setItemDetailMinHeight(itemDetailMinHeight);
        this.getPageLayout().setHorizontalSpacing(sHorizontalSpacing);
        this.getPageLayout().setVerticalSpacing(sVerticalSpacing);
        this.getPageLayout().setViewMode(GridViewMode.Detail);

        this.getPaginator().initializePageData(count, this.getPaginator().getPageSize());
    }
    
    /**
     * absolute index of adapter's items
     */
    public HashSet<Integer> getSelection()
    {
        return mSelections;
    }
    /**
     * absolute index of adapter's items
     */
    public void addSelection(int index)
    {
        Integer i = Integer.valueOf(index);
        if (!mSelections.contains(i)) {
            mSelections.add(i);
        }
    }
    
    public void removeSelection(int index)
    {
        Integer i = Integer.valueOf(index);
        if (mSelections.contains(i)) {
            mSelections.remove(i);
        }
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
        
        int idx = -1;
        idx = this.getPaginator().getAbsoluteIndex(position);
        
        String text = null;
        if (mItems != null) {
            text = mItems.get(idx).first;
        }
        else {
            assert(mAppNames != null);
            text = mAppNames[idx];
        }
        
        if (convertView != null) {
            ret_view = convertView;
        }
        else {
            ret_view = View.inflate(mContext, R.layout.dialog_settings_gridview_item, null);
        }
        
        TextView text_view = (TextView)ret_view.findViewById(R.id.textview_gridview_item);
        text_view.setText(text);
        /*Todo Phone UI need these.*/
        //CheckBox check_box = (CheckBox) ret_view.findViewById(R.id.checkbox_gridview_item);
        Integer i = Integer.valueOf(idx);
        if (mSelections.contains(i)) {
            //check_box.setChecked(true);
        }
        else {
            //check_box.setChecked(false);
        }
        
        // TODO: getItemCurrentHeight() maybe 0, need further watch
        final int height = Math.max(this.getPageLayout().getItemCurrentHeight(),
                this.getPageLayout().getItemMinHeight());
        text_view.getLayoutParams().height = height;

        ret_view.setTag(text);
        
        return ret_view;
    }

}
